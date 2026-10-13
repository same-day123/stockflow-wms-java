package org.jeecg.modules.wms.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.wms.analysis.service.BigscreenService;
import org.jeecg.modules.wms.analysis.vo.RankItemVO;
import org.jeecg.modules.wms.analysis.vo.TrendPoint;
import org.jeecg.modules.wms.analysis.vo.TodoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @Description: 首页大屏接口
* @Author: jeecg-boot
* @Date:   2025-04-23
* @Version: V1.0
*/
@Tag(name="首页大屏接口")
@RestController
@RequestMapping("/bigscreen")
@Slf4j
public class BigscreenController {

    @Autowired
    private BigscreenService bigscreenService;

   @Operation(summary="待办任务列表")
   @GetMapping(value = "/todo-tasks")
   public Result<List<TodoTask>> todoTaskList(HttpServletRequest request) {
       //制造一组数据存入List<TodoTask>
//        List<TodoTask> todoTaskList = Arrays.asList(
//                new TodoTask("待收货任务","visit-count|svg",10,5,"当天"),
//                new TodoTask("待上架任务","total-sales|svg",10,5,"当天"),
//                new TodoTask("待拣货任务","download-count|svg",10,5,"当天"),
//                new TodoTask("待发货任务","download-count|svg",10,5,"当天")
//        );

       //仓库id
       String warehouseId = request.getHeader("X-Warehouse-Id");

       List<TodoTask> todoTaskList = bigscreenService.findTodoTaskList(warehouseId);

       return Result.OK(todoTaskList);
   }

    @Operation(summary="获取入库趋势")
    @GetMapping(value = "/inbound-trend")
    public Result<List<TrendPoint>> inboundTrend(HttpServletRequest request,
                                                 @RequestParam(required = false) Integer year) {

        //仓库id
        String warehouseId = request.getHeader("X-Warehouse-Id");

        List<TrendPoint> inOrderData = bigscreenService.findInboundTrend(warehouseId,year);

        return Result.OK(inOrderData);
    }

    @Operation(summary="获取出库趋势")
    @GetMapping(value = "/outbound-trend")
    public Result<List<TrendPoint>> outboundTrend(HttpServletRequest request,
                                                  @RequestParam(required = false) Integer year) {

        //仓库id
        String warehouseId = request.getHeader("X-Warehouse-Id");

        List<TrendPoint> outOrderData = bigscreenService.findOutboundTrend(warehouseId,year);

        return Result.OK(outOrderData);
    }


    @Operation(summary="获取货主出库排行榜")
    @GetMapping(value = "/consignee-ranking")
    public Result<List<RankItemVO>> consigneeRanking(HttpServletRequest request) {

        //仓库id
        String warehouseId = request.getHeader("X-Warehouse-Id");

        List<RankItemVO> inOrderData = bigscreenService.findConsigneeRanking(warehouseId);
        return Result.OK(inOrderData);
    }

}
