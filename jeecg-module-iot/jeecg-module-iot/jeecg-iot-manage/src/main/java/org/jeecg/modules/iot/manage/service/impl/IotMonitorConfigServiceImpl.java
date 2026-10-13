package org.jeecg.modules.iot.manage.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.jeecg.modules.iot.base.config.InfluxdbConfig;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.modules.iot.manage.constant.SeriesDataStartEnum;
import org.jeecg.modules.iot.manage.entity.InfluxQueryHelper;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotMonitorConfig;
import org.jeecg.modules.iot.manage.entity.IotProductModel;
import org.jeecg.modules.iot.manage.mapper.IotMonitorConfigMapper;
import org.jeecg.modules.iot.manage.mapper.IotProductModelMapper;
import org.jeecg.modules.iot.manage.service.IIotDeviceService;
import org.jeecg.modules.iot.manage.service.IIotMonitorConfigService;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.jeecg.modules.iot.manage.vo.MonitorChart;
import org.jeecg.modules.iot.manage.vo.MonitorPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 监控配置表
 * @Author: jeecg-boot
 * @Date: 2025-05-01
 * @Version: V1.0
 */
@Slf4j
@Service
public class IotMonitorConfigServiceImpl extends ServiceImpl<IotMonitorConfigMapper, IotMonitorConfig> implements IIotMonitorConfigService {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Autowired
    private IotProductModelMapper modelMapper;

    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private InfluxdbConfig influxdbConfig;
    /**
     * 查询监控数据点
     *
     * @param iotMonitorConfig
     * @param page
     * @return
     */
    @Override
    public IPage<MonitorChart> queryMonitorPoint(IotMonitorConfig iotMonitorConfig,
                                                 Page<IotMonitorConfig> page,
                                                 QueryWrapper<IotMonitorConfig> queryWrapper) {
        queryWrapper.orderByDesc("create_time");
        List<String> warehouseCodes = warehouseService.getWarehouseCodes(iotMonitorConfig.getWarehouseCodes());

        // 查询监控配置分页数据
        Page<IotMonitorConfig> monitorConfigPage = super.page(page, queryWrapper);
        List<IotMonitorConfig> monitorConfigs = monitorConfigPage.getRecords();
        log.info("监控配置列表，{}", monitorConfigs);
        // 按监控指标(modelCode)组织为映射表
        Map<String, IotMonitorConfig> monitorConfigMap = monitorConfigs.stream()
                .collect(Collectors.toMap(
                        config -> config.getDeviceCode() + ":" + config.getModelCode(),
                        config -> config,
                        (c1, c2) -> c1,
                        () -> new LinkedHashMap<>()));

        // 监控图表Page
        Page<MonitorChart> chartPage = new Page<>(monitorConfigPage.getCurrent(), monitorConfigPage.getSize());
        List<MonitorChart> monitorChartRecords = new ArrayList<>();
        chartPage.setRecords(monitorChartRecords);
        chartPage.setTotal(monitorConfigPage.getTotal());
        chartPage.setPages(monitorConfigPage.getPages());

        if (CollectionUtils.isEmpty(monitorConfigs)) {
            return chartPage;
        }

        QueryApi queryApi = influxDBClient.getQueryApi();
        // 按单指标查询时序数据
        monitorConfigMap.entrySet()
                .forEach(e -> {
                    String field = e.getKey().split(":")[1];
                    IotMonitorConfig config = e.getValue();
                    // 构造 flux 查询语句
                    InfluxQueryHelper influxQueryHelper = new InfluxQueryHelper(influxdbConfig.getBucket(),config.getProductCode(), config.getDeviceCode(), field);
                    influxQueryHelper.setStart(SeriesDataStartEnum.ONE_HOUR.getVal());
                    List<FluxTable> tables = queryApi.query(influxQueryHelper.getFlux());
                    log.info("查询到时序数据为，{}", tables);

                    // 构造监控图表对象
                    MonitorChart monitorChart = new MonitorChart()
                            .setId(config.getId())
                            .setProductCode(config.getProductCode())
                            .setProductName(config.getProductName())
                            .setDeviceCode(config.getDeviceCode())
                            .setDeviceName(config.getDeviceName())
                            .setDeviceWithWarehouseName(config.getDeviceWithWarehouseName())
                            .setModelCode(config.getModelCode())
                            .setModelName(config.getModelName())
                            .setUnit(config.getUnit())
                            .setPoints(new ArrayList<>());
                    monitorChartRecords.add(monitorChart);

                    if (CollectionUtils.isEmpty(tables)) {
                        return;
                    }

                    // 如果有数据，因为是以单指标获取，则直接get(0)
                    List<FluxRecord> fluxRecords = tables.get(0).getRecords();
                    for (FluxRecord record : fluxRecords) {
                        LocalDateTime localDateTime = record.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        String formatTime = LocalDateTimeUtil.format(localDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        // 将点位放入图表
                        MonitorPoint monitorPoint = new MonitorPoint()
                                .setModelCode(record.getField())
                                .setTime(formatTime)
                                .setValue(record.getValue());
                        monitorChart.getPoints().add(monitorPoint);
                    }
                });

        return chartPage;
    }

    @Override
    public boolean save(IotMonitorConfig entity) {
        this.check(entity);
        this.setModelInfo(entity);
        entity.setTenantId(Integer.valueOf(TenantContext.getTenant()));
        IotDevice device = deviceService.getOne(new LambdaQueryWrapper<IotDevice>().eq(IotDevice::getDeviceCode, entity.getDeviceCode()));
        entity.setSysOrgCode(device.getWarehouseCode());
        entity.setDeviceWithWarehouseName(device.getDeviceName() + "(" + Optional.ofNullable(device.getWarehouseName()).orElse("-") + ")");
        return super.save(entity);
    }

    @Override
    public boolean updateById(IotMonitorConfig entity) {
        this.check(entity);
        this.setModelInfo(entity);
        IotDevice device = deviceService.getOne(new LambdaQueryWrapper<IotDevice>().eq(IotDevice::getDeviceCode, entity.getDeviceCode()));
        entity.setDeviceWithWarehouseName(entity.getDeviceName() + "(" + device.getWarehouseName() + ")");
        return super.updateById(entity);
    }

    /**
     * 校验监控指标不能重复
     * 相同设备+相同功能
     *
     * @param iotMonitorConfig
     */
    private void check(IotMonitorConfig iotMonitorConfig) {
        if (iotMonitorConfig.getDeviceCode() == null || iotMonitorConfig.getModelCode() == null) {
            throw new BusiException("监控设备和功能不能为空");
        }
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<IotMonitorConfig>()
                .ne(IotMonitorConfig::getId, iotMonitorConfig.getId())
                .eq(IotMonitorConfig::getDeviceCode, iotMonitorConfig.getDeviceCode())
                .eq(IotMonitorConfig::getModelCode, iotMonitorConfig.getModelCode()));
        if (exists) {
            throw new BusiException("不能重复添加监控");
        }
    }

    /**
     * 设置物模型相关属性
     *
     * @param entity
     */
    private void setModelInfo(IotMonitorConfig entity) {
        IotProductModel model = modelMapper.selectOne(new LambdaQueryWrapper<IotProductModel>()
                .eq(IotProductModel::getProductCode, entity.getProductCode())
                .eq(IotProductModel::getCode, entity.getModelCode()));
        if (model != null) {
            entity.setModelName(model.getModelName());
            entity.setUnit(model.getUnit());
        }
    }
}
