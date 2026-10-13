package org.jeecg.modules.iot.base.mqtt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比较运算符枚举
 */
@Getter
@AllArgsConstructor
public enum CompareOperatorEnum {
    GT("gt", ">", (a, b) -> a > b),
    LT("lt", "<", (a, b) -> a < b),
    EGT("egt", ">=", (a, b) -> a >= b),
    ELT("elt", "<=", (a, b) -> a <= b),
    EQ("eq", "==", (a, b) -> a == b),
    NEQ("neq", "!=", (a, b) -> a != b),
    ;

    private String val;
    private String desc;
    private CompareFunction compareFunc;

    /**
     * 通过value 查询指定枚举
     *
     * @param val
     * @return
     */
    public static CompareOperatorEnum getOperatorEnum(String val) {
        for (CompareOperatorEnum e : values()) {
            if (e.getVal().equals(val)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 比较函数
     */
    @FunctionalInterface
    public interface CompareFunction {
        /**
         * 比较两数大小
         *
         * @param a
         * @param b
         */
        boolean compare(Double a, Double b);
    }
}

