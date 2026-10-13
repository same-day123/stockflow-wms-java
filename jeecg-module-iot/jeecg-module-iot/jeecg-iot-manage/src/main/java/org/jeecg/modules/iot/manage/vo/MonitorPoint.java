package org.jeecg.modules.iot.manage.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author muht
 * @date 2025/5/1 23:00
 */
@Data
@Accessors(chain = true)
public class MonitorPoint {
    /**
     * 物模型指标
     */
    private String modelCode;
    /**
     * 时间戳，yyyy-MM-dd HH:mm:ss
     */
    private String time;
    /**
     * 监控指标值
     */
    private Object value;
}
