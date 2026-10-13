package org.jeecg.modules.wms.inorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrderItemsMapper;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsStockInOrderItemsServiceImpl extends ServiceImpl<WmsStockInOrderItemsMapper, WmsStockInOrderItems> implements IWmsStockInOrderItemsService {

	@Autowired
	private WmsStockInOrderItemsMapper wmsStockInOrderItemsMapper;

	@Autowired
	private IWmsTasksRecordsService wmsTasksRecordsService;

	@Override
	public List<WmsStockInOrderItems> selectByMainId(String mainId) {
		return wmsStockInOrderItemsMapper.selectByMainId(mainId);
	}

	@Override
	public void updateReceivedStatus(String stockInOrderItemId) {
		//查询入库单明细
		WmsStockInOrderItems stockInOrderItemUpdate = getById(stockInOrderItemId);
		//根据入库单明细id查询收货记录
		LambdaQueryWrapper<WmsTasksRecords> eq = new LambdaQueryWrapper<WmsTasksRecords>()
				.eq(WmsTasksRecords::getStockInOrderItemId, stockInOrderItemId);
		List<WmsTasksRecords> wmsTasksRecords = wmsTasksRecordsService.list(eq);

		//根据stream流汇总wmsTasksRecords中的不良品数量
		int badQuantity  = wmsTasksRecords.stream()
				.filter(wmsTasksRecords1 -> WarehouseDictEnum.INVENTORY_ATTRIBUTE_DEFECTIVE.getCode().equals(wmsTasksRecords1.getInventoryAttribute()))
				.mapToInt(WmsTasksRecords::getExecQuantity)
				.sum();

		//根据stream流汇总wmsTasksRecords中的良品数量
		int goodQuantity  = wmsTasksRecords.stream()
				.filter(wmsTasksRecords1 -> WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode().equals(wmsTasksRecords1.getInventoryAttribute()))
				.mapToInt(WmsTasksRecords::getExecQuantity)
				.sum();

		if(badQuantity > 0){
			stockInOrderItemUpdate.setDefectiveQuantity(badQuantity);
		}
		if(goodQuantity > 0){
			stockInOrderItemUpdate.setReceivedQuantity(goodQuantity);
		}

		if(stockInOrderItemUpdate.getExpectedQuantity().equals(stockInOrderItemUpdate.getReceivedQuantity() + stockInOrderItemUpdate.getDefectiveQuantity())){
			stockInOrderItemUpdate.setStatus(WarehouseDictEnum.INBOUND_DETAIL_RECEIVED.getCode());
		}
		updateById(stockInOrderItemUpdate);
	}

	/**
	 * 更新上架完成状态
	 * @param stockInOrderItemId 入库单明细id
	 */
	public void updatePutawayStatus(String stockInOrderItemId){
		//将完成数量更新至入库单明细
		WmsStockInOrderItems stockInOrderItemUpdate = getById(stockInOrderItemId);
		//根据入库单明细id查询任务记录表中上架数量总和
		LambdaQueryWrapper<WmsTasksRecords> eq = new LambdaQueryWrapper<WmsTasksRecords>()
				.eq(WmsTasksRecords::getStockInOrderItemId, stockInOrderItemId)
				.eq(WmsTasksRecords::getTaskType, WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode());
		List<WmsTasksRecords> wmsTasksRecordsList = wmsTasksRecordsService.list(eq);
		int completedQuantity = wmsTasksRecordsList.stream().mapToInt(WmsTasksRecords::getExecQuantity).sum();
		stockInOrderItemUpdate.setShelvedQuantity(completedQuantity);
		//如果实际上架数量等于入库单明细的收货数量则更新入库单明细状态为上架完成
		if(stockInOrderItemUpdate.getReceivedQuantity().equals(stockInOrderItemUpdate.getShelvedQuantity())){
			stockInOrderItemUpdate.setStatus(WarehouseDictEnum.INBOUND_DETAIL_PUTAWAYED.getCode());//上架完成
		}

		updateById(stockInOrderItemUpdate);


	}
}
