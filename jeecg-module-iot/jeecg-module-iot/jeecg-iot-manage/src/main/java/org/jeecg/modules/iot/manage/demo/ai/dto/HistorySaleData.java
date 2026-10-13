package org.jeecg.modules.iot.manage.demo.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * 历史销售数据
 *
 * @author muht
 * @create 2025/8/8 11:24
 */
@Data
public class HistorySaleData {
    /**
     * 商品信息
     */
    private GoodsInfo GoodsInfo;
    /**
     * 月销售数据
     */
    private List<MonthSaleData> monthSaleDatas;
    /**
     * 库存数量
     */
    private int stockQuantity;

}
