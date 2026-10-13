package org.jeecg.modules.wms.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 库区表
 * @Author: jeecg-boot
 * @Date:   2025-04-09
 * @Version: V1.0
 */
public interface IWmsStorageZonesService extends IService<WmsStorageZones> {
    /**
     * 分页查询库区信息
     */
    public IPage<WmsStorageZones> queryList(WmsStorageZones wmsStorageZones, Integer pageNo, Integer pageSize);
}
