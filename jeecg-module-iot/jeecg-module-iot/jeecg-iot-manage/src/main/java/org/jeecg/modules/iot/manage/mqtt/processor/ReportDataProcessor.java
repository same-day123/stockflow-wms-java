package org.jeecg.modules.iot.manage.mqtt.processor;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.jeecg.modules.iot.base.config.MqttConfig;
import org.jeecg.modules.iot.base.mqtt.constant.CRUD;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import org.jeecg.modules.iot.base.mqtt.dto.TransferToAlertMessageDto;
import org.jeecg.modules.iot.base.mqtt.report.ReportDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.manage.constant.DeviceStatusEnum;
import org.jeecg.modules.iot.manage.dto.DeviceWebSocketPushDto;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.mapper.IotDeviceMapper;
import org.jeecg.modules.iot.manage.sender.AlertSender;
import org.jeecg.modules.iot.manage.service.CacheService;
import org.jeecg.modules.iot.manage.websocket.IotWebSocket;
import org.jeecg.modules.message.websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基础上报数据处理器
 *
 * @author muht
 * @create 2025/4/21 16:40
 */
@Slf4j
@Component
public class ReportDataProcessor {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Autowired
    private AlertSender alertSender;

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IotWebSocket webSocket;
//    private IotWebSocket webSocket;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MqttConfig mqttConfig;

    /**
     * 设备上线(操作缓存)
     *
     * @param deviceCache
     */
    public void deviceOnline(IotDevice deviceCache, ReportDataDTO reportData) {
        try {
            // 设备心跳缓存（6s过期）
            redisUtil.set(String.format(RedisConst.DEVICE_HEARTBEAT_KEY, deviceCache.getDeviceCode()), deviceCache.getDeviceCode(), mqttConfig.getReportInterval());

            // 设备在线状态表
            redisUtil.hset(RedisConst.DEVICE_STATUS_TABLE_KEY, deviceCache.getDeviceCode(), DeviceStatusEnum.ONLINE.getStatus());

            // ip地址同步到数据库，方便后续管理页面查询
            if (StringUtils.hasText(reportData.getIp()) && !reportData.getIp().equals(deviceCache.getIpAddr())) {
                IotDevice updDeviceIp = new IotDevice();
                updDeviceIp.setId(deviceCache.getId());
                updDeviceIp.setIpAddr(reportData.getIp());
                iotDeviceMapper.updateById(updDeviceIp);
            }

            // 更新设备实体，在线状态及IP信息
            deviceCache.setDeviceStatus(DeviceStatusEnum.ONLINE.getStatus());
            deviceCache.setIpAddr(reportData.getIp());
            cacheService.processDeviceCache(CRUD.UPDATE, deviceCache);
        } catch (Exception e) {
            log.error("deviceCode={}, 更新在线状态或心跳时间戳异常！", e);
        }
    }

    /**
     * 上报数据处理
     *
     * @param modelCodes 物模型列表
     * @param reportData 上报数据
     */
    public void processDeviceReport(Set<String> modelCodes, ReportDataDTO reportData) {
        // 上报数据存储
        this.saveReportData(reportData, modelCodes);

        // 数据转发告警中心
        this.publishToAlert(reportData);
    }

    /**
     * 保存上报数据（通用逻辑）
     * measurement：productCode
     * tag: deviceCode
     * field: modelCode
     *
     * @param reportData
     */
    private void saveReportData(ReportDataDTO reportData, Set<String> modelCodes) {
        log.info("开始保存时序数据... deviceCode={}, data={}", reportData.getDeviceCode(), reportData.getData());

        Map<String, Object> allReportFieldDatas = JSONObject.parseObject(JSON.toJSONString(reportData.getData()), HashMap.class);
        // 根据物模型过滤，只保留物模型中定义好的属性物模型
        Map<String, Object> fieldDatas = allReportFieldDatas.entrySet().stream()
                .filter(e -> modelCodes.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        try {
            // 构造时序数据点
            // measurement -> productCode
            // tag -> deviceCode
            // field -> model 属性物模型
//            Point point = Point.measurement("temperature")
//                    .addTag("location", "west")
//                    .addField("value", 55D)
//                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);

            Point point = Point.measurement(
                    reportData.getProductCode())
                    .addTag("deviceCode", reportData.getDeviceCode())
                    .addFields(fieldDatas)
                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);
            // 写入 influxdb
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            writeApi.writePoint(point);
            log.info("influxdb 写入成功, deviceCode={}, 数据={}", reportData.getDeviceCode(), JSON.toJSONString(fieldDatas));

            // 推送websocket到浏览器
            DeviceWebSocketPushDto pushDto = new DeviceWebSocketPushDto();
            pushDto.setDeviceCode(reportData.getDeviceCode());
            Map<String, HashMap> reportDataMap = new HashMap<>();
            fieldDatas.entrySet().stream().forEach(e -> {
//                DeviceLatestData deviceLatestData = new DeviceLatestData(e.getKey(), e.getValue(), Instant.now().getEpochSecond());
                //将deviceLatestData转为Map
                HashMap<String, Object> deviceLatestDataMap = new HashMap<>();
                deviceLatestDataMap.put("field",e.getKey());
                deviceLatestDataMap.put("value",e.getValue());
                String formatTime = LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                deviceLatestDataMap.put("time",formatTime);
                reportDataMap.put(e.getKey(), deviceLatestDataMap);
//                reportDataMap.put(e.getKey(), new DeviceLatestData(e.getKey(), e.getValue(), Instant.now().getEpochSecond()));
            });
            pushDto.setReportDataMap(reportDataMap);
            webSocket.pushMessage(JSON.toJSONString(pushDto));
//            webSocket.sendMessageToUsers(JSON.toJSONString(pushDto));
//            webSocket.sendAllMessage(JSON.toJSONString(pushDto));
        } catch (Exception e) {
            log.error("influx 写入数据失败", e);
        }

    }

    /**
     * 发布数据上报消息到告警中心
     *
     * @param reportData
     */
    private void publishToAlert(ReportDataDTO reportData) {
        // 类型转换
        TransferToAlertMessageDto messageDto = new TransferToAlertMessageDto();
        messageDto.setDeviceCode(reportData.getDeviceCode());
        messageDto.setDeviceName(reportData.getDeviceName());
        messageDto.setData(reportData.getData());
        log.info("开始将上报发送到告警服务...{}", JSON.toJSONString(messageDto));
        alertSender.send(JSON.toJSONString(messageDto));
    }

}
