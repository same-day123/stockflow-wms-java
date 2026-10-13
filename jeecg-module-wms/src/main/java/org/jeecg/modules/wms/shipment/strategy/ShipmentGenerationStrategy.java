package org.jeecg.modules.wms.shipment.strategy;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.shipment.vo.ShipmentGenerationResult;

import java.util.List;

/**
 * 包裹生成策略接口
 */
public interface ShipmentGenerationStrategy {

    /**
     * 为出库单生成包裹
     * @param order 出库单
     * @param items 出库单明细项
     * @return 生成的包裹结果列表
     */
    List<ShipmentGenerationResult> generateShipments(WmsOutOrders order,
                                                     List<WmsOutOrdersItems> items);

    /**
     * 是否支持该订单类型
     */
    boolean supports(WmsOutOrders order);

    /**
     * 策略名称
     */
    String getStrategyName();
}
