package org.jeecg.modules.iot.manage.vo;

import lombok.Data;

/**
 * 设备功能
 *
 * @author muht
 * @create 2025/5/5 15:35
 */
@Data
public class DeviceServiceModel {
    /**
     * 功能编码
     */
    private String modelCode;
    /**
     * 功能名称
     */
    private String modelName;
    /**
     * 指令
     */
    private String command;
    /**
     * 是否额外传参
     */
    private Integer needExtraArg;
    /**
     * 参数类型
     */
    private String argType;
    /**
     * 功能描述
     */
    private String modelDesc;
}
