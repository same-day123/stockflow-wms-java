package org.jeecg.modules.wms.shipment.vo;

import java.util.List;

import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 包裹主表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
@Data
@Schema(description="包裹主表")
public class WmsShipmentPage {

	/**主键*/
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
	/**包裹编码*/
	@Excel(name = "包裹编码", width = 15)
	@Schema(description = "包裹编码")
    private String shipmentNo;
	/**出库单id*/
	@Excel(name = "出库单id", width = 15)
	@Schema(description = "出库单id")
    private String orderId;
	/**波次id*/
	@Excel(name = "波次id", width = 15)
	@Schema(description = "波次id")
    private String waveId;
	/**包裹类型*/
	@Excel(name = "包裹类型", width = 15)
	@Schema(description = "包裹类型")
    private String shipmentType;
	/**承运商id*/
	@Excel(name = "承运商id", width = 15)
	@Schema(description = "承运商id")
    private String carrierId;
	/**物流单号*/
	@Excel(name = "物流单号", width = 15)
	@Schema(description = "物流单号")
    private String trackingNo;
	/**包材id*/
	@Excel(name = "包材id", width = 15)
	@Schema(description = "包材id")
    private String packagingId;
	/**总重量*/
	@Excel(name = "总重量", width = 15)
	@Schema(description = "总重量")
    private Double totalWeight;
	/**重量单位*/
	@Excel(name = "重量单位", width = 15)
	@Schema(description = "重量单位")
    private String weightUnit;
	/**长*/
	@Excel(name = "长", width = 15)
	@Schema(description = "长")
    private Double sizeLength;
	/**宽*/
	@Excel(name = "宽", width = 15)
	@Schema(description = "宽")
    private Double sizeWidth;
	/**高*/
	@Excel(name = "高", width = 15)
	@Schema(description = "高")
    private Double sizeHeight;
	/**尺寸单位*/
	@Excel(name = "尺寸单位", width = 15)
	@Schema(description = "尺寸单位")
    private String sizeUnit;
	/**总体积*/
	@Excel(name = "总体积", width = 15)
	@Schema(description = "总体积")
    private Double totalVolume;
	/**体积单位*/
	@Excel(name = "体积单位", width = 15)
	@Schema(description = "体积单位")
    private String volumeUnit;
	/**包裹件数*/
	@Excel(name = "包裹件数", width = 15)
	@Schema(description = "包裹件数")
    private Integer packageCount;
	/**送货地址*/
	@Excel(name = "送货地址", width = 15)
	@Schema(description = "送货地址")
    private String fromAddress;
	/**收货地址*/
	@Excel(name = "收货地址", width = 15)
	@Schema(description = "收货地址")
    private String toAddress;
	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
	@Schema(description = "联系方式")
    private String contact;
	/**预计到达时间*/
	@Excel(name = "预计到达时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "预计到达时间")
    private Date estimatedArrival;
	/**实际发货时间*/
	@Excel(name = "实际发货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "实际发货时间")
    private Date actualDeparture;
	/**实际到达时间*/
	@Excel(name = "实际到达时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "实际到达时间")
    private Date actualArrival;
	/**状态*/
	@Excel(name = "状态", width = 15)
	@Schema(description = "状态")
    private String status;
	/**打包人*/
	@Excel(name = "打包人", width = 15)
	@Schema(description = "打包人")
    private String packerId;
	/**发货人*/
	@Excel(name = "发货人", width = 15)
	@Schema(description = "发货人")
    private String shipperId;

	@ExcelCollection(name="包裹明细表")
	@Schema(description = "包裹明细表")
	private List<WmsShipmentDetail> wmsShipmentDetailList;

}
