package org.jeecg.modules.iot.manage.dto;

import lombok.Data;

/**
 * 设备详情页指标历史数据
 *
 * @author muht
 * @create 2025/5/9 11:38
 */
@Data
public class DeviceHistoryData {
    /**
     * 序号
     */
    private Integer no;
    /**
     * 功能名称
     */
    private String modelName;
    /**
     * 值，30℃
     */
    private String value;
    /**
     * 时间，yyyy-MM-dd HH:mm:ss
     */
    private String time;
}
