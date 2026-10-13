package org.jeecg.modules.iot.simulate.base;

import com.alibaba.fastjson2.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.util.List;

/**
 * mqtt 客户端
 *
 * @author muht
 * @create 2025/4/18 16:45
 **/
@Slf4j
@Getter
@Setter
@EqualsAndHashCode
public class DeviceMqttClient extends MqttClient {
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 客户端ID
     */
    private String deviceCode;
    /**
     * 上报频率
     */
    private Integer interval;
    /**
     * ip 地址
     */
    private String ip;
    /**
     * 发布消息主题
     */
    private String pubTopic;
    /**
     * 模拟上报数据
     */
    private List<JSONObject> samples;
    /**
     * 样本策略枚举
     */
    private SamplesRuleEnum samplesRule;

    public DeviceMqttClient(String serverURI, String deviceCode) throws MqttException {
        super(serverURI, deviceCode);
        this.deviceCode = deviceCode;
        WmsMqttCallback wmsMqttCallback = new WmsMqttCallback(serverURI, deviceCode);
        this.setCallback(wmsMqttCallback);
    }
}
