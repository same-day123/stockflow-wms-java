package org.jeecg.modules.iot.manage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.iot.base.mqtt.constant.CRUD;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.manage.constant.DeviceStatusEnum;
import org.jeecg.modules.iot.manage.entity.IotAlertRule;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotProductModel;
import org.jeecg.modules.iot.manage.mapper.IotDeviceMapper;
import org.jeecg.modules.iot.manage.mapper.IotProductModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缓存统一操作
 *
 * @author muht
 * @create 2025/5/6 16:08
 */
@Slf4j
@Component
public class CacheService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IotDeviceMapper deviceMapper;

    @Autowired
    private IotProductModelMapper productModelMapper;

    /**
     * 处理设备缓存
     *
     * @param crud
     * @param device 全量设备信息
     */
    public void processDeviceCache(CRUD crud, IotDevice device) {
        try {
            String deviceKey = String.format(RedisConst.DEVICE_ENTITY_CACHE_KEY, device.getDeviceCode());
            if (CRUD.CREATE.equals(crud) || CRUD.UPDATE.equals(crud)) {
                redisUtil.hmset(deviceKey, JSONObject.parseObject(JSON.toJSONString(device), Map.class));
            } else {
                redisUtil.del(deviceKey);
            }
        } catch (Exception e) {
            log.error("同步设备缓存异常！", e);
        }
    }

    /**
     * 初始化全量设备状态表
     */
    public synchronized Map<String, Object> initDeviceStatusTable() {
        Map<Object, Object> onlineMap = redisUtil.hmget(RedisConst.DEVICE_STATUS_TABLE_KEY);
        if (CollectionUtils.isEmpty(onlineMap)) {
            List<IotDevice> devices = deviceMapper.selectList(
                    new LambdaQueryWrapper<IotDevice>().select(IotDevice.class, o -> o.getColumn().equals("device_code")));
            List<String> deviceCodes = devices.stream().map(IotDevice::getDeviceCode).toList();
            log.info("查询到全量设备编码列表为：{}", deviceCodes);
            Map<String, Object> onlineTableMap = deviceCodes.stream()
                    .collect(Collectors.toMap(code -> code, code -> DeviceStatusEnum.OFFLINE.getStatus()));
            redisUtil.hmset(RedisConst.DEVICE_STATUS_TABLE_KEY, onlineTableMap);
            return onlineTableMap;
        }

        return onlineMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
    }

    /**
     * 获取设备信息缓存
     * 双重检查实现数据库自动同步机制
     *
     * @param deviceCode
     * @return
     */
    public IotDevice getDevice(String deviceCode) {
        String deviceKey = String.format(RedisConst.DEVICE_ENTITY_CACHE_KEY, deviceCode);
        Map<Object, Object> d = redisUtil.hmget(deviceKey);
        IotDevice device;
        if (CollectionUtils.isEmpty(d)) {
            synchronized (this) {
                d = redisUtil.hmget(deviceKey);
                if (CollectionUtils.isEmpty(d)) {
                    // 数据库同步缓存
                    device = deviceMapper.selectOne(new LambdaQueryWrapper<IotDevice>().eq(IotDevice::getDeviceCode, deviceCode));
                    if (device != null) {
                        // 放入缓存
                        this.processDeviceCache(CRUD.CREATE, device);
                    } else {
                        // 未查询到该设备
                        return null;
                    }
                } else {
                    // 二次检查，查询到设备缓存，直接返回
                    device = JSONObject.parseObject(JSON.toJSONString(d), IotDevice.class);
                }
            }
        } else {
            device = JSONObject.parseObject(JSON.toJSONString(d), IotDevice.class);
        }
        return device;
    }



    /**
     * 查询物模型映射缓存
     *
     * @param productCode
     * @return
     */
    public Map<String, String> getModelCodesByProductCode(String productCode) {
        // 产品-物模型映射关系缓存
        String mappingCacheKey = String.format(RedisConst.PRODUCT_MODEL_MAPPING_CACHE_KEY, productCode);
        Map<Object, Object> modelsMap = redisUtil.hmget(mappingCacheKey);
        Map<String, String> modelCodeMap = new HashMap<>();
        if (CollectionUtils.isEmpty(modelsMap)) {
            synchronized (this) {
                modelsMap = redisUtil.hmget(mappingCacheKey);
                if (CollectionUtils.isEmpty(modelsMap)) {
                    // 取DB，并同步Redis
                    List<IotProductModel> models = productModelMapper.selectList(new LambdaQueryWrapper<IotProductModel>()
                            .eq(IotProductModel::getProductCode, productCode));
                    // 物模型列表转为 Map
                    if (CollectionUtils.isEmpty(models)) {
                        return modelCodeMap;
                    } else {
                        redisUtil.hmset(mappingCacheKey,
                                models.stream().collect(Collectors.toMap(m -> m.getCode(), m -> m.getType())),
                                RedisConst.EXPIRE_60s);
                    }
                } else {
                    modelCodeMap = JSONObject.parseObject(JSON.toJSONString(modelsMap), Map.class);
                }
            }
        } else {
            modelCodeMap = JSONObject.parseObject(JSON.toJSONString(modelsMap), Map.class);
        }

        return modelCodeMap;
    }

//    public void processAlertRuleCache(CRUD crud, IotAlertRule alertRule) {
//        try {
//            String ruleCacheKey = String.format(RedisConst.ALERT_RULE_KEY, alertRule.getDeviceCode(), alertRule.getId());
//            if (CRUD.CREATE.equals(crud) || CRUD.UPDATE.equals(crud)) {
//                redisUtil.hmset(ruleCacheKey, JSONObject.parseObject(JSON.toJSONString(alertRule), Map.class));
//            } else if (CRUD.DELETE.equals(crud)) {
//                redisUtil.del(ruleCacheKey);
//            }
//        } catch (Exception e) {
//            log.error("同步规则缓存异常！", e);
//        }
//    }
}
