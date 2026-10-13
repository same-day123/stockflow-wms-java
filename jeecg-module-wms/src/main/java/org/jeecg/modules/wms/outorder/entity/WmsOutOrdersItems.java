package org.jeecg.modules.wms.outorder.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 出库单明细
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
@Schema(description="出库单明细")
@Data
@TableName("wms_out_orders_items")
public class WmsOutOrdersItems implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
	/**出库单id*/
    @Schema(description = "出库单id")
    private String orderId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String skuId;
	/**预期出库数量*/
	@Excel(name = "预期出库数量", width = 15)
    @Schema(description = "预期出库数量")
    private Integer expectedQuantity;
	/**分配数量*/
	@Excel(name = "分配数量", width = 15)
    @Schema(description = "分配数量")
    private Integer allocatedQuantity;
	/**拣货数量*/
	@Excel(name = "拣货数量", width = 15)
    @Schema(description = "拣货数量")
    private Integer pickedQuantity;
	/**打包数量*/
	@Excel(name = "打包数量", width = 15)
    @Schema(description = "打包数量")
    private Integer packedQuantity;
//	/**单位*/
//	@Excel(name = "单位", width = 15)
//    @Schema(description = "单位")
//    private String unit;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNumber;
	/**保质期*/
	@Excel(name = "保质期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "保质期")
    private Date expiryDate;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;

    /**
     * 商品名称
     */
    @TableField(exist = false)
    private String productName;
}
