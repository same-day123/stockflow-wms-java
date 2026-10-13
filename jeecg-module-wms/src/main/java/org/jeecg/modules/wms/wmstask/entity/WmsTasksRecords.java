package org.jeecg.modules.wms.wmstask.entity;

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
 * @Description: 任务执行记录表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Data
@TableName("wms_tasks_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="任务执行记录表")
public class WmsTasksRecords implements Serializable {
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
	/**任务号*/
	@Excel(name = "任务号", width = 15)
    @Schema(description = "任务号")
    private String taskNumber;
	/**任务类型*/
	@Excel(name = "任务类型", width = 15)
    @Schema(description = "任务类型")
    private String taskType;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String productId;
	/**执行数量*/
	@Excel(name = "执行数量", width = 15)
    @Schema(description = "执行数量")
    private Integer execQuantity;
	/**来源储位编码*/
	@Excel(name = "来源储位编码", width = 15)
    @Schema(description = "来源储位编码")
    private String sourceLocationCode;
	/**目的储位编码*/
	@Excel(name = "目的储位编码", width = 15)
    @Schema(description = "目的储位编码")
    private String targetLocationCode;
	/**来源容器编码*/
	@Excel(name = "来源容器编码", width = 15)
    @Schema(description = "来源容器编码")
    private String sourceContainerCode;
	/**目的容器编码*/
	@Excel(name = "目的容器编码", width = 15)
    @Schema(description = "目的容器编码")
    private String targetContainerCode;
	/**执行人*/
	@Excel(name = "执行人", width = 15)
    @Schema(description = "执行人")
    private String operator;
	/**执行时间*/
	@Excel(name = "执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "执行时间")
    private Date operationTime;
	/**入库单id*/
	@Excel(name = "入库单id", width = 15)
    @Schema(description = "入库单id")
    private String stockInOrderId;
	/**入库明细id*/
	@Excel(name = "入库明细id", width = 15)
    @Schema(description = "入库明细id")
    private String stockInOrderItemId;
	/**波次单id*/
	@Excel(name = "波次单id", width = 15)
    @Schema(description = "波次单id")
    private String waveOrderId;
	/**库存属性*/
	@Excel(name = "库存属性", width = 15)
    @Schema(description = "库存属性")
    private String inventoryAttribute;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @Schema(description = "任务id")
    private String taskId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNumber;
	/**保质期*/
	@Excel(name = "保质期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "保质期")
    private Date expiryDate;
	/**出库单id*/
	@Excel(name = "出库单id", width = 15)
    @Schema(description = "出库单id")
    private String outOrderId;
	/**波次拣货明细id*/
	@Excel(name = "波次拣货明细id", width = 15)
    @Schema(description = "波次拣货明细id")
    private String waveSkuSummaryId;
	/**来源仓库*/
	@Excel(name = "来源仓库", width = 15)
    @Schema(description = "来源仓库")
    private String sourceWarehouseId;
	/**目的仓库*/
	@Excel(name = "目的仓库", width = 15)
    @Schema(description = "目的仓库")
    private String targetWarehouseId;


    //入库单号
    @TableField(exist = false)
    private java.lang.String stockInOrderNumber;

    //商品名称
    @TableField(exist = false)
    private java.lang.String productName;

    //货主
    @TableField(exist = false)
    private java.lang.String ownerName;
    //仓库名称
    @TableField(exist = false)
    private java.lang.String warehouseName;
}
