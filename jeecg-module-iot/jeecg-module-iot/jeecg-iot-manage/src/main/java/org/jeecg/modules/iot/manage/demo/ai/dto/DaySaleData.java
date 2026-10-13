package org.jeecg.modules.iot.manage.demo.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 日销售数据
 *
 * @author muht
 * @create 2025/8/8 11:26
 */
@Data
@Accessors(chain = true)
public class DaySaleData {
    /**
     * 销售日期
     */
    private LocalDate saleDate;
    /**
     * 日销售量
     */
    private Integer saleCount;
}
