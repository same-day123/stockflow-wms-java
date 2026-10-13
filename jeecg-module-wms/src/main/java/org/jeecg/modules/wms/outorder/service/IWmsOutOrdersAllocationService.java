package org.jeecg.modules.wms.outorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 出库单分配明细
 * @Author: jeecg-boot
 * @Date:   2025-05-21
 * @Version: V1.0
 */
public interface IWmsOutOrdersAllocationService extends IService<WmsOutOrdersAllocation> {
    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<WmsOutOrdersAllocation>
     */
    public List<WmsOutOrdersAllocation> selectByMainId(String mainId);

    /**
     * 根据波次统计出库单的商品分配数量，分页查询结果
     * @param waveId 波次id
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return
     */
    public IPage<WmsOutOrdersAllocation> selectAllocatedQuantityByWaveId(String waveId, Integer pageNo, Integer pageSize);



}
