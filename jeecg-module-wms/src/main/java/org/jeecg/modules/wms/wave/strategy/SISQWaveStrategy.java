package org.jeecg.modules.wms.wave.strategy;

import lombok.RequiredArgsConstructor;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import org.jeecg.modules.wms.wave.entity.WmsWaveStrategy;
import org.jeecg.modules.wms.wave.service.IWaveStrategyService;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单品单件策略
 */
@Component
@RequiredArgsConstructor
public class SISQWaveStrategy extends IWaveStrategy{

    private final IWmsWaveMasterService wmsWaveMasterService;

    private final IWaveStrategyService waveStrategyService;

    private final IWmsOutOrdersItemsService wmsOutOrdersItemsService;

    /**
     * 单品单件策略
     * @param orders
     * @param allocationMap
     */
    @Override
    void process(List<WmsOutOrders> orders, Map<String, List<WmsOutOrdersAllocation>> allocationMap) {
        //包裹策略为STANDARD_STRATEGY
        String shipmentStrategy = "STANDARD_STRATEGY";
        //选出满足单品单件的出库单
        List<WmsOutOrders> collect = orders.stream().filter(order -> order.getTotalSku() == 1 && order.getTotalQuantity() == 1).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return;
        }
        //拿到波次的最小订单数
        WmsWaveStrategy strategyByCode = waveStrategyService.getStrategyByCode(getStrategyType());
        Integer minOrderCount = strategyByCode.getMinOrderCount();
        //获取出库单id
        List<String> orderIds = collect.stream().map(WmsOutOrders::getId).collect(Collectors.toList());
        //根据出库单id获取出库单明细
        List<WmsOutOrdersItems> wmsOutOrdersItems = wmsOutOrdersItemsService.selectByOrderIds(orderIds);
        //将出库单明细中的skuId赋值到入库单collect中
        collect.forEach(order -> {
            WmsOutOrdersItems outOrdersItems = wmsOutOrdersItems.stream().filter(item -> item.getOrderId().equals(order.getId())).findFirst().orElse(null);
            order.setSkuId(outOrdersItems.getSkuId());
        });
        //对collect商品类型SkuId进行分组
        Map<String, List<WmsOutOrders>> collect1 = collect.stream().collect(Collectors.groupingBy(WmsOutOrders::getSkuId));
        //创建一个List用来存放已经分配波次的订单
        ArrayList<WmsOutOrders> list = new ArrayList<>();
        //遍历商品类型SkuId
        for (Map.Entry<String, List<WmsOutOrders>> entry : collect1.entrySet()) {
            String key = entry.getKey();
            List<WmsOutOrders> value = entry.getValue();
            //订单数要大于策略里面的最小订单数
            if (value.size() >= minOrderCount) {
                //创建波次
                wmsWaveMasterService.addWave(value,getStrategyType(),shipmentStrategy);
                list.addAll(value);
            }
        }
        //通过把有波次号的订单放到List当中来拿到剩余订单，没有分配波次的订单，剩下的订单
        List<WmsOutOrders> wmsOutOrders = orders.stream().filter(order -> !list.contains(order)).collect(Collectors.toList());
        //调用下一个策略
        processNext(wmsOutOrders,allocationMap);

    }

    @Override
    String getStrategyType() {
        return "SISQ";
    }

    @Override
    int getPriority() {
        return 1;
    }
}
