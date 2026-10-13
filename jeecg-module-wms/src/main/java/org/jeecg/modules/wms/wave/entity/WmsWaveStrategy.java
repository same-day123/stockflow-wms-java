package org.jeecg.modules.wms.wave.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 波次策略表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Data
@TableName("wms_wave_strategy")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="波次策略表")
public class WmsWaveStrategy implements Serializable {
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
	/**策略名称*/
	@Excel(name = "策略名称", width = 15)
    @Schema(description = "策略名称")
    private String strategyName;
	/**策略编码*/
	@Excel(name = "策略编码", width = 15)
    @Schema(description = "策略编码")
    private String strategyCode;
	/**优先级*/
	@Excel(name = "优先级", width = 15)
    @Schema(description = "优先级")
    private Integer priority;
	/**最小订单数*/
	@Excel(name = "最小订单数", width = 15)
    @Schema(description = "最小订单数")
    private Integer minOrderCount;
	/**最大订单数*/
	@Excel(name = "最大订单数", width = 15)
    @Schema(description = "最大订单数")
    private Integer maxOrderCount;
	/**最小sku品项数*/
	@Excel(name = "最小sku品项数", width = 15)
    @Schema(description = "最小sku品项数")
    private Integer minSkuItems;
	/**最大sku品项数*/
	@Excel(name = "最大sku品项数", width = 15)
    @Schema(description = "最大sku品项数")
    private Integer maxSkuItems;
	/**最小sku件数*/
	@Excel(name = "最小sku件数", width = 15)
    @Schema(description = "最小sku件数")
    private Integer minSkuQuantity;
	/**最大sku件数*/
	@Excel(name = "最大sku件数", width = 15)
    @Schema(description = "最大sku件数")
    private Integer maxSkuQuantity;
	/**是否自动执行*/
	@Excel(name = "是否自动执行", width = 15)
    @Schema(description = "是否自动执行")
    private String isAutoExecute;
	/**是否按货主分组*/
	@Excel(name = "是否按货主分组", width = 15)
    @Schema(description = "是否按货主分组")
    private String groupByOwner;
	/**是否按承运商分组*/
	@Excel(name = "是否按承运商分组", width = 15)
    @Schema(description = "是否按承运商分组")
    private String groupByCarrier;
	/**储区编码*/
	@Excel(name = "储区编码", width = 15)
    @Schema(description = "储区编码")
    private String storageAreaCode;
	/**储区名称*/
	@Excel(name = "储区名称", width = 15)
    @Schema(description = "储区名称")
    private String storageAreaName;
	/**波次内最大储区数*/
	@Excel(name = "波次内最大储区数", width = 15)
    @Schema(description = "波次内最大储区数")
    private Integer maxStorageAreas;
	/**单订单最小sku数*/
	@Excel(name = "单订单最小sku数", width = 15)
    @Schema(description = "单订单最小sku数")
    private Integer minSkuPerOrder;
	/**单订单最大sku数*/
	@Excel(name = "单订单最大sku数", width = 15)
    @Schema(description = "单订单最大sku数")
    private Integer maxSkuPerOrder;
	/**是否启用*/
	@Excel(name = "是否启用", width = 15)
    @Schema(description = "是否启用")
    private String isEnabled;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;


}
