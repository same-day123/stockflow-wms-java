package org.jeecg.modules.wms.wave.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
//import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByPick;
import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByPick;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.mapper.WmsOutOrdersAllocationMapper;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersAllocationService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.pickroute.MultiProductPickingRouteOptimized;
import org.jeecg.modules.wms.pickroute.dto.Location;
import org.jeecg.modules.wms.pickroute.dto.Node;
import org.jeecg.modules.wms.pickroute.dto.Product;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.jeecg.modules.wms.wave.entity.WmsShortageRegistration;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import org.jeecg.modules.wms.wave.entity.WmsWaveSkuSummary;
import org.jeecg.modules.wms.wave.mapper.WmsWaveMasterMapper;
import org.jeecg.modules.wms.wave.service.IPickingTasksService;
import org.jeecg.modules.wms.wave.service.IWmsShortageRegistrationService;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.jeecg.modules.wms.wave.service.IWmsWaveSkuSummaryService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.jeecg.modules.wms.wmstask.service.impl.WmsTasksRecordsServiceImpl;
import org.jeecg.modules.wms.wmstask.service.impl.WmsTasksServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


import static org.jeecg.modules.wms.pickroute.MultiProductPickingRouteOptimized.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 拣货服务类
 * @date 2025/6/6 8:31
 */
@Service
public class PickingTasksServiceImpl implements IPickingTasksService {
    @Autowired
    private IWmsTasksService wmsTasksService;
    @Autowired
    private IWmsWaveMasterService wmsWaveMasterService;
    @Autowired
    private IWmsOutOrdersService wmsOutOrdersService;

    @Autowired
    private IWmsProductsService wmsProductsService;

    @Autowired
    private IWmsOutOrdersItemsService wmsOutOrdersItemsService;

    @Autowired
    private WmsOutOrdersAllocationMapper wmsOutOrdersAllocationMapper;

    @Autowired
    private IWmsOutOrdersAllocationService wmsOutOrdersAllocationService;

    @Autowired
    private IWmsWaveSkuSummaryService wmsWaveSkuSummaryService;
    @Autowired
    private WmsInventoryTransByPick inventoryTransByPick;


//    @Autowired
//    private WmsInventoryTransByPick inventoryTransByPick;

    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;

    @Autowired
    private IWmsShortageRegistrationService wmsShortageRegistrationService;

    @Autowired
    private IWmsStorageLocationsService wmsStorageLocationsService;

    //储区service
    @Autowired
    private IWmsStorageZonesService wmsStorageZonesService;

