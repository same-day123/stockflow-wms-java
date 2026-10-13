package org.jeecg.modules.wms.goods.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
 * @Description: 商品信息表
 * @Author: jeecg-boot
 * @Date:   2025-04-14
 * @Version: V1.0
 */
@Data
@TableName("wms_products")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="商品信息表")
public class WmsProducts implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    @ExcelIgnore
    private String id;
    /**创建人*/
    @Schema(description = "创建人")
    @ExcelIgnore
    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    @ExcelIgnore
    private Date createTime;
    /**更新人*/
    @Schema(description = "更新人")
    @ExcelIgnore
    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    @ExcelIgnore
    private Date updateTime;
    /**所属部门*/
    @Schema(description = "所属部门")
    @ExcelIgnore
    private String sysOrgCode;
    /**商品名称*/
    @Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    @ExcelProperty("商品名称")
    private String productName;
    /**货主id*/
    @Excel(name = "货主id", width = 15)
    @Schema(description = "货主id")
    @ExcelIgnore
    private String ownerId;
    /**商品编码*/
    @Excel(name = "sku编码", width = 15)
    @Schema(description = "sku编码")
    @ExcelProperty("sku编码")
    private String productCode;
    /**商品条码*/
    @Excel(name = "商品条码", width = 15)
    @Schema(description = "商品条码")
    @ExcelProperty("商品条码")
    private String productBarcode;
    /**商品规格*/
    @Excel(name = "商品规格", width = 15)
    @Schema(description = "商品规格")
    @ExcelProperty("商品规格")
    private String productSpec;
    /**商品品牌*/
    @Excel(name = "商品品牌", width = 15)
    @Schema(description = "商品品牌")
    @ExcelIgnore
    private String productBrand;
    /**商品批次*/
    @Excel(name = "商品批次", width = 15)
    @Schema(description = "商品批次")
    @ExcelProperty("商品批次")
    private String productBatch;
    /**供应商条码*/
    @Excel(name = "供应商条码", width = 15)
    @Schema(description = "供应商条码")
    @ExcelProperty("供应商条码")
    private String supplierBarcode;
    /**宽*/
    @Excel(name = "宽", width = 15)
    @Schema(description = "宽")
    @ExcelProperty("宽")
    private Double width;
    /**长*/
    @Excel(name = "长", width = 15)
    @Schema(description = "长")
    @ExcelProperty("长")
    private Double length;
    /**高*/
    @Excel(name = "高", width = 15)
    @Schema(description = "高")
    @ExcelProperty("高")
    private Double height;
    /**体积*/
    @Excel(name = "体积", width = 15)
    @Schema(description = "体积")
    @ExcelProperty("体积")
    private Double volume;
    /**毛重*/
    @Excel(name = "毛重", width = 15)
    @Schema(description = "毛重")
    @ExcelProperty("毛重")
    private Double grossWeight;
    /**净重*/
    @Excel(name = "净重", width = 15)
    @Schema(description = "净重")
    @ExcelProperty("净重")
    private Double netWeight;
    /**商品一级分类id*/
    @Excel(name = "商品一级分类id", width = 15)
    @Schema(description = "商品一级分类id")
    @ExcelIgnore
    private String categoryId;
    /**包装规格*/
    @Excel(name = "包装规格", width = 15)
    @Schema(description = "包装规格")
    @ExcelProperty("包装规格")
    private String packagingSpec;
    /**养护周期(天)*/
    @Excel(name = "养护周期(天)", width = 15)
    @Schema(description = "养护周期(天)")
    @ExcelProperty("养护周期(天)")
    private Integer maintenanceCycle;
    /**保质期(天)*/
    @Excel(name = "保质期(天)", width = 15)
    @Schema(description = "保质期(天)")
    @ExcelProperty("保质期(天)")
    private Integer shelfLife;
    /**计量单位*/
    @Excel(name = "计量单位", width = 15)
    @Schema(description = "计量单位")
    @ExcelProperty("计量单位")
    private String unit;
    /**是否保质期管控*/
    @Excel(name = "是否保质期管控", width = 15)
    @Schema(description = "是否保质期管控")
    @ExcelProperty("是否保质期管控")
    private Integer isExpiryControlled;
    /**状态*/
    @Excel(name = "状态", width = 15, dicCode = "dict_item_status")
    @Dict(dicCode = "wms_status")
    @Schema(description = "状态")
    @ExcelProperty("状态")
    private String status;

    //货主名称
    @TableField(exist = false)
    @ExcelProperty("货主名称")
    private String ownerName;

    //分类名称
    @TableField(exist = false)
    @ExcelProperty("商品分类")
    private String categoryName;

    //商品图片
    @TableField(exist = false)
    @ExcelIgnore
    private String productImgs;

    //商品品牌名称
    @TableField(exist = false)
    @ExcelProperty("商品品牌")
    private String productBrandName;
}
