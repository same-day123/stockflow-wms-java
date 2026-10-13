package org.jeecg.modules.iot.simulate.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Random;

/**
 * 样本数据规则
 */
@Getter
@AllArgsConstructor
public enum SamplesRuleEnum {
    RANDOM("random", getRandomRule(), "随机策略"),
    BACK_AND_FORTH("back_and_forth", getBackAndForthRule(), "往复策略"),
    LOOP("loop", getLoopRule(), "循环策略");

    private String name;
    private SampleRule rule;
    private String desc;

    /**
     * 获取随机样本规则
     *
     * @return
     */
    public static SampleRule getRandomRule() {
        return (samples, round) -> {
            if (CollectionUtils.isEmpty(samples)) {
                throw new RuntimeException("样本为空！");
            }
            Random random = new Random();
            int index = random.nextInt(samples.size());
            return samples.get(index);
        };
    }

    /**
     * 获取往复策略
     *
     * @return
     */
    public static SampleRule getBackAndForthRule() {
        return (samples, round) -> {
            if (CollectionUtils.isEmpty(samples)) {
                throw new RuntimeException("样本为空！");
            }
            int idx = round % samples.size();
            boolean forth = (round / samples.size()) % 2 == 0;
            if (!forth) {
                idx = samples.size() - 1 - idx;
            }
            return samples.get(idx);
        };
    }

    /**
     * 获取循环策略
     *
     * @return
     */
    public static SampleRule getLoopRule() {
        return (samples, round) -> {
            if (CollectionUtils.isEmpty(samples)) {
                throw new RuntimeException("样本为空！");
            }
            int idx = round % samples.size();
            return samples.get(idx);
        };
    }

    /**
     * 根据name 查找
     *
     * @param name
     * @return
     */
    public static SamplesRuleEnum getSamplesRule(String name) {
        for (SamplesRuleEnum rule : SamplesRuleEnum.values()) {
            if (rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }
}
