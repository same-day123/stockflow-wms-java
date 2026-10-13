package org.jeecg.modules.wms.wave.strategy;

import lombok.RequiredArgsConstructor;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.wave.entity.WmsWaveStrategy;
import org.jeecg.modules.wms.wave.service.IWaveStrategyService;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一品波次策略 - SIW
 * 条件：订单中只有一个SKU种类
 */
@Component
@RequiredArgsConstructor
public class SIWWaveStrategy extends IWaveStrategy {

    private final IWmsWaveMasterService wmsWaveMasterService;

    private final IWaveStrategyService waveStrategyService;

    private final IWmsOutOrdersItemsService wmsOutOrdersItemsService;
    @Override
    public void process(List<WmsOutOrders> orders,
                                    Map<String, List<WmsOutOrdersAllocation>> allocationsMap) {
        String shipmentStrategy="SPLIT_BY_WEIGHT_STRATEGY";

         List<WmsOutOrders> matchedOrders = orders.stream()
            .filter(order -> order.getTotalSku() == 1 )
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchedOrders)) {
            processNext(orders, allocationsMap);
            return;
        }

        //从数据库取出当前策略的详细信息
        WmsWaveStrategy strategyByCode = waveStrategyService.getStrategyByCode(this.getStrategyType());
        //最小订单数
        int minOrderCount = strategyByCode.getMinOrderCount();
        //最大订单数
        int maxOrderCount = strategyByCode.getMaxOrderCount();
        //出库单id
        List<String> orderIds = matchedOrders.stream().map(WmsOutOrders::getId).collect(Collectors.toList());

        //根据出库单id查询出库单明细
        List<WmsOutOrdersItems> allocations = wmsOutOrdersItemsService.selectByOrderIds(orderIds);

        //遍历matchedOrders,从allocations中找到对应的出库单明细并找到skuId，复制到matchedOrders的对象中
        for (WmsOutOrders order : matchedOrders) {
            WmsOutOrdersItems allocation = allocations.stream()
                    .filter(item -> item.getOrderId().equals(order.getId()))
                    .findFirst()
                    .orElse(new WmsOutOrdersItems());
            order.setSkuId(allocation.getSkuId());
        }

        //对matchedOrders按skuId分组
        Map<String, List<WmsOutOrders>> groupedOrders = matchedOrders.stream().collect(Collectors.groupingBy(WmsOutOrders::getSkuId));
        //添加到波次的订单
        List<WmsOutOrders> wmsOutOrders = new ArrayList<>();
        //遍历groupedOrders
        for (Map.Entry<String, List<WmsOutOrders>> entry : groupedOrders.entrySet()) {
            List<WmsOutOrders> skuOrders = entry.getValue();
            if (skuOrders.size() >= minOrderCount) {
                wmsWaveMasterService.addWave(skuOrders, getStrategyType(), shipmentStrategy);
                wmsOutOrders.addAll(skuOrders);
            }
        }


        List<WmsOutOrders> collect = orders.stream()
                .filter(order -> !wmsOutOrders.contains(order))
                .collect(Collectors.toList());
        processNext(collect, allocationsMap);
    }
    //继续执行
    public void processNext(List<WmsOutOrders> orders, Map<String, List<WmsOutOrdersAllocation>> allocationsMap) {
        if (orders.size() > 0 && next() != null) {
            next().process(orders, allocationsMap);
        }
    }
    @Override
    public String getStrategyType() {
        return "SIW";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
