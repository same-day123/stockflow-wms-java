package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 计数器
 *
 * @author muht
 * @create 2025/5/13 14:24
 */
@Data
public class CounterInfo {
    @Schema(description = "产品数量")
    private Integer productCount=0;
    @Schema(description = "设备数量")
    private Integer deviceCount=0;
    @Schema(description = "当前报警设备")
    private Integer alertDeviceCount=0;
    @Schema(description = "当前报警信息")
    private Integer currentAlertCount=0;
    @Schema(description = "近7天报警信息")
    private Integer last7DaysAlertCount=0;
}
