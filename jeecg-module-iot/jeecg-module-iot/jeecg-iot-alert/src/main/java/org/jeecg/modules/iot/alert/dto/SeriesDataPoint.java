package org.jeecg.modules.iot.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * 时序数据点
 *
 * @author muht
 * @create 2025/5/7 15:12
 */
@Data
@AllArgsConstructor
public class SeriesDataPoint {
    /**
     * 时间
     */
    private Instant time;
    /**
     * 值
     */
    private Number value;
}
