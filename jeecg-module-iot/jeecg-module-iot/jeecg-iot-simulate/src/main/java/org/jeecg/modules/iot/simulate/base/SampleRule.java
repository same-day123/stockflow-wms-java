package org.jeecg.modules.iot.simulate.base;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 样本规则函数
 */
@FunctionalInterface
public interface SampleRule {
    /**
     * 获取样本数据
     *
     * @param samples 全样本列表
     * @param round   当前轮次
     * @return
     */
    Object getSample(List<JSONObject> samples, int round);
}
