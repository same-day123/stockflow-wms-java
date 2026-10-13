package org.jeecg.modules.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.mapper.WmsStorageLocationsMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
@Service
public class WmsStorageLocationsServiceImpl extends ServiceImpl<WmsStorageLocationsMapper, WmsStorageLocations> implements IWmsStorageLocationsService {

    @Override
    public WmsStorageLocations queryByLocationCode(String locationCode) {
        LambdaQueryWrapper<WmsStorageLocations> eq = new LambdaQueryWrapper<WmsStorageLocations>()
                .eq(WmsStorageLocations::getLocationCode, locationCode);
        WmsStorageLocations one = this.getOne(eq);
        return one;
    }
}
