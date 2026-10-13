package org.jeecg.modules.wms.inventory.service;

import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;

/**
 * @Description: 库存变更表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface IWmsInventoryTransService extends IService<WmsInventoryTrans> {
    /**
     * 变更库存
     */
    void transfer(WmsInventoryTransParam inventoryTransParam);
}
