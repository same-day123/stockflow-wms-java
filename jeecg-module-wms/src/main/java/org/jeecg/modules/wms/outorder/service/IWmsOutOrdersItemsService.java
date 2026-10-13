package org.jeecg.modules.wms.outorder.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 出库单明细
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
public interface IWmsOutOrdersItemsService extends IService<WmsOutOrdersItems> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<WmsOutOrdersItems>
	 */
	public List<WmsOutOrdersItems> selectByMainId(String mainId);

	/**
	 * 根据出库单id查询出库单明细中状态不为已拣货的
	 * @param orderIds
	 */
	public List<WmsOutOrdersItems> selectNotPickedOrderItemsByOrderId(List<String> orderIds);



	/**
	 * 更新出库单明细的分配状态
	 * @param orderItemId
	 * @return 分配状态
	 */
	public String updateAllocateStatus(String orderItemId);


	/**
	 * 更新出库单明细的拣货数量及状态更新为分拣完成
	 */
	public void updatePickedQuantityAndStatus(String orderItemId, Integer pickedQuantity);
	/**
	 * 根据出库单ids查询出库单明细
	 */
	public List<WmsOutOrdersItems> selectByOrderIds(List<String> orderIds);

	/**
	 * 分配库存
	 * @param orderItemId 出库单明细id
	 * @param allocateQty 分配数量
	 */
	public void allocateStock(String orderItemId, Integer allocateQty);
}
