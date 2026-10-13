package org.jeecg.modules.wms.shipment.service;

import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 包裹明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
public interface IWmsShipmentDetailService extends IService<WmsShipmentDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<WmsShipmentDetail>
	 */
	public List<WmsShipmentDetail> selectByMainId(String mainId);
}
