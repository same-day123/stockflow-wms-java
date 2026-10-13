package org.jeecg.modules.iot.base.mqtt.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上报数据结构对象
 *
 * @author muht
 * @create 2025/4/21 16:06
 */
@Data
public class ReportDataDTO {
    /**
     * 上报主题
     */
    private String topic;
    /**
     * 设备 Code
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备ip地址
     */
    private String ip;
    /**
     * 产品编号
     */
    private String productCode;
    /**
     * 上报时间
     */
    private LocalDateTime reportTime;
    /**
     * 监控数据
     */
    private Object data;
}
