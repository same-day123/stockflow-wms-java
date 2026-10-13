package org.jeecg.modules.wms.outorder.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;

/**
 * @Description: 出库单
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
public interface WmsOutOrdersMapper extends BaseMapper<WmsOutOrders> {

    /**
     * 查询列表
     */
    public List<WmsOutOrders> queryList(WmsOutOrders wmsOutOrders);

    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<WmsOutOrders>
     */
    public List<WmsOutOrders> selectByMainId(@Param("mainId") String mainId);

    /**
     * 获取未分配波次的出库单,状态为已分配 ALLOCATED
     */
    public List<WmsOutOrders> selectUnassignedOrders(String warehouseId);

    /**
     * 根据出库单id更新订单的波次ID
     */
    public void batchUpdateWaveId(@Param("orderIds") List<String> orderIds, @Param("waveId") String waveId, @Param("shipmentStrategy") String shipmentStrategy);

}
