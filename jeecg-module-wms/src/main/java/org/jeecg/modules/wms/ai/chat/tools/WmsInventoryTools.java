package org.jeecg.modules.wms.ai.chat.tools;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.ai.chat.vo.WmsInventoryQuery;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 库存工具类
 * @date 2025/8/24 14:57
 */
@Component
public class WmsInventoryTools {

    @Autowired
    private IWmsInventoryService wmsInventoryService;

    @Autowired
    private IWmsCargoOwnersService wmsCargoOwnersService;

    @Autowired
    private IWmsWarehousesService wmsWarehousesService;

    @Autowired
    private IWmsProductsService wmsProductsService;

    /**
     * 查询库存
     */
    @Tool(description = "根据条件查询库存信息")
    public List<WmsInventory> queryInventory(@ToolParam(description = "库存查询条件")WmsInventoryQuery  query, @ToolParam(description = "库存查询页码")Integer pageNo) {
        if(pageNo==null || pageNo<=0){
            pageNo = 1;
        }
        WmsInventory wmsInventory = new WmsInventory();
        BeanUtils.copyProperties(query, wmsInventory);
        IPage<WmsInventory> wmsInventoryIPage = wmsInventoryService.queryList(wmsInventory, pageNo, 10);
        List<WmsInventory> records = wmsInventoryIPage.getRecords();
        return records;
    }

    /**
     * 查询货主信息
     */
    @Tool(description = "查询货主信息")
    public List<WmsCargoOwners> queryOwnerInfo() {
        List<WmsCargoOwners> wmsCargoOwners = wmsCargoOwnersService.getBaseMapper().selectList(null);
        return wmsCargoOwners;
    }
    /**
     * 查询仓库信息
     */
    @Tool(description = "查询仓库信息")
    public List<WmsWarehouses> queryWarehouses() {
        List<WmsWarehouses> warehouses = wmsWarehousesService.getBaseMapper().selectList(null);
        return warehouses;
    }
    /**
     * 查询商品信息
     */
    @Tool(description = "查询商品信息")
    public List<WmsProducts> queryProducts() {
        List<WmsProducts> products = wmsProductsService.getBaseMapper().selectList(null);
        return products;
    }

}