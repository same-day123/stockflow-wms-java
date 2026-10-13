package org.jeecg.modules.iot.alert.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.iot.alert.dto.AlertCounter;
import org.jeecg.modules.iot.alert.entity.IotAlertLog;
import org.jeecg.modules.iot.alert.entity.IotAlertRule;
import org.jeecg.modules.iot.alert.mapper.IotAlertLogMapper;
import org.jeecg.modules.iot.alert.service.IIotAlertRuleService;
import org.jeecg.modules.iot.alert.service.IReportDataAnalysisService;
import org.jeecg.modules.iot.alert.util.AlertUtil;
import org.jeecg.modules.iot.base.mqtt.constant.*;
import org.jeecg.modules.iot.base.mqtt.dto.TransferToAlertMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 上报数据分析服务
 *
 * @author muht
 * @create 2025/4/28 14:04
 */
@Slf4j
@Component
public class ReportDataAnalysisServiceImpl implements IReportDataAnalysisService {

    /**
     * 报警状态，1：报警，0：未触发
     */
    private static final Integer ALERT = 1, NORMAL = 0;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IIotAlertRuleService iotAlertRuleService;

    @Autowired
    private IotAlertLogMapper iotAlertLogMapper;

    @Override
    public void reportAnalyse(TransferToAlertMessageDto message) {
        String deviceCode = message.getDeviceCode();
        // 查询告警规则
        List<IotAlertRule> allRules = iotAlertRuleService.queryAlertRules(deviceCode);
        if (CollectionUtils.isEmpty(allRules)) {
            log.info("无报警规则，不处理，{}", deviceCode);
            return;
        }

        // <ruleId报警规则id, value=1报警、0未触发报警>
        Map<String, Integer> ruleStatusMap = this.checkAlertRules(message, allRules);
        log.info("报警分析结果，ruleStatusMap={}", JSON.toJSONString(ruleStatusMap));

        // 报警与恢复处理
//        this.processAlertRules(ruleStatusMap, allRules, message);
        for (IotAlertRule rule : allRules) {
            try {
                //触发报警标记
                boolean hit = ALERT.equals(ruleStatusMap.get(rule.getId()));
                //处理计数
                AlertCounter alertCounter = this.processCount(hit, rule);
                // 处理单条报警规则
//                this.processSingleRule(ruleStatusMap, rule, message);
                // 指标与指标数据
                String field = rule.getModelCode();
                Double fieldData = AlertUtil.getFieldData(message, field);
                if (hit) {
                    // 场景1：告警处理
                    this.processAlertTrigger(alertCounter, rule, fieldData);
                } else {
                    // 场景2：告警恢复处理
                    this.processAutoRecover(alertCounter, rule, fieldData);
                }
            } catch (Exception ex) {
                log.error("处理报警流程异常!", ex);
            }
        }
    }

