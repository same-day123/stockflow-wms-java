package org.jeecg.modules.wms.inorder.mapper;

import java.util.List;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface WmsStockInOrderItemsMapper extends BaseMapper<WmsStockInOrderItems> {

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
   * @return List<WmsStockInOrderItems>
   */
	public List<WmsStockInOrderItems> selectByMainId(@Param("mainId") String mainId);
}
