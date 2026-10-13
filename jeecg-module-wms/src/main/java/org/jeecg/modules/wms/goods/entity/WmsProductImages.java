package org.jeecg.modules.wms.goods.entity;

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
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 商品图片表
 * @Author: jeecg-boot
 * @Date:   2025-04-15
 * @Version: V1.0
 */
@Data
@TableName("wms_product_images")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="商品图片表")
public class WmsProductImages implements Serializable {
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
	/**原始图片地址*/
	@Excel(name = "原始图片地址", width = 15)
    @Schema(description = "原始图片地址")
    private String original;
	/**是否默认图片*/
	@Excel(name = "是否默认图片", width = 15)
    @Schema(description = "是否默认图片")
    private String isDefault;
	/**小图路径*/
	@Excel(name = "小图路径", width = 15)
    @Schema(description = "小图路径")
    private String small;
	/**缩略图路径*/
	@Excel(name = "缩略图路径", width = 15)
    @Schema(description = "缩略图路径")
    private String thumbnail;

}