    /**
     * 生成拣货任务
     * @param waveIds 波次id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPickTask(List<String> waveIds) {
        //遍历波次id集合，调用创建拣货任务方法
        for (String waveId : waveIds) {
            createPickTask(waveId);
        }
    }

    /**
     * 根据波次统计出库单的商品分配数量,添加到波次拣货明细表
     * @param waveId 波次id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void createPickTask(String waveId) {
        WmsWaveMaster wmsWaveMaster = wmsWaveMasterService.getById(waveId);
        //将波次状态更新为拣货中
        LambdaUpdateWrapper<WmsWaveMaster> set = new LambdaUpdateWrapper<WmsWaveMaster>()
                .eq(WmsWaveMaster::getId, waveId)
                .eq(WmsWaveMaster::getStatus, WarehouseDictEnum.WAVE_CREATED.getCode())
                .set(WmsWaveMaster::getStatus, WarehouseDictEnum.WAVE_PICKING.getCode());
        boolean update = wmsWaveMasterService.update(null, set);
        if (!update) {
            throw new JeecgBootException("波次状态更新失败");
        }
        //通过波次id来查询出库单的分配明细
        IPage<WmsOutOrdersAllocation> wmsOutOrdersAllocationIPage = wmsOutOrdersAllocationService.selectAllocatedQuantityByWaveId(waveId, 1, 100);
        List<WmsOutOrdersAllocation> records = wmsOutOrdersAllocationIPage.getRecords();
        //还要判断这个集合是否为空，如果为空那么就直接返回
        if (records.size() == 0) {
            throw  new JeecgBootException("没有找到波次下的库存分配明细");
        }
        //定义一个页码
        int pageNo = 1;
        while (true) {
            //将records转化成List<WmsWaveSkuSummary>
            List<WmsWaveSkuSummary> collect = records.stream().map(wmsOutOrdersAllocation -> {
                WmsWaveSkuSummary wmsWaveSkuSummary = new WmsWaveSkuSummary();
                wmsWaveSkuSummary.setWaveId(waveId);//波次id
                wmsWaveSkuSummary.setSkuId(wmsOutOrdersAllocation.getSkuId());//商品id
                wmsWaveSkuSummary.setOwnerId(wmsOutOrdersAllocation.getOwnerId());//货主id
                wmsWaveSkuSummary.setProductBarcode(wmsOutOrdersAllocation.getProductBarcode());//商品条码
                wmsWaveSkuSummary.setLocationCode(wmsOutOrdersAllocation.getLocationCode());//储位编码
                wmsWaveSkuSummary.setBatchNumber(wmsOutOrdersAllocation.getBatchNumber());//批次号
                wmsWaveSkuSummary.setAllocatedQuantity(wmsOutOrdersAllocation.getAllocatedQuantity());//分配数量
                wmsWaveSkuSummary.setPickedQuantity(0);//拣货数量
                wmsWaveSkuSummary.setStatus(WarehouseDictEnum.WAVE_PICKING.getCode());//状态
                return wmsWaveSkuSummary;
            }).collect(Collectors.toList());
            //根据统计的库存分配明细来向波次拣货明细wms_wave_sku_summary中插入记录
            boolean b = wmsWaveSkuSummaryService.saveBatch(collect);
            if (!b) {
                throw new JeecgBootException("波次拣货明细保存失败");
            }

            //将List<WmsWaveSkuSummary>转换成List<WmsTasks>
            List<WmsTasks> tasksList = collect.stream().map(wmsWaveSkuSummary -> {
                //创建收货任务
                WmsTasks tasks = new WmsTasks();
                //任务类型,拣货任务
                tasks.setTaskType(WarehouseDictEnum.TASK_TYPE_PICKING.getCode());
                //任务状态，已创建
                tasks.setTaskStatus(WarehouseDictEnum.TASK_STATUS_CREATED.getCode());
                //任务创建时间
                tasks.setCreateTime(new Date());
                //任务号
                tasks.setTaskNumber(wmsTasksService.generateTaskCode());
                //波次id
                tasks.setWaveOrderId(waveId);
                //商品id
                tasks.setProductId(wmsWaveSkuSummary.getSkuId());
                //商品待拣货数量
                tasks.setQuantity(wmsWaveSkuSummary.getAllocatedQuantity());
                //来源储位编码
                tasks.setSourceLocationCode(wmsWaveSkuSummary.getLocationCode());
                //来源仓库
                tasks.setSourceWarehouseId(wmsWaveMaster.getWarehouseId());
                //目的仓库
                tasks.setTargetWarehouseId(wmsWaveMaster.getWarehouseId());
                //商品批次号
                tasks.setBatchNumber(wmsWaveSkuSummary.getBatchNumber());
                //目的储位编码
                tasks.setTargetLocationCode("");
                //波次拣货明细id
                tasks.setWaveSkuSummaryId(wmsWaveSkuSummary.getId());
                //完成数量为0
                tasks.setCompletedQuantity(0);
                return tasks;
            }).collect(Collectors.toList());
            //将List<WmsWaveSkuSummary>插入到tasksList任务表中
            boolean b1 = wmsTasksService.saveBatch(tasksList);
            if (!b1) {
                throw new JeecgBootException("任务保存失败");
            }
            //继续查询库存分配明细下一页
            wmsOutOrdersAllocationIPage = wmsOutOrdersAllocationService.selectAllocatedQuantityByWaveId(waveId,++pageNo,100);
            List<WmsOutOrdersAllocation> records1 = wmsOutOrdersAllocationIPage.getRecords();
            //还要判断这个集合是否为空，如果为空那么就直接返回
            if (records1.size() == 0) {
                break;
            }
            records = records1;
        }

        //根据波次拣货明细来向任务表中插入拣货任务

        //更新出库单已分配为拣货中
        //sql update wms_out_orders set status = 拣货中 where wave_id = ? and status = 已分配
        LambdaUpdateWrapper<WmsOutOrders> set1 = new LambdaUpdateWrapper<WmsOutOrders>()
                .eq(WmsOutOrders::getWaveId, waveId)
                .eq(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_ALLOCATED.getCode())
                .set(WmsOutOrders::getStatus, WarehouseDictEnum.OUTBOUND_PICKING.getCode());
        boolean update2 = wmsOutOrdersService.update(set1);
        if(!update2){
            throw new JeecgBootException("波次状态更新失败");
        }
        //通过waveId查询波次下的出库单
        List<WmsOutOrders> wmsOutOrders = wmsOutOrdersService.selectByWaveId(waveId);
        //取出出库单的下的id
        List<String> orderIds = wmsOutOrders.stream().map(WmsOutOrders::getId).collect(Collectors.toList());
        //更新出库单明细为拣货中
        //sql update wms_out_orders_items set status = 拣货中 where order_id in ? and status = 已分配
        LambdaUpdateWrapper<WmsOutOrdersItems> set2 = new LambdaUpdateWrapper<WmsOutOrdersItems>()
                .in(WmsOutOrdersItems::getOrderId, orderIds)
                .eq(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_ALLOCATED.getCode())
                .set(WmsOutOrdersItems::getStatus, WarehouseDictEnum.OUTBOUND_PICKING.getCode());
        boolean update3 = wmsOutOrdersItemsService.update(set2);
        if(!update3){
            throw new JeecgBootException("波次状态更新失败");
        }

    }
    /**
     * 完成拣货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePickTask(List<String> waveIds){
        for (String waveId : waveIds) {
            completePickTask(waveId);
        }
    }

    /**
     * 完成拣货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePickTask(String waveId){
        //查询该波次下的拣货任务列表
        LambdaQueryWrapper<WmsTasks> eq = new LambdaQueryWrapper<WmsTasks>()
                .eq(WmsTasks::getWaveOrderId, waveId);
        List<WmsTasks> wmsTasks = wmsTasksService.list(eq);

        //找没有完成的拣货任务
        List<WmsTasks> wmsTasks1 = wmsTasks.stream()
                .filter(wmsTask -> !WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode().equals(wmsTask.getTaskStatus()))
                .collect(Collectors.toList());
        //遍历未完成任务列表，判断是否进行缺货登记
        for (WmsTasks wmsTask : wmsTasks1) {
            if (wmsTask.getCompletedQuantity() < wmsTask.getQuantity()) {
                //剩余拣货数量
                int remainingQuantity = wmsTask.getQuantity() - wmsTask.getCompletedQuantity();
                //查询缺货登记
                WmsShortageRegistration shortageRegistration = wmsShortageRegistrationService.getById(wmsTask.getId());
                if (shortageRegistration == null || shortageRegistration.getShortageQuantity()==null) {
                    throw new JeecgBootException("任务"+wmsTask.getTaskNumber()+"未进行缺货登记");
                }
                //如果缺货登记数量不等于剩余拣货数量
                if (shortageRegistration.getShortageQuantity() != remainingQuantity) {
                    throw new JeecgBootException("任务"+wmsTask.getTaskNumber()+"的缺货登记数量与剩余拣货数量不一致");
                }
                //将任务状态更新为已拣货
                wmsTask.setTaskStatus(WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode());
                boolean update = wmsTasksService.updateById(wmsTask);
                if(! update){
                    throw new JeecgBootException("更新任务状态失败");
                }
            }
        }
        //只要存在一个未拣货完成则抛出异常
        boolean b = wmsTasks.stream().anyMatch(wmsTask -> !WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode().equals(wmsTask.getTaskStatus()));
        if (b) {
            throw new RuntimeException("波次下存在未完成拣货任务");
        }
        //更新波次下拣货明细表的状态为已拣货
        wmsWaveSkuSummaryService.updatePickedStatus(waveId);
        //更新波次主表的拣货状态，如果波次下拣货明细的状态为已拣货，则更新波次主表的拣货状态为拣货完成
        wmsWaveMasterService.updatePickStatus(waveId);
    }

    /**
     * 拣货校验：已拣货数量+缺货数量不能大于计划数量
     * @param pickTaskId 拣货任务id
     */
    @Override
    public void checkPickedQuantity(String pickTaskId) {
        //根据任务id查询任务
        WmsTasks wmsTasks = wmsTasksService.getById(pickTaskId);
        //根据任务id查询任务记录表
        List<WmsTasksRecords> wmsTasksRecords1 = wmsTasksRecordsService.getBaseMapper().selectList(new LambdaQueryWrapper<WmsTasksRecords>().eq(WmsTasksRecords::getTaskId, pickTaskId));
        //获取已拣货总数量
        Integer pickedQuantity = wmsTasksRecords1.stream()
                .mapToInt(WmsTasksRecords::getExecQuantity)
                .sum();
        //根据任务id查询缺货记录
        List<WmsShortageRegistration> wmsShortageRegistrations = wmsShortageRegistrationService.getBaseMapper().selectList(new LambdaQueryWrapper<WmsShortageRegistration>().eq(WmsShortageRegistration::getTaskId, pickTaskId));
        //获取缺货总数量
        Integer shortageQuantity = wmsShortageRegistrations.stream().mapToInt(WmsShortageRegistration::getShortageQuantity).sum();
        if (pickedQuantity + shortageQuantity > wmsTasks.getQuantity()) {
            throw new JeecgBootException("已拣货数量+缺货数量不能大于计划拣货数量");
        }
    }

