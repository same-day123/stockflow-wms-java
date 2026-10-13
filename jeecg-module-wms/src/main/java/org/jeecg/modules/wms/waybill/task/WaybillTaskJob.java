package org.jeecg.modules.wms.waybill.task;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.config.ThreadPoolConfiguration;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.waybill.service.IWmsWaybillService;
import org.jeecg.modules.wms.waybill.task.handler.SimpleThreadCallable;
import org.jeecg.modules.wms.waybill.task.handler.WaybillThreadCallable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 定时任务类
 * @date 2025/9/13 10:10
 */
@Component
@Slf4j
public class WaybillTaskJob {

    @Resource(name = "waybillThreadPool")
    private ThreadPoolExecutor waybillThreadPool;

    // 是否正在执行
    private volatile Boolean isRunning = false;

    @Autowired
    private IWmsWaybillService wmsWaybillService;

    @Autowired
    private IWmsOutOrdersService wmsOutOrdersService;

//    @Scheduled(cron = "0/2 * * * * ?")
//    public void test1() {
//        log.info("==============开始执行定时任务=================");
//        for (int i=0;i<10;i++) {
//            //定义任务对象
//            SimpleThreadCallable simpleThreadCallable= new SimpleThreadCallable();
//            // 启动多个线程完成请求顺丰下单并获取运单
//            waybillThreadPool.submit(simpleThreadCallable);
//        }
//    }
//
//    @Scheduled(cron = "0/2 * * * * ?")
//    public void test2() throws ExecutionException, InterruptedException {
//        //防止重入
//        if(isRunning) {
//            log.info("上一轮任务未完成，请勿重复执行");
//            return;
//        }
//        //如果上一轮任务没有完成不在继续执行
//        //标记任务开始执行
//        isRunning = true;
//        log.info("==============开始执行定时任务=================");
//
//        List<Future<Boolean>> futureList = new ArrayList<>();
//
//        for (int i=0;i<10;i++) {
//            //定义任务对象
//            SimpleThreadCallable simpleThreadCallable= new SimpleThreadCallable();
//            // 启动多个线程完成请求顺丰下单并获取运单
////            Future future = waybillThreadPool.submit(simpleThreadCallable);
//            Future<Boolean> future = waybillThreadPool.submit(simpleThreadCallable);
//            futureList.add(future);
//        }
//
//        for (Future<Boolean> future : futureList) {
//            try {
//                //阻塞方法
//                Boolean aBoolean = future.get(30, TimeUnit.SECONDS);
//            } catch (Exception e) {
//                log.info("任务执行出现异常");
//                future.cancel(true);
//            }
//        }
//        //执行到这里了说明上一轮执行完成
//        isRunning = false;
//    }

    /**
     * 定时获取面单
     * 首先获取打包完成的波次，调用顺丰接口进行下单，获取面单 pdf并存储至minio
     */
//    @Scheduled(cron = "0/20 * * * * ?")
    public void createWaybills(){
        //防止重入
        if(isRunning) {
            log.info("上一轮任务未完成，请勿重复执行");
            return;
        }
        //如果上一轮任务没有完成不在继续执行
        //标记任务开始执行
        log.info("==============开始执行定时任务=================");
        long startTime = System.currentTimeMillis();
        isRunning = true;
        try {
            if (waybillThreadPool.getQueue().size() > 1000) {
                log.info("当前任务队列已满，请稍后再试");
                return;
            }
            //查询一批没有生成运单的订单
            WmsOutOrders wmsOutOrders = new WmsOutOrders();
            wmsOutOrders.setStatus(WarehouseDictEnum.OUTBOUND_PACKED.getCode());
            wmsOutOrders.setCreatedWaybill("0");
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
            IPage<WmsOutOrders> wmsOutOrdersIPage = wmsOutOrdersService.queryList(wmsOutOrders, 1, 1000);
            InterceptorIgnoreHelper.clearIgnoreStrategy();
            List<WmsOutOrders> records = wmsOutOrdersIPage.getRecords();
            if (records.size() <= 0) {
                return;
            }
            //创建任务列表
            List<Future<Boolean>> futureList = new ArrayList<>(10);

            for (WmsOutOrders items : records) {
                //创建一个生成运单的任务
                WaybillThreadCallable waybillThreadCallable = new WaybillThreadCallable(wmsWaybillService, items.getId());
                // 启动多个线程完成请求顺丰下单并获取运单
//            Future future = waybillThreadPool.submit(simpleThreadCallable);
                FutureTask<Boolean> future = new FutureTask<>(waybillThreadCallable);
                futureList.add(future);
                waybillThreadPool.submit(future);
            }
            //等待所有任务执行完成最多30秒
            int successCount = 0;
            for (Future<Boolean> future : futureList) {
                try {
                    //阻塞方法
                    Boolean aBoolean = future.get(30, TimeUnit.SECONDS);
                    if (Boolean.TRUE.equals(aBoolean)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.info("任务执行出现异常",e);
                    future.cancel(true);
                }
            }
        } finally {
            //执行到这里了说明上一轮执行完成
            isRunning = false;
        }
    }

}