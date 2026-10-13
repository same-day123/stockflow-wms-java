package org.jeecg.modules.iot.manage.demo.ai.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.jeecg.modules.iot.manage.demo.ai.dto.GoodsInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author muht
 * @create 2025/8/12 15:15
 */
@Data
public class AiForecastOutput {
    /**
     * 商品信息
     */
    private GoodsInfo GoodsInfo;
    /**
     * 预测销售数据
     */
    private List<ForecastSaleData> forecastSaleDatas;
    /**
     * 预测周期开始时间
     */
    private String startDate;
    /**
     * 预测结束时间
     */
    private String endDate;

    public static void main(String[] args) {
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoodsName("商品名称");
        goodsInfo.setGoodsId("商品id");
        goodsInfo.setGoodsType("商品类型");
        goodsInfo.setGoodsPrice(new BigDecimal("19.9"));

        AiForecastOutput output = new AiForecastOutput();
        output.setGoodsInfo(goodsInfo);
        output.setStartDate("2025-08-01");
        output.setEndDate("2025-10-31");

        ForecastSaleData next = new ForecastSaleData();
        next.setSaleCount(300);
        next.setSaleMonth("2025-8月");
        next.setGrowthRatio(-0.1);
        next.setRemark("根据过去效率变化分析，本月预测销量的特别说明");

        ForecastSaleData next2 = new ForecastSaleData();
        next2.setSaleCount(400);
        next2.setSaleMonth("2025-9月");
        next2.setGrowthRatio(0.33);
        next2.setRemark("根据过去效率变化分析，本月预测销量的特别说明");

        ForecastSaleData next3 = new ForecastSaleData();
        next3.setSaleCount(500);
        next3.setSaleMonth("2025-9月");
        next3.setGrowthRatio(0.25);
        next3.setRemark("根据过去效率变化分析，本月预测销量的特别说明");
        // 未来预测销售数据
        output.setForecastSaleDatas(List.of(next, next2, next3));

        System.out.println(JSON.toJSONString(output));
    }

    /**
     * 预测销售数据
     */
    @Data
    public static class ForecastSaleData {
        /**
         * 销售月份
         */
        private String saleMonth;
        /**
         * 预测销售量
         */
        private Integer saleCount;
        /**
         * 增长率
         */
        private Double growthRatio;
        /**
         * 月度预测说明
         */
        private String remark;
    }
}
