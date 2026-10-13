package org.jeecg.modules.wms.goods.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 商品导入模型类
 * @Author: Mr.M
 * @Date:   2025-08-05
 * @Version: V1.0
 */
@Data
public class WmsProductsImport implements Serializable {
    private static final long serialVersionUID = 1L;
   /**商品名称*/
    @ExcelProperty("商品名称")
    private String productName;
   /**商品编码*/
   @ExcelProperty("sku编码")
   private String productCode;
   /**商品条码*/
    @ExcelProperty("商品条码")
    private String productBarcode;
    @ExcelProperty("商品规格")
    private String productSpec;
    @ExcelProperty("商品批次")
    private String productBatch;
    @ExcelProperty("供应商条码")
    private String supplierBarcode;
   /**宽*/
    @ExcelProperty("宽")
    private Double width;
   /**长*/
    @ExcelProperty("长")
    private Double length;
   /**高*/
    @ExcelProperty("高")
    private Double height;
   /**体积*/
    @ExcelProperty("体积")
    private Double volume;
   /**毛重*/
    @ExcelProperty("毛重")
    private Double grossWeight;
   /**净重*/
    @ExcelProperty("净重")
    private Double netWeight;

   /**包装规格*/
    @ExcelProperty("包装规格")
    private String packagingSpec;
   /**养护周期(天)*/
    @ExcelProperty("养护周期(天)")
    private Integer maintenanceCycle;
   /**保质期(天)*/
    @ExcelProperty("保质期(天)")
    private Integer shelfLife;
   /**计量单位*/
    @ExcelProperty("计量单位")
    private String unit;
   /**是否保质期管控*/
    @ExcelProperty("是否保质期管控")
    private Integer isExpiryControlled;
   /**状态*/
    @ExcelProperty("status")
    private String status;
    //货主名称
    @ExcelProperty("货主名称")
    private String ownerName;

    //分类名称
    @ExcelProperty("商品分类")
    private String categoryName;

    @ExcelProperty("商品品牌")
    private String productBrandName;
}
