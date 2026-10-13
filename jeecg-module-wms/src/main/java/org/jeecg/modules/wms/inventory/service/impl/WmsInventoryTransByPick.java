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
 * @description  拣货执行库存变更
 * @date 2025/7/19 16:23
 */
@Service
public class WmsInventoryTransByPick extends ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans> implements IWmsInventoryTransService {

    @Autowired
    private IWmsInventoryService wmsInventoryService;


    //注入储位表service
    @Autowired
    private IWmsStorageLocationsService wmsStorageLocationsService;

    //注入储区service
    @Autowired
    private IWmsStorageZonesService wmsStorageZonesService;

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
            throw new RuntimeException("请填写商品id、原始储位编码、目标储位编码、数量");
        }

        //查询商品信息
        WmsProducts products = wmsProductsService.getById(inventoryTransParam.getProductId());
        if (products == null) {
            //抛出异常
            throw new RuntimeException("商品id在商品表中不存在");
        }
        //===================源储位扣减库存=====================
        /**
         * ===拣货
         * 原储位：
         * 在库数量：减少
         * 可用数量：不动
         * 分配数量：减少
         *
         *
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
                .setSql("stock_quantity=stock_quantity-{0}", inventoryTransParam.getExecQuantity())
                .setSql("allocated_quantity=allocated_quantity-{0}", inventoryTransParam.getExecQuantity())
                .ge(WmsInventory::getStockQuantity,inventoryTransParam.getExecQuantity())
                .ge(WmsInventory::getAllocatedQuantity,inventoryTransParam.getExecQuantity());
        boolean update = wmsInventoryService.update(null, wmsInventoryLambdaUpdateWrapper);
        if (!update) {
            //抛出异常
            throw new RuntimeException("更新库存失败");
        }
        //插入库存变更表
        WmsInventoryTrans wmsInventoryTrans = new WmsInventoryTrans();
        wmsInventoryTrans.setProductId(inventoryTransParam.getProductId());
        wmsInventoryTrans.setLocationCode(inventoryTransParam.getSourceLocationCode());
        wmsInventoryTrans.setBatchNumber(inventoryTransParam.getBatchNumber());
        wmsInventoryTrans.setChangeQuantity(inventoryTransParam.getExecQuantity()*-1);
        wmsInventoryTrans.setTransactionType(inventoryTransParam.getTransactionType());
        wmsInventoryTrans.setRemarks(inventoryTransParam.getRemarks());
        wmsInventoryTrans.setTransactionTime(new Date());
        save(wmsInventoryTrans);
        //===================目标储位增加库存=====================
        /**
         * 新储位(拣货储位)：
         * 在库数量：增加
         * 可用数量：不动
         * 分配数量：增加
         */

        //根据唯一key获取库存
        WmsInventory wmsInventoryDb = wmsInventoryService.getInventoryByUniqueKey(inventoryTransParam.getProductId(), inventoryTransParam.getTargetLocationCode(), null);
        //判断库存是否为空
        if (wmsInventoryDb == null) {
            //根据储位编码查询储位 信息
            LambdaQueryWrapper<WmsStorageLocations> eq = new LambdaQueryWrapper<WmsStorageLocations>().eq(WmsStorageLocations::getLocationCode, inventoryTransParam.getTargetLocationCode());
            WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.getBaseMapper().selectOne(eq);
            if (wmsStorageLocations == null) {
                //抛出异常
                throw new RuntimeException("储位编码在储位表中不存在");
            }
            //是否可售库存
            String isSellable = wmsStorageLocations.getIsSellable();
            //创建库存对象
            wmsInventoryDb = new WmsInventory();
            BeanUtils.copyProperties(inventoryTransParam,wmsInventoryDb);
            wmsInventoryDb.setBatchNumber(null);
            wmsInventoryDb.setLocationCode(inventoryTransParam.getTargetLocationCode());
            wmsInventoryDb.setOwnerId(products.getOwnerId());
            wmsInventoryDb.setStockQuantity(inventoryTransParam.getExecQuantity());
            wmsInventoryDb.setAvailableQuantity(0);
            wmsInventoryDb.setAllocatedQuantity(inventoryTransParam.getExecQuantity());
            //是否可售
            wmsInventoryDb.setIsSellable(isSellable);
            //入库时间
            wmsInventoryDb.setStockInTime(inventoryTransParam.getOperationTime());
            wmsInventoryService.save(wmsInventoryDb);
        }else{
            LambdaUpdateWrapper<WmsInventory> wmsInventoryLambdaUpdateWrapper2 = new LambdaUpdateWrapper<WmsInventory>()
                    .eq(WmsInventory::getId, wmsInventoryDb.getId())
                    .setSql("stock_quantity=stock_quantity+{0}", inventoryTransParam.getExecQuantity())
                    .setSql("allocated_quantity=allocated_quantity+{0}", inventoryTransParam.getExecQuantity());
            wmsInventoryService.update(null,wmsInventoryLambdaUpdateWrapper2);

        }
        //插入库存变更表
        WmsInventoryTrans wmsInventoryTrans2 = new WmsInventoryTrans();
        wmsInventoryTrans2.setProductId(inventoryTransParam.getProductId());
        wmsInventoryTrans2.setLocationCode(inventoryTransParam.getTargetLocationCode());
        wmsInventoryTrans2.setBatchNumber(inventoryTransParam.getBatchNumber());
        wmsInventoryTrans2.setChangeQuantity(inventoryTransParam.getExecQuantity());
        wmsInventoryTrans2.setTransactionType(inventoryTransParam.getTransactionType());
        wmsInventoryTrans2.setRemarks(inventoryTransParam.getRemarks());
        wmsInventoryTrans2.setTransactionTime(new Date());
        save(wmsInventoryTrans2);
    }
}
