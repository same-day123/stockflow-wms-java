package org.jeecg.modules.wms.outorder.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
//import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByAllocation;
import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByAllocation;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersItemsMapper;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersMapper;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersAllocationService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 出库单
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
@Service
public class WmsOutOrdersServiceImpl extends ServiceImpl<WmsOutOrdersMapper, WmsOutOrders> implements IWmsOutOrdersService {

	@Autowired
	private WmsOutOrdersMapper wmsOutOrdersMapper;
	@Autowired
	private WmsOutOrdersItemsMapper wmsOutOrdersItemsMapper;
	@Autowired
	private IWmsOutOrdersItemsService wmsOutOrdersItemsService;
	//注入库存service
	@Autowired
	private IWmsInventoryService iWmsInventoryService;

	@Autowired
	private WmsInventoryTransByAllocation wmsInventoryTransByAllocation;

	//注入自己
	@Autowired
	private IWmsOutOrdersService owner;

	//注入wmsOutOrdersAllocationService
	@Autowired
	private IWmsOutOrdersAllocationService outOrdersAllocationService;

	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 获取出库单号
	 */
	@Override
	public IPage<WmsOutOrders> queryList(WmsOutOrders wmsOutOrders, Integer pageNo, Integer pageSize) {
		Page<WmsOutOrders> page = PageHelper.startPage(pageNo, pageSize);
		List<WmsOutOrders> list = wmsOutOrdersMapper.queryList(wmsOutOrders);
		PageDTO<WmsOutOrders> wmsOutOrdersPageDTO = new PageDTO<>();
		wmsOutOrdersPageDTO.setRecords(list);
		wmsOutOrdersPageDTO.setTotal(page.getTotal());
		wmsOutOrdersPageDTO.setSize(page.getPageSize());
		wmsOutOrdersPageDTO.setCurrent(page.getPageNum());
		wmsOutOrdersPageDTO.setPages(page.getPages());
		return wmsOutOrdersPageDTO;
	}

