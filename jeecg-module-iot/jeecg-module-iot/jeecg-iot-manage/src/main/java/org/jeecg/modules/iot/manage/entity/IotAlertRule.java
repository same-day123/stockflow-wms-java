package org.jeecg.modules.iot.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 报警规则表
 * @Author: jeecg-boot
 * @Date:   2025-05-03
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_alert_rule")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="报警规则表")
public class IotAlertRule implements Serializable {
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
	/**告警规则名称*/
	@Excel(name = "告警规则名称", width = 15)
    @Schema(description = "告警规则名称")
    private String alertName;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**所属产品*/
	@Excel(name = "所属产品", width = 15)
    @Schema(description = "所属产品")
    private String productName;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @Schema(description = "设备编码")
    private String deviceCode;
	/**关联设备*/
	@Excel(name = "关联设备", width = 15)
    @Schema(description = "关联设备")
    private String deviceName;
	/**指标编码*/
	@Excel(name = "指标编码", width = 15)
    @Schema(description = "指标编码")
    private String modelCode;
	/**监控指标*/
	@Excel(name = "监控指标", width = 15)
    @Schema(description = "监控指标")
    private String modelName;
	/**报警级别*/
	@Excel(name = "报警级别", width = 15)
    @Schema(description = "报警级别")
    private String alertLevel;
	/**运算符*/
	@Excel(name = "运算符", width = 15)
    @Schema(description = "运算符")
    private String operator;
	/**阈值*/
	@Excel(name = "阈值", width = 15)
    @Schema(description = "阈值")
    private Double threshold;
	/**连续周期*/
	@Excel(name = "连续周期", width = 15)
    @Schema(description = "连续周期")
    private Integer cycle;
	/**沉默周期*/
	@Excel(name = "沉默周期", width = 15)
    @Schema(description = "沉默周期")
    private Integer ignoreDuration;
	/**启用/禁用*/
	@Excel(name = "启用/禁用", width = 15)
    @Schema(description = "启用/禁用")
    private String isEnable;
	/**通知方式*/
	@Excel(name = "通知方式", width = 15)
    @Schema(description = "通知方式")
    private String notifyType;
    @Schema(description = "租户ID")
    private Integer tenantId;
    @TableField(exist = false)
    private String warehouseCodes;
    @Schema(description = "所属仓库")
    private String warehouseName;
}
