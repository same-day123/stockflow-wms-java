package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备分布
 *
 * @author muht
 * @create 2025/5/16 09:28
 */
@Data
public class DeviceDistribution {
    @Schema(description = "仓库名次")
    private String warehouseName;
    @Schema(description = "产品数量")
    private Integer productCount;
    @Schema(description = "设备数量")
    private Integer deviceCount;
    @Schema(description = "报警信息")
    private Integer alertCount;
    @Schema(description = "产品设备列表")
    private List<ProductDevices> productDevicesList;

    /**
     * 产品设备列表对象
     */
    @Data
    public static class ProductDevices {
        @Schema(description = "产品名称")
        private String productName;
        @Schema(description = "设备信息列表")
        private List<DeviceInfo> devices;
    }

    /**
     * 设备信息
     */
    @Data
    public static class DeviceInfo {
        @Schema(description = "设备名称")
        private String deviceName;
        @Schema(description = "设备状态")
        private String statusName;
        @Schema(description = "报警状态")
        private String alertStatus;
    }
}
