package org.jeecg.modules.iot.manage.sender;

import org.jeecg.modules.iot.base.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 告警中心消息发送器
 *
 * @author muht
 * @date 2025/4/27 18:21
 */
@Component
public class AlertSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * rabbitmq 消息发送
     * @param message
     */
    public void send(String message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.WMS_IOT_EXCHANGE, RabbitConfig.ROUTING_KEY, message);
    }
}
