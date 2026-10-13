package org.jeecg.modules.iot.manage.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备状态枚举
 */
@Getter
@AllArgsConstructor
public enum DeviceStatusEnum {
    UNACTIVATE(-1, "未激活"),
    OFFLINE(0, "离线"),
    ONLINE(1, "在线"),
    DISABLE(2, "禁用");

    /**
     * 状态code
     */
    private final Integer status;
    /**
     * 状态名称
     */
    private final String statusName;

    /**
     * 根据设备状态查询枚举
     *
     * @param status
     * @return
     */
    public static DeviceStatusEnum getByStatus(Integer status) {
        for (DeviceStatusEnum e : values()) {
            if (e.status.equals(status)) {
                return e;
            }
        }
        return null;
    }

}
