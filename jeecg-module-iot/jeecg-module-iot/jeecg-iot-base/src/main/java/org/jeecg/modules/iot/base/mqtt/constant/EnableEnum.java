package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用枚举
 */
@Getter
@AllArgsConstructor
public enum EnableEnum {
    YES("1"),
    NO("0");
    private String value;
}
