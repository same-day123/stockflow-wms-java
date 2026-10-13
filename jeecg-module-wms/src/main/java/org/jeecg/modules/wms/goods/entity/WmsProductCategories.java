package org.jeecg.modules.wms.goods.entity;

import java.io.Serializable;
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
import java.io.UnsupportedEncodingException;

/**
 * @Description: 商品类别
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
@Data
@TableName("wms_product_categories")
@Schema(description="商品类别")
public class WmsProductCategories implements Serializable {
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
	/**类别名称*/
	@Excel(name = "类别名称", width = 15)
    @Schema(description = "类别名称")
    private String categoryName;
	@Excel(name = "类别编码", width = 15)
    @Schema(description = "类别编码")
    private String categoryCode;
	/**父节点*/
	@Excel(name = "父节点", width = 15)
    @Schema(description = "父节点")
    private String parentId;
	/**状态 0-未启用 1-启用*/
	@Excel(name = "状态 0-未启用 1-启用", width = 15, dicCode = "dict_item_status")
	@Dict(dicCode = "dict_item_status")
    @Schema(description = "状态 0-未启用 1-启用")
    private String status;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否有子节点")
    private String hasChild;
}
