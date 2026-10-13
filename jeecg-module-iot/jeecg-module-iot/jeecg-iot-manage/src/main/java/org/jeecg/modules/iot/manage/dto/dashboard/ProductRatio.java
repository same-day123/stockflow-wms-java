package org.jeecg.modules.iot.manage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author muht
 * @create 2025/5/13 14:59
 */
@Data
@AllArgsConstructor
public class ProductRatio {
    @Schema(description = "产品名称")
    @Excel(name="产品名称")
    private String productName;
    @Schema(description = "产品编码")
    @Excel(name="产品编码")
    private String productCode;
    @Schema(description = "设备数量（台）")
    @Excel(name="设备数量（台）")
    private Integer deviceCount;
    @Schema(description = "比例（%）")
    @Excel(name="比例（%）")
    private String ratio;
}
