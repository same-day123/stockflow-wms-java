package org.jeecg.modules.iot.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mqtt 配置属性
 *
 * @author muht
 * @create 2025/4/18 17:31
 **/
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    /**
     * broker地址
     */
    private String brokerUri;
    /**
     * 客户端id
     */
    private String serverClientId;
    /**
     * 连接密码
     */
    private String password;
    /**
     * 设备上报时间间隔(s)
     */
    private Integer reportInterval = 6;
}
