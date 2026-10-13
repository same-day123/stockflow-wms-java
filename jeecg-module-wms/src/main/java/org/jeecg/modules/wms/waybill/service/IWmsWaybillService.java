package org.jeecg.modules.wms.waybill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 运单服务接口类
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
public interface IWmsWaybillService  {


	/**
	 * 发货
	 * @param waveId
	 */
	public void send(String waveId);

	/**
	 * 根据波次id查询运单id
	 * @param waveId 波次id
	 * @return 运单id
	 */
//	public List<String> selectWaybillNosByWaveId(String waveId);


	/**
	 * 请求顺丰生成指定订单的运单
	 * 首先请求顺丰下单，成功后生成运单
	 */
//	public void generateWaybillForOrder(String orderId) throws Exception;


}
