package org.jeecg.modules.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.mapper.WmsStorageZonesMapper;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 库区表
 * @Author: jeecg-boot
 * @Date:   2025-04-09
 * @Version: V1.0
 */
@Service
public class WmsStorageZonesServiceImpl extends ServiceImpl<WmsStorageZonesMapper, WmsStorageZones> implements IWmsStorageZonesService {

    @Override
    public IPage<WmsStorageZones> queryList(WmsStorageZones wmsStorageZones, Integer pageNo, Integer pageSize) {
        //开启分页查询,当执行查询时，插件进行相关的sql拦截进行分页操作，返回一个page对象
        Page<WmsStorageZones> page = PageHelper.startPage(pageNo, pageSize);
        List<WmsStorageZones> list = baseMapper.queryList(wmsStorageZones);
        PageDTO<WmsStorageZones> wmsStorageZonesPageDTO = new PageDTO<>();
        wmsStorageZonesPageDTO.setRecords(list);
        wmsStorageZonesPageDTO.setTotal(page.getTotal());
        wmsStorageZonesPageDTO.setSize(page.getPageSize());
        wmsStorageZonesPageDTO.setCurrent(page.getPageNum());
        wmsStorageZonesPageDTO.setPages(page.getPages());
        return wmsStorageZonesPageDTO;
    }
}
