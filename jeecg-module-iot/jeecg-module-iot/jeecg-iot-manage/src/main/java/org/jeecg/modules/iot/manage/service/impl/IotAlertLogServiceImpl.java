package org.jeecg.modules.iot.manage.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.base.mqtt.constant.ProcessStatusEnum;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.manage.dto.AlertLogCount;
import org.jeecg.modules.iot.manage.dto.AlertLogPageResp;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotProductModel;
import org.jeecg.modules.iot.manage.mapper.IotAlertLogMapper;
import org.jeecg.modules.iot.manage.mapper.IotDeviceMapper;
import org.jeecg.modules.iot.manage.mapper.IotProductModelMapper;
import org.jeecg.modules.iot.manage.service.IIotAlertLogService;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date: 2025-05-06
 * @Version: V1.0
 */
@Service
public class IotAlertLogServiceImpl extends ServiceImpl<IotAlertLogMapper, IotAlertLog> implements IIotAlertLogService {

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private IotProductModelMapper iotProductModelMapper;

    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public AlertLogPageResp pageWithProcessStatusCount(Page<IotAlertLog> page, IotAlertLog iotAlertLog, Wrapper<IotAlertLog> queryWrapper) {
        List<String> warehouseCodes = warehouseService.getWarehouseCodes(iotAlertLog.getWarehouseCodes());
        Page<IotAlertLog> logPage = super.page(page, queryWrapper);

        // 各处理进度数量统计
        List<AlertLogCount> alertCounts = baseMapper.getAlertCount(warehouseCodes, iotAlertLog);
        if (CollectionUtils.isEmpty(alertCounts)) {
            return new AlertLogPageResp(logPage);
        }
        // 封装统计信息
        Map<ProcessStatusEnum, Integer> processCountMap = alertCounts.stream()
                .collect(Collectors.toMap(c -> ProcessStatusEnum.getByValue(c.getProcessStatus()), c -> c.getCount()));

        AlertLogPageResp logPageResp = new AlertLogPageResp(logPage);
        logPageResp.setUnsolvedCount(Optional.ofNullable(processCountMap.get(ProcessStatusEnum.UNSOLVED)).orElse(0));
        logPageResp.setSolvedCount(Optional.ofNullable(processCountMap.get(ProcessStatusEnum.RESOLVED)).orElse(0));
        logPageResp.setIgnoreCount(Optional.ofNullable(processCountMap.get(ProcessStatusEnum.IGNORE)).orElse(0));
        logPageResp.setAllCount(logPageResp.getUnsolvedCount() + logPageResp.getSolvedCount() + logPageResp.getIgnoreCount());
        return logPageResp;
    }

    @Override
    public IotAlertLog getById(Serializable id) {
        IotAlertLog alertLog = super.getById(id);
        LambdaQueryWrapper<IotDevice> deviceQuery = new LambdaQueryWrapper<>();
        deviceQuery.eq(IotDevice::getDeviceCode, alertLog.getDeviceCode());
        IotDevice deviceInfo = iotDeviceMapper.selectOne(deviceQuery);

        LambdaQueryWrapper<IotProductModel> modelQuery = new LambdaQueryWrapper<>();
        modelQuery.eq(IotProductModel::getProductCode, alertLog.getProductCode());
        modelQuery.eq(IotProductModel::getCode, alertLog.getModelCode());
        IotProductModel modelInfo = iotProductModelMapper.selectOne(modelQuery);
        // 查询额外信息
        alertLog.setProductName(deviceInfo.getProductName());
        alertLog.setWarehouseName(deviceInfo.getWarehouseName());
        alertLog.setUnit(Optional.ofNullable(modelInfo).orElse(new IotProductModel()).getUnit());
        return alertLog;
    }

    @Override
    public boolean updateById(IotAlertLog entity) {
        IotAlertLog alertLog = super.getById(entity);

        // 管理端操作处理
        if (ProcessStatusEnum.RESOLVED.getValue().equals(entity.getProcessStatus())
                || ProcessStatusEnum.IGNORE.getValue().equals(entity.getProcessStatus())) {
            // 设置处理时间
            entity.setProcessTime(LocalDateTimeUtil.formatNormal(LocalDateTime.now()));

            // 重置报警状态，不处理沉默周期缓存，沉默周期缓存依然可以生效
            String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, alertLog.getDeviceCode(), alertLog.getRuleId());
            redisUtil.hset(counterKey, "alertStatus", 0);

            // 清除沉默周期
            redisUtil.del(String.format(RedisConst.ALERT_QUIET_CACHE_KEY, alertLog.getRuleId()));
        }

        return super.updateById(entity);
    }
}
