package org.jeecg.modules.wms.waybill.task;

import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2025/7/10 13:23
 */
public class FutureTaskUtils {
    public static <V> V get(FutureTask<V> futureTask) {
        try {
            return futureTask.get();
        }catch (Exception e){
            return null;
        }
    }
    public static  <T> void  block(List<FutureTask<T>> futureTasks){
        for (FutureTask<T> futureTask : futureTasks) {
            get(futureTask);
        }
    }
}
