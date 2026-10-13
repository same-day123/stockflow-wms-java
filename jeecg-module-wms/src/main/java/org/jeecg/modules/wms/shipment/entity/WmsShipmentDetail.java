package org.jeecg.modules.wms.shipment.entity;

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
 * @Description: 包裹明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
@Schema(description="包裹明细表")
@Data
@TableName("wms_shipment_detail")
public class WmsShipmentDetail implements Serializable {
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
	/**包裹id*/
    @Schema(description = "包裹id")
    private String shipmentId;
	/**出库单id*/
	@Excel(name = "出库单id", width = 15)
    @Schema(description = "出库单id")
    private String orderId;
	/**出库单明细id*/
	@Excel(name = "出库单明细id", width = 15)
    @Schema(description = "出库单明细id")
    private String orderItemId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String skuId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @Schema(description = "数量")
    private Integer quantity;
    /**商品名称*/
    @TableField(exist = false)
    private String productName;
    /**剩余数量*/
    @TableField(exist = false)
    private Integer surplus;


}