    /**
     * 拣货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pick(WmsTasksRecords wmsTasksRecords) {
        //执行任务
        WmsTasks wmsTasks = wmsTasksService.execute(wmsTasksRecords);
        //校验缺货数量+拣货数量不能大于计划数量
        checkPickedQuantity(wmsTasksRecords.getTaskId());
        //执行数量
        Integer execQuantity = wmsTasksRecords.getExecQuantity();
        //计划数量
        Integer quantity = wmsTasks.getQuantity();
        //波次拣货明细id
        String waveSkuSummaryId = wmsTasks.getWaveSkuSummaryId();
        //根据波次拣货明细id更新波次拣货数量
        updatePickedQuantityByWaveSkuSummaryId(waveSkuSummaryId, quantity,execQuantity);
        //更新出库单明细拣货数量以及状态
        updateOutOrderItemsPickStatus(wmsTasks,execQuantity);
        //5.更新出库单的拣货状态，如果出库单下所有出库单明细的拣货状态都为已拣货，则更新出库单的拣货状态为拣货完成
        List<String> orderIds = wmsOutOrdersService.selectByWaveId(wmsTasks.getWaveOrderId())
                .stream()
                .map(WmsOutOrders::getId)
                .collect(Collectors.toList());
        if (!orderIds.isEmpty()) {
            wmsOutOrdersService.updatePickStatus(orderIds);
        }
        //4.更新波次主表的拣货状态，如果波次下拣货明细的状态为已拣货，则更新波次主表的拣货状态为拣货完成
        wmsWaveMasterService.updatePickStatus(wmsTasks.getWaveOrderId());
        //向库存表中添加库存记录
        WmsInventoryTransParam inventoryTransParam = new WmsInventoryTransParam();
        inventoryTransParam.setProductId(wmsTasksRecords.getProductId());
        inventoryTransParam.setExecQuantity(wmsTasksRecords.getExecQuantity());
        inventoryTransParam.setSourceLocationCode(wmsTasksRecords.getSourceLocationCode());
        inventoryTransParam.setTargetLocationCode(wmsTasksRecords.getTargetLocationCode());
        inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
        inventoryTransParam.setBatchNumber(wmsTasksRecords.getBatchNumber());
        inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate());
        inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_PICKING.getCode());
        //上架时间
        inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationTime());
        inventoryTransByPick.transfer(inventoryTransParam);
    }
    /**
     * 根据波次拣货明细id更新拣货数量
     * @param waveSkuSummaryId 波次拣货明细id
     * @param quantity 计划拣货数量
     * @param pickedQuantity 拣货数量
     */
    public void updatePickedQuantityByWaveSkuSummaryId(String waveSkuSummaryId, Integer quantity,Integer pickedQuantity) {
        //更新波次拣货明细的拣货数量
        //sql="update wms_wave_sku_summary set picked_quantity = picked_quantity+"+pickedQuantity+" where id = '"+waveSkuSummaryId+"' and picked_quantity <="+quantity-pickedQuantity;
        LambdaUpdateWrapper<WmsWaveSkuSummary> waveSkuSummaryLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        waveSkuSummaryLambdaUpdateWrapper
                .setSql("picked_quantity = picked_quantity+"+pickedQuantity)
                .eq(WmsWaveSkuSummary::getId,waveSkuSummaryId)
                .le(WmsWaveSkuSummary::getPickedQuantity,quantity-pickedQuantity);
        int update2 = wmsWaveSkuSummaryService.getBaseMapper().update(null, waveSkuSummaryLambdaUpdateWrapper);
        if(update2 <= 0){
            throw new JeecgBootException("波次拣货明细的拣货数量不能大于分配数量!");
        }
        //查询波次拣货明细
        WmsWaveSkuSummary wmsWaveSkuSummary = wmsWaveSkuSummaryService.getBaseMapper().selectById(waveSkuSummaryId);
        //如果拣货数量等于计划拣货数量，则更新波次拣货明细的拣货状态为已拣货
        boolean picked = wmsWaveSkuSummary.getPickedQuantity() != null && wmsWaveSkuSummary.getPickedQuantity().equals(quantity);
        wmsWaveSkuSummary.setStatus(picked
                ? WarehouseDictEnum.WAVESKU_PICKED.getCode()
                : WarehouseDictEnum.WAVESKU_PICKING.getCode());
        wmsWaveSkuSummaryService.updateById(wmsWaveSkuSummary);
//        //通过波次拣货明细id来查询任务单
//        if (wmsWaveSkuSummary.getStatus().equals(WarehouseDictEnum.WAVESKU_PICKED.getCode())) {
//            //更新出库单明细拣货数量及状态（拣满置已拣货，否则置拣货中）
//            updateOutOrderItemsPickStatus(wmsTasks, pickedQuantity);
//            //更新出库单状态：该出库单所有明细都拣完才置为已拣货
//            List<String> orderIds = wmsOutOrdersService.selectByWaveId(wmsTasks.getWaveOrderId())
//                    .stream().map(WmsOutOrders::getId).collect(Collectors.toList());
//            if (!orderIds.isEmpty()) {
//                wmsOutOrdersService.updatePickStatus(orderIds);
//            }
//            //更新波次主表的拣货状态，如果波次下拣货明细的状态为已拣货，则更新波次主表的拣货状态为拣货完成
//            wmsWaveMasterService.updatePickStatus(wmsTasks.getWaveOrderId());
//        }

    }

