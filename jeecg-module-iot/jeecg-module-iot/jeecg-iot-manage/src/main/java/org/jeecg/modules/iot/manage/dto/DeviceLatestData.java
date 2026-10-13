package org.jeecg.modules.iot.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author muht
 * @create 2025/5/5 16:22
 */
@Data
@AllArgsConstructor
public class DeviceLatestData {
    /**
     * 指标
     */
    private String field;
    /**
     * 值
     */
    private Object value;
    /**
     * 时间戳 - 秒
     */
    private Long time;
}
