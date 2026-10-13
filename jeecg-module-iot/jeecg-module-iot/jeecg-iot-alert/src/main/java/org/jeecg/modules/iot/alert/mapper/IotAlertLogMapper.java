package org.jeecg.modules.iot.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.iot.alert.entity.IotAlertLog;

import java.util.List;

/**
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date: 2025-05-06
 * @Version: V1.0
 */
public interface IotAlertLogMapper extends BaseMapper<IotAlertLog> {

    /**
     * 查询报警日志以及规则
     *
     * @return
     */
    List<IotAlertLog> selectUnsolvedAlertLogsAlongWithRule();

    /**
     * 批量自动更新报警状态
     *
     * @param ruleId 报警规则ID
     * @param processTime
     * @param processPlan
     */
    void autoRecoverAlertLog(String ruleId, String processTime, String processPlan);
}
