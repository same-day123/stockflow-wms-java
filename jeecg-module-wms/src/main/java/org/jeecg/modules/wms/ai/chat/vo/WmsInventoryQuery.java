package org.jeecg.modules.wms.ai.chat.vo;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author Mr.M
 * @version 1.0
 * @description 库存查询条件
 * @date 2025/8/24 15:04
 */
@Data
public class WmsInventoryQuery {

    //商品名称
    @ToolParam(required = true, description = "商品名称(必填)")
    private String productName;

    //商品编码
    @ToolParam(required = false, description = "商品编码")
    private String productCode;

    //货主名称
    @ToolParam(required = true, description = "货主名称(必填)")
    private String ownerName;

    //货主编码
    @ToolParam(required = false, description = "货主编码")
    private String ownerCode;

    //仓库名称
    @ToolParam(required = true, description = "仓库名称(必填)")
    private String warehouseName;

    //仓库编码
    @ToolParam(required = false, description = "仓库编码")
    private String warehouseCode;

    //储位编码
    @ToolParam(required = false, description = "储位编码")
    private String locationCode;
}