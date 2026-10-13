package org.jeecg.modules.wms.analysis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoTask {
    private String title;     // 任务标题
    private String icon;      // 图标类名
    private Integer value;    // 待处理数量
    private Integer total;    // 已处理数量
    private String action;    // 日期

}