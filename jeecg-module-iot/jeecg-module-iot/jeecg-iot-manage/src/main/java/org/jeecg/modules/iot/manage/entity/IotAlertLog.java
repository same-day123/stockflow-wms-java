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
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date:   2025-05-06
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_alert_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="报警日志表")
public class IotAlertLog implements Serializable {
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
	/**报警规则ID*/
	@Excel(name = "报警规则ID", width = 15)
    @Schema(description = "报警规则ID")
    private String ruleId;
	/**报警规则名称*/
	@Excel(name = "报警规则名称", width = 15)
    @Schema(description = "报警规则名称")
    private String ruleName;
	/**报警规则*/
	@Excel(name = "报警规则", width = 15)
    @Schema(description = "报警规则")
    private String ruleInfo;
	/**报警级别*/
	@Excel(name = "报警级别", width = 15)
    @Schema(description = "报警级别")
    private String alertLevel;
	/**报警设备编码*/
	@Excel(name = "报警设备编码", width = 15)
    @Schema(description = "报警设备编码")
    private String deviceCode;
	/**报警设备名称*/
	@Excel(name = "报警设备名称", width = 15)
    @Schema(description = "报警设备名称")
    private String deviceName;
    /** 物模型code*/
    @Schema(description = "指标code")
    private String modelCode;
	/**异常数据*/
	@Excel(name = "异常数据", width = 15)
    @Schema(description = "异常数据")
    private Double alertData;
	/**处理进度*/
	@Excel(name = "处理进度", width = 15)
    @Schema(description = "处理进度")
    private String processStatus;
	/**处理时间*/
	@Excel(name = "处理时间", width = 15)
    @Schema(description = "处理时间")
    private String processTime;
	/**处理方案*/
	@Excel(name = "处理方案", width = 15)
    @Schema(description = "处理方案")
    private String processPlan;
	/**报警时间*/
	@Excel(name = "报警时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "报警时间")
    private Date alertTime;
	/**通知信息*/
	@Excel(name = "通知信息", width = 15)
    @Schema(description = "通知信息")
    private String notifyInfo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
    /** 产品名称*/
    private String productName;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 仓库名称
     */
    private String warehouseName;
    @TableField(exist = false)
    private String unit;
    @Schema(description = "租户ID")
    private Integer tenantId;
    @TableField(exist = false)
    private String warehouseCodes;
}
