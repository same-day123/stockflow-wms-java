package org.jeecg.modules.wms.wave.entity;

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
 * @Description: 缺货登记表
 * @Author: jeecg-boot
 * @Date:   2025-08-09
 * @Version: V1.0
 */
@Data
@TableName("wms_shortage_registration")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="缺货登记表")
public class WmsShortageRegistration implements Serializable {
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
	/**波次ID*/
	@Excel(name = "波次ID", width = 15)
    @Schema(description = "波次ID")
    private String waveId;
	/**波次拣货明细ID*/
	@Excel(name = "波次拣货明细ID", width = 15)
    @Schema(description = "波次拣货明细ID")
    private String waveSkuSummaryId;
	/**关联任务ID*/
	@Excel(name = "关联任务ID", width = 15)
    @Schema(description = "关联任务ID")
    private String taskId;
	/**商品ID*/
	@Excel(name = "商品ID", width = 15)
    @Schema(description = "商品ID")
    private String productId;

	/**储位编码*/
	@Excel(name = "储位编码", width = 15)
    @Schema(description = "储位编码")
    private String sourceLocationCode;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNumber;
	/**缺货数量*/
	@Excel(name = "缺货数量", width = 15)
    @Schema(description = "缺货数量")
    private Integer shortageQuantity;
	/**状态(待处理/已补货/已取消)*/
	@Excel(name = "状态(待处理/已补货/已取消)", width = 15)
    @Schema(description = "状态(待处理/已补货/已取消)")
    private String status;
	/**处理方式(补货/换货/取消)*/
	@Excel(name = "处理方式(补货/换货/取消)", width = 15)
    @Schema(description = "处理方式(补货/换货/取消)")
    private String processMethod;
	/**处理人*/
	@Excel(name = "处理人", width = 15)
    @Schema(description = "处理人")
    private String processor;
	/**处理时间*/
	@Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private Date processTime;
	/**处理备注*/
	@Excel(name = "处理备注", width = 15)
    @Schema(description = "处理备注")
    private String processRemark;
}
