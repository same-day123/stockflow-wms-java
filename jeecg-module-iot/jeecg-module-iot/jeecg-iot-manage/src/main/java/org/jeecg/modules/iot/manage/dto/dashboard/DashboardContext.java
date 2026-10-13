package org.jeecg.modules.iot.manage.dto.dashboard;

import lombok.Data;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作台处理上下文
 *
 * @author muht
 * @create 2025/5/15 10:48
 */
@Data
public class DashboardContext {
    /**
     * 仓库集合
     */
    private List<String> warehouseCodeList;
    /**
     * 产品map
     * key=productCode
     */
    private Map<String, IotProduct> productMap;
    /**
     * 设备表
     * key=deviceCode
     */
    private Map<String, IotDevice> deviceMap;
    /**
     * 报警列表
     */
    private List<IotAlertLog> alertLogs;
    /**
     * 设备报警map
     * key=deviceCode
     */
    private Map<String, List<IotAlertLog>> deviceAlertLogMap;

    /**
     * 构造上下文
     *
     * @param products
     * @param devices
     */
    public DashboardContext(List<IotProduct> products, List<IotDevice> devices) {
        this.productMap = Optional.ofNullable(products).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(p -> p.getProductCode(), p -> p));
        this.deviceMap = Optional.ofNullable(devices).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(d -> d.getDeviceCode(), p -> p));
    }

    public void setAlertLogs(List<IotAlertLog> alertLogs) {
        this.setDeviceAlertLogMap(alertLogs);
    }

    private void setDeviceAlertLogMap(List<IotAlertLog> alertLogs) {
        this.alertLogs = Optional.ofNullable(alertLogs).orElse(new ArrayList<>());
        this.deviceAlertLogMap = Optional.ofNullable(alertLogs).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(IotAlertLog::getDeviceCode));
    }
}
