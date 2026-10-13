package org.jeecg.modules.wms.inventory.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface WmsInventoryMapper extends BaseMapper<WmsInventory> {
    /**
     * 综合查询库存列表
     */
    public List<WmsInventory> queryList(WmsInventory wmsInventory);


}
