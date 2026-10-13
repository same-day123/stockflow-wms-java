package org.jeecg.modules.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.mapper.WmsWarehousesMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
@Service
public class WmsWarehousesServiceImpl extends ServiceImpl<WmsWarehousesMapper, WmsWarehouses> implements IWmsWarehousesService {

    /**
     * 启用仓库
     * @param id
     */
    @Override
    public void enable(String id) {
        //查询仓库信息
        WmsWarehouses wmsWarehouses = this.getById(id);
        if (wmsWarehouses == null) {
            throw new RuntimeException("仓库不存在");
        }
        //只当前是创建状态和禁用状态方可启用
        if (!(wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_CREATED.getCode())
                || wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_INACTIVE.getCode()))) {
            throw new RuntimeException("前是创建状态和禁用状态方可启用");
        }
        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_ACTIVE.getCode());
        this.updateById(wmsWarehouses);
    }

    @Override
    public void disable(String id) {
        //查询仓库信息
        WmsWarehouses wmsWarehouses = this.getById(id);
        if (wmsWarehouses == null) {
            throw new RuntimeException("仓库不存在");
        }
        //只当前是启用状态方可禁用
        if (!wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_ACTIVE.getCode())) {
            throw new RuntimeException("当前是启用状态方可禁用");
        }
        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_INACTIVE.getCode());
        this.updateById(wmsWarehouses);
    }

    @Override
    public void delete(String id) {
        //查询仓库信息
        WmsWarehouses wmsWarehouses = this.getById(id);
        if (wmsWarehouses == null) {
            throw new RuntimeException("仓库不存在");
        }
        //只当前是创建状态方可删除
        if (!wmsWarehouses.getStatus().equals(WarehouseDictEnum.STATUS_CREATED.getCode())) {
            throw new RuntimeException("当前是创建状态方可删除");
        }
        removeById(id);
    }

    @Override
    public void add(WmsWarehouses wmsWarehouses) {
        //校验仓库代码唯一性
        //根据仓库代码查询仓库信息
        LambdaQueryWrapper<WmsWarehouses> ne = new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseCode, wmsWarehouses.getWarehouseCode());
        if (this.count(ne) > 0) {
            throw new RuntimeException("仓库代码已存在");
        }
        //初始状态为“创建”
        wmsWarehouses.setStatus(WarehouseDictEnum.STATUS_CREATED.getCode());
        save(wmsWarehouses);
    }

    @Override
    public void edit(WmsWarehouses wmsWarehouses) {
        //根据仓库代码查询仓库信息
        LambdaQueryWrapper<WmsWarehouses> ne = new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseCode, wmsWarehouses.getWarehouseCode())
                .ne(WmsWarehouses::getId, wmsWarehouses.getId());
        if (this.count(ne) > 0) {
            throw new RuntimeException("仓库代码已存在");
        }
        updateById(wmsWarehouses);
    }
}
