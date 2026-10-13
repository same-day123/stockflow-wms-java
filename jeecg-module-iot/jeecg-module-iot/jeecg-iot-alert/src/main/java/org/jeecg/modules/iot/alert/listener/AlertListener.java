package org.jeecg.modules.iot.alert.listener;

import com.alibaba.fastjson2.JSON;
import org.jeecg.modules.iot.base.config.RabbitConfig;
import org.jeecg.modules.iot.alert.service.IReportDataAnalysisService;
import org.jeecg.modules.iot.base.mqtt.dto.TransferToAlertMessageDto;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author muht
 * @date 2025/4/27 18:00
 */
@Slf4j
@Component
public class AlertListener {

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            5,
            10,
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Autowired
    private IReportDataAnalysisService analysisService;

    @RabbitListener(queues = RabbitConfig.ALERT_QUEUE)
    public void listen(Message message, Channel channel) throws IOException {
        try {
            threadPoolExecutor.submit(() -> {
                String msgStr = new String(message.getBody());
                TransferToAlertMessageDto messageDto = JSON.parseObject(msgStr, TransferToAlertMessageDto.class);
                log.info("告警中心服务接收到数据上报消息，{}", messageDto);
                // 告警分析
                analysisService.reportAnalyse(messageDto);
            });
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("接收消息失败！", e);
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    true
            );
        }
    }
}
