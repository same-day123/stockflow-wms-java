package org.jeecg.modules.iot.base.mqtt.report;

import lombok.Data;

/**
 * 温湿度数据对象
 *
 * @author muht
 * @create 2025/4/21 14:02
 **/
@Data
public class TemperatureHumidity {
    /**
     * 温度(℃)
     */
    private Double temperature;
    /**
     * 湿度(%)
     */
    private Integer humidity;
}
