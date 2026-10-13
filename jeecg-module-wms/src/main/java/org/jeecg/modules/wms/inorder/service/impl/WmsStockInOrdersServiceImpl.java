package org.jeecg.modules.wms.inorder.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrderItemsMapper;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrdersMapper;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService;
import org.jeecg.modules.wms.inorder.vo.WmsStockInOrdersPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsStockInOrdersServiceImpl extends ServiceImpl<WmsStockInOrdersMapper, WmsStockInOrders> implements IWmsStockInOrdersService {

	@Autowired
	private WmsStockInOrdersMapper wmsStockInOrdersMapper;
	@Autowired
	private WmsStockInOrderItemsMapper wmsStockInOrderItemsMapper;
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private IWmsStockInOrderItemsService stockInOrderItemsService;

	@Override
	public void add(WmsStockInOrders wmsStockInOrders) {
		//生成入库单号
		String orderNumber = generateOrderNumber();
		wmsStockInOrders.setOrderNumber(orderNumber);
		//设置入库单状态为初始状态
		wmsStockInOrders.setStatus(WarehouseDictEnum.INBOUND_INITIAL.getCode());
		//入库单表插入一条记录
		save(wmsStockInOrders);
	}

	/**
	 * 生成入库单号
	 */
	public String generateOrderNumber() {
		//8位年月日+4位流水号
		String time = DateUtils.now().substring(0,10).replace("-", "");
		//4位流水号
		String key = "wms:asn_number"+time;
		//获取流水号
		long incr = redisUtil.incr(key, 1);
		if(incr == 1){
			//如果是第一次设置流水号则设置过期 时间为24小时+10秒
			redisUtil.expire(key, 24*60*60+10);
		}
		String serialNumber = String.format("%04d", incr);
		//入库单号
		String orderNumber = "ASN"+time + serialNumber;
		return orderNumber;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(WmsStockInOrders wmsStockInOrders, List<WmsStockInOrderItems> wmsStockInOrderItemsList) {
		wmsStockInOrdersMapper.insert(wmsStockInOrders);
		if(wmsStockInOrderItemsList!=null && wmsStockInOrderItemsList.size()>0) {
			for(WmsStockInOrderItems entity:wmsStockInOrderItemsList) {
				//外键设置
				entity.setOrderId(wmsStockInOrders.getId());
				wmsStockInOrderItemsMapper.insert(entity);
			}
		}
	}
	/**
	 * 审核入库单
	 * @param wmsStockInOrdersPage
	 */
	public void audit(WmsStockInOrdersPage wmsStockInOrdersPage){
		//如果没有选择入库单则返回错误
		if(oConvertUtils.isEmpty(wmsStockInOrdersPage.getId())){
			//抛出异常
			throw new JeecgBootException("请选择要审核的入库单");
		}
		WmsStockInOrders wmsStockInOrdersEntity = getById(wmsStockInOrdersPage.getId());
		if(wmsStockInOrdersEntity==null) {
			//异常
			throw new JeecgBootException("入库单不存在");
		}
		//入库单明细
		List<WmsStockInOrderItems> wmsStockInOrderItemsList = stockInOrderItemsService.selectByMainId(wmsStockInOrdersPage.getId());
		if(wmsStockInOrderItemsList==null || wmsStockInOrderItemsList.size()<=0){
			//异常
			throw new JeecgBootException("请添加入库明细");
		}
		//当前状态只能是提交审核状态
		if(!WarehouseDictEnum.INBOUND_SUBMIT_AUDIT.getCode().equals(wmsStockInOrdersEntity.getStatus())){
			//异常
			throw new JeecgBootException("当前状态是提交审核状态方可进行审核");
		}
		//更新状态为审核通过或审核不通过
		wmsStockInOrdersEntity.setStatus(wmsStockInOrdersPage.getStatus());
		updateById(wmsStockInOrdersEntity);
	}
	/**
	 * 提交审核
	 * @param wmsStockInOrdersPage
	 */
	public void submitAudit(WmsStockInOrdersPage wmsStockInOrdersPage){
		//如果没有选择入库单则返回错误
		if(wmsStockInOrdersPage==null || oConvertUtils.isEmpty(wmsStockInOrdersPage.getId())){
			//抛出异常
			throw new JeecgBootException("请选择要审核的入库单");
		}
		WmsStockInOrders wmsStockInOrdersEntity = getById(wmsStockInOrdersPage.getId());
		if(wmsStockInOrdersEntity==null) {
			//异常
			throw new JeecgBootException("入库单不存在");
		}
		//当前状态只能是初始状态、审核失败状态
		if(!(WarehouseDictEnum.INBOUND_INITIAL.getCode().equals(wmsStockInOrdersEntity.getStatus())
				|| WarehouseDictEnum.INBOUND_REJECTED.getCode().equals(wmsStockInOrdersEntity.getStatus()))){
			throw new JeecgBootException("非初始状态、审核失败状态入库单不允许审核");
		}
		//更新状态为提交审核
		wmsStockInOrdersEntity.setStatus(WarehouseDictEnum.INBOUND_SUBMIT_AUDIT.getCode());
		updateById(wmsStockInOrdersEntity);
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList) {
		//初始状态、审核失败状态可以修改
		if(!(WarehouseDictEnum.INBOUND_INITIAL.getCode().equals(wmsStockInOrders.getStatus())
				|| WarehouseDictEnum.INBOUND_REJECTED.getCode().equals(wmsStockInOrders.getStatus()))){
			throw new JeecgBootException("非初始状态、审核失败状态入库单不允许修改");
		}

		if(wmsStockInOrderItemsList==null || wmsStockInOrderItemsList.size()<=0){
			//抛出异常
			throw new JeecgBootException("请选择入库单明细");
		}
		wmsStockInOrdersMapper.updateById(wmsStockInOrders);

		//1.先删除子表数据
		wmsStockInOrderItemsMapper.deleteByMainId(wmsStockInOrders.getId());

		//对入库单明细的数据按商品分组(对明细中重复的商品进行合并 )
		//<商品id,入库单明细>
		Map<String, List<WmsStockInOrderItems>> collect = wmsStockInOrderItemsList.stream()
				.filter(item -> item.getProductId() != null)
				.collect(Collectors.groupingBy(WmsStockInOrderItems::getProductId));
		//合并结果集
		List<WmsStockInOrderItems> mergeList = new ArrayList<>();
		//遍历collect
		for (Map.Entry<String, List<WmsStockInOrderItems>> entry : collect.entrySet()) {
			List<WmsStockInOrderItems> list = entry.getValue();
			//如果list集合大小1
			if(list.size() >1){
				//计算采购总数
				int expectedQuantity = list.stream().mapToInt(WmsStockInOrderItems::getExpectedQuantity).sum();
				//合并后的对象
				WmsStockInOrderItems merge = new WmsStockInOrderItems();
				WmsStockInOrderItems wmsStockInOrderItems = list.get(0);
				BeanUtils.copyProperties(wmsStockInOrderItems, merge);
				merge.setExpectedQuantity(expectedQuantity);
				mergeList.add(merge);

			}else{
				mergeList.add(list.get(0));
			}
		}
		//计算预期采购总量
		int totalExpectedQuantity = mergeList.stream().mapToInt(WmsStockInOrderItems::getExpectedQuantity).sum();
		//2.子表数据重新插入
		for(WmsStockInOrderItems entity:mergeList) {
			//外键设置
			entity.setOrderId(wmsStockInOrders.getId());
			//状态为初始状态
			entity.setStatus(WarehouseDictEnum.INBOUND_DETAIL_INITIAL.getCode());
			wmsStockInOrderItemsMapper.insert(entity);
		}
		wmsStockInOrders.setTotalExpectedQuantity(totalExpectedQuantity);
		wmsStockInOrdersMapper.updateById(wmsStockInOrders);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		wmsStockInOrderItemsMapper.deleteByMainId(id);
		wmsStockInOrdersMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			wmsStockInOrderItemsMapper.deleteByMainId(id.toString());
			wmsStockInOrdersMapper.deleteById(id);
		}
	}

	@Override
	public String updateReceivedStatus(String stockInOrderId) {

		/**
		 1.根据入库单id查询入库单明细列表
		 2.计算良品总数和不良品总数
		 3.如果入库单明细全部完成收货则入库单状态为完成收货
		 */
		WmsStockInOrders stockInOrdersUpdate = getById(stockInOrderId);
		if(stockInOrdersUpdate==null) {
			//异常
			throw new JeecgBootException("入库单不存在");
		}
		//当前状态只有是收货中或审核通过时方可更新收货状态
		if(!(WarehouseDictEnum.INBOUND_RECEIVING.getCode().equals(stockInOrdersUpdate.getStatus())
				|| WarehouseDictEnum.INBOUND_APPROVED.getCode().equals(stockInOrdersUpdate.getStatus()))){
			throw new JeecgBootException("非收货中、审核通过状态入库单不允许更新收货状态");
		}
		//根据入库单id查询所有入库单明细的完成数量总和
		List<WmsStockInOrderItems> stockInOrderItems = stockInOrderItemsService.selectByMainId(stockInOrderId);
		//使用stream流汇总stockInOrderItems中完成收货数量总和
		int totalCompletedQuantity = stockInOrderItems.stream().mapToInt(WmsStockInOrderItems::getReceivedQuantity).sum();
		//不良品数量
		int totalBadQuantity = stockInOrderItems.stream().mapToInt(WmsStockInOrderItems::getDefectiveQuantity).sum();
		//入库单明细中只要存在一个状态是未完成收货则入库单的状态为收货中
		boolean b = stockInOrderItems.stream().anyMatch(item -> !WarehouseDictEnum.INBOUND_DETAIL_RECEIVED.getCode().equals(item.getStatus()));
		//新状态
		String status = null;
		if(b){
			status = WarehouseDictEnum.INBOUND_RECEIVING.getCode();
			stockInOrdersUpdate.setStatus(WarehouseDictEnum.INBOUND_RECEIVING.getCode());
		}else{
			status = WarehouseDictEnum.INBOUND_RECEIVED.getCode();
			stockInOrdersUpdate.setStatus(WarehouseDictEnum.INBOUND_RECEIVED.getCode());
		}
		stockInOrdersUpdate.setTotalReceivedQuantity(totalCompletedQuantity);
		stockInOrdersUpdate.setTotalDefectiveQuantity(totalBadQuantity);
		boolean update = updateById(stockInOrdersUpdate);
		if(!update){
			throw new JeecgBootException("更新入库单收货状态失败");
		}
		return status;
	}
	/**
	 *  更新上架完成状态
	 *  @param stockInOrderId 入库单id
	 * @return 更新后的入库单状态
	 */
	public String updatePutawayStatus(String stockInOrderId){
		//根据入库单id查询所有入库单明细的完成数量总和
		List<WmsStockInOrderItems> stockInOrderItems = stockInOrderItemsService.selectByMainId(stockInOrderId);
		//使用stream流汇总stockInOrderItems中完成数量总和
		int totalCompletedQuantity = stockInOrderItems.stream().mapToInt(WmsStockInOrderItems::getShelvedQuantity).sum();
		//将完成数量更新至入库单
		WmsStockInOrders stockInOrdersUpdate = getById(stockInOrderId);
		stockInOrdersUpdate.setTotalShelvedQuantity(totalCompletedQuantity);
		//入库单状态更新,入库单状态更新为上架完成
		String status = totalCompletedQuantity == stockInOrdersUpdate.getTotalReceivedQuantity()
				? WarehouseDictEnum.INBOUND_PUTAWAYED.getCode()
				: WarehouseDictEnum.INBOUND_PUTAWAYING.getCode();
		stockInOrdersUpdate.setStatus(status);

		boolean b = updateById(stockInOrdersUpdate);
		if(b){
			//返回入库单状态
			return status;
		}
		//异常
		throw new JeecgBootException("更新入库单上架完成状态失败");
	}
}
