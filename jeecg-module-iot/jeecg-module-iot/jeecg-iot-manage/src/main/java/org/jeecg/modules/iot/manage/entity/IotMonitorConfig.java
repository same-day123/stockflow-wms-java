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
 * @Description: 监控配置表
 * @Author: jeecg-boot
 * @Date:   2025-05-01
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_monitor_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="监控配置表")
public class IotMonitorConfig implements Serializable {
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
	/**产品code*/
	@Excel(name = "产品code", width = 15)
    @Schema(description = "产品code")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @Schema(description = "设备编码")
    private String deviceCode;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    private String deviceName;
	/**物模型编码*/
	@Excel(name = "物模型编码", width = 15)
    @Schema(description = "物模型编码")
    private String modelCode;
	/**物模型名称*/
	@Excel(name = "物模型名称", width = 15)
    @Schema(description = "物模型名称")
    private String modelName;
    @Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "租户ID")
    private Integer tenantId;
    @TableField(exist = false)
    private String warehouseCodes;
    @Schema(description = "设备+仓库名")
    private String deviceWithWarehouseName;
}
