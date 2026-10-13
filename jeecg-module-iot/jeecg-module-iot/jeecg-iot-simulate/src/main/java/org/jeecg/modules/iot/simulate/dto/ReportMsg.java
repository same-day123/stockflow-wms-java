package org.jeecg.modules.iot.simulate.dto;

import lombok.Data;

/**
 * 设备上报数据
 *
 * @author muht
 * @date 2025/4/19 17:42
 */
@Data
public class ReportMsg {

    private String deviceName;
    private String deviceCode;
    private String ip;
    private String pubTopic;
    /**
     * 数据对象
     */
    private Object data;

}
