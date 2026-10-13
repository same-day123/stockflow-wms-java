package org.jeecg.modules.iot.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date: 2025-05-06
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_alert_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class IotAlertLog implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 所属部门
     */
    private String sysOrgCode;
    /**
     * 报警规则ID
     */
    private String ruleId;
    /**
     * 报警规则名称
     */
    private String ruleName;
    /**
     * 报警规则
     */
    private String ruleInfo;
    /**
     * 报警级别
     */
    private String alertLevel;
    /**
     * 报警设备编码
     */
    private String deviceCode;
    /**
     * 报警设备名称
     */
    private String deviceName;
    /**
     * 异常数据
     */
    private Double alertData;
    /**
     * 处理进度
     */
    private String processStatus;
    /**
     * 处理时间
     */
    private String processTime;
    /**
     * 处理方案
     */
    private String processPlan;
    /**
     * 报警时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alertTime;
    /**
     * 通知信息
     */
    private String notifyInfo;
    /**
     * 备注
     */
    private String remark;
    /**
     * 指标
     */
    private String modelCode;
    /**
     * 运算符
     */
    @TableField(exist = false)
    private String operator;
    /**
     * 阈值
     */
    @TableField(exist = false)
    private Double threshold;
    /**
     * 周期
     */
    @TableField(exist = false)
    private Integer cycle;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 租户ID
     */
    private Integer tenantId;
}
