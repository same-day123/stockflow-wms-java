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
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2025-04-24
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="设备表")
public class IotDevice implements Serializable {
    protected static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    protected String id;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    protected String deviceName;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @Schema(description = "设备编码")
    protected String deviceCode;
	/**产品编号*/
	@Excel(name = "产品编号", width = 15)
    @Schema(description = "产品编号")
    protected String productCode;
    /** 所属产品*/
    @Excel(name = "所属产品", width = 15)
    @Schema(description = "所属产品")
    protected String productName;
    /**设备状态*/
    @Excel(name = "设备状态", width = 15)
    @Schema(description = "设备状态, -1：未激活(初始默认值)，0：离线，1：在线，2：禁用")
    protected java.lang.Integer deviceStatus;
	/**启用/禁用*/
	@Excel(name = "启用/禁用", width = 15)
    @Schema(description = "启用/禁用")
    protected String isEnable;
	/**设备备注*/
	@Excel(name = "设备备注", width = 15)
    @Schema(description = "设备备注")
    protected String remark;
	/**创建人*/
    @Schema(description = "创建人")
    protected String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    protected Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    protected String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    protected Date updateTime;
    /** ip地址*/
    @Schema(description = "IP地址")
    protected String ipAddr;
    /** 仓库ID*/
    @Schema(description = "仓库编码")
    protected String warehouseCode;
    /** 所属仓库*/
    @Schema(description = "所属仓库")
    protected String warehouseName;
    @TableField(exist = false)
    protected String deviceWithWarehouseName;
    /** 图片地址*/
    @Schema(description = "图片地址")
    @TableField(exist = false)
    protected String imageUrl;
    @Schema(description = "所属部门")
    private String sysOrgCode;
    /**
     * 查询使用的仓库编码列表
     */
    @TableField(exist = false)
    private String warehouseCodes;
    @Schema(description = "租户ID")
    protected Integer tenantId;
}
