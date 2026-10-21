package org.jeecg.modules.wms.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.excel.ImportResult;
import org.jeecg.modules.wms.inventory.excel.WmsInventoryExcel;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Slf4j
@Service
public class WmsInventoryServiceImpl extends ServiceImpl<WmsInventoryMapper, WmsInventory> implements IWmsInventoryService {

    @Autowired
    private IWmsWarehousesService wmsWarehousesService;
    @Autowired
    private IWmsCargoOwnersService wmsCargoOwnersService;
    @Autowired
    private IWmsProductsService wmsProductsService;
    @Autowired
    private IWmsStorageLocationsService wmsStorageLocationsService;


    /**
     * 根据商品id，仓库id，批次号查询唯一库存
     * @param productId 商品id
     * @param locationCode 库位
     * @param batchNumber 批次号
     * @return
     */
    @Override
    public WmsInventory getInventoryByUniqueKey(String productId, String locationCode, String batchNumber) {
        LambdaQueryWrapper<WmsInventory> eq = new LambdaQueryWrapper<WmsInventory>()
                .eq(WmsInventory::getProductId, productId)
                .eq(WmsInventory::getLocationCode, locationCode)
                .eq(StringUtils.isNotEmpty(batchNumber),WmsInventory::getBatchNumber, batchNumber)
                .eq(StringUtils.isEmpty(batchNumber),WmsInventory::getBatchNumber,"");
        //查询
        WmsInventory inventory = this.getOne(eq);
        return inventory;
    }

    @Override
    public IPage<WmsInventory> queryList(WmsInventory wmsInventory, Integer pageNo, Integer pageSize) {
        //开启分页查询,当执行查询时，插件进行相关的sql拦截进行分页操作，返回一个page对象
        Page<WmsInventory> page = PageHelper.startPage(pageNo, pageSize);
        List<WmsInventory> list = baseMapper.queryList(wmsInventory);
        PageDTO<WmsInventory> wmsInventoryPageDTO = new PageDTO<>();
        wmsInventoryPageDTO.setRecords(list);
        wmsInventoryPageDTO.setTotal(page.getTotal());
        wmsInventoryPageDTO.setSize(page.getPageSize());
        wmsInventoryPageDTO.setCurrent(page.getPageNum());
        wmsInventoryPageDTO.setPages(page.getPages());
        return wmsInventoryPageDTO;
    }

    @Override
    public List<WmsInventory> selectAvailableBySku(String warehouseId, WmsOutOrdersItems skuItem) {
        //查询该明细对应商品在仓库的可用库存列表，通过商品id，仓库id，批次号，库存数量大于0，查询按过期时间升序，入库时间升序排序
        //sql = "select * from wms_inventory
        // where product_id = #{skuItem.skuId}
        // and warehouse_id = #{warehouseId}
        // and batch_number = #{skuItem.batchNumber}
        // and is_sellable = '1'
        // and stock_quantity > 0
        // order by expiry_date asc,stock_in_time asc";
        List<WmsInventory> list = this.list(new LambdaQueryWrapper<WmsInventory>()
                .eq(WmsInventory::getProductId, skuItem.getSkuId())//商品id
                .eq(WmsInventory::getWarehouseId, warehouseId)//仓库id
                .eq(StringUtils.isNotEmpty(skuItem.getBatchNumber()), WmsInventory::getBatchNumber, skuItem.getBatchNumber())
                .eq(WmsInventory::getIsSellable, "1")
                .gt(WmsInventory::getStockQuantity, 0)
                .orderByAsc(WmsInventory::getExpiryDate)
                .orderByAsc(WmsInventory::getStockInTime));
        return list;
    }

