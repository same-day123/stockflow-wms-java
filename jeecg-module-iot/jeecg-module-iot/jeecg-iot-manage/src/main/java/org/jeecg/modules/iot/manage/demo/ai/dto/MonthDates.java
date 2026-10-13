package org.jeecg.modules.iot.manage.demo.ai.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 当月日期
 *
 * @author muht
 * @create 2025/8/8 15:44
 */
@Data
public class MonthDates {
    /**
     * 月份
     */
    private Integer month;
    /**
     * 日期列表
     */
    private final List<LocalDate> dates = new ArrayList<>();
}
