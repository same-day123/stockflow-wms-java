package org.jeecg.modules.wms.wave.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 波次主表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Data
@TableName("wms_wave_master")
@Schema(description="波次主表")
public class WmsWaveMaster implements Serializable {
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
	/**波次编号*/
    @Excel(name = "波次编号", width = 15)
    @Schema(description = "波次编号")
    private String waveNo;
	/**仓库id*/
    @Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private String warehouseId;
	/**储区id*/
    @Excel(name = "储区id", width = 15)
    @Schema(description = "储区id")
    private String zoneId;
	/**波次策略*/
    @Excel(name = "波次策略", width = 15)
    @Schema(description = "波次策略")
    private String waveRuleId;
	/**订单数量*/
    @Excel(name = "订单数量", width = 15)
    @Schema(description = "订单数量")
    private Integer totalOrders;
	/**sku种类数量*/
    @Excel(name = "sku种类数量", width = 15)
    @Schema(description = "sku种类数量")
    private Integer totalItems;
	/**状态*/
    @Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
	/**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**sku数量*/
    @Excel(name = "sku数量", width = 15)
    @Schema(description = "sku数量")
    private Integer totalSkus;

    /**拣货数量*/
    @Excel(name = "拣货数量", width = 15)
    @Schema(description = "拣货数量")
    private Integer pickedQuantity;
    /**分拣数量*/
    @Excel(name = "分拣数量", width = 15)
    @Schema(description = "分拣数量")
    private Integer sortingQuantity;
    /**分拣订单数量*/
    @Excel(name = "分拣订单数量", width = 15)
    @Schema(description = "分拣订单数量")
    private Integer sortingOrderQuantity;

    /**拣货路径*/
    @Excel(name = "拣货路径", width = 15)
    @Schema(description = "拣货路径")
    private String pickPath;

    /**拣货路径图片*/
    @Excel(name = "拣货路径图片", width = 15)
    @Schema(description = "拣货路径图片")
    private String pickPathImg;

    /**包裹数量total_shipments*/
    @TableField(exist = false)
    private Integer totalShipments;

    /**仓库名称*/
    @TableField(exist = false)
    @Schema(description = "仓库名称")
    private String warehouseName;
}
