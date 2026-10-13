package org.jeecg.modules.wms.analysis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.wms.analysis.mapper.BigscreenMapper;
import org.jeecg.modules.wms.analysis.service.BigscreenService;
import org.jeecg.modules.wms.analysis.vo.RankItemVO;
import org.jeecg.modules.wms.analysis.vo.TrendPoint;
import org.jeecg.modules.wms.analysis.vo.TodoTask;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 首页大屏接口实现类
 * @date 2025/8/13 19:03
 */

@Service
@Slf4j
public class BigscreenServiceImpl implements BigscreenService {

    @Autowired
    private BigscreenMapper bigscreenMapper;

    /**
     * 待办任务列表
     */
    @Override
    // 旧代码：warehouseId 为空时，Spring Cache 会生成 Null key 并直接抛异常。
    // @Cacheable(value = "sys:cache:bigscreen:todotasklist",key = "#warehouseId")
    @Cacheable(
            value = "sys:cache:bigscreen:todotasklist",
            key = "T(java.util.Objects).toString(#warehouseId, 'ALL')",
            condition = "#warehouseId != null && #warehouseId != ''"
    )
    public List<TodoTask> findTodoTaskList(String warehouseId) {

        //统计当前总的数量

        //初始化对应的数据
        //所有待收货任务数量
        int totalReceivingTaskCount = 0;
        //所以待上架任务数量
        int totalPutawayTaskCount = 0;
        //所有待拣货任务数量
        int totalPickingTaskCount = 0;
        //所有待发货任务数量
        int totalDeliveryTaskCount = 0;

        //已完成收货任务数量
        int completedReceivingTaskCount = 0;
        //已完成上架任务数量
        int completedPutawayTaskCount = 0;
        //已完成拣货任务数量
        int completedPickingTaskCount = 0;
        //已完成发货任务数量
        int completedDeliveryTaskCount = 0;

        String date = DateUtils.formatDate(new Date(), "yy-MM-dd");
        String queryDate = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        WmsTasks wmsTasks = new WmsTasks();
        //仓库id
        wmsTasks.setTargetWarehouseId(warehouseId);
        // 旧代码没有设置日期，统计结果会把历史任务也算进去。
        // wmsTasks.setCreateTimeString(date);
        wmsTasks.setCreateTimeString(queryDate);
        //总的待办任务数量
        List<WmsTasks> list = bigscreenMapper.countTaskList(wmsTasks);
        for (WmsTasks tasks: list) {
            switch (tasks.getTaskType()) {
                case "RECEIVING_TASK":
                    totalReceivingTaskCount = tasks.getTaskCount();
                    break;
                case "PUTAWAY_TASK":
                    totalPutawayTaskCount = tasks.getTaskCount();
                    break;
                case "PICKING_TASK":
                    totalPickingTaskCount = tasks.getTaskCount();
                    break;
                case "DELIVERY_TASK":
                    totalDeliveryTaskCount = tasks.getTaskCount();
                    break;
            }
        }
        // 旧代码把“已完成”状态写进了 taskType，导致完成任务统计条件错误。
        // WmsTasks tasks = wmsTasks.setTaskType(WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode());
        WmsTasks tasks = wmsTasks.setTaskStatus(WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode());
        //已完成的任务数量
        List<WmsTasks> wmsTasksList = bigscreenMapper.countTaskList(tasks);
        for (WmsTasks task : wmsTasksList) {
            switch (task.getTaskType()) {
                case "RECEIVING_TASK":
                    completedReceivingTaskCount = task.getTaskCount();
                    break;
                case "PUTAWAY_TASK":
                    completedPutawayTaskCount = task.getTaskCount();
                    break;
                case "PICKING_TASK":
                    completedPickingTaskCount = task.getTaskCount();
                    break;
                case "DELIVERY_TASK":
                    completedDeliveryTaskCount = task.getTaskCount();
            }
        }
        //待收货任务数量
        int todoReceivingTaskCount = totalReceivingTaskCount - completedReceivingTaskCount;
        //待上架任务数量
        int todoPutawayTaskCount = totalPutawayTaskCount - completedPutawayTaskCount;
        //待拣货任务数量
        int todoPickingTaskCount = totalPickingTaskCount - completedPickingTaskCount;
        //待发货任务数量
        int todoDeliveryTaskCount = totalDeliveryTaskCount - completedDeliveryTaskCount;

        ArrayList<TodoTask> todoTasks = new ArrayList<>();
        todoTasks.add(new TodoTask("待收货任务","visit-count|svg",todoReceivingTaskCount,completedReceivingTaskCount,date));
        todoTasks.add(new TodoTask("待上架任务","total-sales|svg",todoPutawayTaskCount,completedPutawayTaskCount,date));
        todoTasks.add(new TodoTask("待拣货任务","download-count|svg",todoPickingTaskCount,completedPickingTaskCount,date));
        todoTasks.add(new TodoTask("待发货任务","deliver-count|svg",todoDeliveryTaskCount,completedDeliveryTaskCount,date));

        return todoTasks;
    }

    @Override
    public List<TrendPoint> findInboundTrend(String warehouseId,Integer year) {

        //判断年份是否为空如果为空那么将现如今的年份填入
        int y = (year == null) ? LocalDate.now().getYear() : year;

        List<TrendPoint> trendPoints = bigscreenMapper.sumInboundByMonth(warehouseId, y);
        log.info("大屏入库趋势查询：warehouseId={}, year={}, count={}, data={}",
                warehouseId, y, trendPoints == null ? 0 : trendPoints.size(), trendPoints);
        return trendPoints;
    }

    @Override
    public List<TrendPoint> findOutboundTrend(String warehouseId, Integer year) {

        //判断年份是否为空如果为空那么将现如今的年份填入
        int y = (year == null) ? LocalDate.now().getYear() : year;

        // 旧代码误调用入库趋势查询。
        // List<TrendPoint> trendPoints = bigscreenMapper.sumInboundByMonth(warehouseId, y);
        List<TrendPoint> trendPoints = bigscreenMapper.sumOutboundByMonth(warehouseId, y);
        log.info("大屏出库趋势查询：warehouseId={}, year={}, count={}, data={}",
                warehouseId, y, trendPoints == null ? 0 : trendPoints.size(), trendPoints);
        return trendPoints;

    }

    @Override
    public List<RankItemVO> findConsigneeRanking(String warehouseId) {

        List<RankItemVO> trendPoints = bigscreenMapper.countConsigneeOutbound(warehouseId);
        log.info("大屏货主排行榜查询：warehouseId={}, count={}, data={}",
                warehouseId, trendPoints == null ? 0 : trendPoints.size(), trendPoints);
        return trendPoints;

    }
}
