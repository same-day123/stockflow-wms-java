package org.jeecg.modules.iot.base.mqtt.dto;

import lombok.Data;

import java.util.List;

/**
 * 设备产品-物模型信息
 *
 * @author muht
 * @create 2025/4/21 17:25
 */
@Data
public class DeviceProductModelDto {
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 物模型列表
     */
    private List<ModelDto> modelList;
}
