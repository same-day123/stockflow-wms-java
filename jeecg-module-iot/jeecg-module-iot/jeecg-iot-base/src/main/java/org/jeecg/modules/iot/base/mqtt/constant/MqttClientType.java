package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * mqtt 客户端类型
 */
@Getter
@AllArgsConstructor
public enum MqttClientType {
    /**
     * 应用端/系统端
     */
    SERVER,
    /**
     * 设备端
     */
    DEVICE
}
