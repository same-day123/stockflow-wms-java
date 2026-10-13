package org.jeecg.modules.wms.outorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersItemsMapper;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * @Description: 出库单明细
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
@Service
public class WmsOutOrdersItemsServiceImpl extends ServiceImpl<WmsOutOrdersItemsMapper, WmsOutOrdersItems> implements IWmsOutOrdersItemsService {

	@Autowired
	private WmsOutOrdersItemsMapper wmsOutOrdersItemsMapper;

	@Override
	public List<WmsOutOrdersItems> selectByMainId(String mainId) {
		return wmsOutOrdersItemsMapper.selectByMainId(mainId);
	}
	/**
	 * 根据出库单id查询出库单明细中状态不为已拣货的
	 * @param orderIds
	 */
	@Override
	public List<WmsOutOrdersItems> selectNotPickedOrderItemsByOrderId(List<String> orderIds){
		LambdaQueryWrapper<WmsOutOrdersItems> wmsOutOrdersItemsLambdaQueryWrapper = new LambdaQueryWrapper<>();
		wmsOutOrdersItemsLambdaQueryWrapper.in(WmsOutOrdersItems::getOrderId, orderIds)
				.ne(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_DETAIL_PICKED.getCode());
		return this.list(wmsOutOrdersItemsLambdaQueryWrapper);
	}



	/**
	 * 更新出库单明细的分配状态
	 * @param orderItemId
	 */
	@Override
	public String updateAllocateStatus(String orderItemId) {

		// 分配成功
		int i = wmsOutOrdersItemsMapper.updateAllocated(orderItemId);
		if (i > 0) {
			return WarehouseDictEnum.OUTBOUND_DETAIL_ALLOCATED.getCode();
		}else{
			wmsOutOrdersItemsMapper.updateAllocatedFailed(orderItemId);
			return WarehouseDictEnum.OUTBOUND_DETAIL_FAILED.getCode();
		}
	}

	/**
	 * 更新出库单明细的拣货数量及状态更新为分拣完成
	 */
	@Override
	public void updatePickedQuantityAndStatus(String orderItemId, Integer pickedQuantity){
		//sql update wms_out_orders_items set picked_quantity = ? ,status = '已拣货' where id = orderItemId  and status = '拣货中'
		LambdaUpdateWrapper<WmsOutOrdersItems> wmsOutOrdersItemsLambdaUpdateWrapper = new LambdaUpdateWrapper<WmsOutOrdersItems>()
				.eq(WmsOutOrdersItems::getId, orderItemId)
//				.eq(WmsOutOrdersItems::getAllocatedQuantity, pickedQuantity)
				.eq(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_DETAIL_PICKING.getCode())
				.set(WmsOutOrdersItems::getPickedQuantity, pickedQuantity)
				.set(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_DETAIL_PICKED.getCode());
		int update = wmsOutOrdersItemsMapper.update(null, wmsOutOrdersItemsLambdaUpdateWrapper);
		if(update<=0){
			throw new JeecgBootException("为出库单明细拣货数量及状态失败!");
		}
	}

	@Override
	public List<WmsOutOrdersItems> selectByOrderIds(List<String> orderIds) {
		if(CollectionUtils.isEmpty(orderIds)){
			return Collections.emptyList();
		}
		LambdaQueryWrapper<WmsOutOrdersItems> wmsOutOrdersItemsLambdaQueryWrapper = new LambdaQueryWrapper<>();
		wmsOutOrdersItemsLambdaQueryWrapper.in(WmsOutOrdersItems::getOrderId, orderIds);
		return this.list(wmsOutOrdersItemsLambdaQueryWrapper);
	}
	@Override
	public void allocateStock(String orderItemId, Integer allocateQty) {
		//查询出库单明细
		WmsOutOrdersItems wmsOutOrdersItems = wmsOutOrdersItemsMapper.selectById(orderItemId);
		if(wmsOutOrdersItems==null){
			throw new JeecgBootException("未找到出库单明细!");
		}
		//预期出库数量
		Integer expectedQuantity = wmsOutOrdersItems.getExpectedQuantity();
		LambdaUpdateWrapper<WmsOutOrdersItems> le = new LambdaUpdateWrapper<WmsOutOrdersItems>()
				.eq(WmsOutOrdersItems::getId, orderItemId)
				.setSql("allocated_quantity = allocated_quantity + {0} ", allocateQty)
				.le(WmsOutOrdersItems::getAllocatedQuantity, expectedQuantity - allocateQty);
		int update = wmsOutOrdersItemsMapper.update(null, le);
		if(update<=0){
			throw new JeecgBootException("更新出库单明细分配数量失败!");
		}


	}
}
