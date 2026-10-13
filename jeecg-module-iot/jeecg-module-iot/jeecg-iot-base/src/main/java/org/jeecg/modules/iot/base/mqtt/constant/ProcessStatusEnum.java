package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessStatusEnum {
    UNSOLVED("待解决", "0"),
    RESOLVED("已解决", "1"),
    IGNORE("不予解决", "2");

    private String desc;
    private String value;

    public static ProcessStatusEnum getByValue(String value) {
        for (ProcessStatusEnum status : ProcessStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
