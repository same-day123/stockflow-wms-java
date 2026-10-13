package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mqtt 主题枚举类
 *
 * @author itcast
 * @create 2025/4/21 14:14
 **/
@Getter
@AllArgsConstructor
public enum MqttTopicEnum {
    DEVICE_REPORT("wms/iot/report", MqttClientType.SERVER, "设备上报"),
    ;

    /**
     * 主题
     */
    private String topic;
    /**
     * 订阅方类型
     */
    private MqttClientType subscriber;
    /**
     * 描述
     */
    private String desc;

    public static List<MqttTopicEnum> getBySubscriber(MqttClientType subscriber) {
        return Arrays.stream(values())
                .filter(v -> subscriber.equals(v.getSubscriber()))
                .collect(Collectors.toList());

    }
}
