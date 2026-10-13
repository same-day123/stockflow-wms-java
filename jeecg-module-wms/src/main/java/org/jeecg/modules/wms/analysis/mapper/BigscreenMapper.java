package org.jeecg.modules.wms.analysis.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.analysis.vo.RankItemVO;
import org.jeecg.modules.wms.analysis.vo.TrendPoint;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;

import java.util.List;

public interface BigscreenMapper {

    /**
     * 统计代办任务列表
     */
    // 大屏按仓库统计。旧代码会被租户拦截器追加 tenant_id 条件，
    // 当历史业务数据的租户字段不一致时会导致首页统计全部为空。
    @InterceptorIgnore(tenantLine = "true")
    List<WmsTasks> countTaskList(WmsTasks wmsTasks);

    /**
     * 统计入库趋势
     */
    // 旧声明依赖编译器保留参数名，MyBatis 在部分环境下无法解析 warehouseId/year。
    // List<TrendPoint> sumInboundByMonth(String warehouseId,Integer year);
    // 旧声明保留：List<TrendPoint> sumInboundByMonth(String warehouseId,Integer year);
    @InterceptorIgnore(tenantLine = "true")
    List<TrendPoint> sumInboundByMonth(@Param("warehouseId") String warehouseId, @Param("year") Integer year);

    /**
     * 统计出库趋势
     */
    // 旧声明依赖编译器保留参数名。
    // List<TrendPoint> sumOutboundByMonth(String warehouseId,Integer year);
    @InterceptorIgnore(tenantLine = "true")
    List<TrendPoint> sumOutboundByMonth(@Param("warehouseId") String warehouseId, @Param("year") Integer year);
    /**
     * 货主出库排行榜
     */
    // 旧声明依赖编译器保留参数名。
    // List<RankItemVO> countConsigneeOutbound(String warehouseId);
    @InterceptorIgnore(tenantLine = "true")
    List<RankItemVO> countConsigneeOutbound(@Param("warehouseId") String warehouseId);



}
