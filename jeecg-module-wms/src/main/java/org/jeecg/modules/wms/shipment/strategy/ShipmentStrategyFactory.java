package org.jeecg.modules.wms.shipment.strategy;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 打包生成策略工厂
 */
@Service
public class ShipmentStrategyFactory {

    private final List<ShipmentGenerationStrategy> strategies;

    @Autowired
    public ShipmentStrategyFactory(List<ShipmentGenerationStrategy> strategies) {
        // 根据@Order注解排序
        this.strategies = strategies.stream()
            .sorted(AnnotationAwareOrderComparator.INSTANCE)
            .collect(Collectors.toList());
    }

    public ShipmentGenerationStrategy getStrategy(WmsOutOrders order) {
        return strategies.stream()
            .filter(strategy -> strategy.supports(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("不支持的订单类型: " + order.getOrderType()));
    }
}
