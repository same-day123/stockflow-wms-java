package org.jeecg.modules.wms.wave.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersAllocationMapper;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import org.jeecg.modules.wms.wave.entity.WmsWaveSkuSummary;
import org.jeecg.modules.wms.wave.mapper.WmsWaveMasterMapper;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.jeecg.modules.wms.wave.service.IWmsWaveSkuSummaryService;
import org.jeecg.modules.wms.wave.strategy.IWaveStrategy;
import org.jeecg.modules.wms.wave.strategy.WaveCreateClient;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 波次主表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Service
public class WmsWaveMasterServiceImpl extends ServiceImpl<WmsWaveMasterMapper, WmsWaveMaster> implements IWmsWaveMasterService {

	@Autowired
	private WmsWaveMasterMapper wmsWaveMasterMapper;
	@Autowired
	private IWmsOutOrdersService wmsOutOrdersService;

	@Autowired
	private WmsOutOrdersAllocationMapper wmsOutOrdersAllocationMapper;

	@Autowired
	private IWmsWaveSkuSummaryService iWmsWaveSkuSummaryService;

	@Autowired
	private List<IWaveStrategy> strategies;

	@Override
	public IPage<WmsWaveMaster> queryList(WmsWaveMaster wmsWaveMaster, Integer pageNo, Integer pageSize) {
		//开启分页查询,当执行查询时，插件进行相关的sql拦截进行分页操作，返回一个page对象
		Page<WmsWaveMaster> page = PageHelper.startPage(pageNo, pageSize);
		List<WmsWaveMaster> list = wmsWaveMasterMapper.queryList(wmsWaveMaster);
		PageDTO<WmsWaveMaster> wmsWaveMasterPageDTO = new PageDTO<>();
		wmsWaveMasterPageDTO.setRecords(list);
		wmsWaveMasterPageDTO.setTotal(page.getTotal());
		wmsWaveMasterPageDTO.setSize(page.getPageSize());
		wmsWaveMasterPageDTO.setCurrent(page.getPageNum());
		wmsWaveMasterPageDTO.setPages(page.getPages());
		return wmsWaveMasterPageDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
//		wmsOutOrdersMapper.deleteByMainId(id);
		wmsWaveMasterMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
//			wmsOutOrdersMapper.deleteByMainId(id.toString());
			wmsWaveMasterMapper.deleteById(id);
		}
	}


