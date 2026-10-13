package org.jeecg.modules.iot.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 产品物模型
 * @Author: jeecg-boot
 * @Date:   2025-04-24
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_product_model")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="产品物模型")
public class IotProductModel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**功能名称*/
	@Excel(name = "功能名称", width = 15)
    @Schema(description = "功能名称")
    private String modelName;
	/**功能编码*/
	@Excel(name = "功能编码", width = 15)
    @Schema(description = "功能编码")
    private String code;
	/**功能类型*/
	@Excel(name = "功能类型", width = 15, dicCode = "model_type_dict")
	@Dict(dicCode = "model_type_dict")
    @Schema(description = "功能类型")
    private String type;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**指令*/
	@Excel(name = "指令", width = 15)
    @Schema(description = "指令")
    private String command;
	/**是否传参*/
	@Excel(name = "是否传参", width = 15, dicCode = "yes_no_dict")
	@Dict(dicCode = "yes_no_dict")
    @Schema(description = "是否传参")
    private Integer needExtraArg;
	/**参数类型*/
	@Excel(name = "参数类型", width = 15, dicCode = "model_arg_type_dict")
	@Dict(dicCode = "model_arg_type_dict")
    @Schema(description = "参数类型")
    private String argType;
	/**功能描述*/
	@Excel(name = "功能描述", width = 15)
    @Schema(description = "功能描述")
    private String modelDesc;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
    /** 单位 */
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "所属部门")
    private String sysOrgCode;
    @Schema(description = "租户ID")
    private Integer tenantId;
}
