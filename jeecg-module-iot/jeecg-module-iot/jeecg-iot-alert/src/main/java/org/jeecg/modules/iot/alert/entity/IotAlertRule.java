package org.jeecg.modules.iot.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
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
public class IotAlertRule implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    private String id;
	/**创建人*/
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
	/**更新人*/
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
	/**所属部门*/
    private String sysOrgCode;
	/**告警规则名称*/
    private String alertName;
	/**产品编码*/
    private String productCode;
	/**所属产品*/
    private String productName;
	/**设备编码*/
    private String deviceCode;
	/**关联设备*/
    private String deviceName;
	/**指标编码*/
    private String modelCode;
	/**监控指标*/
    private String modelName;
	/**报警级别*/
    private String alertLevel;
	/**运算符*/
    private String operator;
	/**阈值*/
    private Double threshold;
	/**连续周期*/
    private Integer cycle;
	/**沉默周期*/
    private Integer ignoreDuration;
	/**启用/禁用*/
    private String isEnable;
	/**通知方式*/
    private String notifyType;
    /**
     * 租户ID
     */
    private Integer tenantId;
}
