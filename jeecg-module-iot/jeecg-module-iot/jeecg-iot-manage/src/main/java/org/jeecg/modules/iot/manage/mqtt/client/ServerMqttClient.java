package org.jeecg.modules.iot.manage.mqtt.client;

import org.jeecg.modules.iot.base.config.MqttConfig;
import org.jeecg.modules.iot.base.mqtt.constant.Const;
import org.jeecg.modules.iot.base.mqtt.constant.MqttClientType;
import org.jeecg.modules.iot.base.mqtt.constant.MqttTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 服务器MQTT客户端
 * mqtt broker web 地址：http://118.178.184.211:18083/
 * user=admin
 * password=admin_admin
 * @author muht
 * @create 2025/4/21 15:01
 **/
@Slf4j
@Component
public class ServerMqttClient extends MqttClient implements InitializingBean {

    private MqttConfig mqttConfig;

    /**
     * 服务器消息回调
     */
    private final ServerMqttCallback serverMqttCallback;

    public ServerMqttClient(MqttConfig mqttConfig, ServerMqttCallback serverMqttCallback) throws MqttException {
        super(mqttConfig.getBrokerUri(), mqttConfig.getServerClientId());
        this.serverMqttCallback = serverMqttCallback;
        this.mqttConfig = mqttConfig;
    }

    /**
     * 执行回调注册，连接与消息订阅操作
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // 设置回调函数
            this.setCallback(serverMqttCallback);

            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setAutomaticReconnect(true);
            if(mqttConfig.getPassword() != null){
                options.setPassword(mqttConfig.getPassword().getBytes());
            }


            // 执行连接操作
            this.connect(options);

            // 订阅主题
            for (MqttTopicEnum topic : MqttTopicEnum.getBySubscriber(MqttClientType.SERVER)) {
                this.subscribe(topic.getTopic(), Const.DEF_QOS);
            }

            log.info("服务端监听已启动，正在监听设备上报数据...");
        } catch (Exception e) {
            log.error("服务端连接 broker 异常", e);
        }
    }
}
