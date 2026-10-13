package org.jeecg.modules.wms.warehouse.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
@Data
@TableName("wms_warehouses")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="仓库表")
public class WmsWarehouses implements Serializable {
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
	/**仓库代码*/
	@Excel(name = "仓库代码", width = 15)
    @Schema(description = "仓库代码")
    private String warehouseCode;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @Schema(description = "仓库名称")
    private String warehouseName;
	/**仓库属性*/
	@Excel(name = "仓库属性", width = 15, dicCode = "warehouse_attr")
	@Dict(dicCode = "warehouse_attr")
    @Schema(description = "仓库属性")
    private String warehouseAttr;
	/**状态:创建,启用,禁用*/
	@Excel(name = "状态:创建,启用,禁用", width = 15, dicCode = "dict_item_status")
	@Dict(dicCode = "wms_status")
    @Schema(description = "状态:创建,启用,禁用")
    private String status;
}