	/**
	 * 新增出库单
	 */
	public void add(WmsOutOrders wmsOutOrders){
		//生成出库单号
		String orderNumber = generateOrderNumber();
		wmsOutOrders.setOrderNo(orderNumber);
		//状态默认为创建状态 "CREATED"
		wmsOutOrders.setStatus(WarehouseDictEnum.OUTBOUND_CREATED.getCode());
		wmsOutOrdersMapper.insert(wmsOutOrders);
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(WmsOutOrders wmsOutOrders, List<WmsOutOrdersItems> wmsOutOrdersItemsList) {
		//生成出库单号
		String orderNumber = generateOrderNumber();
		wmsOutOrders.setOrderNo(orderNumber);
		//状态默认为创建状态 "CREATED"
		wmsOutOrders.setStatus(WarehouseDictEnum.OUTBOUND_CREATED.getCode());
		wmsOutOrdersMapper.insert(wmsOutOrders);
		//总商品数量
		int totalQuantity = 0;
		if(wmsOutOrdersItemsList!=null && wmsOutOrdersItemsList.size()>0) {
			for(WmsOutOrdersItems entity:wmsOutOrdersItemsList) {
				//商品数量
				totalQuantity += entity.getExpectedQuantity();
				//外键设置
				entity.setOrderId(wmsOutOrders.getId());
				//状态默认为创建状态 "CREATED"
				entity.setStatus(WarehouseDictEnum.OUTBOUND_DETAIL_CREATED.getCode());
				wmsOutOrdersItemsMapper.insert(entity);
			}
		}
		//更新商品数量
		wmsOutOrders.setTotalQuantity(totalQuantity);
		//商品sku种类数
		wmsOutOrders.setTotalSku(wmsOutOrdersItemsList.size());
		wmsOutOrdersMapper.updateById(wmsOutOrders);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(WmsOutOrders wmsOutOrders,List<WmsOutOrdersItems> wmsOutOrdersItemsList) {
		wmsOutOrdersMapper.updateById(wmsOutOrders);

		//1.先删除子表数据
		wmsOutOrdersItemsMapper.deleteByMainId(wmsOutOrders.getId());

		//总商品数量
		int totalQuantity = 0;
		if(wmsOutOrdersItemsList!=null && wmsOutOrdersItemsList.size()>0) {
			for(WmsOutOrdersItems entity:wmsOutOrdersItemsList) {
				//商品数量
				totalQuantity += entity.getExpectedQuantity();
				//外键设置
				entity.setOrderId(wmsOutOrders.getId());
				//状态默认为创建状态 "CREATED"
				entity.setStatus(WarehouseDictEnum.OUTBOUND_DETAIL_CREATED.getCode());
				wmsOutOrdersItemsMapper.insert(entity);
			}
		}
		//更新商品数量
		wmsOutOrders.setTotalQuantity(totalQuantity);
		//商品sku种类数
		wmsOutOrders.setTotalSku(wmsOutOrdersItemsList.size());
		wmsOutOrdersMapper.updateById(wmsOutOrders);

	}

	/**
	 * 删除一对多
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		//出库单状态为创建状态或审核失败状态可以删除
		WmsOutOrders wmsOutOrders = wmsOutOrdersMapper.selectById(id);
		if(!(WarehouseDictEnum.OUTBOUND_CREATED.getCode().equals(wmsOutOrders.getStatus())
				|| WarehouseDictEnum.OUTBOUND_REJECTED.getCode().equals(wmsOutOrders.getStatus()))){
			throw new JeecgBootException("非创建状态、审核失败状态出库单不允许删除");
		}
		wmsOutOrdersItemsMapper.deleteByMainId(id);
		wmsOutOrdersMapper.deleteById(id);
	}

	/**
	 * 批量删除
	 * @param idList
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			//出库单状态为创建状态或审核失败状态可以删除
			WmsOutOrders wmsOutOrders = wmsOutOrdersMapper.selectById(id);
			if(!(WarehouseDictEnum.OUTBOUND_CREATED.getCode().equals(wmsOutOrders.getStatus())
					|| WarehouseDictEnum.OUTBOUND_REJECTED.getCode().equals(wmsOutOrders.getStatus()))){
				throw new JeecgBootException("非创建状态、审核失败状态出库单不允许删除");
			}
			wmsOutOrdersItemsMapper.deleteByMainId(id.toString());
			wmsOutOrdersMapper.deleteById(id);
		}
	}

	public String generateOrderNumber() {
		//当前8位时间戳(年月日)"-"
		String time = DateUtils.now().substring(0, 10).replace("-", "");
		//key
		String key = "asn_number"+time;
		long incr = redisUtil.incr(key, 1);
		if(incr == 1){
			//设置过期时间，设置24小时+10秒的目的是避免并发产生订单号重复
			redisUtil.expire(key, 24*60*60+10);
		}
		//将incr组成4位字符串
		String incrStr = String.format("%04d", incr);
		String orderNumber = "OBD"+time+incrStr;

		return orderNumber;
	}

	/**
	 * 分配库存
	 * @param orderId 出库单ID
	 * @return 分配状态 ："ALLOCATED"-分配成功 分配失败 "ALLOCATED_FAILED"-分配失败
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String allocateStock(String orderId) {
		//获取入库单
		WmsOutOrders wmsOutOrders = wmsOutOrdersMapper.selectById(orderId);
		//出库单状态为审核通过状态或分配失败状态可以分配库存
		if (!(WarehouseDictEnum.OUTBOUND_APPROVED.getCode().equals(wmsOutOrders.getStatus()))
				&& !(WarehouseDictEnum.OUTBOUND_FAILED.getCode().equals(wmsOutOrders.getStatus()))) {
			throw new JeecgBootException("非审核通过状态、分配失败状态出库单不允许分配库存");
		}
		List<WmsOutOrdersItems> wmsOutOrdersItems = wmsOutOrdersItemsMapper.selectByMainId(orderId);
		//过滤出创建状态、分配失败状态的明细
		wmsOutOrdersItems = wmsOutOrdersItems.stream().filter(items -> WarehouseDictEnum.OUTBOUND_DETAIL_CREATED.getCode().equals(items.getStatus())
				|| WarehouseDictEnum.OUTBOUND_DETAIL_FAILED.getCode().equals(items.getStatus())).collect(Collectors.toList());
		//如果列表为空则返回分配失败
		if (wmsOutOrdersItems == null || wmsOutOrdersItems.size() == 0) {
			return WarehouseDictEnum.OUTBOUND_FAILED.getCode();
		}
		//遍历未分配的出库单明细
		for (WmsOutOrdersItems outOrdersItems : wmsOutOrdersItems) {
			//查询该明细对应商品在仓库的可用库存列表，通过商品id，仓库id，批次号，查询按过期时间升序，入库时间升序排序
			List<WmsInventory> wmsInventories = iWmsInventoryService.selectAvailableBySku(wmsOutOrders.getWarehouseId(), outOrdersItems);
			//拿着查到的库存列表进行库存分配
			String status = allocateStockToItems(outOrdersItems, wmsInventories);
//			if (status.equals(WarehouseDictEnum.OUTBOUND_DETAIL_FAILED.getCode())) {
//				throw new JeecgBootException("分配失败");
//			}
		}
		//更新出库单的状态为分配完毕
		String allocateStatus = updateAllocateStatus(orderId);
		return allocateStatus;
	}


	/**
	 * 更新出库单分配状态
	 */
	@Override
	public String updateAllocateStatus(String orderId) {
		//查询出库单明细
		List<WmsOutOrdersItems> skuItems = wmsOutOrdersItemsService.selectByMainId(orderId);
		//skuItem中的状态有一个是分配失败则出库单的状态为分配失败，否则分配成功，用stream流实现
		WarehouseDictEnum allocateStatus = skuItems.stream()
				.filter(item -> WarehouseDictEnum.OUTBOUND_DETAIL_FAILED.getCode().equals(item.getStatus()))
				.findAny()
				.map(item -> WarehouseDictEnum.OUTBOUND_FAILED)
				.orElse(WarehouseDictEnum.OUTBOUND_ALLOCATED);

		wmsOutOrdersMapper.update(null,
				new LambdaUpdateWrapper<WmsOutOrders>().eq(WmsOutOrders::getId,orderId)
						.and(wq->wq.eq(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_APPROVED.getCode())
								.or()
								.eq(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_FAILED.getCode()))
						.set(WmsOutOrders::getStatus,allocateStatus.getCode()));
		//返回分配状态
		return allocateStatus.getCode();
	}

	/**
	 * 更新出库单拣货状态
	 */
	public void updatePickStatus(List<String> orderIds){
		//根据出库单id查询状态非拣货完成的记录
		List<WmsOutOrdersItems> wmsOutOrdersItemsNotPicked = wmsOutOrdersItemsService.selectNotPickedOrderItemsByOrderId(orderIds);
		//提取出未分拣的出库单ID
		List<String> orderIdsNotPicked = wmsOutOrdersItemsNotPicked.stream().map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
		//取出orderIds中不包含orderIdsNotPicked的记录，即取出分拣完成的出库单
		List<String> orderIdsPicked = orderIds.stream()
				.filter(orderId -> !orderIdsNotPicked.contains(orderId))
				.collect(Collectors.toList());
		if(orderIdsPicked.size()<=0){
			return;
		}
		//更新 出库单 的拣货状态为拣货完成，由拣货中更新中拣货完成
		LambdaUpdateWrapper<WmsOutOrders> set = new LambdaUpdateWrapper<WmsOutOrders>()
				.in(WmsOutOrders::getId, orderIdsPicked)
				.eq(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_PICKING.getCode())
				.set(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_PICKED.getCode());
		int update = wmsOutOrdersMapper.update(null, set);
		if(update<=0){
			throw new JeecgBootException("为出库单拣货数量及状态失败!");
		}
	}

	/**
	 * 提交审核
	 * @param id
	 */
	public void submitAudit(String id){
		//如果没有选择出库单则返回错误
		if( oConvertUtils.isEmpty(id)){
			//抛出异常
			throw new JeecgBootException("请选择要审核的出库单");
		}
		WmsOutOrders wmsOutOrdersEntity = getById(id);
		if(wmsOutOrdersEntity==null) {
			//异常
			throw new JeecgBootException("出库单不存在");
		}
		Long count = wmsOutOrdersItemsMapper.selectCount(new LambdaQueryWrapper<WmsOutOrdersItems>()
				.eq(WmsOutOrdersItems::getOrderId, id));
		//如果出库单的出库明细为空，则不给提交审核
		if (count == null || count <= 0) {
			throw new JeecgBootException("未添加出库单明细");
		}
		//当前状态只能是创建状态、审核失败状态才允许提交审核
		if(!(WarehouseDictEnum.OUTBOUND_CREATED.getCode().equals(wmsOutOrdersEntity.getStatus())
				|| WarehouseDictEnum.OUTBOUND_REJECTED.getCode().equals(wmsOutOrdersEntity.getStatus()))){
			throw new JeecgBootException("非初始状态、审核失败状态出库单不允许审核");
		}
		//更新状态为提交审核
		wmsOutOrdersEntity.setStatus(WarehouseDictEnum.OUTBOUND_SUBMIT_AUDIT.getCode());
		updateById(wmsOutOrdersEntity);
	}

	@Override
	public void audit(WmsOutOrders wmsOutOrders) {
		//如果没有选择出库单则返回错误
		if(oConvertUtils.isEmpty(wmsOutOrders.getId())){
			//抛出异常
			throw new JeecgBootException("请选择要审核的出库单");
		}
		WmsOutOrders wmsOutOrdersEntity = getById(wmsOutOrders.getId());
		if(wmsOutOrdersEntity==null) {
			//异常
			throw new JeecgBootException("出库单不存在");
		}
		//当前状态只能是提交审核状态
		if(!WarehouseDictEnum.OUTBOUND_SUBMIT_AUDIT.getCode().equals(wmsOutOrdersEntity.getStatus())){
			//异常
			throw new JeecgBootException("当前状态是提交审核状态方可进行审核");
		}
		//校验出库单的完整性

		//更新状态为审核通过或审核不通过
		wmsOutOrdersEntity.setStatus(wmsOutOrders.getStatus());
		updateById(wmsOutOrdersEntity);
	}

	/**
	 * 分配库存
	 * @param item
	 * @param availableStocks
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String allocateStockToItems(WmsOutOrdersItems item, List<WmsInventory> availableStocks) {
		//获取期望出库商品数
		Integer expectedQuantity = item.getExpectedQuantity();
		//遍历库存表
		for (WmsInventory stocks : availableStocks) {
			//已分配的数量
			Integer allocatedQuantity = item.getAllocatedQuantity();
			//剩余分配数量
			Integer availableQuantity = expectedQuantity - allocatedQuantity;
			if (availableQuantity <= 0) {
				break;
			}
			//可分配的库存数量
			Integer stockQuantity = stocks.getAvailableQuantity();
			//本次要分配的库存数量
			Integer min = Math.min(availableQuantity, stockQuantity);
			//向库存分配明细表中添加库存分配记录
			WmsOutOrdersAllocation wmsOutOrdersAllocation = new WmsOutOrdersAllocation();
			wmsOutOrdersAllocation.setOrderId(item.getOrderId());
			wmsOutOrdersAllocation.setOrderItemId(item.getId());
			wmsOutOrdersAllocation.setSkuId(item.getSkuId());
			wmsOutOrdersAllocation.setLocationCode(stocks.getLocationCode());
			wmsOutOrdersAllocation.setBatchNumber(stocks.getBatchNumber());
			wmsOutOrdersAllocation.setContainerCode(stocks.getContainerCode());
			wmsOutOrdersAllocation.setAllocatedQuantity(min);
			wmsOutOrdersAllocation.setInventoryId(stocks.getId());
			wmsOutOrdersAllocation.setPickedQuantity(0);
			boolean save = outOrdersAllocationService.save(wmsOutOrdersAllocation);
			if (!save) {
				throw new JeecgBootException("保存库存分配明细失败");
			}
			//锁定库存
			WmsInventoryTransParam wmsInventoryTransParam = new WmsInventoryTransParam();
			wmsInventoryTransParam.setProductId(item.getSkuId());
			wmsInventoryTransParam.setExecQuantity(min);
			wmsInventoryTransParam.setWarehouseId(stocks.getWarehouseId());
			wmsInventoryTransParam.setSourceLocationCode(stocks.getLocationCode());
			wmsInventoryTransParam.setBatchNumber(stocks.getBatchNumber());
			wmsInventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_ALLOCATION.getCode());
			wmsInventoryTransParam.setIsSellable(stocks.getIsSellable());
			wmsInventoryTransByAllocation.transfer(wmsInventoryTransParam);
			//更新出库单的已分配数量 出库单明细的已分配数量
			wmsOutOrdersItemsService.allocateStock(item.getId(),min);
			//重新查询出库单明细
			item = wmsOutOrdersItemsService.getById(item.getId());
		}
		//更新出库单明细的分配状态
		String status = wmsOutOrdersItemsService.updateAllocateStatus(item.getId());
		return status;
	}

	/**
	 * 根据出库单id查询出库单明细
	 */
	@Override
	public List<WmsOutOrders> selectByMainId(String mainId) {
		return wmsOutOrdersMapper.selectByMainId(mainId);
	}

	@Override
	public List<WmsOutOrders> selectByWaveId(String waveId) {
		//根据波次id查询其下的出库单id
		List<WmsOutOrders> wmsOutOrders = wmsOutOrdersMapper.selectList(new LambdaQueryWrapper<WmsOutOrders>().eq(WmsOutOrders::getWaveId, waveId));
		return wmsOutOrders;
	}

	/**
	 * 获取未分配波次的出库单,状态为已分配 ALLOCATED
	 */
	public List<WmsOutOrders> selectUnassignedOrders(String warehouseId){
		return wmsOutOrdersMapper.selectUnassignedOrders(warehouseId);
	}
	/**
	 * 根据出库单id更新订单的波次ID
	 */
	public void batchUpdateWaveId( List<String> orderIds, String waveId,String shipmentStrategy){
		wmsOutOrdersMapper.batchUpdateWaveId(orderIds, waveId,shipmentStrategy);
	}

}