package org.jeecg.modules.iot.alert.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.alert.entity.IotAlertRule;
import org.jeecg.modules.iot.alert.mapper.IotAlertRuleMapper;
import org.jeecg.modules.iot.alert.service.IIotAlertRuleService;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 报警规则表
 * @Author: jeecg-boot
 * @Date: 2025-05-03
 * @Version: V1.0
 */
@Slf4j
@Service
public class IotAlertRuleServiceImpl extends ServiceImpl<IotAlertRuleMapper, IotAlertRule> implements IIotAlertRuleService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IotAlertRuleMapper iotAlertRuleMapper;

    @Override
    public List<IotAlertRule> queryAlertRules(String deviceCode) {
        // RedisConst.ALERT_RULE_KEY
        String alert_rule_key = String.format(RedisConst.ALERT_RULE_KEY, deviceCode);
        //缓存是否存在
        boolean exists = redisUtil.hasKey(alert_rule_key);
        List<IotAlertRule> alertRules;
        if (!exists) {
            synchronized (this) {
                exists = redisUtil.hasKey(alert_rule_key);
                if (!exists) {
                    // 查询设备下所有报警规则（包括禁用和启用的）
                    alertRules = iotAlertRuleMapper.selectList(new LambdaQueryWrapper<IotAlertRule>()
                            .eq(IotAlertRule::getDeviceCode, deviceCode));
                    log.info("查询到 dbAlertRules={}", alertRules);
                    // 批量写入 redis
                    this.batchSetAlertRulesToCache(deviceCode,alertRules);
                } else {
                    // 从缓存获取报警规则
                    alertRules = batchGetAlertRulesFromCache(deviceCode);
                }
            }
        } else {
            alertRules = batchGetAlertRulesFromCache(deviceCode);
        }

        log.info("查询到deviceCode={} 的报警规则清单为={}", deviceCode, alertRules);
        return alertRules;
    }
//    @Override
//    public List<IotAlertRule> queryAlertRules(String deviceCode) {
//        // RedisConst.ALERT_RULE_KEY
//        String keyPattern = String.format(RedisConst.ALERT_RULE_KEY, deviceCode, RedisConst.STAR);
//
//        List<IotAlertRule> alertRules;
//        List<String> ruleKeys = redisUtil.scan(keyPattern);
//        if (CollectionUtils.isEmpty(ruleKeys)) {
//            synchronized (this) {
//                List<String> ruleKeysAgain = redisUtil.scan(keyPattern);
//                if (CollectionUtils.isEmpty(ruleKeysAgain)) {
//                    // 查询设备下所有报警规则（包括禁用和启用的）
//                    alertRules = iotAlertRuleMapper.selectList(new LambdaQueryWrapper<IotAlertRule>()
//                            .eq(IotAlertRule::getDeviceCode, deviceCode));
//                    log.info("查询到 dbAlertRules={}", alertRules);
//                    // 批量写入 redis
//                    this.batchSetAlertRulesToCache(alertRules);
//                } else {
//                    // 从缓存获取报警规则
//                    alertRules = batchGetAlertRulesFromCache(ruleKeysAgain);
//                }
//            }
//        } else {
//            alertRules = batchGetAlertRulesFromCache(ruleKeys);
//        }
//
//        log.info("查询到deviceCode={} 的报警规则清单为={}", deviceCode, alertRules);
//        return alertRules;
//    }

    /**
     * 批量查询规则缓存
     *
     * @param deviceCode 设备编码
     * @return
     */
    private List<IotAlertRule> batchGetAlertRulesFromCache(String deviceCode) {
        try {
            String alert_rule_key = String.format(RedisConst.ALERT_RULE_KEY, deviceCode);
            //定义一个List存储报警规则
            List<IotAlertRule> rules = new java.util.ArrayList<>();
            //从缓存获取hash
            Map<String, Object> entries = redisTemplate.opsForHash().entries(alert_rule_key);
            entries.entrySet().forEach(entry -> {
                String ruleId = (String) entry.getKey();
                IotAlertRule rule = JSONObject.parseObject((String) entry.getValue(), IotAlertRule.class);
                rules.add( rule);
            });
            return rules;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    private List<IotAlertRule> batchGetAlertRulesFromCache(List<String> keys) {
//        // pipeline 批量查询 hash 缓存
//        List<Map<String, Object>> ruleMaps = redisTemplate.executePipelined(
//                (RedisCallback<Map<String, Object>>) connection -> {
//                    keys.forEach(key -> connection.hashCommands().hGetAll(key.getBytes()));
//                    return null;
//                });
//
//        List<IotAlertRule> rules = ruleMaps.stream()
//                .map(m -> JSONObject.parseObject(JSON.toJSONString(m), IotAlertRule.class))
//                .collect(Collectors.toList());
//        return rules;
//    }

    /**
     * 批量插入
     * 过期时间60s
     */
    private void batchSetAlertRulesToCache(String deviceCode,List<IotAlertRule> alertRules) {
        //定义待写入redis的map
        //<ruleId,ruld对象json串>
        Map<String, String> ruleMap = new HashMap<>();
        //将alertRules转为ruleMap
        alertRules.stream().forEach(rule -> {
            ruleMap.put(rule.getId(), JSON.toJSONString(rule));
        });
        //将ruleMap写入redis
        String key = String.format(RedisConst.ALERT_RULE_KEY, deviceCode);
        redisTemplate.opsForHash().putAll(key, ruleMap);
    }
//    private void batchSetAlertRulesToCache(List<IotAlertRule> alertRules) {
//        HashOperations hashOps = redisTemplate.opsForHash();
//
//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            alertRules.forEach(rule -> {
//                String id = rule.getId();
//                String deviceCode = rule.getDeviceCode();
//                String redisKey = String.format(RedisConst.ALERT_RULE_KEY, deviceCode, id);
//                hashOps.putAll(redisKey, JSONObject.parseObject(JSON.toJSONString(rule), Map.class));
//                redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
//            });
//            return null;
//        });
//    }
}
