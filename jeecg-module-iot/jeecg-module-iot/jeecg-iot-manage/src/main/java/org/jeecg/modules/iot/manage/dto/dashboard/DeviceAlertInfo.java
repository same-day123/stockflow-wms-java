package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备报警信息(未解决的报警)
 *
 * @author muht
 * @create 2025/5/16 10:56
 */
@Data
public class DeviceAlertInfo {
    @Schema(description = "紧急数量")
    private Integer emerCount;
    @Schema(description = "紧急比例")
    private Double emerRatio;
    @Schema(description = "警告数量")
    private Integer warnCount;
    @Schema(description = "警告比例")
    private Double warnRatio;
    @Schema(description = "一般数量")
    private Integer generalCount;
    @Schema(description = "一般比例")
    private Double generalRatio;
    @Schema(description = "报警信息列表倒序")
    private List<AlertLogInfo> alertLogs;

    /**
     * 报警日志
     */
    @Data
    public static class AlertLogInfo {
        @Schema(description = "报警日志id")
        private String id;
        @Schema(description = "报警规则名称")
        private String ruleName;
        @Schema(description = "关联设备名称")
        private String deviceName;
        @Schema(description = "报警时间")
        private String alertTime;
        @Schema(description = "报警级别")
        private String alertLevel;
    }
}
