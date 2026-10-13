package org.jeecg.modules.wms.warehouse.service;

import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
public interface IWmsStorageLocationsService extends IService<WmsStorageLocations> {

    /**
     * 根据储位编码来查询储位信息
     * @param locationCode
     * @return
     */
    WmsStorageLocations queryByLocationCode(String locationCode);

}
