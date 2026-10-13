package org.jeecg.modules.wms.outorder.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 出库单
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
@Schema(description="出库单")
@Data
@TableName("wms_out_orders")
public class WmsOutOrders implements Serializable {
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
	/**出库单号*/
	@Excel(name = "出库单号", width = 15)
    @Schema(description = "出库单号")
    private String orderNo;
	/**出库类型*/
    @Dict(dicCode = "out_order_type")
	@Excel(name = "出库类型", width = 15)
    @Schema(description = "出库类型")
    private String orderType;
	/**订单来源*/
	@Excel(name = "订单来源", width = 15)
    @Schema(description = "订单来源")
    private String orderSource;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
    @Schema(description = "来源单号")
    private String orderSourceNo;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private String warehouseId;
	/**货主id*/
	@Excel(name = "货主id", width = 15)
    @Schema(description = "货主id")
    private String ownerId;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**预计发货时间*/
	@Excel(name = "预计发货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预计发货时间")
    private Date expectedShipTime;
	/**实际发货时间*/
	@Excel(name = "实际发货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际发货时间")
    private Date actualShipTime;
	/**总商品数量*/
	@Excel(name = "总商品数量", width = 15)
    @Schema(description = "总商品数量")
    private Integer totalQuantity;
	/**总sku种类数*/
	@Excel(name = "总sku种类数", width = 15)
    @Schema(description = "总sku种类数")
    private Integer totalSku;
	/**总重量*/
	@Excel(name = "总重量", width = 15)
    @Schema(description = "总重量")
    private Integer totalWeight;
	/**总体积*/
	@Excel(name = "总体积", width = 15)
    @Schema(description = "总体积")
    private Integer totalVolume;
	/**承运商编码*/
	@Excel(name = "承运商编码", width = 15)
    @Schema(description = "承运商编码")
    private String carrierCode;
	/**收货人*/
	@Excel(name = "收货人", width = 15)
    @Schema(description = "收货人")
    private String consignee;
	/**收货时间*/
	@Excel(name = "收货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "收货时间")
    private Date shippingTime;

    //收件人省
    @Excel(name = "收件人省", width = 15)
    @Schema(description = "收件人省")
    private String shippingProvince;

    //收件人市
    @Excel(name = "收件人市", width = 15)
    @Schema(description = "收件人市")
    private String shippingCity;

    //收件人县
    @Excel(name = "收件人县", width = 15)
    @Schema(description = "收件人县")
    private String shippingCounty;

    @Excel(name = "收件人省市区编码", width = 15)
    @Schema(description = "收件人省市区编码")
    private String region;
	/**收货地址*/
	@Excel(name = "收货地址", width = 15)
    @Schema(description = "收货地址")
    private String shippingAddress;
	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
    @Schema(description = "联系方式")
    private String contact;
	/**状态*/
    @Dict(dicCode = "out_order_status")
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;

    //wave_id波次id
    @Excel(name = "波次id", width = 15)
    @Schema(description = "波次id")
    private String waveId;

    @Excel(name = "是否创建运单", width = 15)
    @Schema(description = "是否创建运单")
    private String createdWaybill;

    @Excel(name = "包裹策略", width = 15)
    @Schema(description = "包裹策略")
    private String shipmentStrategy;

    //货主名称,非数据库字段
    @TableField(exist = false)
    private String ownerName;

    @TableField(exist = false)
    private String waveNo;

    //包裹数量
    @TableField(exist = false)
    private Integer packageCount;

    //状态数组
    @TableField(exist = false)
    private String[] statusArray;

    //skuId
    @TableField(exist = false)
    private String skuId;

}
