package org.jeecg.modules.iot.manage.service;

import com.influxdb.query.FluxRecord;
import org.jeecg.modules.iot.manage.dto.DeviceHistoryData;
import org.jeecg.modules.iot.manage.dto.DeviceLatestData;
import org.jeecg.modules.iot.manage.entity.IotDevice;

import java.util.List;

/**
 * 时序数据库服务
 *
 * @author muht
 * @create 2025/5/5 15:49
 */
public interface IInfluxdbService {

    /**
     * 查询设备下上报的时序数据
     *
     * @param deviceCode
     * @param productCode
     */
    List<DeviceLatestData> queryDeviceLatestData(String deviceCode, String productCode);

    /**
     * 查询分页数据
     *
     * @param device
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<FluxRecord> queryHistoryDatas(IotDevice device, String modelCode, Integer pageNo, Integer pageSize);
}
