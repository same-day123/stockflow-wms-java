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
 * @Description: 库区表
 * @Author: jeecg-boot
 * @Date:   2025-04-09
 * @Version: V1.0
 */
@Data
@TableName("wms_storage_zones")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="库区表")
public class WmsStorageZones implements Serializable {
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
	/**库区编码*/
	@Excel(name = "库区编码", width = 15)
    @Schema(description = "库区编码")
    private String zoneCode;
	/**库区名称*/
	@Excel(name = "库区名称", width = 15)
    @Schema(description = "库区名称")
    private String zoneName;
	/**库区类型*/
	@Excel(name = "库区类型", width = 15)
    @Schema(description = "库区类型")
    @Dict(dicCode = "zone_type")
    private String zoneType;
	/**状态:创建,启用,禁用*/
	@Excel(name = "状态:创建,启用,禁用", width = 15, dicCode = "dict_item_status")
	@Dict(dicCode = "wms_status")
    @Schema(description = "状态:创建,启用,禁用")
    private String status;
	/**是否可售库存 0-否, 1-是*/
	@Excel(name = "是否可售库存 0-否, 1-是", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否可售库存 0-否, 1-是")
    private String isSellable;
	/**所属仓库*/
	@Excel(name = "所属仓库", width = 15)
    @Schema(description = "所属仓库")
    private String warehouseId;

    //仓库名称用于显示
    @TableField(exist = false)
    private String warehouseName;
}
