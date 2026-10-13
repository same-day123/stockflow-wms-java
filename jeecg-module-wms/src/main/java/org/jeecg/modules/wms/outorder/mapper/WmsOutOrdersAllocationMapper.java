package org.jeecg.modules.wms.outorder.mapper;

import java.util.List;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 出库单分配明细
 * @Author: jeecg-boot
 * @Date:   2025-05-21
 * @Version: V1.0
 */
public interface WmsOutOrdersAllocationMapper extends BaseMapper<WmsOutOrdersAllocation> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<WmsOutOrdersAllocation>
   */
	public List<WmsOutOrdersAllocation> selectByMainId(@Param("mainId") String mainId);

	/**
	 * 获取这些出库单的分配明细
	 */
	public List<WmsOutOrdersAllocation> selectAllocationsByOrderIds(@Param("orderIds") List<String> orderIds);

	/**
	 * 根据波次统计出库单的商品分配数量
	 */
	public List<WmsOutOrdersAllocation> selectAllocatedQuantityByWaveId(String waveId);

}
