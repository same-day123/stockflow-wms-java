package org.jeecg.modules.iot.base.mqtt.dto;

import lombok.Data;

/**
 * manage -> alert 传输数据结构
 * @author muht
 * @create 2025/5/5 18:19
 */
@Data
public class TransferToAlertMessageDto {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 上报数据
     */
    private Object data;
}
