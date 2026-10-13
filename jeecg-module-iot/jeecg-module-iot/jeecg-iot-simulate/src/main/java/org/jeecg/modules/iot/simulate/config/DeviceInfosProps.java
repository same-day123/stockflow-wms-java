package org.jeecg.modules.iot.simulate.config;

import com.alibaba.fastjson2.JSONObject;
import org.jeecg.modules.iot.simulate.base.SamplesRuleEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author muht
 * @date 2025/4/19 16:09
 */
@Data
@Component
@ConfigurationProperties(prefix = "device-infos")
public class DeviceInfosProps {
    /**
     * broker地址
     */
    private String brokerUri;
    /**
     * 设备列表
     */
    private List<Device> devices;
    /**
     * 上报数据间隔(s)
     */
    private Integer reportInterval;
    /**
     * 订阅主题
     */
    private String subTopic;
    /**
     * 发布主题
     */
    private String pubTopic;

    @Data
    public static class Device {
        /**
         * 设备名称
         */
        private String deviceName;
        /**
         * 设备编码
         */
        private String deviceCode;
        /**
         * 是否启用设备
         */
        private boolean enable = true;
        /**
         * 客户端密码
         */
        private String password = "wms-iot";
        /**
         * 上报数据间隔(s)
         */
        private Integer reportInterval;
        /**
         * 订阅主题
         */
        private String subTopic;
        /**
         * 发布主题
         */
        private String pubTopic;
        /**
         * 样本数据
         */
        private List<JSONObject> samples;
        /**
         * 样本使用规则
         * 默认往复策略
         */
        private String samplesRule = SamplesRuleEnum.LOOP.getName();
    }
}
