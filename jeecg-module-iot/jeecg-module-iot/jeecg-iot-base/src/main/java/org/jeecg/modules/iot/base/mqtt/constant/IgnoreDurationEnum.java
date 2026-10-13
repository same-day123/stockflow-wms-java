package org.jeecg.modules.iot.base.mqtt.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ignore_duration_dict 对应枚举
 */
@Getter
@AllArgsConstructor
public enum IgnoreDurationEnum {
    ONE_MINUTE(1, 60),
    TEN_MINUTES(2, 10 * 60),
    HALF_HOUR(3, 30 * 60),
    ONE_HOUR(4, 60 * 60),
    ONE_DAY(5, 24 * 60 * 60);

    /**
     * 枚举序号（与ignore_duration_dict字典值对应）
     */
    private Integer idx;
    /**
     * 秒值
     */
    private Integer seconds;

    public static IgnoreDurationEnum getByIdx(Integer idx) {
        for (IgnoreDurationEnum e : values()) {
            if (e.getIdx().equals(idx)) {
                return e;
            }
        }
        // 如果序号不存在，默认选择 10 分钟
        return TEN_MINUTES;
    }

    /**
     * 通过索引直接获取秒值
     *
     * @param idx
     * @return
     */
    public static Integer getSecByIdx(Integer idx) {
        return getByIdx(idx).getSeconds();
    }

    /**
     * 通过秒值获取索引
     *
     * @param sec
     * @return
     */
    public static Integer getIdxBySec(Integer sec) {
        for (IgnoreDurationEnum e : values()) {
            if (e.getSeconds().equals(sec)) {
                return e.getIdx();
            }
        }
        return null;
    }
}
