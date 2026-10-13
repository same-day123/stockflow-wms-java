package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报警级别枚举
 */
@Getter
@AllArgsConstructor
public enum AlertLevelEnum {
    CRIT("emergency", "紧急", 1),
    WARN("warn", "警告", 2),
    GENERAL("general", "一般", 3);

    /**
     * 级别
     */
    private String level;
    /**
     * 级别名称
     */
    private String name;
    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 根据级别标识获取枚举
     *
     * @param level
     * @return
     */
    public static AlertLevelEnum getAlertLevelEnum(String level) {
        if (level == null) {
            return null;
        }
        return AlertLevelEnum.valueOf(level.toUpperCase());
    }
}
