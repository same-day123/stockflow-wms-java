package org.jeecg.modules.wms.goods.service;

import org.jeecg.modules.wms.goods.entity.WmsProductsBatchnum;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商品批次表
 * @Author: jeecg-boot
 * @Date:   2025-06-02
 * @Version: V1.0
 */
public interface IWmsProductsBatchnumService extends IService<WmsProductsBatchnum> {

    /**
     * 根据货主id和批次号查询商品批次表，如果没有查询到则新增一条记录
     */
     WmsProductsBatchnum getOrCreate(String ownerId, String batchNumber);
}
