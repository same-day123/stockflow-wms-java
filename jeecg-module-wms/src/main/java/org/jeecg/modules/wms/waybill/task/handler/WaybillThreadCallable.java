package org.jeecg.modules.wms.waybill.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.waybill.service.IWmsWaybillService;

import java.util.concurrent.Callable;


/**
 * 波次任务执行体
 */
@Slf4j
public class WaybillThreadCallable implements Callable {

    /**
     * 出库单id
     */
    private String orderId;

    /**
     * 包裹service
     */
    private IWmsWaybillService wmsWaybillService;


    public WaybillThreadCallable(IWmsWaybillService wmsWaybillService, String orderId) {
        this.orderId = orderId;
        this.wmsWaybillService = wmsWaybillService;
    }

    @Override
    public Object call() throws Exception {
        try {
            //执行任务处理
//            wmsWaybillService.generateWaybillForOrder(orderId);
            return true;
        }catch (Exception e){
            log.error("WaveThreadCallable:{},e:",orderId, e);
            return false;
        }
    }
}