package org.jeecg.modules.iot.base.mqtt.dto;

import lombok.Data;

/**
 * @author muht
 * @date 2025/4/21 21:50
 */
@Data
public class ModelDto {
    /**
     * 物模型类型（property 属性; service 服务）
     */
    private String modelType;
    /**
     * 物模型名称
     */
    private String modelName;
    /**
     * 物模型编码
     */
    private String modelCode;
    /**
     * 物模型指令（service类型时需要）
     */
    private String command;
    /**
     * 物模型描述信息
     */
    private String desc;
}
