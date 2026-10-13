package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备状态统计表
 *
 * @author muht
 * @create 2025/5/13 15:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusChart {
    @Schema(description = "产品名称")
    private String productName;
    @Schema(description = "未激活")
    private Integer unactivatedCount = 0;
    @Schema(description = "离线")
    private Integer offlineCount = 0;
    @Schema(description = "在线")
    private Integer onlineCount = 0;
    @Schema(description = "禁用")
    private Integer disabledCount = 0;

    public DeviceStatusChart(String productName) {
        this.productName = productName;
    }
}
