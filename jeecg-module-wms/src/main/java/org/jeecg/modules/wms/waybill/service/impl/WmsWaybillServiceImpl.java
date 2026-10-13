package org.jeecg.modules.wms.waybill.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.aspect.annotation.Lock;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByDeliver;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import org.jeecg.modules.wms.shipment.mapper.WmsShipmentDetailMapper;
import org.jeecg.modules.wms.shipment.mapper.WmsShipmentMapper;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentDetailService;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
import org.jeecg.modules.wms.shipment.strategy.ShipmentGenerationStrategy;
import org.jeecg.modules.wms.shipment.strategy.ShipmentStrategyFactory;
import org.jeecg.modules.wms.shipment.vo.ShipmentGenerationResult;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.jeecg.modules.wms.waybill.service.IWmsSfService;
import org.jeecg.modules.wms.waybill.service.IWmsWaybillService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 运单服务接口
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
@Service
@Slf4j
public class WmsWaybillServiceImpl  implements IWmsWaybillService {

	@Autowired
	private IWmsShipmentService wmsShipmentService;

	@Autowired
	private IWmsOutOrdersItemsService wmsOutOrdersItemsService;

	@Autowired
	private IWmsTasksRecordsService wmsTasksRecordsService;

	@Autowired
	private WmsWaybillServiceImpl owner;

	@Autowired
	private IWmsOutOrdersService wmsOutOrdersService;
	@Autowired
	private IWmsWaveMasterService wmsWaveMasterService;

	@Autowired
	private WmsInventoryTransByDeliver wmsInventoryTransByDeliver;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private IWmsSfService sfService;


