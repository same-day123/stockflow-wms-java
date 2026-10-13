package org.jeecg.modules.iot.simulate.service;

import jakarta.annotation.Resource;
import org.jeecg.modules.iot.simulate.base.DeviceMqttClient;
import org.jeecg.modules.iot.simulate.base.SamplesRuleEnum;
import org.jeecg.modules.iot.simulate.config.DeviceInfosProps;
import org.jeecg.modules.iot.simulate.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 设备客户端装载工厂
 *
 * @author muht
 * @date 2025/4/19 16:47
 */
@Slf4j
@Component
public class DeviceMqttClientFactory {

    /**
     * 设备客户端池
     */
    public static final ConcurrentHashMap<String, DeviceMqttClient> deviceClientMap = new ConcurrentHashMap();

    @Resource
    private DeviceInfosProps deviceInfosProps;

    /**
     * 解析配置，并组装客户端对象
     *
     * @return
     */
    public void initDeviceClients() throws Exception {
        List<DeviceInfosProps.Device> deviceProps = deviceInfosProps.getDevices().stream()
                .filter(d -> d.isEnable())
                .collect(Collectors.toList());
        log.info("开始初始化设备端 mqtt client, 设备ID列表={}", Optional.ofNullable(deviceProps).orElse(new ArrayList<>()).stream()
                .map(d -> d.getDeviceCode()).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(deviceProps)) {
            throw new Exception("未配置设备信息，初始化设备端 mqtt client 异常");
        }

        for (DeviceInfosProps.Device deviceProp : deviceProps) {
            try {
                DeviceMqttClient deviceMqttClient = new DeviceMqttClient(deviceInfosProps.getBrokerUri(), deviceProp.getDeviceCode());
                // 设置相关属性
                deviceMqttClient.setDeviceName(deviceProp.getDeviceName());
                deviceMqttClient.setDeviceCode(deviceProp.getDeviceCode());
                // 上报频率，优先设备自定义频率
                deviceMqttClient.setInterval(Optional.ofNullable(deviceProp.getReportInterval()).orElse(deviceInfosProps.getReportInterval()));
                deviceMqttClient.setPubTopic(deviceInfosProps.getPubTopic());
                deviceMqttClient.setIp(IpUtils.getLocalIPs().get(0));
                deviceMqttClient.setSamples(deviceProp.getSamples());
                // 样本策略，默认往复策略
                deviceMqttClient.setSamplesRule(SamplesRuleEnum.getSamplesRule(deviceProp.getSamplesRule()));

                // 设置连接选项
                MqttConnectionOptions options = new MqttConnectionOptions();
                options.setAutomaticReconnect(true);
                if(deviceProp.getPassword() != null){
                    options.setPassword(deviceProp.getPassword().getBytes());
                }

                // 连接broker
                deviceMqttClient.connect(options);
                // 订阅消息(优先设备自定义配置),前提是客户端需要先连接 broker
                String pubTopic = Optional.ofNullable(deviceProp.getSubTopic()).orElse(deviceInfosProps.getSubTopic());
                deviceMqttClient.subscribe(pubTopic, 1);

                // 保存到客户端池
                deviceClientMap.put(deviceProp.getDeviceCode(), deviceMqttClient);
            } catch (Exception e) {
                log.error("创建设备客户端异常!deviceCode={}", deviceProp.getDeviceCode(), e);
            }
        }
    }
}
