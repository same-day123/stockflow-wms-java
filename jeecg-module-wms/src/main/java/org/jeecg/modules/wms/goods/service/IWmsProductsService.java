package org.jeecg.modules.wms.goods.service;

import org.jeecg.modules.wms.goods.entity.WmsProducts;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.goods.excel.WmsProductsImport;

import java.util.List;

/**
 * @Description: 商品信息表
 * @Author: jeecg-boot
 * @Date:   2025-04-14
 * @Version: V1.0
 */
public interface IWmsProductsService extends IService<WmsProducts> {

    /**
     * 添加商品
     */
    void add(WmsProducts wmsProducts);

    /**
     * 修改商品
     */
    void edit(WmsProducts wmsProducts);
    /**
     * 导入商品
     * @param wmsProductsImportList
     */
    void importProduct(List<WmsProductsImport> wmsProductsImportList);
}
