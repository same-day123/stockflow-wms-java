package org.jeecg.modules.wms.warehouse.mapper;

import java.util.List;

import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 库区表
 * @Author: jeecg-boot
 * @Date:   2025-04-09
 * @Version: V1.0
 */
public interface WmsStorageZonesMapper extends BaseMapper<WmsStorageZones> {
    /**
     * 分页查询库区信息
     */
    public List<WmsStorageZones> queryList(WmsStorageZones wmsStorageZones);
}
