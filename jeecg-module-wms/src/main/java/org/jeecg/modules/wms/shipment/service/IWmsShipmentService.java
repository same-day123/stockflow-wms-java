package org.jeecg.modules.wms.shipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 包裹主表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
public interface IWmsShipmentService extends IService<WmsShipment> {

	/**
	 * 添加一对多
	 *
	 * @param wmsShipment
	 * @param wmsShipmentDetailList
	 */
	public void saveMain(WmsShipment wmsShipment,List<WmsShipmentDetail> wmsShipmentDetailList) ;

	/**
	 * 修改一对多
	 *
   * @param wmsShipment
   * @param wmsShipmentDetailList
	 */
	public void updateMain(WmsShipment wmsShipment,List<WmsShipmentDetail> wmsShipmentDetailList);

	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);

	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends String> idList);

	/**
	 * 创建包裹
	 * @param waveId
	 * @return 包裹数量
	 */
//	int createShipment(List<String> waveIds);

	public int createShipment(String waveId);


	/**
	 * 查询包裹列表
	 * @param wmsShipment
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public IPage<WmsShipment> queryList(WmsShipment wmsShipment, Integer pageNo, Integer pageSize);
	/**
	 * 生成包裹编号
	 */
	public String generateShipmentNumber();

	/**
	 * 完成某订单的打包
	 */
	public void completePackByOrder(WmsOutOrders order);

	/**
	 * 完成某波次的打包
	 */
	public void completePackByWave(String waveId);
	/**
	 * 批量完成某波次的打包
	 */
	public void completePackByWaveBatch(String waveIds);

	/**
	 * 根据订单id查询包裹
	 * @param orderId
	 * @return
	 */
	public List<WmsShipment> selectByOrderId(String orderId);

}
