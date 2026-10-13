package org.jeecg.modules.iot.manage.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 监控图表对象
 *
 * @author muht
 * @date 2025/5/2 07:51
 */
@Data
@Accessors(chain = true)
public class MonitorChart {
    /**
     * 监控配置ID
     */
    private String id;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备名+仓库名
     */
    private String deviceWithWarehouseName;
    /**
     * 物模型编码
     */
    private String modelCode;
    /**
     * 物模型名称
     */
    private String modelName;
    /**
     * 单位
     */
    private String unit;
    /**
     * 监控数据点集合
     */
    private List<MonitorPoint> points;
}
