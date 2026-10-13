package org.jeecg.modules.wms.inventory.entity;

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
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Data
@TableName("wms_inventory")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="库存表")
public class WmsInventory implements Serializable {
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
	/**商品id*/
	@Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String productId;
	/**储位编码*/
	@Excel(name = "储位编码", width = 15)
    @Schema(description = "储位编码")
    private String locationCode;
	/**容器编码*/
	@Excel(name = "容器编码", width = 15)
    @Schema(description = "容器编码")
    private String containerCode;
	/**在库数量*/
	@Excel(name = "在库数量", width = 15)
    @Schema(description = "在库数量")
    private Integer stockQuantity;
	/**分配数量*/
	@Excel(name = "分配数量", width = 15)
    @Schema(description = "分配数量")
    private Integer allocatedQuantity;
	/**可用数量*/
	@Excel(name = "可用数量", width = 15)
    @Schema(description = "可用数量")
    private Integer availableQuantity;
	/**批号 */
	@Excel(name = "批号 ", width = 15)
    @Schema(description = "批号 ")
    private String batchNumber;
	/**入库时间*/
	@Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "入库时间")
    private Date stockInTime;
	/**保质期到期日*/
	@Excel(name = "保质期到期日", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "保质期到期日")
    private Date expiryDate;
	/**货主*/
	@Excel(name = "货主", width = 15)
    @Schema(description = "货主")
    private String ownerId;
	/**是否可售*/
	@Excel(name = "是否可售", width = 15)
    @Schema(description = "是否可售")
    private String isSellable;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private String warehouseId;


    /**
     * 货主名称
     */
    @Excel(name = "货主名称", width = 15)
    @Schema(description = "货主名称")
    @TableField(exist = false)
    private String ownerName;
    /**
     * 货主编码
     */
    @Excel(name = "货主编码", width = 15)
    @Schema(description = "货主编码")
    @TableField(exist = false)
    private String ownerCode;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    @TableField(exist = false)
    private String productName;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码", width = 15)
    @Schema(description = "商品编码")
    @TableField(exist = false)
    private String productCode;

    /**
     * 储位类别
     */
    @Excel(name = "储位类别", width = 15)
    @Schema(description = "储位类别")
    @TableField(exist = false)
    private String locationCategory;

    /**
     * 储区名称
     */
    @Excel(name = "储区名称", width = 15)
    @Schema(description = "储区名称")
    @TableField(exist = false)
    private String zoneName;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称", width = 15)
    @Schema(description = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    @Excel(name = "仓库编码", width = 15)
    @Schema(description = "仓库编码")
    @TableField(exist = false)
    private String warehouseCode;

    /**储位类型*/
    @Excel(name = "储位类型", width = 15)
    @Schema(description = "储位类型")
    @TableField(exist = false)
    private String locationType;
    /**储区编码*/
    @Excel(name = "储区编码", width = 15)
    @Schema(description = "储区编码")
    @TableField(exist = false)
    private String zoneCode;
    /**储区类型*/
    @Excel(name = "储区类型", width = 15)
    @Schema(description = "储区类型")
    @TableField(exist = false)
    private String zoneType;
}
