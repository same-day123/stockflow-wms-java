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
 * @Description: 波次拣货明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-05
 * @Version: V1.0
 */
@Data
@TableName("wms_wave_sku_summary")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="波次拣货明细表")
public class WmsWaveSkuSummary implements Serializable {
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
	/**波次id*/
	@Excel(name = "波次id", width = 15)
    @Schema(description = "波次id")
    private String waveId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String skuId;
	/**货主id*/
	@Excel(name = "货主id", width = 15)
    @Schema(description = "货主id")
    private String ownerId;
	/**商品条码*/
	@Excel(name = "商品条码", width = 15)
    @Schema(description = "商品条码")
    private String productBarcode;
	/**储位编码*/
	@Excel(name = "储位编码", width = 15)
    @Schema(description = "储位编码")
    private String locationCode;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNumber;
	/**容器编码*/
	@Excel(name = "容器编码", width = 15)
    @Schema(description = "容器编码")
    private String containerCode;
	/**分配数量*/
	@Excel(name = "分配数量", width = 15)
    @Schema(description = "分配数量")
    private Integer allocatedQuantity;
	/**拣货数量*/
	@Excel(name = "拣货数量", width = 15)
    @Schema(description = "拣货数量")
    private Integer pickedQuantity;

	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;



}
