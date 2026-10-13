package org.jeecg.modules.wms.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsInventoryServiceImpl extends ServiceImpl<WmsInventoryMapper, WmsInventory> implements IWmsInventoryService {
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
}
