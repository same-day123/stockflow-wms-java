package org.jeecg.modules.iot.manage.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author muht
 * @create 2025/5/5 15:55
 */
@Data
public class DeviceWebSocketPushDto {
    /**
     * 设备code
     */
    private String deviceCode;
    /**
     * 上报数据
     * <field, data>
     */
    private Map<String, HashMap> reportDataMap;
}
