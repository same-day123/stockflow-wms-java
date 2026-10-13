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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 设备产品表
 * @Author: jeecg-boot
 * @Date:   2025-04-24
 * @Version: V1.0
 */
@Data
@TableName("wms_iot_product")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="设备产品表")
public class IotProduct implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**节点类型*/
	@Excel(name = "节点类型", width = 15, dicCode = "product_node_type_dict")
	@Dict(dicCode = "product_node_type_dict")
    @Schema(description = "节点类型")
    private String nodeType;
	/**联网方式*/
	@Excel(name = "联网方式", width = 15, dicCode = "product_network_mode_dict")
	@Dict(dicCode = "product_network_mode_dict")
    @Schema(description = "联网方式")
    private String networkMode;
	/**认证方式*/
	@Excel(name = "认证方式", width = 15)
    @Schema(description = "认证方式")
    private String authType;
	/**产品描述*/
	@Excel(name = "产品描述", width = 15)
    @Schema(description = "产品描述")
    private String productDesc;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    private String imageUrl;
    /**
     * 关联设备数量
     */
    @TableField(exist = false)
    @Schema(description = "关联设备数量")
    private Integer num;
    @Schema(description = "所属部门")
    private String sysOrgCode;
    @Schema(description = "租户ID")
    private Integer tenantId;
}
