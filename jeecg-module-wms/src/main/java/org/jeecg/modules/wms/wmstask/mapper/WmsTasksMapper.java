package org.jeecg.modules.wms.wmstask.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface WmsTasksMapper extends BaseMapper<WmsTasks> {
    /**
     * 查询任务列表
     */
    List<WmsTasks> queryTaskList(WmsTasks wmsTasks);
}
