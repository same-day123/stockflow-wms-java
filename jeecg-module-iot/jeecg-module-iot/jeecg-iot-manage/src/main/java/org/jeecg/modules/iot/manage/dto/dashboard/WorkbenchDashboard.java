package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 工作台仪表盘
 *
 * @author muht
 * @create 2025/5/13 14:12
 */
@Data
public class WorkbenchDashboard {
    @Schema(description = "计数信息")
    private CounterInfo counterInfo;
    @Schema(description = "产品分布")
    private List<ProductRatio> productRatios;
    @Schema(description = "设备状态列表")
    private List<DeviceStatusChart> deviceStatusList;
    @Schema(description = "报警排名")
    private List<AlertRank> alertRanks;
    @Schema(description = "设备分布")
    private DeviceDistribution deviceDistribution;
    @Schema(description = "设备报警")
    private DeviceAlertInfo deviceAlertInfo;
}
