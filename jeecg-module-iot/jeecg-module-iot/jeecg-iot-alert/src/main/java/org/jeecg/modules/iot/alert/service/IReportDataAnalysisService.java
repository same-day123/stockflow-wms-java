package org.jeecg.modules.iot.alert.service;

import org.jeecg.modules.iot.base.mqtt.dto.TransferToAlertMessageDto;

/**
 * 上报数据分析服务
 *
 * @author muht
 * @create 2025/4/28 14:04
 */
public interface IReportDataAnalysisService {

    /**
     * 处理告警分析
     * 1、正常数据无需记录报警日志，但需要检查上次报警日志是否可以标记为"已解决"
     * 2、异常数据要记录报警日志
     */
    void reportAnalyse(TransferToAlertMessageDto messageDto);
}
