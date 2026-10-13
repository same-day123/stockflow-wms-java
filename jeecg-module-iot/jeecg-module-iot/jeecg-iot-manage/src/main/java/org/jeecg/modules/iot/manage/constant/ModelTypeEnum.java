package org.jeecg.modules.iot.manage.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物模型类型枚举
 */
@Getter
@AllArgsConstructor
public enum ModelTypeEnum {
    /**
     * 属性物模型
     */
    FIELD("field"),
    /**
     * 服务物模型
     */
    SERVICE("service");

    private String type;
}
