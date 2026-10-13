package org.jeecg.modules.iot.manage.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 监控图标类，用于组织监控列表返回数据，组织同设备多张图表
 *
 * @author muht
 * @date 2025/5/2 07:34
 */
@Data
public class MonitorCharts {
    /**
     * 图表数据
     */
    private final List<MonitorChart> charts = new ArrayList<>();
}
