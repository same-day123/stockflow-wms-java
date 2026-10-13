package org.jeecg.modules.wms.shipment.service.impl;

import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import org.jeecg.modules.wms.shipment.mapper.WmsShipmentDetailMapper;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 包裹明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
@Service
public class WmsShipmentDetailServiceImpl extends ServiceImpl<WmsShipmentDetailMapper, WmsShipmentDetail> implements IWmsShipmentDetailService {

	@Autowired
	private WmsShipmentDetailMapper wmsShipmentDetailMapper;

	@Override
	public List<WmsShipmentDetail> selectByMainId(String mainId) {
		return wmsShipmentDetailMapper.selectByMainId(mainId);
	}
}
