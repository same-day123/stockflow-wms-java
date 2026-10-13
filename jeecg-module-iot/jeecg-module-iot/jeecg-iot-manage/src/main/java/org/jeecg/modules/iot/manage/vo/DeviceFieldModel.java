package org.jeecg.modules.iot.manage.vo;

import lombok.Data;

/**
 * 设备详情属性数据
 *
 * @author muht
 * @create 2025/5/5 15:31
 */
@Data
public class DeviceFieldModel {
    /**
     * 属性编码
     */
    private String modelCode;
    /**
     * 属性名称
     */
    private String modelName;
    /**
     * 属性值
     */
    private Object value;
    /**
     * 单位
     */
    private String unit;
    /**
     * 更新时间
     */
    private String updateTime;
}
