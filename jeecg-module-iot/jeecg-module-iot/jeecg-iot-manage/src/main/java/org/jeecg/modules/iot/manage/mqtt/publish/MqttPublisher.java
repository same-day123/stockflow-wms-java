package org.jeecg.modules.iot.manage.mqtt.publish;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.jeecg.modules.iot.manage.mqtt.client.ServerMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * mqtt消息发布器
 *
 * @author muht
 * @create 2025/5/8 10:43
 */
@Slf4j
@Component
public class MqttPublisher {

    @Autowired
    private ServerMqttClient serverClient;

    /**
     * 发布消息
     *
     * @param topic
     * @param cmd
     */
    public boolean publish(String topic, String cmd) {
        MqttMessage mqttMsg = new MqttMessage(cmd.getBytes(StandardCharsets.UTF_8));
        mqttMsg.setQos(1);
        mqttMsg.setRetained(true);
        try {
            serverClient.publish(topic, mqttMsg);
        } catch (Exception e) {
            log.error("发送 mqtt 消息到设备端异常！", e);
            return false;
        }
        return true;
    }
}
