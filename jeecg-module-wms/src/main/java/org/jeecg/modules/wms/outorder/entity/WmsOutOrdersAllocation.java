package org.jeecg.modules.wms.outorder.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 出库单分配明细
 * @Author: jeecg-boot
 * @Date:   2025-05-21
 * @Version: V1.0
 */
@Data
@TableName("wms_out_orders_allocation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="出库单分配明细")
public class WmsOutOrdersAllocation implements Serializable {
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
	@Excel(name = "出库单id", width = 15)
    @Schema(description = "出库单id")
    private String orderId;
	/**出库单明细ID*/
	@Excel(name = "出库单明细ID", width = 15)
    @Schema(description = "出库单明细ID")
    private String orderItemId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String skuId;
	/**储位编号*/
	@Excel(name = "储位编号", width = 15)
    @Schema(description = "储位编号")
    private String locationCode;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNumber;
	/**容器编号*/
	@Excel(name = "容器编号", width = 15)
    @Schema(description = "容器编号")
    private String containerCode;
	/**分配数量*/
	@Excel(name = "分配数量", width = 15)
    @Schema(description = "分配数量")
    private Integer allocatedQuantity;
	/**拣货数量*/
	@Excel(name = "拣货数量", width = 15)
    @Schema(description = "拣货数量")
    private Integer pickedQuantity;
    /**库存id*/
    @Excel(name = "库存id", width = 15)
    @Schema(description = "库存id")
    private String inventoryId;
    /**状态*/
    @Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;

    /**货主id*/
    @TableField(exist = false)
    private String ownerId;
    /**商品编码*/
    @TableField(exist = false)
    private String productCode;
    /**商品条码*/
    @TableField(exist = false)
    private String productBarcode;

    /**商品名称*/
    @TableField(exist = false)
    private String productName;
}
