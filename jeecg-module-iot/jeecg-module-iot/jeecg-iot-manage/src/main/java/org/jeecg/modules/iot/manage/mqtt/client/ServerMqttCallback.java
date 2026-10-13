package org.jeecg.modules.iot.manage.mqtt.client;

import org.jeecg.modules.iot.manage.mqtt.listener.MqttListener;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mqtt 回调函数
 *
 * @author muht
 * @date 2025/4/19 15:07
 */
@Slf4j
@Component
public class ServerMqttCallback implements MqttCallback {

    @Autowired
    private MqttListener mqttMsgListener;

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        log.info("客户端已断开连接, mqttDisconnectResponse={}", mqttDisconnectResponse);
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        mqttMsgListener.dispatchThread(s, mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        log.info("deliveryComplete={}", iMqttToken);

    }

    @Override
    public void connectComplete(boolean b, String s) {
        log.info("已连接，b={}, s={}", b, s);

    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {

    }
}
