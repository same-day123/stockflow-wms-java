package org.jeecg.modules.wms.inorder.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Schema(description="入库单明细")
@Data
@TableName("wms_stock_in_order_items")
public class WmsStockInOrderItems implements Serializable {
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
	/**入库单id*/
    @Schema(description = "入库单id")
    private String orderId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String productId;
	/**采购数量*/
	@Excel(name = "采购数量", width = 15)
    @Schema(description = "采购数量")
    private Integer expectedQuantity;
	/**实际收货数量*/
	@Excel(name = "实际收货数量", width = 15)
    @Schema(description = "实际收货数量")
    private Integer receivedQuantity;
	/**上架数量*/
	@Excel(name = "上架数量", width = 15)
    @Schema(description = "上架数量")
    private Integer shelvedQuantity;
	/**不良品数量*/
	@Excel(name = "不良品数量", width = 15)
    @Schema(description = "不良品数量")
    private Integer defectiveQuantity;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remarks;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;

    //商品名称
    @TableField(exist = false)
    private String productName;
    //商品编码
    @TableField(exist = false)
    private String productCode;
    //入库单号
    @TableField(exist = false)
    private String orderNumber;

    //序号
    @TableField(exist = false)
    private java.lang.Integer serialNumber;
}