    @Override
    public ImportResult importInventory(List<WmsInventoryExcel> cachedDataList) {
        ImportResult importResult = new ImportResult();
        if (cachedDataList == null || cachedDataList.isEmpty()) {
            throw new JeecgBootException("导入数据为空");
        }
        //遍历这个集合
        for (WmsInventoryExcel wmsInventoryExcel : cachedDataList) {
            Integer rowNum = wmsInventoryExcel.getRowNum();
            try {
                //把每个WmsInventoryExcel转换成WmsInventory
                WmsInventory wmsInventory = convertWmsInventory(wmsInventoryExcel);
                if (inventoryExists(wmsInventory)) {
                    importResult.addSkip();
                    log.info("第{}行库存已存在，跳过不导入", rowNum);
                    continue;
                }
                if (save(wmsInventory)) {
                    importResult.addSuccess();
                } else {
                    importResult.addFail(rowNum, "保存失败");
                }
            } catch (IllegalArgumentException e) {
                importResult.addFail(rowNum, e.getMessage());
            } catch (Exception e) {
                importResult.addFail(rowNum, "保存异常：" + e.getMessage());
                log.error("第{}行库存导入失败", rowNum, e);
            }

        }
        log.info("库存导入：成功 {} 条，跳过 {} 条，失败 {} 条", importResult.getSuccessCount(), importResult.getSkipCount(), importResult.getFailCount());
        if (!importResult.getFailMessages().isEmpty()) {
            log.warn("失败明细：{}", String.join("；", importResult.getFailMessages()));
        }
        return importResult;
    }

    private WmsInventory convertWmsInventory(WmsInventoryExcel wmsInventoryExcel) {
        WmsInventory wmsInventory = new WmsInventory();
        //将原来Inventory就有的数据copy进去
        BeanUtils.copyProperties(wmsInventoryExcel,wmsInventory);

        //仓库名称
        String warehouseName = wmsInventoryExcel.getWarehouseName();
        LambdaQueryWrapper<WmsWarehouses> queryWarehouses = new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseName, warehouseName);
        WmsWarehouses wmsWarehouses = wmsWarehousesService.getOne(queryWarehouses);
        wmsInventory.setWarehouseName(wmsWarehouses.getWarehouseName());
        wmsInventory.setWarehouseId(wmsWarehouses.getId());
        //货主编码
        String ownerCode = wmsInventoryExcel.getOwnerCode();
        LambdaQueryWrapper<WmsCargoOwners> queryCargo = new LambdaQueryWrapper<WmsCargoOwners>()
                .eq(WmsCargoOwners::getOwnerCode,ownerCode);
        WmsCargoOwners wmsCargoOwners = wmsCargoOwnersService.getOne(queryCargo);
        wmsInventory.setOwnerId(wmsCargoOwners.getId());
        wmsInventory.setOwnerCode(wmsCargoOwners.getOwnerCode());
        //商品编码
        String productCode = wmsInventoryExcel.getProductCode();
        LambdaQueryWrapper<WmsProducts> queryProduct = new LambdaQueryWrapper<WmsProducts>()
                .eq(WmsProducts::getProductCode,productCode);
        WmsProducts wmsProducts = wmsProductsService.getOne(queryProduct);
        wmsInventory.setProductId(wmsProducts.getId());
        wmsInventory.setProductCode(wmsProducts.getProductCode());
        //商品名称
        wmsInventory.setProductName(wmsProducts.getProductName());
        wmsInventory.setStockInTime(new Date());      // 数据库 NOT NULL，不设就是 ERROR 1364
        wmsInventory.setAllocatedQuantity(0);         // 新导入的库存没有被分配
        LambdaQueryWrapper<WmsStorageLocations> wmsStorageLocationsLambdaQuery = new LambdaQueryWrapper<WmsStorageLocations>()
                .eq(WmsStorageLocations::getWarehouseId, wmsWarehouses.getId())
                .eq(WmsStorageLocations::getLocationCode, wmsInventoryExcel.getLocationCode());
        WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.getOne(wmsStorageLocationsLambdaQuery);
        wmsInventory.setIsSellable(wmsStorageLocations.getIsSellable());
        wmsInventory.setAvailableQuantity("1".equals(wmsStorageLocations.getIsSellable()) ? wmsInventoryExcel.getStockQuantity() : 0);       // 需要按储位可售性推算，不是无条件等于数量


        return wmsInventory;
    }
    /**
     * 判断库存是否已存在（商品id + 储位编码 + 批次号）
     */
    private boolean inventoryExists(WmsInventory inventory) {
        String batchNumber = StringUtils.isEmpty(inventory.getBatchNumber()) ? "" : inventory.getBatchNumber();
        return this.count(new LambdaQueryWrapper<WmsInventory>()
                .eq(WmsInventory::getProductId, inventory.getProductId())
                .eq(WmsInventory::getLocationCode, inventory.getLocationCode())
                .eq(WmsInventory::getBatchNumber, batchNumber)) > 0;
    }
}
