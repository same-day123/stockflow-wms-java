package org.jeecg.modules.iot.simulate.base;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

/**
 * mqtt 回调函数
 *
 * @author muht
 * @date 2025/4/19 15:07
 */
@Slf4j
public class WmsMqttCallback implements MqttCallback {

    private String brokerUri;

    private String clientId;

    public WmsMqttCallback(String brokerUri, String clientId) {
        this.brokerUri = brokerUri;
        this.clientId = clientId;
    }

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        log.info("客户端已断开连接, mqttDisconnectResponse={}", mqttDisconnectResponse);
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        // 转发到消息处理器
        log.info("设备端收到控制消息，topic={}, mqttMessage={}", s, mqttMessage.toString());
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