    /**
     * 报警规则触发状态
     *
     * @param messageDto
     * @param alertRules
     * @return <k=ruleId, v=1报警、0正常>
     */
    private Map<String, Integer> checkAlertRules(TransferToAlertMessageDto messageDto, List<IotAlertRule> alertRules) {
        if (CollectionUtils.isEmpty(alertRules)) {
            return null;
        }
        String deviceCode = messageDto.getDeviceCode();
        // <指标, 报警规则>
        Map<String, List<IotAlertRule>> rulesMap = alertRules.stream()
                .collect(Collectors.groupingBy(
                        // 指标分组
                        IotAlertRule::getModelCode,
                        // 多规则按报警优先级排序：紧急>警告>一般
                        Collectors.collectingAndThen(Collectors.toList(),
                                rules -> rules.stream()
                                        .sorted(Comparator.comparingInt(rule -> AlertLevelEnum.getAlertLevelEnum(rule.getAlertLevel()).getPriority()))
                                        .collect(Collectors.toList())
                        )));
        log.debug("规则分组后, {}", JSON.toJSONString(rulesMap));


        /*
         * 报警优先级原则：一个指标只触发优先级最高的规则，其余指标不论是否达到阈值，都视为未触发
         */
        // 触发的报警规则结果<指标编码,报警规则>
        Map<String, IotAlertRule> hitAlertRuleMap = new HashMap<>();
        // <指标编码,未触发的报警规则>
        Map<String, List<IotAlertRule>> normalAlertRuleMap = new HashMap<>();
        JSONObject reportDatas = AlertUtil.getJsonData(messageDto);
        // 以报警规则为遍历基准，避免因为上报数据缺失而无法感知到数据异常
        rulesMap.entrySet().stream().forEach(e -> {
            // 指标编码
            String modelCode = e.getKey();
            //触发报警的规则对象
            AtomicReference<IotAlertRule> hitAlertRule = new AtomicReference<>();
            // 按报警规则优先级是从高到低排序的
            List<IotAlertRule> rules = e.getValue();
            if (reportDatas.containsKey(modelCode)) {
                Double fieldData = reportDatas.getDouble(modelCode);
                for (IotAlertRule rule : rules) {
                    // 报警规则是开启状态，且触发报警阈值
                    if (EnableEnum.YES.getValue().equals(rule.getIsEnable())
                            && AlertUtil.isOverRuleThreshold(fieldData, rule)
                            && hitAlertRule.get() == null) {
                        log.info("触发报警规则，deviceCode={}, field={}, value={}, threshold={}", deviceCode, modelCode, fieldData, rule.getThreshold());
                        hitAlertRuleMap.put(modelCode, rule);
                        hitAlertRule.set(rule);
                    }else{
                        //记录未触发的报警规则
                        List<IotAlertRule> iotAlertRules = normalAlertRuleMap.get(modelCode);
                        if (iotAlertRules == null) {
                            iotAlertRules = new ArrayList<>();
                        }
                        iotAlertRules.add( rule);
                        normalAlertRuleMap.put(modelCode, iotAlertRules);
                    }
                }
            } else {
                log.warn("该报警规则未检测到上报数据，deviceCode={}, field={}", deviceCode, modelCode);
            }
        });

        // 记录未触发规则

//        rulesMap.entrySet().stream().forEach(e -> {
//            IotAlertRule alertRule = Optional.ofNullable(hitAlertRuleMap.get(e.getKey())).orElse(new IotAlertRule());
//            List<IotAlertRule> normalRules = e.getValue().stream()
//                    .filter(rule -> !rule.getId().equals(alertRule.getId()))
//                    .collect(Collectors.toList());
//            normalAlertRuleMap.put(e.getKey(), normalRules);
//        });
        // 指标Set
        Set<String> fields = rulesMap.keySet();
        // <ruleId, value=1触发 / 0未触发>
        Map<String, Integer> ruleStatusMap = new HashMap<>();
        fields.forEach(field -> {
            IotAlertRule alertRule = hitAlertRuleMap.get(field);
            if (alertRule != null) {
                ruleStatusMap.put(alertRule.getId(), ALERT);
            }
            List<IotAlertRule> normalRules = normalAlertRuleMap.get(field);
            if (!CollectionUtils.isEmpty(normalRules)) {
                normalRules.forEach(rule -> ruleStatusMap.put(rule.getId(), NORMAL));
            }
        });

        return ruleStatusMap;
    }

//    /**
//     * 报警规则处理流程
//     *
//     * @param ruleStatusMap 报警规则状态
//     * @param allRules      设备下所有报警规则
//     * @param message       上报消息
//     */
//    private void processAlertRules(Map<String, Integer> ruleStatusMap,
//                                   List<IotAlertRule> allRules,
//                                   TransferToAlertMessageDto message) {
//        if (CollectionUtils.isEmpty(allRules)) {
//            return;
//        }
//
//        Map<String, IotAlertRule> ruleMap = allRules.stream()
//                .collect(Collectors.toMap(rule -> rule.getId(), r -> r));
//
//        for (IotAlertRule rule : ruleMap.values()) {
//            try {
//                // 处理单条报警规则
//                this.processSingleRule(ruleStatusMap, rule, message);
//            } catch (Exception ex) {
//                log.error("处理报警流程异常!", ex);
//            }
//        }
//    }

//    private void processSingleRule(Map<String, Integer> ruleStatusMap,
//                                   IotAlertRule rule,
//                                   TransferToAlertMessageDto message) {
//        // 触发报警规则标识,是否是告警状态
//        boolean hit = ALERT.equals(ruleStatusMap.get(rule.getId()));
//        // 指标与指标数据
//        String field = rule.getModelCode();
//        Double fieldData = AlertUtil.getFieldData(message, field);
//        // 查询报警计数器
//        String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
//        Map<Object, Object> alertCountMap = redisUtil.hmget(counterKey);
//        // 初始化报警计数器
//        if (CollectionUtils.isEmpty(alertCountMap)) {
//            AlertCounter alertCounter = new AlertCounter();
//            if (hit) {
//                alertCounter.setAlertCount(1);
//            } else {
//                alertCounter.setNormalCount(1);
//            }
//            Map<String, Object> cacheMap = alertCounter.toMap();
//            redisUtil.hmset(counterKey, cacheMap);
//            return;
//        }
//
//        AlertCounter counter = JSONObject.parseObject(JSON.toJSONString(alertCountMap), AlertCounter.class);
//        if (hit) {
//            // 场景1：触发报警
//            this.processHitRule(counter, rule, fieldData);
//        } else {
//            // 场景2：自动恢复
//            this.processAutoRecover(counter, rule, fieldData);
//        }
//    }
    static final  AlertCounter alertCounterHit = new AlertCounter(1);
    /**
     * 报警处理-计数
     *
     * @param hit 触发报警标识
     * @param rule  需要校验的报警规则
     */
    private AlertCounter processCount(boolean hit,IotAlertRule rule) {
        // 查询报警计数器
        String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
        Map<Object, Object> alertCountMap = redisUtil.hmget(counterKey);
        AlertCounter counter = null;
        //如果触发报警
        if( hit){
            // 初始化报警计数器
            if (CollectionUtils.isEmpty(alertCountMap)) {
                AlertCounter alertCounter = new AlertCounter();
                alertCounter.setAlertCount(1);
                Map<String, Object> cacheMap = alertCounter.toMap();
                redisUtil.hmset(counterKey, cacheMap);
                alertCountMap = new HashMap<>();
                alertCountMap.putAll(cacheMap);
            }
            counter = JSONObject.parseObject(JSON.toJSONString(alertCountMap), AlertCounter.class);
            // 查询沉默周期
            String alertQuietKey = String.format(RedisConst.ALERT_QUIET_CACHE_KEY, rule.getId());
            String quietCache = (String) redisUtil.get(alertQuietKey);
            // 存在沉默周期不计数
            if (quietCache != null ) {
                return counter;
            }else{
                // 累加报警计数
                redisUtil.hincr(counterKey, "alertCount", 1);
                // 清空正常计数
                redisUtil.hset(counterKey, "normalCount", 0);
                counter.setNormalCount(0);
                counter.setAlertCount(counter.getAlertCount() + 1);
            }
        }else{

            // 初始化报警计数器
            if (CollectionUtils.isEmpty(alertCountMap)) {
                AlertCounter alertCounter = new AlertCounter();
                alertCounter.setNormalCount(1);
                Map<String, Object> cacheMap = alertCounter.toMap();
                redisUtil.hmset(counterKey, cacheMap);
                alertCountMap = new HashMap<>();
                alertCountMap.putAll(cacheMap);
            }
            counter = JSONObject.parseObject(JSON.toJSONString(alertCountMap), AlertCounter.class);
            //不存在未解决报警,则不计数
            if (counter.getAlertStatus().equals(0)) {
                return counter;
            }else{
                // 累加正常计数
                redisUtil.hincr(counterKey, "normalCount", 1);
                // 清空报警计数
                redisUtil.hset(counterKey, "alertCount", 0);
                counter.setAlertCount(0);
                counter.setNormalCount(counter.getNormalCount() + 1);
            }

        }

        return counter;
    }

