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
 * @Description: 收货时进行库存变更
 * @Author: jeecg-boot
 * @Date: 2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsInventoryTransByReceiving extends ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans> implements IWmsInventoryTransService {

    @Autowired
    private IWmsProductsService wmsProductsService;

    @Autowired
    private IWmsStorageLocationsService wmsStorageLocationsService;

    @Autowired
    private IWmsInventoryService wmsInventoryService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transfer(WmsInventoryTransParam inventoryTransParam) {
        //非空判断，目标储位、商品id、执行数量,是否可售
        if (StringUtils.isEmpty(inventoryTransParam.getTargetLocationCode())
                || StringUtils.isEmpty(inventoryTransParam.getProductId())
                ||  inventoryTransParam.getExecQuantity() == null
                || StringUtils.isEmpty(inventoryTransParam.getIsSellable())) {
            throw new RuntimeException("目标储位、商品id、执行数量、是否可售不能为空");
        }

        //查询商品信息
        WmsProducts products = wmsProductsService.getById(inventoryTransParam.getProductId());
        if (products == null) {
            //抛出异常
            throw new RuntimeException("商品id在商品表中不存在");
        }
        //根据储位编码查询储位 信息
        LambdaQueryWrapper<WmsStorageLocations> eq = new LambdaQueryWrapper<WmsStorageLocations>().eq(WmsStorageLocations::getLocationCode, inventoryTransParam.getTargetLocationCode());
        WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.getBaseMapper().selectOne(eq);
        if (wmsStorageLocations == null) {
            //抛出异常
            throw new RuntimeException("储位编码在储位表中不存在");
        }
        //是否可售库存
        String isSellable = wmsStorageLocations.getIsSellable();
        //是否可售库存
        if(!inventoryTransParam.getIsSellable().equals(isSellable)){
            throw new RuntimeException("良品放在可售储位，不良品放在不可售储位");
        }
        //根据唯一key获取库存
        WmsInventory wmsInventoryDb = wmsInventoryService.getInventoryByUniqueKey(inventoryTransParam.getProductId(), inventoryTransParam.getTargetLocationCode(), inventoryTransParam.getBatchNumber());
        //判断库存是否为空
        if (wmsInventoryDb == null) {

            //创建库存对象
            wmsInventoryDb = new WmsInventory();
            wmsInventoryDb.setOwnerId(products.getOwnerId());
            BeanUtils.copyProperties(inventoryTransParam,wmsInventoryDb);
            wmsInventoryDb.setStockQuantity(inventoryTransParam.getExecQuantity());
            wmsInventoryDb.setLocationCode(inventoryTransParam.getTargetLocationCode());
            //如果储位是可售库存，则设置可用库存为数量
            wmsInventoryDb.setAvailableQuantity(isSellable.equals("1")?inventoryTransParam.getExecQuantity():0);
            wmsInventoryDb.setAllocatedQuantity(0);
            //是否可售
            wmsInventoryDb.setIsSellable(isSellable);
            //入库时间
            wmsInventoryDb.setStockInTime(inventoryTransParam.getOperationTime());
            wmsInventoryService.save(wmsInventoryDb);
        }else{
            LambdaUpdateWrapper<WmsInventory> wmsInventoryLambdaUpdateWrapper = new LambdaUpdateWrapper<WmsInventory>()
                    .eq(WmsInventory::getId, wmsInventoryDb.getId())
                    .setSql("stock_quantity=stock_quantity+{0}", inventoryTransParam.getExecQuantity())
                    .setSql("available_quantity=available_quantity+{0}", inventoryTransParam.getExecQuantity());
            wmsInventoryService.update(null,wmsInventoryLambdaUpdateWrapper);

        }
        //插入库存变更表
        WmsInventoryTrans wmsInventoryTrans = new WmsInventoryTrans();
        wmsInventoryTrans.setProductId(inventoryTransParam.getProductId());
        wmsInventoryTrans.setLocationCode(inventoryTransParam.getTargetLocationCode());
        wmsInventoryTrans.setBatchNumber(inventoryTransParam.getBatchNumber());
        wmsInventoryTrans.setChangeQuantity(inventoryTransParam.getExecQuantity());
        wmsInventoryTrans.setTransactionType(inventoryTransParam.getTransactionType());
        wmsInventoryTrans.setRemarks(inventoryTransParam.getRemarks());
        wmsInventoryTrans.setTransactionTime(new Date());
        save(wmsInventoryTrans);

    }
}
