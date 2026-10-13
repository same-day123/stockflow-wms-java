package org.jeecg.modules.wms.inorder.service;

import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface IWmsStockInOrderItemsService extends IService<WmsStockInOrderItems> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<WmsStockInOrderItems>
	 */
	public List<WmsStockInOrderItems> selectByMainId(String mainId);

	/**
	 * 更新收货完成状态
	 * @param stockInOrderItemId 入库单明细id
	 */
	void updateReceivedStatus(String stockInOrderItemId);

	/**
	 * 更新上架完成状态
	 * @param stockInOrderItemId 入库单明细id
	 */
	void updatePutawayStatus(String stockInOrderItemId);


}
