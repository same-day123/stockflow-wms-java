package org.jeecg.modules.wms.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.wms.goods.entity.WmsProductsBatchnum;
import org.jeecg.modules.wms.goods.mapper.WmsProductsBatchnumMapper;
import org.jeecg.modules.wms.goods.service.IWmsProductsBatchnumService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商品批次表
 * @Author: jeecg-boot
 * @Date:   2025-06-02
 * @Version: V1.0
 */
@Service
public class WmsProductsBatchnumServiceImpl extends ServiceImpl<WmsProductsBatchnumMapper, WmsProductsBatchnum> implements IWmsProductsBatchnumService {

    @Override
    public WmsProductsBatchnum getOrCreate(String ownerId, String batchNumber) {
        //根据货主id和批次号查询商品批次表，如果没有查询到则新增一条记录
         WmsProductsBatchnum wmsProductsBatchnum = this.getOne(new LambdaQueryWrapper<WmsProductsBatchnum>()
                 .eq(WmsProductsBatchnum::getOwnerId, ownerId)
                 .eq(WmsProductsBatchnum::getBatchNumber, batchNumber));
         if(wmsProductsBatchnum==null){
             wmsProductsBatchnum = new WmsProductsBatchnum();
             wmsProductsBatchnum.setOwnerId(ownerId);
             wmsProductsBatchnum.setBatchNumber(batchNumber);
             this.save(wmsProductsBatchnum);
         }
          return wmsProductsBatchnum;
    }
}
