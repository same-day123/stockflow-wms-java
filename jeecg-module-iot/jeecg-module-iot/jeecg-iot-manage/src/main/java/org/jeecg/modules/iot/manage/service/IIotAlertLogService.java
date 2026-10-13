package org.jeecg.modules.iot.manage.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.manage.dto.AlertLogPageResp;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;

/**
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date:   2025-05-06
 * @Version: V1.0
 */
public interface IIotAlertLogService extends IService<IotAlertLog> {

    AlertLogPageResp pageWithProcessStatusCount(Page<IotAlertLog> page, IotAlertLog iotAlertLog, Wrapper<IotAlertLog> queryWrapper);
}