//
//	/**
//	 * 处理订单波次分配
//	 */
//	public void process(List<WmsOutOrders> orders,
//						Map<String, List<WmsOutOrdersAllocation>> allocationsMap,
//						List<String> selectedStrategies) {
//		// 按优先级排序策略
//		this.strategies = strategies.stream()
//				.sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
//				.collect(Collectors.toList());
//		List<WmsOutOrders> remainingOrders = new ArrayList<>(orders);
//
//		// 只处理选中的策略并按优先级排序
//		List<IWaveStrategy> filteredStrategies = strategies.stream()
//				.filter(strategy -> selectedStrategies.contains(strategy.getStrategyType()))
//				.sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
//				.collect(Collectors.toList());
//
//		for (IWaveStrategy strategy : filteredStrategies) {
//			remainingOrders = strategy.process(remainingOrders, allocationsMap);
//			if (remainingOrders.isEmpty()) {
//				break;
//			}
//		}
//	}

	@Override
	@Transactional
	public WmsWaveMaster addWave(List<WmsOutOrders> orders, String strategyType,String shipmentStrategy) {
		if (orders.isEmpty()) {
			return null;
		}

		// 创建波次记录
		WmsWaveMaster wave = new WmsWaveMaster();
		wave.setWaveNo(generateWaveNo(strategyType));
		wave.setWaveRuleId(strategyType);
		wave.setStatus(WarehouseDictEnum.WAVE_CREATED.getCode());
		wave.setTotalOrders(orders.size());

		// 计算波次总商品数量和SKU种类数
		int totalQuantity = orders.stream().mapToInt(WmsOutOrders::getTotalQuantity).sum();
		wave.setTotalSkus(totalQuantity);
		//计算波次总商品种类数
		int totalItems = orders.stream().mapToInt(WmsOutOrders::getTotalSku).sum();
		wave.setTotalItems(totalItems);
		wave.setCreateTime(new Date());
		//仓库
		wave.setWarehouseId(orders.get(0).getWarehouseId());
		// 保存波次
		save(wave);

		// 根据出库单id更新订单的波次ID
		List<String> orderIds = orders.stream()
				.map(WmsOutOrders::getId)
				.collect(Collectors.toList());
		wmsOutOrdersService.batchUpdateWaveId(orderIds, wave.getId(), shipmentStrategy);

		return wave;
	}

	/**
	 * 更新波次主表的拣货状态，如果所有波次下出库单的状态为已拣货，则更新波次主表的拣货状态为拣货完成
	 * @param waveId 波次ID
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updatePickStatus(String waveId){
		//根据波次id查询波次主表
		WmsWaveMaster wave = getById(waveId);
		if (wave == null) {
			//抛出异常
			throw new JeecgBootException("波次不存在");
		}
		//根据波次id查询波次拣货任务明细
		List<WmsWaveSkuSummary> skuSummaries = iWmsWaveSkuSummaryService.selectByWaveId(waveId);

		if (skuSummaries.isEmpty()) {
			return;
		}
		//判断所有波次下出库单的状态是否为已拣货
		boolean allPicked = skuSummaries.stream()
				.allMatch(skuSummary ->
						WarehouseDictEnum.WAVESKU_PICKED.getCode()
								.equals(skuSummary.getStatus()));
		if(allPicked){
			//计算总拣货数量
			int pickedQuantitySum = skuSummaries.stream()
					.mapToInt(WmsWaveSkuSummary::getPickedQuantity)
					.sum();
			if(pickedQuantitySum <=0){
				throw new JeecgBootException("波次拣货数量必须大于0");
			}
			wave.setPickedQuantity(pickedQuantitySum);
			wave.setStatus(WarehouseDictEnum.WAVE_PICKED.getCode());
			boolean b = updateById(wave);
			if (!b) {
				throw new JeecgBootException("更新波次拣货数量失败!");
			}
		}

	}

	/**
	 * 向波次表增加分拣数量
	 */
	@Override
	public void addSortingQuantity(String waveId, Integer sortingQuantity) {
		//查询波次
		WmsWaveMaster wave = getById(waveId);
		if (wave == null) {
			throw new JeecgBootException("波次不存在");
		}
		//拣货数量
		Integer pickedQuantity = wave.getPickedQuantity();
		LambdaUpdateWrapper<WmsWaveMaster> updateWrapper = new LambdaUpdateWrapper<WmsWaveMaster>()
				.eq(WmsWaveMaster::getId, waveId)
				.setSql("sorting_quantity = sorting_quantity + {0}", sortingQuantity)
				.le(WmsWaveMaster::getSortingQuantity, pickedQuantity-sortingQuantity);
		boolean update = update(null, updateWrapper);
		if (!update) {
			throw new JeecgBootException("更新分拣数量失败!");
		}
	}


	@Override
	public void updateCompleteSortingOrderQuantity(List<String> waveIds) {
		for (String waveId : waveIds) {
			//根据波次id查询出库单，并且找到出库单状态为拣货完成的数量,一个出库单算一个数量
			List<WmsOutOrders> orders = wmsOutOrdersService.selectByWaveId(waveId);
			if (orders.isEmpty()) {
				return;
			}
			long count = orders.stream()
					.filter(order -> WarehouseDictEnum.OUTBOUND_PICKED.getCode().equals(order.getStatus()))
					.count();
			WmsWaveMaster wave = new WmsWaveMaster();
			wave.setId(waveId);
			wave.setSortingOrderQuantity((int) count);
			updateById(wave);
		}

	}
	@Override
	@Transactional
	public void createWave(String warehouseId,List<String> strategyCodes) {
		// 1. 获取已分配库存且未创建波次的出库单
		List<WmsOutOrders> unassignedOrders = wmsOutOrdersService.selectUnassignedOrders(warehouseId);
		if (unassignedOrders.isEmpty()) {
			return;
		}

		// 2. 获取这些出库单的分配明细
		List<String> orderIds = unassignedOrders.stream()
				.map(WmsOutOrders::getId)
				.collect(Collectors.toList());
		Map<String, List<WmsOutOrdersAllocation>> allocationsMap = wmsOutOrdersAllocationMapper.selectAllocationsByOrderIds(orderIds)
				.stream()
				.collect(Collectors.groupingBy(WmsOutOrdersAllocation::getOrderId));

		// 3. 使用责任链处理波次创建
		WaveCreateClient waveCreateClient = new WaveCreateClient(strategies, strategyCodes);
		waveCreateClient.process(unassignedOrders,allocationsMap);
	}
//	public void updatePickStatus(String waveId){
//		WmsWaveMaster wave = getById(waveId);
//		if (wave == null) {
//			//抛出异常
//			throw new JeecgBootException("波次不存在");
//		}
//		List<WmsOutOrders> orders = wmsOutOrdersService.selectByWaveId(waveId);
//		if (orders.isEmpty()) {
//			return;
//		}
//		boolean allPicked = orders.stream()
//				.allMatch(order -> WarehouseDictEnum.OUTBOUND_PICKED.getCode().equals(order.getStatus()));
//		if(allPicked){
//			wave.setStatus(WarehouseDictEnum.WAVE_PICKED.getCode());
//			updateById(wave);
//		}
//
//	}


	private String generateWaveNo(String strategyType) {
		return "WAVE-" + strategyType + "-" + System.currentTimeMillis();
	}

}