	/**
	 * 发货
	 * @param waveId
	 */
	@Transactional(rollbackFor = Exception.class)
	public void send(String waveId){
		//根据波次id查询出库单
		List<WmsOutOrders> wmsOutOrders = wmsOutOrdersService.selectByMainId(waveId);
		//判断wmsOutOrders中是否存在未创建运单的出库单
		if(wmsOutOrders.stream().anyMatch(order -> order.getCreatedWaybill().equals("0"))){
			throw new JeecgBootException("波次下存在未创建运单的出库单暂时无法发货！");
		}
		//更新订单的状态为已发货
		LambdaUpdateWrapper<WmsOutOrders> eq = new LambdaUpdateWrapper<WmsOutOrders>()
				.in(WmsOutOrders::getWaveId, waveId)
				.set(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_SHIPPED.getCode())
				.eq(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_PACKED.getCode());
		boolean update = wmsOutOrdersService.update(null, eq);
		if(!update){
			throw new JeecgBootException("更新订单状态为已发货失败");
		}
		//更新波次状态为已发货
		LambdaUpdateWrapper<WmsWaveMaster> eq1 = new LambdaUpdateWrapper<WmsWaveMaster>()
				.eq(WmsWaveMaster::getId, waveId)
				.set(WmsWaveMaster::getStatus, WarehouseDictEnum.WAVE_SHIPPED.getCode())
				.eq(WmsWaveMaster::getStatus, WarehouseDictEnum.WAVE_PACKED.getCode());
		boolean update1 = wmsWaveMasterService.update(null, eq1);
		if(!update1){
			throw new JeecgBootException("更新波次状态为已发货失败");
		}
		//更新包裹状态为已发货
		//从wmsOutOrders中找到未打包的出库单并提取订单id
		List<String> orderIds = wmsOutOrders.stream().map(WmsOutOrders::getId).collect(Collectors.toList());
		LambdaUpdateWrapper<WmsShipment> eq2 = new LambdaUpdateWrapper<WmsShipment>()
				.in(WmsShipment::getOrderId, orderIds)
				.set(WmsShipment::getStatus, WarehouseDictEnum.PACKAGE_STATUS_SHIPPED.getCode())
				.eq(WmsShipment::getStatus, WarehouseDictEnum.PACKAGE_STATUS_PACKED.getCode());
		boolean update2 = wmsShipmentService.update(null, eq2);
		if(!update2){
			throw new JeecgBootException("更新包裹状态失败");
		}

		//更新出库单明细状态为已发货
		LambdaUpdateWrapper<WmsOutOrdersItems> eq3 = new LambdaUpdateWrapper<WmsOutOrdersItems>()
				.in(WmsOutOrdersItems::getOrderId, orderIds)
				.set(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_DETAIL_SHIPPED.getCode())
				.eq(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_DETAIL_PACKED.getCode());
				boolean update3 = wmsOutOrdersItemsService.update(null, eq3);
				if(!update3){
					throw new JeecgBootException("更新出库单明细状态为已发货失败");
				}

		//============================扣减库存==========================
		//找到拣货储位
		List<WmsTasksRecords> list = wmsTasksRecordsService.list(new LambdaQueryWrapper<WmsTasksRecords>().eq(WmsTasksRecords::getWaveOrderId, waveId));
		WmsTasksRecords wmsTasksRecords = list.get(0);
		String targetLocationCode = wmsTasksRecords.getTargetLocationCode();
		//遍历出库单扣减库存
		for (WmsOutOrders outOrder : wmsOutOrders){
			//查询出库单明细
			List<WmsOutOrdersItems> wmsOutOrdersItems = wmsOutOrdersItemsService.selectByMainId(outOrder.getId());
			for (WmsOutOrdersItems item : wmsOutOrdersItems){
				WmsInventoryTransParam inventoryTransParam = new WmsInventoryTransParam();
				inventoryTransParam.setProductId(item.getSkuId());
				inventoryTransParam.setExecQuantity(item.getPackedQuantity());//打包数量
				inventoryTransParam.setSourceLocationCode(targetLocationCode);
				inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
//				inventoryTransParam.setBatchNumber(item.getBatchNumber());
				inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_OUTBOUND.getCode());
				//上架时间
				inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationTime());
				wmsInventoryTransByDeliver.transfer(inventoryTransParam);
			}
		}



	}


	/**
	 * 根据波次id查询运单id
	 * @param waveId 波次id
	 * @return 运单id
	 */
	public List<String> selectWaybillNosByWaveId(String waveId){
		//根据波次id查询出库单
		List<WmsOutOrders> wmsOutOrders = wmsOutOrdersService.selectByMainId(waveId);
		//判断wmsOutOrders中是否存在未创建运单的出库单
		if(wmsOutOrders.stream().anyMatch(order -> order.getCreatedWaybill().equals("0"))){
			throw new JeecgBootException("波次下存在未创建运单的出库单，请稍后再试！");
		}
		//根据订单id查询包裹并得到运单号
		LambdaQueryWrapper<WmsShipment> in = new LambdaQueryWrapper<WmsShipment>()
				.in(WmsShipment::getOrderId, wmsOutOrders.stream().map(WmsOutOrders::getId).collect(Collectors.toList()));
		List<WmsShipment> wmsShipments = wmsShipmentService.list(in);
		//获取运单号
		List<String> waybillNos = wmsShipments.stream().map(WmsShipment::getTrackingNo).collect(Collectors.toList());
		return waybillNos;
	}

	/**
	 * 请求顺丰生成指定订单的运单
	 * 首先请求顺丰下单，成功后生成运单
	 */
	@Lock(formatter = "ORDERS:CREATEWAYBILL:LOCK:#{orderId}",  startDog=true)
	public void generateWaybillForOrder(String orderId) throws Exception {
		//使用redisson分布式锁控制本方法
		//查询出库单
		WmsOutOrders wmsOutOrders = wmsOutOrdersService.getById(orderId);
		//如果状态不是打包完成，则不能生成运单
		if (!wmsOutOrders.getStatus().equals(WarehouseDictEnum.OUTBOUND_PACKED.getCode())) {
			throw new JeecgBootException("出库单" + wmsOutOrders.getOrderNo() + "状态不是打包完成，不能生成运单");
		}
		//如果运单已经生成，则不能重复生成
		if (wmsOutOrders.getCreatedWaybill().equals("1")) {
			throw new JeecgBootException("出库单" + wmsOutOrders.getOrderNo() + "已生成运单，不能重复生成");
		}
		//根据出库单id查询下边的包裹
		List<WmsShipment> wmsShipments = wmsShipmentService.selectByOrderId(orderId);
		//得到了包裹数
		int size = wmsShipments.size();
		if (size == 0) {
			throw new JeecgBootException("出库单" + wmsOutOrders.getOrderNo() + "没有包裹，不能生成运单");
		}
		//调用顺丰的接口生成运单
		List<String> stringList = sfService.requestSfCreateOrder(wmsOutOrders, wmsShipments);
		//拿到运单号存到包裹表
		owner.saveWaybill(stringList, wmsShipments);

	}

