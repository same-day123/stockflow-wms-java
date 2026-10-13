package org.jeecg.modules.iot.manage.mqtt.listener;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.jeecg.modules.iot.base.config.MqttConfig;
import org.jeecg.modules.iot.base.mqtt.constant.EnableEnum;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;
import org.jeecg.modules.iot.base.mqtt.report.ReportDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.jeecg.modules.iot.manage.constant.ModelTypeEnum;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.mapper.IotDeviceMapper;
import org.jeecg.modules.iot.manage.mqtt.processor.ReportDataProcessor;
import org.jeecg.modules.iot.manage.service.CacheService;
import org.jeecg.modules.iot.manage.service.IIotProductModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * mqtt消息监听器
 *
 * @author muht
 * @create 2025/4/21 15:19
 */
@Slf4j
@Component
public class MqttListener {

    /**
     * 自定义线程池
     */
    private static final ExecutorService executor =
            ExecutorBuilder.create()
                    .setCorePoolSize(10)
                    .setMaxPoolSize(20)
                    .setKeepAliveTime(30, TimeUnit.SECONDS)
                    .build();
    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private IotDeviceMapper iotDeviceMapper;
    @Autowired
    private IIotProductModelService iotProductModelService;
    @Autowired
    private ReportDataProcessor reportDataProcessor;
    @Autowired
    private CacheService cacheService;

    public void dispatchThread(String topic, MqttMessage mqttMessage) {
        executor.submit(() -> listen(topic, mqttMessage));
    }

    /**
     * 处理上报消息
     *
     * @param topic
     * @param mqttMessage
     */
    public void listen(String topic, MqttMessage mqttMessage) {
        log.info("收到mqtt消息，brokerUri={}, topic={}, mqttMessage={}", mqttConfig.getBrokerUri(), topic, mqttMessage.toString());
        // 上报消息解析
        ReportDataDTO reportData = this.parseMessage(topic, mqttMessage);
        // 查询设备信息
        final IotDevice deviceCache = cacheService.getDevice(reportData.getDeviceCode());
        if (deviceCache == null || EnableEnum.NO.getValue().equals(deviceCache.getIsEnable())) {
            log.error("上报设备未注册或被禁用，device_code={}", reportData.getDeviceCode());
            return;
        }
        reportData.setProductCode(deviceCache.getProductCode());

        // 更新设备 online 状态
        reportDataProcessor.deviceOnline(deviceCache, reportData);

        // 查询属性物模型
        Set<String> modelCodes = this.getAndCheckFieldModels(reportData);

        // 上报数据处理
        reportDataProcessor.processDeviceReport(modelCodes, reportData);
    }

    /**
     * 消息解析
     *
     * @param mqttMessage
     * @return
     */
    private ReportDataDTO parseMessage(String topic, MqttMessage mqttMessage) {
        JSONObject reportMsg = JSON.parseObject(mqttMessage.toString());

        ReportDataDTO reportDataDTO = new ReportDataDTO();
        reportDataDTO.setDeviceName(reportMsg.getString("deviceName"));
        reportDataDTO.setDeviceCode(reportMsg.getString("deviceCode"));
        reportDataDTO.setIp(reportMsg.getString("ip"));
        reportDataDTO.setTopic(topic);
        reportDataDTO.setReportTime(LocalDateTime.now());
        // 上报数据 json 结构对象
        reportDataDTO.setData(reportMsg.get("data"));

        return reportDataDTO;
    }

    /**
     * 查询并校验物模型
     *
     * @param reportData
     * @return
     */
    private Set<String> getAndCheckFieldModels(ReportDataDTO reportData) {
        Map<String, String> modelCodesMap = cacheService.getModelCodesByProductCode(reportData.getProductCode());
        log.info("查询到该产品({})下物模型编码列表为：{}", reportData.getProductCode(), modelCodesMap);
        Set<String> fieldModelCodes = modelCodesMap.entrySet().stream()
                // 只查询指标类型的物模型
                .filter(e -> ModelTypeEnum.FIELD.getType().equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(fieldModelCodes)) {
            throw new BusiException("未配置属性物模型，无法处理上报数据，productCode=" + reportData.getProductCode());
        }

        return fieldModelCodes;
    }
}
