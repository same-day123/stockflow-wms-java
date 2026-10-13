package org.jeecg.modules.wms.wave.service;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 拣货任务服务接口
 * @date 2025/6/6 6:05
 */
public interface IPickingTasksService  {

    /**
     * 创建波次拣货明细
     */
    void createPickTask(List<String> waveIds);
    void createPickTask(String waveId);

    /**
     * 完成拣货
     */
    void completePickTask(List<String> waveIds);
    void completePickTask(String waveIds);

    /**
     * 拣货校验：已拣货数量+缺货数量不能大于计划数量
     * @param pickTaskId 拣货任务id
     */
    public void checkPickedQuantity(String pickTaskId);
    /**
     * 拣货
     * @param wmsTasksRecords
     */
    public void pick(WmsTasksRecords wmsTasksRecords);

    /**
     * 完成分拣
     * @param wmsOutOrdersItemsList 出库单明细id
     */
    public void completeSorting(List<WmsOutOrdersItems> wmsOutOrdersItemsList);

    /**
     * 获取拣货路径
     * @param waveId 波次id
     * @return svg代码
     */
    public String viewPickPath(String waveId);
}
