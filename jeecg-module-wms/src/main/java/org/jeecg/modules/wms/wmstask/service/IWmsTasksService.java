package org.jeecg.modules.wms.wmstask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface IWmsTasksService extends IService<WmsTasks> {
    /**
     * 创建收货任务
     * @param inOrderId 入库单id
     * @param operator 执行人
     */
    public void createReceiveTask(String inOrderId,String operator);

    /**
     * 生成任务编号
     * 规则: TSK+年月日+5位序号，序号使用redis自增序号实现
     */
    public String generateTaskCode();

    /**
     * 查询任务列表
     * @param wmsTasks
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<WmsTasks> list(WmsTasks wmsTasks, Integer pageNo, Integer pageSize);

    /**
     * 收货
     * @param wmsTasksRecords
     */
    void receive(WmsTasksRecords wmsTasksRecords);

    /**
     * 执行任务
     */
    WmsTasks execute(WmsTasksRecords wmsTasksRecords);

    /**
     * 创建上架任务
     * @param orderId
     */
    public void createPutawayTask(String orderId);

    /**
     * 上架方法
     */
    public void putaway(WmsTasksRecords wmsTasksRecords);
}
