package org.jeecg.modules.wms.analysis.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendPoint {
    /** 显示名，如 "1月" */
    private String name;
    /** 数量 */
    private Integer value;
}
