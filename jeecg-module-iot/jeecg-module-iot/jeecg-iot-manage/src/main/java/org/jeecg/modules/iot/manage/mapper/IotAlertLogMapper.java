package org.jeecg.modules.iot.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.iot.manage.dto.AlertLogCount;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;

import java.util.List;

/**
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date: 2025-05-06
 * @Version: V1.0
 */
public interface IotAlertLogMapper extends BaseMapper<IotAlertLog> {

    /**
     * 统计各处理进度数量
     *
     * @return
     */
    List<AlertLogCount> getAlertCount(@Param(value = "orgCodes") List<String> orgCodes,
                                      @Param(value = "log") IotAlertLog alertLog);
}
