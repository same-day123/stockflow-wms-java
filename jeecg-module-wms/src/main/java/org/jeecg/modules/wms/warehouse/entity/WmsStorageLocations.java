package org.jeecg.modules.wms.warehouse.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
@Data
@TableName("wms_storage_locations")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="库位表")
public class WmsStorageLocations implements Serializable {
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
	/**库位编码*/
	@Excel(name = "库位编码", width = 15)
    @Schema(description = "库位编码")
    private String locationCode;
	/**库位类别*/
	@Excel(name = "库位类别", width = 15)
    @Schema(description = "库位类别")
    private String locationCategory;
	/**库位类型*/
	@Excel(name = "库位类型", width = 15, dicCode = "location_type")
	@Dict(dicCode = "location_type")
    @Schema(description = "库位类型")
    private String locationType;
	/**状态:创建,启用,禁用*/
	@Excel(name = "状态:创建,启用,禁用", width = 15, dicCode = "dict_item_status")
	@Dict(dicCode = "wms_status")
    @Schema(description = "状态:创建,启用,禁用")
    private String status;
	/**所属仓库*/
	@Excel(name = "所属仓库", width = 15)
    @Schema(description = "所属仓库")
    private String warehouseId;
	/**所属库区*/
	@Excel(name = "所属库区", width = 15)
    @Schema(description = "所属库区")
    private String zoneId;
	/**巷道*/
	@Excel(name = "巷道", width = 15)
    @Schema(description = "巷道")
    private String locationAisle;
	/**排*/
	@Excel(name = "排", width = 15)
    @Schema(description = "排")
    private String locationLine;
	/**列*/
	@Excel(name = "列", width = 15)
    @Schema(description = "列")
    private String locationRank;
	/**层*/
	@Excel(name = "层", width = 15)
    @Schema(description = "层")
    private String locationLayer;
	/**长*/
	@Excel(name = "长", width = 15)
    @Schema(description = "长")
    private Double locationLength;
	/**宽*/
	@Excel(name = "宽", width = 15)
    @Schema(description = "宽")
    private Double locationWidth;
	/**容积*/
	@Excel(name = "容积", width = 15)
    @Schema(description = "容积")
    private Double locationCapacity;
	/**称重*/
	@Excel(name = "承重", width = 15)
    @Schema(description = "承重")
    private Double loadCapacity;
	/**是否可售*/
	@Excel(name = "是否可售", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否可售")
    private String isSellable;

    //仓库对象用于显示
    @TableField(exist = false)
    private WmsWarehouses warehouse;

    //库区对象用于显示
    @TableField(exist = false)
    private WmsStorageZones storageZones;
}
