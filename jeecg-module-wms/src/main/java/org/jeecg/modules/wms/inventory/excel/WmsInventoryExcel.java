package org.jeecg.modules.wms.inventory.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 导入库存表格式
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Data
public class WmsInventoryExcel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 仓库名称
     */
    @ExcelProperty("仓库名称")
    private String warehouseName;
    /**
     * 货主编码
     */
    @ExcelProperty("货主编码")
    private String ownerCode;
    /**货主*/
    @ExcelProperty("货主")
    private String ownerId;
    /**
     * 商品编码
     */
    @ExcelProperty("商品编码")
    private String productCode;
    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;
    /**储位编码*/
    @ExcelProperty("储位编码")
    private String locationCode;
    /**批号 */
    @ExcelProperty("批号")
    private String batchNumber;
    /**保质期到期日*/
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty("保质期到期日")
    private Date expiryDate;
    /**在库数量*/
    @ExcelProperty("数量")
    private Integer stockQuantity;
    @ExcelIgnore
    private Integer rowNum;
}
