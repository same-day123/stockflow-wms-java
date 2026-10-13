package org.jeecg.modules.iot.alert.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.jeecg.modules.iot.alert.entity.IotAlertRule;
import org.jeecg.modules.iot.base.mqtt.constant.CompareOperatorEnum;
import org.jeecg.modules.iot.base.mqtt.dto.TransferToAlertMessageDto;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;

/**
 * 常用逻辑工具类
 *
 * @author muht
 * @create 2025/5/6 14:29
 */
public class AlertUtil {

    /**
     * 获取上报指标的数据
     *
     * @param messageDto
     * @param field
     * @return
     */
    public static Double getFieldData(TransferToAlertMessageDto messageDto, String field) {
        JSONObject reportDatas = JSON.parseObject(JSON.toJSONString(messageDto.getData()));
        return reportDatas.getDouble(field);
    }

    /**
     * 上报数据转为 json
     *
     * @param messageDto
     * @return
     */
    public static JSONObject getJsonData(TransferToAlertMessageDto messageDto) {
        return JSON.parseObject(JSON.toJSONString(messageDto.getData()));
    }

    /**
     * 是否超过阈值
     *
     * @param fieldData
     * @param rule
     * @return
     */
    public static boolean isOverRuleThreshold(Double fieldData, IotAlertRule rule) {
        if (rule == null || rule.getThreshold() == null || rule.getOperator() == null) {
            throw new BusiException("isOverRuleThreshold 异常，rule关键数据为空");
        }
        CompareOperatorEnum operatorEnum = CompareOperatorEnum.getOperatorEnum(rule.getOperator());
        Double threshold = rule.getThreshold();
        return operatorEnum.getCompareFunc().compare(fieldData, threshold);
    }

}
