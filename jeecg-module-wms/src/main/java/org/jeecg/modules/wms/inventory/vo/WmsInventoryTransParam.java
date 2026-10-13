package org.jeecg.modules.wms.inventory.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Mr.M
 * @version 1.0
 * @description 库存变更参数类
 * @date 2025/7/20 18:00
 */
@Data
public class WmsInventoryTransParam {

    private static final long serialVersionUID = 1L;

    /**商品id*/
    @Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String productId;
    /**执行数量*/
    @Excel(name = "执行数量", width = 15)
    @Schema(description = "执行数量")
    private Integer execQuantity;
    /**仓库id*/
    @Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private java.lang.String warehouseId;
    /**来源储位编码*/
    @Excel(name = "来源储位编码", width = 15)
    @Schema(description = "来源储位编码")
    private String sourceLocationCode;
    /**目的储位编码*/
    @Excel(name = "目的储位编码", width = 15)
    @Schema(description = "目的储位编码")
    private String targetLocationCode;

    /**batchNumber批次号*/
    @Excel(name = "batchNumber批次号", width = 15)
    @Schema(description = "batchNumber批次号")
    private String batchNumber;

    @Excel(name = "保质期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expiryDate;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remarks;

    /**变更类型*/
    @Excel(name = "变更类型", width = 15)
    @Schema(description = "变更类型")
    private String transactionType;

    /**执行人*/
    @Excel(name = "执行人", width = 15)
    @Schema(description = "执行人")
    private String operator;
    /**执行时间*/
    @Excel(name = "执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "执行时间")
    private Date operationTime;

    /**是否可售 is_sellable*/
    @Excel(name = "是否可售 is_sellable", width = 15)
    @Schema(description = "是否可售 is_sellable")
    private String isSellable;
}
