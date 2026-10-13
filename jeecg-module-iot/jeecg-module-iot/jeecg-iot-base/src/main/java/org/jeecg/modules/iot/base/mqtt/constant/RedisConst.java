package org.jeecg.modules.iot.base.mqtt.constant;

/**
 * @author muht
 * @date 2025/5/4 09:57
 */
public class RedisConst {
    /**
     * 设备缓存 key
     * wms:iot:device:{deviceCode}
     * 缓存类型 redis Hash
     */
    public static final String DEVICE_ENTITY_CACHE_KEY = "wms:iot:device:%s";
    /**
     * 设备心跳缓存
     * wms:iot:device-heartbeat:{deviceCode}
     * String
     */
    public static final String DEVICE_HEARTBEAT_KEY = "wms:iot:device-heartbeat:%s";
    /**
     * 在线状态表
     * Hash
     */
    public static final String DEVICE_STATUS_TABLE_KEY = "wms:iot:device-status";
    /**
     * 报警规则缓存key
     * wms:iot:alert-rule:{deviceCode}
     * 缓存类型 redis Hash
     */
    public static final String ALERT_RULE_KEY = "wms:iot:alert-rule:%s";
//    public static final String ALERT_RULE_KEY = "wms:iot:alert-rule:%s:%s";
    /**
     * 产品物模型映射缓存 key
     * wms:iot:product_models:{productCode}
     * 缓存类型 redis Hash
     */
    public static final String PRODUCT_MODEL_MAPPING_CACHE_KEY = "wms:iot:product_models:%s";
    /**
     * 通配符
     */
    public static final String STAR = "*";
    /**
     * 触发报警计数缓存
     * wms:iot:alert-counter:{deviceCode}:{ruleId}
     * 类型：hash
     */
    public static final String ALERT_COUNTER_HASH_KEY = "wms:iot:alert-counter:%s:%s";
    /**
     * 报警规则沉默周期
     * wms:iot:alert-quiet:{ruleId}
     */
    public static final String ALERT_QUIET_CACHE_KEY = "wms:iot:alert-quiet:%s";
    /**
     * 设备状态字段
     */
    public static final String DEVICE_STATUS = "deviceStatus";
    /**
     * ip字段
     */
    public static final String IP_ADDR = "ipAddr";
    /**
     * 60秒过期时间
     */
    public static final Integer EXPIRE_60s = 60;
}
