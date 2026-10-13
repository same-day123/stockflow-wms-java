package org.jeecg.modules.wms.waybill.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
import org.jeecg.modules.wms.waybill.service.IWmsWaybillService;

import java.util.concurrent.Callable;


/**
 * 测试任务执行体
 */
@Slf4j
public class SimpleThreadCallable implements Callable {


   @Override
    public Object call() throws Exception {
        //当前线程名称
        try {
            String name = Thread.currentThread().getName();
            log.info("{},生成运单",name);
            Thread.sleep(10000);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}