package org.jeecg.modules.wms.shipment.strategy;

import cn.hutool.core.util.ObjectUtil;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
import org.jeecg.modules.wms.shipment.vo.ShipmentGenerationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 标准包裹策略,对应单品单件波次策略
 */
@Component
@Order(100)
public class StandardShipmentStrategy extends AbstractShipmentStrategy {

    private IWmsShipmentService shipmentService;
    private IWmsProductsService productsService;

    @Autowired
    public StandardShipmentStrategy(IWmsShipmentService shipmentService,IWmsProductsService productsService) {
        super(shipmentService,productsService);
        this.shipmentService = shipmentService;
        this.productsService = productsService;
    }

    @Override
    public List<ShipmentGenerationResult> generateShipments(WmsOutOrders order,
                                                            List<WmsOutOrdersItems> items) {
        WmsOutOrdersItems wmsOutOrdersItems = items.get(0);
        //如果打包数量等于拣货数量则直接返回
        if(ObjectUtil.defaultIfNull(wmsOutOrdersItems.getPackedQuantity(),0) == wmsOutOrdersItems.getPickedQuantity()){
            return Collections.emptyList();
        }
        WmsShipment shipment = createBaseShipment(order);
        //包裹类型
        shipment.setShipmentType(WarehouseDictEnum.PACKAGE_TYPE_STANDARD.getCode());
        //件数
        shipment.setPackageCount(items.size());
//        shipment.setTrackingNo(generateTrackingNo());
        // 计算总重量
        double totalWeight = calculateTotalWeight(items);

        shipment.setTotalWeight(totalWeight);
        //生成包裹明细
        List<WmsShipmentDetail> shipmentDetail = createShipmentDetail(shipment, items);
        return Collections.singletonList(
                new ShipmentGenerationResult(shipment, items,shipmentDetail
                )
        );
    }

    @Override
    public boolean supports(WmsOutOrders order) {
        return "STANDARD_STRATEGY".equals(order.getShipmentStrategy());
    }

    @Override
    public String getStrategyName() {
        return "STANDARD_STRATEGY";
    }


}
