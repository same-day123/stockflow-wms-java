package org.jeecg.modules.iot.manage.demo.ai.service;


import org.jeecg.modules.iot.manage.demo.ai.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 数据模拟服务
 *
 * @author muht
 * @create 2025/8/8 11:53
 */
public class MockService {
    private static final Random random = new Random();
    private static final Integer year = Year.now().getValue();
    /**
     * 过去3个月的日期列表
     */
    private static final List<MonthDates> dates = getMonthDates();

    /**
     * mock 多个商品的历史销售数据
     *
     * @return
     */
    public static List<HistorySaleData> getMultiGoodsHistorySaleDatas() {
        List<HistorySaleData> historySaleDatas = new ArrayList<>();
        List<GoodsInfo> goodsInfoList = getGoodsInfoList();
        for (GoodsInfo goodsInfo : goodsInfoList) {
            historySaleDatas.add(getHistorySaleDatas(goodsInfo));
        }
        return historySaleDatas;
    }

    /**
     * Mock 历史销售数据
     *
     * @return
     */
    public static HistorySaleData getHistorySaleDatas(GoodsInfo goodsInfo) {
        List<MonthSaleData> monthSaleDatas = new ArrayList<>();

        // mock 日销售数据
        dates.stream()
                .forEach(monthDates -> {
                    Integer month = monthDates.getMonth();
                    // 当月日期列表
                    List<LocalDate> dateList = monthDates.getDates();
                    // mock随机日销售量（正式逻辑应该从数据库取日销售记录）
                    List<DaySaleData> daySaleDatas = dateList.stream()
                            .map(date -> new DaySaleData()
                                    .setSaleDate(date)
                                    .setSaleCount(getRandomSaleCount()))
                            .toList();

                    // 构造月销售数据（日数据聚合）
                    MonthSaleData monthSaleData = new MonthSaleData();
                    monthSaleData.setSaleMonth(year + "-" + month + "月");
                    // 汇总当月总销售量
                    int amount = daySaleDatas.stream()
                            .map(data -> data.getSaleCount())
                            .mapToInt(Integer::intValue)
                            .sum();
                    monthSaleData.setAmount(amount);
                    // 是否旺季
                    monthSaleData.setIsHighSeason(false);
                    // 是否促销月
                    monthSaleData.setIsPromotion(false);
                    monthSaleData.setDaySaleDatas(daySaleDatas);
                    monthSaleDatas.add(monthSaleData);
                });

        // 构造历史销售数据
        HistorySaleData historySaleData = new HistorySaleData();
        historySaleData.setGoodsInfo(goodsInfo);
        historySaleData.setMonthSaleDatas(monthSaleDatas);
        historySaleData.setStockQuantity(5000);
        return historySaleData;
    }

    /**
     * 获取最近3个月的日期
     *
     * @return
     */
    public static List<MonthDates> getMonthDates() {
        List<MonthDates> monthDatesList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        YearMonth startMonth = YearMonth.from(today).minusMonths(3);
        YearMonth endMonth = YearMonth.from(today).minusMonths(1);

        // 遍历每个月份，获取该月所有日期
        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            int daysInMonth = month.lengthOfMonth();
            MonthDates monthDates = new MonthDates();
            monthDates.setMonth(month.getMonth().getValue());
            for (int day = 1; day <= daysInMonth; day++) {
                monthDates.getDates().add(month.atDay(day));
            }
            monthDatesList.add(monthDates);
        }
        return monthDatesList;
    }

    /**
     * 随机日销售量
     *
     * @return
     */
    public static Integer getRandomSaleCount() {
        Integer min = 1;
        Integer max = 50;
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 模拟10个商品信息
     *
     * @return
     */
    private static List<GoodsInfo> getGoodsInfoList() {
        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        GoodsInfo fridge = new GoodsInfo();
        fridge.setGoodsId("GOODS-1");
        fridge.setGoodsName("冰箱");
        fridge.setGoodsType("家电");
        fridge.setGoodsPrice(new BigDecimal("199.0"));

        GoodsInfo washer = new GoodsInfo();
        washer.setGoodsId("GOODS-2");
        washer.setGoodsName("洗衣机");
        washer.setGoodsType("家电");
        washer.setGoodsPrice(new BigDecimal("340.0"));

        GoodsInfo dryer = new GoodsInfo();
        dryer.setGoodsId("GOODS-3");
        dryer.setGoodsName("电吹风");
        dryer.setGoodsType("日用品");
        dryer.setGoodsPrice(new BigDecimal("98.0"));

        GoodsInfo kettle = new GoodsInfo();
        kettle.setGoodsId("GOODS-4");
        kettle.setGoodsName("电水壶");
        kettle.setGoodsType("日用品");
        kettle.setGoodsPrice(new BigDecimal("34.0"));

        GoodsInfo keyboard = new GoodsInfo();
        keyboard.setGoodsId("GOODS-4");
        keyboard.setGoodsName("键盘");
        keyboard.setGoodsType("办公用品");
        keyboard.setGoodsPrice(new BigDecimal("119.0"));

        return List.of(fridge, washer, dryer, kettle, keyboard);
    }
}
