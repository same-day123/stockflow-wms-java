package org.jeecg.modules.iot.manage.demo.ai.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;

import java.util.List;
import java.util.Objects;

/**
 * @author muht
 * @create 2025/8/8 11:42
 */
public class AiService {
    /**
     * qwen 大模型 API KEY
     */
    // private static final String API_KEY = System.getenv("DASHSCOPE_API_KEY");
    private static final String API_KEY = "sk-b74d2ac3fc4249eb972b3c977792c550";

    /**
     * 咨询AI大模型
     *
     * @param question
     */
    public String askAi(String question) {
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个专业的库存预测AI，需要基于历史销售数据和当前库存，预测未来1个月的库存变化。")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(question)
                .build();

        try {
            GenerationParam param = this.buildGenerationParam(List.of(systemMsg, userMsg), false);
            Generation gen = new Generation();
            GenerationResult genRes = gen.call(param);
            // System.out.println("大模型返回结果：" + JSON.toJSONString(genRes));
            if (Objects.nonNull(genRes)) {
                return genRes.getOutput()
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent();
            }
        } catch (Exception e) {
            System.out.println("咨询大模型异常！" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private GenerationParam buildGenerationParam(List<Message> messages, Boolean streamCall) {
        return GenerationParam.builder()
                .apiKey(API_KEY)
                .model("qwen-max")
                .messages(messages)
                .incrementalOutput(streamCall)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .temperature(0.2f)
                .enableSearch(false)
                .build();
    }
}