    /**
     * 触发报警
     * 1. 该设备该报警规则的报警计数器
     * 2. 报警规则
     * 3. 上报的指标数据
     */
    private void processAlertTrigger(AlertCounter countCache,
                                IotAlertRule rule,
                                Double fieldData) {
        // 报警计数器缓存key
        String countKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
        // 查询沉默周期
        String alertQuietKey = String.format(RedisConst.ALERT_QUIET_CACHE_KEY, rule.getId());
        String quietCache = (String) redisUtil.get(alertQuietKey);
        // 存在沉默周期，或报警状态为1，不处理
        if ((quietCache != null&&!quietCache.equals("null")) ) {
            return;
        }

        // 本次触发报警规则后，是否达到连续周期
        if (countCache.getAlertCount() >= rule.getCycle()) {
            IotAlertLog newAlertLog = this.newAlertLog(rule.getModelCode(), fieldData, rule);
            // 记录沉默周期缓存
            redisUtil.set(alertQuietKey, newAlertLog.getId(), rule.getIgnoreDuration());
            //重置计数器，并标记状态为1
//            AlertCounter newCount = new AlertCounter();
//            newCount.setAlertStatus(1);
//            redisUtil.hmset(countKey, newCount.toMap());
            resetCounterOnAlertTrigger(rule);
            // 检查是否有未解决的报警日志，防止因缓存不一致导致的重复新增
            boolean existsUnsolvedLog = iotAlertLogMapper.exists(new LambdaQueryWrapper<IotAlertLog>()
                    .eq(IotAlertLog::getRuleId, rule.getId())
                    .eq(IotAlertLog::getProcessStatus, ProcessStatusEnum.UNSOLVED.getValue()));
            if (!existsUnsolvedLog) {
                // 新增告警日志
                iotAlertLogMapper.insert(newAlertLog);
            }

            log.info("触发并达到连续周期，生成报警日志，ruleId={}, logId={}, countKey={}", rule.getId(), newAlertLog.getId(), countKey);
        }

        else {
            // 累加报警计数
            redisUtil.hincr(countKey, "alertCount", 1);
            redisUtil.hset(countKey, "normalCount", 0);
        }
    }

