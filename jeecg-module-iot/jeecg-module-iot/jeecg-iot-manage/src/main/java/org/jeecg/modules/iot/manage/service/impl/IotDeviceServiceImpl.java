package org.jeecg.modules.iot.manage.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.influxdb.query.FluxRecord;
import org.jeecg.modules.iot.base.mqtt.constant.CRUD;
import org.jeecg.modules.iot.base.mqtt.constant.EnableEnum;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.manage.constant.DeviceStatusEnum;
import org.jeecg.modules.iot.manage.constant.ModelTypeEnum;
import org.jeecg.modules.iot.manage.convert.DeviceConverter;
import org.jeecg.modules.iot.manage.dto.CtrlDeviceReq;
import org.jeecg.modules.iot.manage.dto.DeviceHistoryData;
import org.jeecg.modules.iot.manage.dto.DeviceLatestData;
import org.jeecg.modules.iot.manage.entity.*;
import org.jeecg.modules.iot.manage.mapper.*;
import org.jeecg.modules.iot.manage.mqtt.publish.MqttPublisher;
import org.jeecg.modules.iot.manage.service.CacheService;
import org.jeecg.modules.iot.manage.service.IInfluxdbService;
import org.jeecg.modules.iot.manage.service.IIotDeviceService;
import org.jeecg.modules.iot.manage.service.IIotProductModelService;
import org.jeecg.modules.iot.manage.vo.DeviceFieldModel;
import org.jeecg.modules.iot.manage.vo.DeviceServiceModel;
import org.jeecg.modules.iot.manage.vo.IotDeviceDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
@Slf4j
@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IIotDeviceService {

    @Autowired
    private IotProductMapper iotProductMapper;

    @Autowired
    private IIotProductModelService iotProductModelService;

    @Autowired
    private IInfluxdbService influxdbService;

    @Autowired
    private MqttPublisher mqttPublisher;

    @Autowired
    private IotWarehousesMapper warehousesMapper;

    @Autowired
    private IotAlertRuleMapper alertRuleMapper;

    @Autowired
    private IotMonitorConfigMapper monitorConfigMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public <E extends IPage<IotDevice>> E page(E page, Wrapper<IotDevice> queryWrapper) {
        E pageRes = super.page(page, queryWrapper);
        // 查询缓存中设备状态
        List<IotDevice> records = pageRes.getRecords();
        // 处理设备状态
        this.processDeviceStatus(records);
        // 拼接所属仓库
        pageRes.getRecords().forEach(d -> d.setDeviceWithWarehouseName(d.getDeviceName() + "(" + Optional.ofNullable(d.getWarehouseName()).orElse("-") + ")"));
        return pageRes;
    }

    @Override
    public boolean save(IotDevice entity) {
        this.checkUnique(entity);
        // 关联 productName
        IotProduct iotProduct = iotProductMapper.selectOne(new LambdaQueryWrapper<IotProduct>()
                .eq(IotProduct::getProductCode, entity.getProductCode()));
        entity.setProductName(iotProduct.getProductName());
        entity.setDeviceStatus(DeviceStatusEnum.UNACTIVATE.getStatus());
        // 当前租户ID
        entity.setTenantId(Integer.valueOf(TenantContext.getTenant()));
        // 部门code和仓库code保持一致
        entity.setSysOrgCode(entity.getWarehouseCode());

        // 关联仓库name
        WmsWarehouses wmsWarehouse = warehousesMapper.selectOne(new LambdaQueryWrapper<WmsWarehouses>().eq(WmsWarehouses::getWarehouseCode, entity.getWarehouseCode()));
        if (wmsWarehouse != null) {
            entity.setWarehouseName(wmsWarehouse.getWarehouseName());
            entity.setDeviceWithWarehouseName(entity.getDeviceName() + "(" + wmsWarehouse.getWarehouseName() + ")");
        }

        boolean saveRes = super.save(entity);

        // 缓存处理
        cacheService.processDeviceCache(CRUD.CREATE, getById(entity.getId()));

        return saveRes;
    }

    @Override
    public boolean updateById(IotDevice entity) {
        // 启用/禁用逻辑
        if (entity.getDeviceName() == null || entity.getDeviceCode() == null) {
            return this.switchEnable(entity);
        }

        this.checkUnique(entity);

        // 关联 productName
        IotProduct iotProduct = iotProductMapper.selectOne(new LambdaQueryWrapper<IotProduct>()
                .eq(IotProduct::getProductCode, entity.getProductCode()));
        entity.setProductName(iotProduct.getProductName());
        entity.setSysOrgCode(entity.getWarehouseCode());
        // 关联仓库name
        WmsWarehouses wmsWarehouses = warehousesMapper.selectOne(new LambdaQueryWrapper<WmsWarehouses>()
                .eq(WmsWarehouses::getWarehouseCode, entity.getWarehouseCode()));
        if (wmsWarehouses != null) {
            entity.setWarehouseName(wmsWarehouses.getWarehouseName());
        }

        // 更新缓存
        cacheService.processDeviceCache(CRUD.UPDATE, entity);

        return super.updateById(entity);
    }

    @Override
    public IotDeviceDetail getDetailById(String id) {
        // 基础设备信息
        IotDevice device = super.getById(id);
        this.processDeviceStatus(List.of(device));
        IotDeviceDetail deviceDetail = new IotDeviceDetail();
        BeanUtils.copyProperties(device, deviceDetail);

        LambdaQueryWrapper<IotProduct> productQuery = new LambdaQueryWrapper<>();
        productQuery.eq(IotProduct::getProductCode, device.getProductCode());
        // 查询产品信息
        IotProduct product = iotProductMapper.selectOne(productQuery);
        deviceDetail.setNetworkMode(product.getNetworkMode());
        deviceDetail.setAuthType(product.getAuthType());
        deviceDetail.setNodeType(product.getNodeType());
        deviceDetail.setImageUrl(product.getImageUrl());
        return deviceDetail;
    }

    @Override
    public boolean removeById(Serializable id) {
        IotDevice device = super.getById(id);
        // 检查报警规则
        List<IotAlertRule> iotAlertRules = alertRuleMapper.selectList(new LambdaQueryWrapper<IotAlertRule>()
                .eq(IotAlertRule::getDeviceCode, device.getDeviceCode()));
        if (!CollectionUtils.isEmpty(iotAlertRules)) {
            throw new BusiException("该设备已绑定报警规则");
        }

        cacheService.processDeviceCache(CRUD.DELETE, device);
        // 同步删除该设备下的所有监控图表
        monitorConfigMapper.delete(new LambdaQueryWrapper<IotMonitorConfig>()
                .eq(IotMonitorConfig::getDeviceCode, device.getDeviceCode()));

        return super.removeById(id);
    }

    @Override
    public IPage<DeviceFieldModel> queryDeviceFieldsPage(String id, String name, Integer pageNo, Integer pageSize) {
        // 基础设备信息
        IotDevice device = super.getById(id);
        String productCode = device.getProductCode();
        // 查询属性物模型
        Page<IotProductModel> modelPage = this.queryModelsPage(productCode, name, pageNo, pageSize, ModelTypeEnum.FIELD);

        // 查询最近一次指标数据
        List<DeviceLatestData> deviceLatestData = influxdbService.queryDeviceLatestData(device.getDeviceCode(), productCode);
        log.info("查询到设备最新指标数据为，deviceLatestData={}", JSON.toJSONString(deviceLatestData));

        // 按指标划分 Map
        Map<String, DeviceLatestData> latestDataMap = deviceLatestData.stream()
                .collect(Collectors.toMap(d -> d.getField(), d -> d));

        List<DeviceFieldModel> fieldModels = new ArrayList<>();
        Page<DeviceFieldModel> fieldModelsPage = new Page<>(pageNo, pageSize);
        fieldModelsPage.setTotal(modelPage.getTotal());
        fieldModelsPage.setPages(modelPage.getPages());
        fieldModelsPage.setRecords(fieldModels);

        if (modelPage.getTotal() > 0) {
            modelPage.getRecords().forEach(model -> {
                DeviceFieldModel deviceFieldModel = new DeviceFieldModel();
                deviceFieldModel.setModelCode(model.getCode());
                deviceFieldModel.setModelName(model.getModelName());
                deviceFieldModel.setUnit(model.getUnit());
                DeviceLatestData fieldData = latestDataMap.get(model.getCode());
                if (fieldData != null) {
                    deviceFieldModel.setValue(fieldData.getValue());
                    LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(fieldData.getTime()), ZoneId.systemDefault());
                    deviceFieldModel.setUpdateTime(LocalDateTimeUtil.format(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                fieldModels.add(deviceFieldModel);
            });
        }

        return fieldModelsPage;
    }

    @Override
    public IPage<DeviceServiceModel> queryDeviceServicesPage(String id, String name, Integer pageNo, Integer pageSize) {
        // 基础设备信息
        IotDevice device = super.getById(id);
        String productCode = device.getProductCode();

        // 查询设备服务
        Page<IotProductModel> modelPage = this.queryModelsPage(productCode, name, pageNo, pageSize, ModelTypeEnum.SERVICE);

        List<DeviceServiceModel> serviceModels = new ArrayList<>();
        Page<DeviceServiceModel> serviceModelsPage = new Page<>(pageNo, pageSize);
        serviceModelsPage.setTotal(modelPage.getTotal());
        serviceModelsPage.setPages(modelPage.getPages());
        serviceModelsPage.setRecords(serviceModels);

        // 处理指标与功能
        if (modelPage.getTotal() > 0) {
            modelPage.getRecords().forEach(model -> {
                DeviceServiceModel deviceServiceModel = new DeviceServiceModel();
                deviceServiceModel.setModelCode(model.getCode());
                deviceServiceModel.setModelName(model.getModelName());
                deviceServiceModel.setModelDesc(model.getModelDesc());
                deviceServiceModel.setCommand(model.getCommand());
                deviceServiceModel.setNeedExtraArg(model.getNeedExtraArg());
                deviceServiceModel.setArgType(model.getArgType());
                serviceModels.add(deviceServiceModel);
            });
        }

        return serviceModelsPage;
    }

    @Override
    public String ctrlDevice(CtrlDeviceReq ctrlDeviceReq) {
        IotDevice device = super.getById(ctrlDeviceReq.getDeviceId());

        if (EnableEnum.NO.getValue().equals(device.getIsEnable())) {
            throw new BusiException("设备禁用状态下无法控制");
        }

        LambdaQueryWrapper<IotProductModel> modelQuery = new LambdaQueryWrapper<>();
        modelQuery.allEq(Map.of(
                IotProductModel::getProductCode, device.getProductCode(),
                IotProductModel::getCode, ctrlDeviceReq.getModelCode()));
        IotProductModel serviceModel = iotProductModelService.getOne(modelQuery);
        if (serviceModel == null) {
            throw new RuntimeException("未找到该功能！" + ctrlDeviceReq.getModelCode());
        }

        String ctrlTopic = "wms/iot/ctrl/" + device.getProductCode();
        // 实际业务场景的指令与参数组装需要根据具体的设备及说明书调整，这里只简单拼接做为演示
        String cmd = serviceModel.getCommand() + ctrlDeviceReq.getArgs();
        boolean publishRes = mqttPublisher.publish(ctrlTopic, cmd);

        return publishRes ? "发送成功" : "发送失败";
    }

    @Override
    public IPage<DeviceHistoryData> queryHistoryData(String id, String modelCode, Integer pageNo, Integer pageSize) {
        IotDevice device = super.getById(id);
        LambdaQueryWrapper<IotProductModel> modelQuery = new LambdaQueryWrapper<>();
        modelQuery.allEq(Map.of(
                IotProductModel::getProductCode, device.getProductCode(),
                IotProductModel::getCode, modelCode));
        IotProductModel model = iotProductModelService.getOne(modelQuery);

        // 查询influxdb
        List<FluxRecord> fluxRecords = influxdbService.queryHistoryDatas(device, modelCode, pageNo, pageSize);
        // 内存分页
        IPage<DeviceHistoryData> page = new Page<>(pageNo, pageSize);
        if (CollectionUtils.isEmpty(fluxRecords)) {
            page.setPages(0);
            page.setTotal(0);
            page.setRecords(List.of());
            return page;
        }

        Integer currentPageFirstNo = pageSize * (pageNo - 1) + 1;
        List<DeviceHistoryData> pageRecords = fluxRecords.stream()
                .sorted(Comparator.comparing(FluxRecord::getTime, Comparator.reverseOrder()))
                .skip((long) (pageNo - 1) * pageSize)
                .limit(pageSize)
                .map(d -> {
                    DeviceHistoryData historyData = new DeviceHistoryData();
                    historyData.setModelName(model.getModelName());
                    String time = LocalDateTimeUtil.formatNormal(LocalDateTime.ofInstant(d.getTime(), ZoneId.systemDefault()));
                    historyData.setTime(time);
                    historyData.setValue(d.getValue() + model.getUnit());
                    return historyData;
                })
                .collect(Collectors.toList());
        // 设置当前页序号
        for (int i = 0; i < pageRecords.size(); i++) {
            pageRecords.get(i).setNo(currentPageFirstNo + i);
        }
        page.setRecords(pageRecords);
        page.setTotal(fluxRecords.size());
        page.setPages((fluxRecords.size() + pageSize - 1) / pageSize);

        return page;
    }

    /**
     * 校验唯一
     */
    private void checkUnique(IotDevice device) {
        if (device.getDeviceCode() == null) {
            return;
        }
        LambdaQueryWrapper<IotDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotDevice::getDeviceCode, device.getDeviceCode())
                .or(QueryWrapper -> QueryWrapper.eq(IotDevice::getDeviceName, device.getDeviceName()));
        IotDevice dbDevice = super.getOne(queryWrapper);
        if (dbDevice != null && !dbDevice.getId().equals(device.getId())) {
            String keyword = "设备编码重复";
            if (dbDevice.getDeviceName().equals(device.getDeviceName())) {
                keyword = "设备名称重复";
            }
            throw new BusiException(keyword);
        }
    }

    /**
     * 查询物模型分页
     *
     * @param productCode
     * @param name
     * @param pageNo
     * @param pageSize
     * @param modelType
     * @return
     */
    private Page<IotProductModel> queryModelsPage(String productCode, String name, Integer pageNo, Integer pageSize, ModelTypeEnum modelType) {
        return iotProductModelService.page(new Page<>(pageNo, pageSize), new LambdaQueryWrapper<IotProductModel>()
                .eq(IotProductModel::getType, modelType.getType())
                .eq(IotProductModel::getProductCode, productCode)
                .like(IotProductModel::getModelName, Optional.ofNullable(name).orElse("")));
    }

    /**
     * 设备启用/禁用切换
     *
     * @param device
     */
    private boolean switchEnable(IotDevice device) {
        IotDevice dbDevice = getById(device.getId());
        dbDevice.setIsEnable(device.getIsEnable());
        // 启用时新增，禁用时删除
        if (EnableEnum.YES.getValue().equals(device.getIsEnable())) {
            cacheService.processDeviceCache(CRUD.CREATE, dbDevice);
        } else {
            cacheService.processDeviceCache(CRUD.DELETE, dbDevice);
        }

        return super.updateById(device);
    }

    /**
     * 处理设备状态
     *
     * @param devices
     */
    private void processDeviceStatus(List<IotDevice> devices) {
        // 查询设备状态表缓存
        Map<Object, Object> deviceStatusTable = redisUtil.hmget(RedisConst.DEVICE_STATUS_TABLE_KEY);
        devices.forEach(device -> {
            if (EnableEnum.NO.getValue().equals(device.getIsEnable())) {
                // 禁用状态
                device.setDeviceStatus(DeviceStatusEnum.DISABLE.getStatus());
            } else {
                Integer status = (Integer) deviceStatusTable.get(device.getDeviceCode());
                if (status != null) {
                    device.setDeviceStatus(status);
                }
            }
        });
    }
}
