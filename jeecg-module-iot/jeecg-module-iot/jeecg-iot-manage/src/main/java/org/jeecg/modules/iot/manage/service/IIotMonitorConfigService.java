package org.jeecg.modules.iot.manage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.manage.entity.IotMonitorConfig;
import org.jeecg.modules.iot.manage.vo.MonitorChart;

/**
 * @Description: 监控配置表
 * @Author: jeecg-boot
 * @Date:   2025-05-01
 * @Version: V1.0
 */
public interface IIotMonitorConfigService extends IService<IotMonitorConfig> {

    /**
     * 查询监控数据点
     * @param iotMonitorConfig
     * @param page
     * @return
     */
    IPage<MonitorChart> queryMonitorPoint(IotMonitorConfig iotMonitorConfig, Page<IotMonitorConfig> page, QueryWrapper<IotMonitorConfig> queryWrapper);
}
