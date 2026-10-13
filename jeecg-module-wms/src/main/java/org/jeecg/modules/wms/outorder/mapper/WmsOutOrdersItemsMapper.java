package org.jeecg.modules.wms.outorder.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 出库单明细
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
public interface WmsOutOrdersItemsMapper extends BaseMapper<WmsOutOrdersItems> {

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
   * @return List<WmsOutOrdersItems>
   */
	public List<WmsOutOrdersItems> selectByMainId(@Param("mainId") String mainId);

	/**
	 * 更新出库单明细状态为已分配完成,状态由为已创建或者分配失败更新到已分配状态，并且已分配数量等于出库数量
	 */
	@Update("update wms_out_orders_items t set t.status = 'ALLOCATED' where t.id = #{orderItemId} and t.allocated_quantity=t.expected_quantity and (t.status='CREATED' or t.status='ALLOCATED_FAILED') ")
	public int updateAllocated(@Param("orderItemId") String orderItemId);

	/**
	 * 更新出库单明细状态为已分配失败,状态由为已创建或者已分配更新到分配失败状态，并且已分配数量小于出库数量
	 */
	@Update("update wms_out_orders_items t set t.status = 'ALLOCATED_FAILED' where t.id = #{orderItemId} and t.allocated_quantity<t.expected_quantity and (t.status='CREATED' or t.status='ALLOCATED_FAILED') ")
	public int updateAllocatedFailed(@Param("orderItemId") String orderItemId);

}
