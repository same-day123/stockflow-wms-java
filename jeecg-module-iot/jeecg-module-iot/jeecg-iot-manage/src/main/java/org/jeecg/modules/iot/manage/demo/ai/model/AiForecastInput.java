package org.jeecg.modules.iot.manage.demo.ai.model;

import lombok.Data;
import org.jeecg.modules.iot.manage.demo.ai.dto.GoodsInfo;

import java.util.List;

/**
 * 输入AI的分析数据
 *
 * @author muht
 * @create 2025/8/12 15:10
 */
@Data
public class AiForecastInput {
    /**
     * 商品信息
     */
    private GoodsInfo GoodsInfo;
    /**
     * 当前库存数量
     */
    private Integer stockQuantity;
    /**
     * 历史月销售数据
     */
    private List<SaleData> saleDatas;

    /**
     * 销售数据
     */
    @Data
    public static class SaleData {
        /**
         * 销售月份
         */
        private String saleMonth;
        /**
         * 月销售量
         */
        private Integer amount;
        /**
         * 是否旺季，true 旺季；false 淡季
         */
        private Boolean isHighSeason;
        /**
         * 是否促销月（或本月是否含有促销活动）
         */
        private Boolean isPromotionMonth;
    }
}
