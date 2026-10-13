package org.jeecg.modules.iot.simulate.service;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.jeecg.modules.iot.simulate.base.DeviceMqttClient;
import org.jeecg.modules.iot.simulate.base.SamplesRuleEnum;
import org.jeecg.modules.iot.simulate.config.DeviceInfosProps;
import org.jeecg.modules.iot.simulate.dto.ReportMsg;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 设备管理器
 *
 * @author muht
 * @date 2025/4/19 15:45
 */
@Slf4j
@Component
public class DeviceRunner implements ApplicationRunner {

    private static final Random RANDOM = new Random();
    /**
     * 客户端线程池
     */
    private static final ThreadPoolExecutor clientExecutors = new ThreadPoolExecutor(
            20,
            20,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.AbortPolicy()
    );
    @Resource
    private DeviceMqttClientFactory deviceMqttClientFactory;
    @Resource
    private DeviceInfosProps deviceInfosProps;

    /**
     * 启动所有配置好的设备客户端
     *
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化客户端池
        deviceMqttClientFactory.initDeviceClients();

        // 获取客户端池
        ConcurrentHashMap<String, DeviceMqttClient> deviceClientMap = DeviceMqttClientFactory.deviceClientMap;
        log.info("设备端已启动，设备id列表={}", deviceClientMap.keySet().stream().collect(Collectors.toList()));
        log.info("开始上报数据......");

        for (DeviceMqttClient deviceClient : deviceClientMap.values()) {
            // 提交客户端上报任务
            clientExecutors.submit(() -> {
                SamplesRuleEnum samplesRule = deviceClient.getSamplesRule();
                log.info("设备{}，使用的样本规则：{}", deviceClient.getDeviceName(), samplesRule);
                // 无限循环，上报数据
                for (int round = 0;; round++) {
                    ReportMsg reportMsg = new ReportMsg();
                    reportMsg.setDeviceCode(deviceClient.getDeviceCode());
                    reportMsg.setIp(deviceClient.getIp());
                    reportMsg.setDeviceName(deviceClient.getDeviceName());
                    reportMsg.setPubTopic(deviceClient.getPubTopic());
                    // 根据样本策略获取样本数据
                    Object sampleData = samplesRule.getRule().getSample(deviceClient.getSamples(), round);
                    reportMsg.setData(sampleData);

                    MqttMessage mqttMsg = new MqttMessage(
                            JSON.toJSON(reportMsg).toString().getBytes(StandardCharsets.UTF_8));
                    mqttMsg.setQos(1);
                    mqttMsg.setRetained(true);

                    try {
                        // 发布上报数据
                        deviceClient.publish(deviceClient.getPubTopic(), mqttMsg);
                        log.info("deviceCode={} 上报消息 msg={}", deviceClient.getDeviceCode(), mqttMsg);
                    } catch (Exception e) {
                        log.error("定时上报数据异常！", e);
                    }

                    // 设置间隔时间
                    TimeUnit.SECONDS.sleep(deviceClient.getInterval());
                }
            });
        }
    }
}
