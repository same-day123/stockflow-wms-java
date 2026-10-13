package org.jeecg.modules.iot.manage.vo;

import lombok.Data;
import org.jeecg.modules.iot.manage.entity.IotDevice;

/**
 * 设备详情信息
 *
 * @author muht
 * @create 2025/5/5 11:46
 */
@Data
public class IotDeviceDetail extends IotDevice {
    /**
     * 联网方式
     */
    private String networkMode;
    /**
     * 认证方式
     */
    private String authType;
    /**
     * 节点类型
     */
    private String nodeType;
}
