package org.jeecg.modules.wms.shipment.vo;

import lombok.Data;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;

import java.util.List;

/**
 * 包裹生成结果封装
 */
@Data
public class ShipmentGenerationResult {
    //包裹主表
    private final WmsShipment shipment;
    //包裹包含的出库单明细
    private final List<WmsOutOrdersItems> outOrdersItems;
    //包裹明细
    private final List<WmsShipmentDetail> shipmentDetail;

    public ShipmentGenerationResult(WmsShipment shipment,
                                  List<WmsOutOrdersItems> outOrdersItems,
                                    List<WmsShipmentDetail> shipmentDetail) {
        this.shipment = shipment;
        this.outOrdersItems = outOrdersItems;
        this.shipmentDetail = shipmentDetail;
    }
}
