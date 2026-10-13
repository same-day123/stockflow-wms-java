package org.jeecg.modules.iot.manage.task;

import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.manage.constant.DeviceStatusEnum;
import org.jeecg.modules.iot.manage.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统定时任务
 * <p>
 * author muht
 *
 * @create 2025/4/28 17:45
 */
@Slf4j
@Component
public class SystemSchedule {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设备离线探测(间隔 3s)
     */
    @Scheduled(initialDelay = 5, fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    public void detectDeviceOffline() {
        Map<Object, Object> onlineTable = redisUtil.hmget(RedisConst.DEVICE_STATUS_TABLE_KEY);
        // 全量设备编码
        Set<String> deviceCodes;
        if (CollectionUtils.isEmpty(onlineTable)) {
            // 初始化设备状态表
            deviceCodes = cacheService.initDeviceStatusTable().keySet();
        } else {
            deviceCodes = onlineTable.keySet().stream().map(k -> k.toString()).collect(Collectors.toSet());
        }

        // 查询设备心跳缓存
        List<String> onlineDevices = redisTemplate.opsForValue().multiGet(
                deviceCodes.stream().map(code -> String.format(RedisConst.DEVICE_HEARTBEAT_KEY, code)).toList());
        Map<String, Object> offlineDeviceMap = deviceCodes.stream()
                .filter(code -> !onlineDevices.contains(code))
                // 此条件是为了避免把未激活状态覆盖为离线状态
                .filter(code -> DeviceStatusEnum.ONLINE.getStatus().equals(onlineTable.get(code)))
                .collect(Collectors.toMap(c -> c, c -> DeviceStatusEnum.OFFLINE.getStatus()));

        // 更新设备状态表
        redisUtil.hmset(RedisConst.DEVICE_STATUS_TABLE_KEY, offlineDeviceMap);
    }
}
