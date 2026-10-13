package org.jeecg.modules.iot.base.mqtt.report;

import lombok.Data;

/**
 * 电量
 *
 * @author muht
 * @create 2025/4/21 15:56
 */
@Data
public class BatteryStatus {
    /**
     * 剩余电量 %
     */
    private Double electricityRatio;
    /**
     * 剩余可用时间 s
     */
    private Integer leftWorkTime;
}
