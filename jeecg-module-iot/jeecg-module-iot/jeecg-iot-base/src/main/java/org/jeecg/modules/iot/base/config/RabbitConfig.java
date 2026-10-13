package org.jeecg.modules.iot.base.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author muht
 * @date 2025/4/27 17:35
 */
@Configuration
public class RabbitConfig {
    /**
     * 交换机
     */
    public static final String WMS_IOT_EXCHANGE = "wms_iot_exchange";
    /**
     * 消息队列
     */
    public static final String ALERT_QUEUE = "alert_queue";
    /**
     * 路由键
     */
    public static final String ROUTING_KEY = "alert_queue";
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(WMS_IOT_EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(ALERT_QUEUE, true);
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }
}