    /**
     * 处理自动恢复
     * 1. 该设备该报警规则的报警计数器
     * 2. 报警规则
     * 3. 上报的指标数据
     */
    private void processAutoRecover(AlertCounter counter,
                                    IotAlertRule rule,
                                    Double fieldData) {
        // 报警计数器缓存key
//        String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
        // 若报警状态为未报警，或报警规则并没有真正恢复，而是升级到更高的报警规则，则不处理
        if (counter.getAlertStatus().equals(0) || AlertUtil.isOverRuleThreshold(fieldData, rule)) {
            return;
        }
        // 达到连续周期
        if (counter.getNormalCount()  >= rule.getCycle()) {
            // 1、恢复报警日志
            String processTime = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
            String processPlan = "设备数据更新，报警自动解除";
            iotAlertLogMapper.autoRecoverAlertLog(rule.getId(), processTime, processPlan);
            // 2、重置报警计数器
//            redisUtil.hmset(counterKey, new AlertCounter().toMap());
            resetCounterOnRecovery(rule);
            // 3、清除沉默周期
            redisUtil.del(String.format(RedisConst.ALERT_QUIET_CACHE_KEY, rule.getId()));
        }
//        else {
//            // 累计正常计数,重置报警计数
//            redisUtil.hincr(counterKey, "normalCount", 1);
//            redisUtil.hset(counterKey, "alertCount", 0);
//        }
    }
    //告警恢复后重新初始化报警计数器
    private void resetCounterOnRecovery(IotAlertRule rule) {
        String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
        redisUtil.hmset(counterKey, new AlertCounter().toMap());
    }

    //上报告警后重置报警计数器
    private void resetCounterOnAlertTrigger(IotAlertRule rule) {
        String counterKey = String.format(RedisConst.ALERT_COUNTER_HASH_KEY, rule.getDeviceCode(), rule.getId());
        AlertCounter newCount = new AlertCounter();
        newCount.setAlertStatus(1);
        redisUtil.hmset(counterKey, newCount.toMap());
    }


    /**
     * 创建新的报警日志
     * @param field 指标编码
     * @param fieldData 指标数据
     * @param iotAlertRule 报警规则
     *
     * @return
     */
    private IotAlertLog newAlertLog(String field, Double fieldData, IotAlertRule iotAlertRule) {
        String deviceCode = iotAlertRule.getDeviceCode();
        Map<Object, Object> deviceMap = redisUtil.hmget(String.format(RedisConst.DEVICE_ENTITY_CACHE_KEY, deviceCode));

        Date currentTime = new Date();
        IotAlertLog alertLog = new IotAlertLog();
        alertLog.setRuleId(iotAlertRule.getId());
        alertLog.setRuleName(iotAlertRule.getAlertName());
        // ruleInfo
        String optDesc = CompareOperatorEnum.getOperatorEnum(iotAlertRule.getOperator()).getDesc();
        alertLog.setRuleInfo(iotAlertRule.getModelName() + optDesc + iotAlertRule.getThreshold() + "，连续" + iotAlertRule.getCycle() + "个周期");
        alertLog.setModelCode(field);
        alertLog.setAlertData(fieldData);
        alertLog.setAlertLevel(iotAlertRule.getAlertLevel());
        alertLog.setAlertTime(currentTime);
        alertLog.setCreateTime(currentTime);
        alertLog.setDeviceCode(iotAlertRule.getDeviceCode());
        alertLog.setDeviceName(iotAlertRule.getDeviceName());
        alertLog.setNotifyInfo(iotAlertRule.getNotifyType());
        alertLog.setProcessStatus(ProcessStatusEnum.UNSOLVED.getValue());
        alertLog.setProductName(iotAlertRule.getProductName());
        alertLog.setProductCode(iotAlertRule.getProductCode());
        alertLog.setWarehouseName(deviceMap.get("warehouseName")!=null?deviceMap.get("warehouseName").toString():"");
        alertLog.setSysOrgCode(deviceMap.get("sysOrgCode").toString());
        alertLog.setTenantId(Integer.valueOf(deviceMap.get("tenantId").toString()));

        return alertLog;
    }
}