    /**
     * 更新出库单明细的拣货数量与状态：拣满置已拣货，否则置拣货中
     */
    public void updateOutOrderItemsPickStatus(WmsTasks wmsTasks,Integer execQuantity){
        //波次下所有出库单
        List<WmsOutOrders> wmsOutOrders = wmsOutOrdersService.selectByWaveId(wmsTasks.getWaveOrderId());
        if (wmsOutOrders == null || wmsOutOrders.isEmpty()) {
            return;
        }
        //波次下所有出库单id
        List<String> collect = wmsOutOrders.stream()
                .map(WmsOutOrders::getId)
                .collect(Collectors.toList());
        //波次下该商品对应的出库单明细（一个出库单明细对应一个SKU）
        List<WmsOutOrdersItems> items = wmsOutOrdersItemsService.selectByOrderIds(collect).stream()
                .filter(item -> wmsTasks.getProductId().equals(item.getSkuId()))
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            return;
        }
        //统计各明细剩余待拣数量
        Map<String, Integer> remainingMap = new HashMap<>();
        int totalRemaining = 0;
        for (WmsOutOrdersItems item : items) {
            int picked = item.getPickedQuantity() == null ? 0 : item.getPickedQuantity();
            int remaining = Math.max(0, item.getAllocatedQuantity() - picked);
            remainingMap.put(item.getId(), remaining);
            totalRemaining += remaining;
            //拣货数量（历史累计 + 本次）
            int newPicked = (item.getPickedQuantity() == null ? 0 : item.getPickedQuantity()) + execQuantity;
            //状态
            String status = newPicked >= item.getAllocatedQuantity()
                    ? WarehouseDictEnum.OUTBOUND_DETAIL_PICKED.getCode()
                    : WarehouseDictEnum.OUTBOUND_DETAIL_PICKING.getCode();
            LambdaUpdateWrapper<WmsOutOrdersItems> updateWrapper = new LambdaUpdateWrapper<WmsOutOrdersItems>()
                    .eq(WmsOutOrdersItems::getId, item.getId())
                    .set(WmsOutOrdersItems::getPickedQuantity, newPicked)
                    .set(WmsOutOrdersItems::getStatus, status);
            wmsOutOrdersItemsService.update(null, updateWrapper);
        }
    }


    /**
     * 完成分拣
     * @param wmsOutOrdersItemsList 出库单明细
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeSorting(List<WmsOutOrdersItems> wmsOutOrdersItemsList) {
        //转成数组
//        String[] outOrderItemIdArray = outOrderItemIds.split(",");
        //出库单idList
        List<String> outOrderIds = new ArrayList<>();
        //波次id List
        List<String> waveIds = new ArrayList<>();
        //遍历出库单明细id
        for (WmsOutOrdersItems items : wmsOutOrdersItemsList) {
            //根据出库单明细id查询出库单明细
            WmsOutOrdersItems wmsOutOrdersItems = wmsOutOrdersItemsService.getById(items.getId());
            if (wmsOutOrdersItems == null) {
                throw new RuntimeException("未找到对应的出库单明细,id:"+items.getId());
            }
            //商品id
            String skuId = wmsOutOrdersItems.getSkuId();
            //拣货数量,页面传入
            Integer pickedQuantity = items.getPickedQuantity();
            //商品信息
            WmsProducts wmsProducts = wmsProductsService.getById(skuId);
            //如果状态为分拣完成状态，则不允许分拣
            if (WarehouseDictEnum.OUTBOUND_DETAIL_PICKED.getCode().equals(wmsOutOrdersItems.getStatus())) {
                throw new RuntimeException("出库单商品"+wmsProducts.getProductName()+"为拣货完成状态,不允许分拣");
            }
            //根据出库单id查询出库单
            WmsOutOrders wmsOutOrders = wmsOutOrdersService.getById(wmsOutOrdersItems.getOrderId());
            if (wmsOutOrders == null) {
                throw new RuntimeException("未找到对应的出库单,id:"+wmsOutOrdersItems.getOrderId());
            }
            outOrderIds.add(wmsOutOrders.getId());
            //波次id
            String waveId = wmsOutOrders.getWaveId();
            waveIds.add(waveId);
            //向波次表增加分拣数量
            wmsWaveMasterService.addSortingQuantity(waveId, pickedQuantity);
            //更新出库单明细的拣货数量及状态更新为分拣完成
            wmsOutOrdersItemsService.updatePickedQuantityAndStatus(items.getId(), pickedQuantity);
        }
        //更新出库单状态为分拣完成
        wmsOutOrdersService.updatePickStatus(outOrderIds);
        //更新该波次完成分拣订单的数量
        wmsWaveMasterService.updateCompleteSortingOrderQuantity(waveIds);

    }

    @Override
    public String viewPickPath(String waveId) {
        //如果波次已经生成拣货路径就不要重复生成了
        WmsWaveMaster wmsWaveMaster = wmsWaveMasterService.getById(waveId);
        if (wmsWaveMaster == null) {
            throw new RuntimeException("未找到对应的波次,id:"+waveId);
        }
        //如果状态是已创建则不允许查询拣货路径
        if (WarehouseDictEnum.WAVE_CREATED.getCode().equals(wmsWaveMaster.getStatus())) {
            throw new RuntimeException("波次"+wmsWaveMaster.getWaveNo()+"未完成拣货,不允许查询拣货路径");
        }
        //如果存在拣货路径图片，则直接返回
        if (StringUtils.isNotBlank(wmsWaveMaster.getPickPathImg())) {
            return wmsWaveMaster.getPickPathImg();
        }


        //调用A*算法来算出最佳路径，生成SVG代码，生成拣货路径
        // 起始位置
        Node startNode = new Node(0, 0);

        //根据波次的id来查询波次拣货明细的信息
        List<WmsWaveSkuSummary> wmsWaveSkuSummaries = wmsWaveSkuSummaryService.selectByWaveId(waveId);
        List<Product> products = wmsWaveSkuSummaries.stream()
                .map(summary -> {
                    //商品id
                    String skuId = summary.getSkuId();
                    //储位的编码
                    String locationCode = summary.getLocationCode();
                    //商品信息
                    WmsProducts wmsProducts = wmsProductsService.getById(skuId);
                    //通过储位的编码拿到储位的信息
                    WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.queryByLocationCode(locationCode);
                    //从储位对象拿到储区的id
                    String zoneId = wmsStorageLocations.getZoneId();
                    //通过储区的id拿到储区信息
                    WmsStorageZones wmsStorageZones = wmsStorageZonesService.getById(zoneId);
                    Location location = new Location(wmsStorageZones.getZoneName(), Integer.parseInt(wmsStorageLocations.getLocationLine()), Integer.parseInt(wmsStorageLocations.getLocationRank()), wmsStorageLocations.getLocationCode());
                    Product product = new Product(wmsProducts.getId(), wmsProducts.getProductName(), location);
                    return product;
                }).collect(Collectors.toList());

        // 多个商品 - 测试相邻行和非相邻行的情况
//        List<Product> products = Arrays.asList(
//                new Product("P003", "商品3", new Location("A", 1, 4,"Z1A-1-4")),
//                new Product("P004", "商品4", new Location("A", 3, 4,"Z1A-3-4")),
//                new Product("P005", "商品5", new Location("A", 3, 5,"Z1A-3-5")),
//                new Product("P006", "商品6", new Location("C", 4, 5,"Z1C-4-5")) // 与前两个商品不相邻
//        );

        // 计算带商品信息的最优路径
        MultiProductPickingRouteOptimized.RouteWithProducts routeWithProducts = calculateOptimalPickingRouteWithProducts(startNode, products);

        // 打印带商品信息的路径
        System.out.println("优化后的拣货路径（带商品信息）:");
        printPathWithProducts(routeWithProducts);

        // 生成图形数据
        System.out.println("\nSVG图形数据:");
        String svgData = generateSVGData(routeWithProducts, startNode, products);


        //将数据更新到数据库中
        WmsWaveMaster wmsWaveMaster1 = new WmsWaveMaster();
        wmsWaveMaster1.setId(waveId);
        wmsWaveMaster1.setPickPathImg(svgData);
        wmsWaveMasterService.updateById(wmsWaveMaster1);
        return svgData;

    }


}
