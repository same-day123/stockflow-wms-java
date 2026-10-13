package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 报警排名
 *
 * @author muht
 * @create 2025/5/15 09:41
 */
@Data
public class AlertRank {
    @Schema(description = "排名")
    private Integer rankNo;
    @Schema(description = "设备编码")
    private String deviceCode;
    @Schema(description = "设备名称")
    private String deviceName;
    @Schema(description = "报警数量")
    private Long alertCount;
    @Schema(description = "环比")
    private Long prevDiff;
}
