package org.jeecg.modules.wms.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryTransMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryTransService;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Mr.M
 * @version 1.0
 * @description  分配库存操作库存表，锁定库存
 * @date 2025/7/19 16:23
 */
@Service
public class WmsInventoryTransByAllocation extends ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans> implements IWmsInventoryTransService {

    @Autowired
    private IWmsInventoryService wmsInventoryService;


    //注入商品service
    @Autowired
    private IWmsProductsService wmsProductsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(WmsInventoryTransParam inventoryTransParam) {
        //非空判断，商品id、原始储位编码、目标储位编码、数量不能为空
        if (StringUtils.isEmpty(inventoryTransParam.getProductId())
                || StringUtils.isEmpty(inventoryTransParam.getSourceLocationCode())
                || inventoryTransParam.getExecQuantity() == null) {
            //抛出异常
            throw new RuntimeException("请填写商品id、原始储位编码、数量");
        }

        //查询商品信息
        WmsProducts products = wmsProductsService.getById(inventoryTransParam.getProductId());
        if (products == null) {
            //抛出异常
            throw new RuntimeException("商品id在商品表中不存在");
        }
        //===================源储位扣减库存=====================
        /**
         * 在库数量：不动
         * 可用数量：减少
         * 分配数量：增加
         */
        //根据源储位编码、商品id、容器编码查询库存表
        WmsInventory wmsInventorySource = wmsInventoryService.getInventoryByUniqueKey(inventoryTransParam.getProductId(), inventoryTransParam.getSourceLocationCode(), inventoryTransParam.getBatchNumber());
        //如果wmsInventorySource为空则抛出异常
        if (wmsInventorySource == null) {
            //抛出异常
            throw new RuntimeException("源储位编码或商品id在库存中不存在");
        }
        LambdaUpdateWrapper<WmsInventory> wmsInventoryLambdaUpdateWrapper = new LambdaUpdateWrapper<WmsInventory>()
                .eq(WmsInventory::getId, wmsInventorySource.getId())
                .setSql("available_quantity=available_quantity-{0}", inventoryTransParam.getExecQuantity())
                .setSql("allocated_quantity=allocated_quantity+{0}", inventoryTransParam.getExecQuantity())
                .ge(WmsInventory::getAvailableQuantity,inventoryTransParam.getExecQuantity());
        boolean update = wmsInventoryService.update(null, wmsInventoryLambdaUpdateWrapper);
        if (!update) {
            //抛出异常
            throw new RuntimeException("源储位扣减库存失败");
        }

    }
}
