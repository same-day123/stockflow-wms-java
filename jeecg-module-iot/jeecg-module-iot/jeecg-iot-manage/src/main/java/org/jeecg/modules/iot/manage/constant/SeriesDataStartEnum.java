package org.jeecg.modules.iot.manage.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 时序数据开始时间枚举
 */
@Getter
@AllArgsConstructor
public enum SeriesDataStartEnum {
    HALF_MIN("-30s"),
    ONE_MIN("-1m"),
    FIVE_MIN("-5m"),
    TEN_MIN("-10m"),
    HALF_HOUR("-30m"),
    ONE_HOUR("-1h"),
    ONE_DAY("-1d");

    /**
     * 值
     */
    private String val;
}
