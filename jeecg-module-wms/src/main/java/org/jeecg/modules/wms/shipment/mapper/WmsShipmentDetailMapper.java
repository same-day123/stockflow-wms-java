package org.jeecg.modules.wms.shipment.mapper;

import java.util.List;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 包裹明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
public interface WmsShipmentDetailMapper extends BaseMapper<WmsShipmentDetail> {

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
   * @return List<WmsShipmentDetail>
   */
	public List<WmsShipmentDetail> selectByMainId(@Param("mainId") String mainId);
}
