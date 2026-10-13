package org.jeecg.modules.wms.outorder.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersAllocationMapper;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 出库单分配明细
 * @Author: jeecg-boot
 * @Date:   2025-05-21
 * @Version: V1.0
 */
@Service
public class WmsOutOrdersAllocationServiceImpl extends ServiceImpl<WmsOutOrdersAllocationMapper, WmsOutOrdersAllocation> implements IWmsOutOrdersAllocationService {
    @Autowired
    private WmsOutOrdersAllocationMapper wmsOutOrdersAllocationMapper;

    @Override
    public List<WmsOutOrdersAllocation> selectByMainId(String mainId) {
        return wmsOutOrdersAllocationMapper.selectByMainId(mainId);
    }
    /**
     * 根据波次统计出库单的商品分配数量，分页查询结果
     */
    public IPage<WmsOutOrdersAllocation> selectAllocatedQuantityByWaveId(String waveId, Integer pageNo, Integer pageSize) {
        Page<WmsStockInOrderItems> page = PageHelper.startPage(pageNo, pageSize);
        List<WmsOutOrdersAllocation> list = wmsOutOrdersAllocationMapper.selectAllocatedQuantityByWaveId(waveId);
        PageDTO<WmsOutOrdersAllocation> wmsOutOrdersAllocationPageDTO = new PageDTO<>();
        wmsOutOrdersAllocationPageDTO.setRecords(list);
        wmsOutOrdersAllocationPageDTO.setTotal(page.getTotal());
        wmsOutOrdersAllocationPageDTO.setSize(page.getPageSize());
        wmsOutOrdersAllocationPageDTO.setCurrent(page.getPageNum());
        wmsOutOrdersAllocationPageDTO.setPages(page.getPages());
        return wmsOutOrdersAllocationPageDTO;
    }
}
