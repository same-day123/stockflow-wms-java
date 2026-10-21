package org.jeecg.modules.wms.wmstask.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByPutway;
import org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransByReceiving;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksMapper;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date: 2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsTasksServiceImpl extends ServiceImpl<WmsTasksMapper, WmsTasks> implements IWmsTasksService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IWmsStockInOrdersService stockInOrdersService;
    @Autowired
    private IWmsStockInOrderItemsService stockInOrderItemsService;
    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;
    @Autowired
    private WmsInventoryTransByReceiving wmsInventoryTransByReceiving;
    @Autowired
    private WmsInventoryTransByPutway wmsInventoryExecByPutway;

    /**
     * 创建收货任务
     *
     * @param inOrderId 入库单id
     * @param operator  操作员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReceiveTask(String inOrderId, String operator) {
        //查询入库单信息
        WmsStockInOrders stockInOrders = stockInOrdersService.getById(inOrderId);
        //仓库
        String warehouseId = stockInOrders.getWarehouseId();
        //入库单状态
        String status = stockInOrders.getStatus();
        //入库单状态非审核通过时不允许创建收货任务
        if (!WarehouseDictEnum.INBOUND_APPROVED.getCode().equals(status)) {
            throw new JeecgBootException("入库单审核通过方可创建收货任务");
        }
        //根据入库单id查询入库单明细列表
        List<WmsStockInOrderItems> stockInOrderItems = stockInOrderItemsService.selectByMainId(inOrderId);
        //总采购数量
        Integer totalQuantity = 0;
        //根据入库明细列表创建收货任务
        for (WmsStockInOrderItems stockInOrderItem : stockInOrderItems) {
            //创建收货任务
            WmsTasks wmsTasks = new WmsTasks();
            //任务类型,收货任务
            wmsTasks.setTaskType(WarehouseDictEnum.TASK_TYPE_RECEIVING.getCode());
            //任务状态，已创建
            wmsTasks.setTaskStatus(WarehouseDictEnum.TASK_STATUS_CREATED.getCode());
            //任务创建时间
            wmsTasks.setCreateTime(new Date());
            //任务号
            wmsTasks.setTaskNumber(generateTaskCode());
            //目的仓库
            wmsTasks.setTargetWarehouseId(warehouseId);
            //入库单id
            wmsTasks.setStockInOrderId(stockInOrderItem.getOrderId());
            //入库单明细id
            wmsTasks.setStockInOrderItemId(stockInOrderItem.getId());
            //商品id
            wmsTasks.setProductId(stockInOrderItem.getProductId());
            //商品采购数量
            wmsTasks.setQuantity(stockInOrderItem.getExpectedQuantity());
            //完成数量为0
            wmsTasks.setCompletedQuantity(0);
            //执行人
            wmsTasks.setOperator(operator);
            //创建任务
            save(wmsTasks);
            //总采购数量
            totalQuantity += stockInOrderItem.getExpectedQuantity();
        }
        //更新入库单状态为收货中
        //sql update wms_stock_in_orders set total_expected_quantity = #{totalExpectedQuantity},status = #{status} where id = #{id} and status = #{status}
        LambdaUpdateWrapper<WmsStockInOrders> eq = new LambdaUpdateWrapper<WmsStockInOrders>().eq(WmsStockInOrders::getId, inOrderId).eq(WmsStockInOrders::getStatus, WarehouseDictEnum.INBOUND_APPROVED.getCode()).set(WmsStockInOrders::getTotalExpectedQuantity, totalQuantity).set(WmsStockInOrders::getStatus, WarehouseDictEnum.INBOUND_RECEIVING.getCode());

        boolean update = stockInOrdersService.update(null, eq);
        if (!update) {
            throw new JeecgBootException("更新入库单状态失败");
        }
        //根据入库单id更新入库单明细状态为收货中
        boolean update2 = stockInOrderItemsService.update(null, new LambdaUpdateWrapper<WmsStockInOrderItems>().eq(WmsStockInOrderItems::getOrderId, inOrderId).set(WmsStockInOrderItems::getStatus, WarehouseDictEnum.INBOUND_DETAIL_RECEIVING.getCode()));
        if (!update2) {
            throw new JeecgBootException("创建收货任务过程中更新入库单明细状态失败");
        }

    }

    /**
     * 生成任务编号
     * 规则: TSK+年月日+5位序号，序号使用redis自增序号实现
     */
    @Override
    public String generateTaskCode() {
        //参考上边的代码实现
        String time = DateUtils.now().substring(0, 10).replace("-", "");
        String key = "tsk_number" + time;
        long incr = redisUtil.incr(key, 1);
        if (incr == 1) {
            //设置过期时间，设置24小时+10秒的目的是避免并发产生订单号重复
            redisUtil.expire(key, 24 * 60 * 60 + 10);
        }
        //将incr组成5位字符串
        String incrStr = String.format("%05d", incr);
        String taskNumber = "TSK" + time + incrStr;

        return taskNumber;
    }

    @Override
    public IPage<WmsTasks> list(WmsTasks wmsTasks, Integer pageNo, Integer pageSize) {

        Page<WmsTasks> page = PageHelper.startPage(pageNo, pageSize);
        List<WmsTasks> list = baseMapper.queryTaskList(wmsTasks);
        PageDTO<WmsTasks> wmsTasksPageDTO = new PageDTO<>();
        wmsTasksPageDTO.setRecords(list);
        wmsTasksPageDTO.setTotal(page.getTotal());
        wmsTasksPageDTO.setPages(page.getPages());
        wmsTasksPageDTO.setCurrent(page.getPageNum());
        wmsTasksPageDTO.setSize(page.getPageSize());
        return wmsTasksPageDTO;
    }

    /**
     * 执行收货任务
     *
     * @param wmsTasksRecords
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void receive(WmsTasksRecords wmsTasksRecords) {
        //执行任务
        WmsTasks wmsTasks = execute(wmsTasksRecords);
        //更新入库单明细的收货数量及状态
        stockInOrderItemsService.updateReceivedStatus(wmsTasks.getStockInOrderItemId());

        //更新入库单的收货数量及状态
        String inOrderStatus = stockInOrdersService.updateReceivedStatus(wmsTasks.getStockInOrderId());
        //存储库存
        //库存属性
        String inventoryAttribute = wmsTasksRecords.getInventoryAttribute();
        //向库存表中添加库存记录
//        wmsInventoryService.createInventoryByReceive(wmsTasksRecords);
        WmsInventoryTransParam inventoryTransParam = new WmsInventoryTransParam();
        inventoryTransParam.setProductId(wmsTasksRecords.getProductId());
        inventoryTransParam.setExecQuantity(wmsTasksRecords.getExecQuantity());
        inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
        inventoryTransParam.setTargetLocationCode(wmsTasksRecords.getTargetLocationCode());
        inventoryTransParam.setBatchNumber(wmsTasksRecords.getBatchNumber());
        inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate());
        inventoryTransParam.setRemarks("收货,向库存新增记录,入库单:" + wmsTasksRecords.getStockInOrderId());
        inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_RECEIVING.getCode());
        //保质期到期日
        inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate());
        //入库时间
        inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationTime());
        //库存属性为良品则可售
        if (WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode().equals(inventoryAttribute)) {
            inventoryTransParam.setIsSellable("1");
        } else {
            inventoryTransParam.setIsSellable("0");
        }
        wmsInventoryTransByReceiving.transfer(inventoryTransParam);

        //如果收货完成则创建上架任务
        //如果入库单收货完成则创建上架任务,根据收货记录创建上架任务
        if (inOrderStatus.equals(WarehouseDictEnum.INBOUND_RECEIVED.getCode())) {
            createPutawayTask(wmsTasks.getStockInOrderId());
        }
    }

    /**
     * 执行任务
     *
     * @param wmsTasksRecords
     * @return
     */
    @Override
    public WmsTasks execute(WmsTasksRecords wmsTasksRecords) {
        //通过任务记录表查询对应的任务id
        String taskId = wmsTasksRecords.getTaskId();
        //通过id查询任务信息
        WmsTasks wmsTasks = getById(taskId);
        //计划执行的数量
        Integer quantity = wmsTasks.getQuantity();
        //已经执行的数量
        Integer execQuantity = wmsTasksRecords.getExecQuantity();
        //已完成执行的数量
        Integer completedQuantity = ObjectUtils.defaultIfNull(wmsTasks.getCompletedQuantity(), 0);
        //如果已完成数量+执行数量大于计划数量则不能执行
        if (completedQuantity + execQuantity > quantity) {
            throw new JeecgBootException("执行数量不能大于计划数量!");
        }
        /**
         * 1.在任务表增加完成收货数量
         * 2.添加任务执行记录
         * 3.更新任务状态，如果完成数量等于计划数量则任务状态更新为完成
         */
        //1.在任务表增加完成收货数量
        //sql update wms_tasks  set completed_quantity=completed_quantity+? where id=? and completed_quantity<=quantity-?
        LambdaUpdateWrapper<WmsTasks> le = new LambdaUpdateWrapper<WmsTasks>()
                .setSql("completed_quantity=completed_quantity+" + execQuantity)
                .eq(WmsTasks::getId, taskId)
                .le(WmsTasks::getCompletedQuantity, quantity - execQuantity);
        boolean update = update(null, le);
        if (!update) {
            throw new JeecgBootException("执行数量不能大于计划数量!");
        }
        //2.添加任务执行记录
        //目标仓库id
        wmsTasksRecords.setTargetWarehouseId(wmsTasks.getTargetWarehouseId());
        //入库单id
        wmsTasksRecords.setStockInOrderId(wmsTasks.getStockInOrderId());
        //入库单明细id
        wmsTasksRecords.setStockInOrderItemId(wmsTasks.getStockInOrderItemId());
        //任务id
        wmsTasksRecords.setTaskId(wmsTasks.getId());
        //商品 id
        wmsTasksRecords.setProductId(wmsTasks.getProductId());
        //任务编码
        wmsTasksRecords.setTaskNumber(wmsTasks.getTaskNumber());
        //波次id
        wmsTasksRecords.setWaveOrderId(wmsTasks.getWaveOrderId());
        //波次明细id
        wmsTasksRecords.setWaveSkuSummaryId(wmsTasks.getWaveSkuSummaryId());
        //任务类型
        wmsTasksRecords.setTaskType(wmsTasks.getTaskType());
        //执行时间
        wmsTasksRecords.setOperationTime(new Date());
        //执行人
        //获取当前用户-通过shiro的方法来获取登录用户的信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        wmsTasksRecords.setOperator(userId);
        boolean save = wmsTasksRecordsService.save(wmsTasksRecords);
        if (!save) {
            throw new JeecgBootException("添加任务执行记录失败!");
        }
        //更新任务状态，如果完成数量等于计划数量则任务状态更新为完成
        wmsTasks = getById(taskId);
        //完成数量
        completedQuantity = wmsTasks.getCompletedQuantity();
        //更新任务为已完成
        if (completedQuantity == wmsTasks.getQuantity()) {
            wmsTasks.setTaskStatus(WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode());
            boolean b = updateById(wmsTasks);
            if (!b) {
                throw new JeecgBootException("更新任务状态失败!");
            }
        }
        return wmsTasks;
    }

    /**
     * 创建上架任务
     *
     * @param orderId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPutawayTask(String orderId) {
        //***根据入库单的收货记录创建上架任务***
        //查询入库单
        WmsStockInOrders stockInOrders = stockInOrdersService.getById(orderId);
        //如果非收货完成状态则不创建上架任务
        if (!stockInOrders.getStatus().equals(WarehouseDictEnum.INBOUND_RECEIVED.getCode())) {
            return;
        }

        //查询入库单的收货记录
        WmsTasksRecords wmsTasksRecords = new WmsTasksRecords();
        wmsTasksRecords.setStockInOrderId(orderId);
        //分页查询收货记录
        IPage<WmsTasksRecords> pageList = wmsTasksRecordsService.pageList(wmsTasksRecords, 1, 1000);
        //遍历收货记录创建上架任务,只将收货商品为良品的创建上架任务
        for (WmsTasksRecords wmsTasksRecords1 : pageList.getRecords()) {
            //库存属性
            String inventoryAttribute = wmsTasksRecords1.getInventoryAttribute();
            if (!WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode().equals(inventoryAttribute)) {
                continue;
            }
            WmsTasks wmsTasks = new WmsTasks();
            wmsTasks.setTaskNumber(generateTaskCode());//任务编号
            wmsTasks.setTaskType(WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode());//上架任务
            wmsTasks.setTaskStatus(WarehouseDictEnum.TASK_STATUS_CREATED.getCode());//已创建
            wmsTasks.setCreateTime(new Date());
            wmsTasks.setStockInOrderId(wmsTasksRecords1.getStockInOrderId());
            wmsTasks.setStockInOrderItemId(wmsTasksRecords1.getStockInOrderItemId());
            wmsTasks.setProductId(wmsTasksRecords1.getProductId());
            wmsTasks.setQuantity(wmsTasksRecords1.getExecQuantity());//待上架数量为收货完成数量
            wmsTasks.setCompletedQuantity(0);
            wmsTasks.setSourceLocationCode(wmsTasksRecords1.getTargetLocationCode());//来源储位编码为收货目标储位编码
            wmsTasks.setSourceContainerCode(wmsTasksRecords1.getTargetContainerCode());//来源容器编码为收货目标容器编码
            wmsTasks.setTargetWarehouseId(wmsTasksRecords1.getTargetWarehouseId());
            wmsTasks.setBatchNumber(wmsTasksRecords1.getBatchNumber()); //批次号
            wmsTasks.setExpiryDate(wmsTasksRecords1.getExpiryDate());//保质期
            wmsTasks.setOperator(wmsTasksRecords1.getOperator()); //执行人
            save(wmsTasks);
        }
    }

    /**
     * 上架
     *
     * @param wmsTasksRecords
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void putaway(WmsTasksRecords wmsTasksRecords) {
        //执行任务
        WmsTasks wmsTasks = execute(wmsTasksRecords);
        //将完成数量更新至入库单明细
        stockInOrderItemsService.updatePutawayStatus(wmsTasks.getStockInOrderItemId());
        //更新入库单状态为上架完成或上架中
        String inOrderStatus = stockInOrdersService.updatePutawayStatus(wmsTasks.getStockInOrderId());

        //向库存表中添加库存记录
//        wmsInventoryService.createInventoryByPutaway(wmsTasksRecords);
        WmsInventoryTransParam inventoryTransParam = new WmsInventoryTransParam();
        inventoryTransParam.setProductId(wmsTasksRecords.getProductId());
        inventoryTransParam.setExecQuantity(wmsTasksRecords.getExecQuantity());
        inventoryTransParam.setSourceLocationCode(wmsTasksRecords.getSourceLocationCode());
        inventoryTransParam.setTargetLocationCode(wmsTasksRecords.getTargetLocationCode());
        inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
        inventoryTransParam.setBatchNumber(wmsTasksRecords.getBatchNumber());
        inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate());
        inventoryTransParam.setRemarks("上架,入库单:" + wmsTasksRecords.getStockInOrderId());
        inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_PUTAWAY.getCode());
        //上架时间
        inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationTime());
        wmsInventoryExecByPutway.transfer(inventoryTransParam);
    }
}
