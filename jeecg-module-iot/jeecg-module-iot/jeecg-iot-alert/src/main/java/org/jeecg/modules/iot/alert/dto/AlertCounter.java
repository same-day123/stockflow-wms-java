package org.jeecg.modules.iot.alert.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.Map;

/**
 * 报警计数缓存结构
 *
 * @author muht
 * @create 2025/5/20 10:11
 */
@Data
public class AlertCounter {
    /**
     * 异常上报计数器
     */
    private Integer alertCount = 0;
    /**
     * 正常上报数据计数器
     */
    private Integer normalCount = 0;
    /**
     * 报警状态，1：有未解决的报警日志；0：无未解决的报警日志
     */
    private Integer alertStatus = 0;

    public AlertCounter() {
    }
    public AlertCounter(Integer alertStatus) {
        this.alertStatus = alertStatus;
    }

    /**
     * 转为 Map
     *
     * @return
     */
    public Map<String, Object> toMap() {
        return JSONObject.parseObject(JSON.toJSONString(this), Map.class);
    }

}
