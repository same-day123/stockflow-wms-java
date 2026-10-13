package org.jeecg.modules.wms.inorder.vo;

import java.util.List;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Data
@Schema(description="入库单主表")
public class WmsStockInOrdersPage {

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
	/**入库单号*/
	@Excel(name = "入库单号", width = 15)
	@Schema(description = "入库单号")
    private String orderNumber;
	/**入库类型*/
	@Excel(name = "入库类型", width = 15, dicCode = "asn_type")
    @Dict(dicCode = "asn_type")
	@Schema(description = "入库类型")
    private String orderType;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
	@Schema(description = "来源单号")
    private String sourceNumber;
	/**货主id*/
	@Excel(name = "货主id", width = 15)
	@Schema(description = "货主id")
    private String ownerId;
	/**预计到货时间*/
	@Excel(name = "预计到货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "预计到货时间")
    private Date expectedArrivalTime;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "asn_status")
    @Dict(dicCode = "asn_status")
	@Schema(description = "状态")
    private String status;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@Schema(description = "备注")
    private String remarks;
	/**预期总数量(冗余字段)*/
	@Excel(name = "预期总数量(冗余字段)", width = 15)
	@Schema(description = "预期总数量(冗余字段)")
    private Integer totalExpectedQuantity;
	/**实际收货总量*/
	@Excel(name = "实际收货总量", width = 15)
	@Schema(description = "实际收货总量")
    private Integer totalReceivedQuantity;
	/**已上架总量*/
	@Excel(name = "已上架总量", width = 15)
	@Schema(description = "已上架总量")
    private Integer totalShelvedQuantity;
	/**不良品总数量(冗余字段)*/
	@Excel(name = "不良品总数量(冗余字段)", width = 15)
	@Schema(description = "不良品总数量(冗余字段)")
    private Integer totalDefectiveQuantity;
	/**仓库*/
	@Excel(name = "仓库", width = 15)
	@Schema(description = "仓库")
    private String warehouseId;

	@ExcelCollection(name="入库单明细")
	@Schema(description = "入库单明细")
	private List<WmsStockInOrderItems> wmsStockInOrderItemsList;

}
