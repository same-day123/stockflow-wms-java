package org.jeecg.modules.iot.manage.service.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.iot.base.config.InfluxdbConfig;
import org.jeecg.modules.iot.manage.dto.DeviceLatestData;
import org.jeecg.modules.iot.manage.entity.InfluxQueryHelper;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.service.IInfluxdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muht
 * @create 2025/5/5 15:50
 */
@Slf4j
@Component
public class InfluxdbServiceImpl implements IInfluxdbService {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Autowired
    private InfluxdbConfig influxdbConfig;

    /**
     * 查询设备下上报的时序数据
     *
     * @param deviceCode
     * @param productCode
     */
    @Override
    public List<DeviceLatestData> queryDeviceLatestData(String deviceCode, String productCode) {
        QueryApi queryApi = influxDBClient.getQueryApi();

        // 构造 flux 查询语句
        InfluxQueryHelper influxQueryHelper = new InfluxQueryHelper(influxdbConfig.getBucket(),productCode, deviceCode, null);
        influxQueryHelper.setStart("-30d");
        String flux = influxQueryHelper.getFlux();

        List<FluxTable> tables = queryApi.query(flux);
        log.info("查询到设备指标数据为，{}", tables);

        List<DeviceLatestData> deviceLatestDataList = new ArrayList<>();
        // 每个 FluxTable 相当于一个指标数据
        for (FluxTable dataRecordsTable : tables) {
            List<FluxRecord> records = dataRecordsTable.getRecords();
            // 获取最后一次上报的数据
            FluxRecord latestData = records.get(records.size() - 1);
            DeviceLatestData deviceLatestData =
                    new DeviceLatestData(latestData.getField(), latestData.getValue(), latestData.getTime().getEpochSecond());
            deviceLatestDataList.add(deviceLatestData);
        }

        return deviceLatestDataList;
    }

    @Override
    public List<FluxRecord> queryHistoryDatas(IotDevice device, String modelCode, Integer pageNo, Integer pageSize) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        InfluxQueryHelper influxQueryHelper = new InfluxQueryHelper(influxdbConfig.getBucket(),device.getProductCode(), device.getDeviceCode(), modelCode);
        influxQueryHelper.setStart("-30d");
        List<FluxTable> datasTable = queryApi.query(influxQueryHelper.getFlux());
        if (CollectionUtils.isEmpty(datasTable)) {
            return List.of();
        }
        return datasTable.get(0).getRecords();
    }
}
