package org.jeecg.modules.wms.analysis.service;

import org.jeecg.modules.wms.analysis.vo.RankItemVO;
import org.jeecg.modules.wms.analysis.vo.TrendPoint;
import org.jeecg.modules.wms.analysis.vo.TodoTask;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 首页大屏接口
 * @date 2025/8/13 19:02
 */
public interface BigscreenService {

    /**
     * 查询待办任务
     */
    public List<TodoTask> findTodoTaskList(String warehouseId);

    /**
     * 获取入库趋势
     */
    public List<TrendPoint> findInboundTrend(String warehouseId,Integer year);

    /**
     * 获取入库趋势
     */
    public List<TrendPoint> findOutboundTrend(String warehouseId,Integer year);

    /**
     * 货主出库排行榜
     */
    public List<RankItemVO> findConsigneeRanking(String warehouseId);


}