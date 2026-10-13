package org.jeecg.modules.wms.warehouse.service;

import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
public interface IWmsWarehousesService extends IService<WmsWarehouses> {

    /**
     * 启用仓库
     * @param id
     */
    void enable(String id);

    /**
     * 禁用仓库
     * @param id
     */
    void disable(String id);

    /**
     * 删除仓库
     * @param id
     */
    void delete(String id);

    /**
     * 新增仓库
     * @param wmsWarehouses
     */
    void add(WmsWarehouses wmsWarehouses);

    /**
     * 修改仓库
     * @param wmsWarehouses
     */
    void edit(WmsWarehouses wmsWarehouses);
}
