package org.jeecg.modules.wms.wave.strategy;

import lombok.RequiredArgsConstructor;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 尾单波次策略 - EOW
 * 条件：未创建波次且已分配库存的订单
 */
@Component
@RequiredArgsConstructor
public class EOWWaveStrategy extends IWaveStrategy {


    private final IWmsWaveMasterService wmsWaveMasterService;

    @Override
    public void process(List<WmsOutOrders> orders,
                                    Map<String, List<WmsOutOrdersAllocation>> allocationsMap) {

        //包裹策略为STANDARD_STRATEGY
        String shipmentStrategy="SPLIT_BY_WEIGHT_STRATEGY";

        List<WmsOutOrders> matchedOrders = orders.stream()
            .filter(order -> order.getWaveId()==null && order.getStatus().equals(WarehouseDictEnum.OUTBOUND_ALLOCATED.getCode()) )
            .collect(Collectors.toList());

        if (!matchedOrders.isEmpty()) {
            wmsWaveMasterService.addWave(matchedOrders, getStrategyType(),shipmentStrategy);
        }

        List<WmsOutOrders> collect = orders.stream()
                .filter(order -> !matchedOrders.contains(order))
                .collect(Collectors.toList());
        if(collect.size()>0 && next()!=null){
            next().process(collect, allocationsMap);
        }
    }

    @Override
    public String getStrategyType() {
        return "EOW";
    }

    @Override
    public int getPriority() {
        return 10;
    }

}
