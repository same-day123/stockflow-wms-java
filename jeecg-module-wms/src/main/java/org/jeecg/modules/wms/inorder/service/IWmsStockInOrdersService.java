package org.jeecg.modules.wms.inorder.service;

import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.inorder.vo.WmsStockInOrdersPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface IWmsStockInOrdersService extends IService<WmsStockInOrders> {

	/**
	 * 添加入库单
	 */
	public void add(WmsStockInOrders wmsStockInOrders);

	/**
	 * 添加一对多
	 *
	 * @param wmsStockInOrders
	 * @param wmsStockInOrderItemsList
	 */
	public void saveMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList) ;
	/**
	 * 审核入库单
	 * @param wmsStockInOrdersPage
	 */
	void audit(WmsStockInOrdersPage wmsStockInOrdersPage);

	/**
	 * 提交审核
	 * @param wmsStockInOrdersPage
	 */
	void submitAudit(WmsStockInOrdersPage wmsStockInOrdersPage);
	/**
	 * 修改一对多
	 *
   * @param wmsStockInOrders
   * @param wmsStockInOrderItemsList
	 */
	public void updateMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList);

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
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 更新收货完成状态
	 * @param stockInOrderId 入库单id
	 * @return 更新后的入库单状态
	 */
	String updateReceivedStatus(String stockInOrderId);

	/**
	 *  更新上架完成状态
	 *  @param stockInOrderId 入库单id
	 * @return 更新后的入库单状态
	 */
	String updatePutawayStatus(String stockInOrderId);

}
