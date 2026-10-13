package org.jeecg.modules.iot.manage.dto;

import lombok.Data;

/**
 * 设备控制请求体
 *
 * @author muht
 * @create 2025/5/8 10:26
 */
@Data
public class CtrlDeviceReq {
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 物模型code
     */
    private String modelCode;
    /**
     * 额外参数
     */
    private String args;
}
