package org.jeecg.modules.iot.manage.demo.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * 月销售数据
 *
 * @author muht
 * @create 2025/8/8 15:50
 */
@Data
public class MonthSaleData {
    /**
     * 销售月份
     */
    private String saleMonth;
    /**
     * 是否旺季
     */
    private Boolean isHighSeason;
    /**
     * 是否促销月
     */
    private Boolean isPromotion;
    /**
     * 月总销售量
     */
    private Integer amount;
    /**
     * 日销售数据
     */
    private List<DaySaleData> daySaleDatas;
}