//	public void generateWaybillForOrder(String orderId) throws Exception {
//		//使用redisson分布式锁控制本方法
//		//定义锁名称
//		String lockName = "ORDERS:CREATEWAYBILL:LOCK:"+orderId;
//		//创建锁的步骤已经注入到这个redissionClient里面了，这里就不用在创建了
//		RLock lock = redissonClient.getLock(lockName);
//		//尝试获取锁
//		boolean b = lock.tryLock(5, -1, TimeUnit.SECONDS);
//		//获取锁成功
//		if (b) {
//			try {
//				//查询出库单
//				WmsOutOrders wmsOutOrders = wmsOutOrdersService.getById(orderId);
//				//如果状态不是打包完成，则不能生成运单
//				if (!wmsOutOrders.getStatus().equals(WarehouseDictEnum.OUTBOUND_PACKED.getCode())) {
//					throw new JeecgBootException("出库单"+wmsOutOrders.getOrderNo()+"状态不是打包完成，不能生成运单");
//				}
//				//如果运单已经生成，则不能重复生成
//				if (wmsOutOrders.getCreatedWaybill().equals("1")) {
//					throw new JeecgBootException("出库单"+wmsOutOrders.getOrderNo()+"已生成运单，不能重复生成");
//				}
//				//根据出库单id查询下边的包裹
//				List<WmsShipment> wmsShipments = wmsShipmentService.selectByOrderId(orderId);
//				//得到了包裹数
//				int size = wmsShipments.size();
//				if (size == 0) {
//					throw new JeecgBootException("出库单"+wmsOutOrders.getOrderNo()+"没有包裹，不能生成运单");
//				}
//				//调用顺丰的接口生成运单
//				List<String> stringList = sfService.requestSfCreateOrder(wmsOutOrders, wmsShipments);
//				//拿到运单号存到包裹表
//				owner.saveWaybill(stringList,wmsShipments);
//			} finally {
//				//释放锁
//				lock.unlock();
//			}
//		}
//
//	}

	/**
	 * 保存运单号
	 * @param waybillNos 运单号
	 * @param shipments 包裹
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveWaybill(List<String> waybillNos,List<WmsShipment> shipments){
		//如果运单号的数量和包裹数量不一致不能继续，抛出异常
		if(waybillNos.size() != shipments.size()){
			throw new RuntimeException("运单数量和包裹数量不一致");
		}
		//将运单号依次保存到包裹中
		for (int i = 0; i < waybillNos.size(); i++) {
			LambdaUpdateWrapper<WmsShipment> set = new LambdaUpdateWrapper<WmsShipment>()
					.eq(WmsShipment::getId, shipments.get(i).getId())
					.set(WmsShipment::getTrackingNo, waybillNos.get(i));
			wmsShipmentService.update(null,set);
		}
		WmsShipment wmsShipment = shipments.get(0);
		//订单id
		String orderId = wmsShipment.getOrderId();
		//更新订单"createdWaybill"为1
		LambdaUpdateWrapper<WmsOutOrders> update = new LambdaUpdateWrapper<WmsOutOrders>()
				.eq(WmsOutOrders::getId, orderId)
				.set(WmsOutOrders::getCreatedWaybill, "1");
		wmsOutOrdersService.update(null, update);
	}



}
