package org.jeecg.modules.wms.shipment.strategy;//package org.jeecg.modules.wms.shipment.strategy;
//
//import org.jeecg.modules.wms.config.WarehouseDictEnum;
//import org.jeecg.modules.wms.goods.service.IWmsProductsService;
//import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
//import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
//import org.jeecg.modules.wms.shipment.entity.WmsShipment;
//import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
//import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
//import org.jeecg.modules.wms.shipment.vo.ShipmentGenerationResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@Order(300)
//public class SplitByWeightShipmentStrategy2 extends AbstractShipmentStrategy {
//
//    private IWmsShipmentService shipmentService;
//    private IWmsProductsService productsService;
//
//    @Autowired
//    public SplitByWeightShipmentStrategy2(IWmsShipmentService shipmentService, IWmsProductsService productsService) {
//        super(shipmentService,productsService);
//        this.shipmentService = shipmentService;
//        this.productsService = productsService;
//    }
//
//    // 最大重量20kg
//    private static final double MAX_WEIGHT = 20.0;
//
//    @Override
//    public List<ShipmentGenerationResult> generateShipments(WmsOutOrders order,
//                                                            List<WmsOutOrdersItems> items) {
//        // 按重量拆分后的包裹信息
//        List<ShipmentGenerationResult> results = new ArrayList<>();
//        // 当前批次
//        List<WmsOutOrdersItems> currentBatch = new ArrayList<>();
//        // 当前批次总重量
//        double currentWeight = 0;
//        // 批次号
//        int batchNumber = 1;
//
//        for (WmsOutOrdersItems item : items) {
//            // 获取商品重量
//            double itemWeight = getItemWeight(item.getSkuId()) * item.getPickedQuantity();
//
//            if (currentWeight + itemWeight > MAX_WEIGHT && !currentBatch.isEmpty()) {
//                results.add(createBatchResult(order, currentBatch, batchNumber++));
//                currentBatch = new ArrayList<>();
//                currentWeight = 0;
//            }
//
//            currentBatch.add(item);
//            currentWeight += itemWeight;
//        }
//
//        if (!currentBatch.isEmpty()) {
//            results.add(createBatchResult(order, currentBatch, batchNumber));
//        }
//
//        return results;
//    }
//
//    private ShipmentGenerationResult createBatchResult(WmsOutOrders order,
//                                                     List<WmsOutOrdersItems> batch,
//                                                     int batchNo) {
//        WmsShipment shipment = createBaseShipment(order);
//        //包裹类型
//        shipment.setShipmentType(WarehouseDictEnum.PACKAGE_TYPE_STANDARD.getCode());
////        shipment.setShipmentType("SPLIT_BY_WEIGHT");
////        shipment.setShipmentNo(generateBatchShipmentNo(order, batchNo));
////        shipment.setTrackingNo(generateTrackingNo());
//
//        double batchWeight = calculateTotalWeight(batch);
//        shipment.setTotalWeight(batchWeight);
////        shipment.setRemark(String.format("按重量拆分包裹-%d (%.2fkg)", batchNo, batchWeight));
//        //生成包裹明细
//        List<WmsShipmentDetail> shipmentDetail = createShipmentDetail(shipment, batch);
//        return new ShipmentGenerationResult(shipment, batch,shipmentDetail
////                , Map.of(
////            "batchNo", batchNo,
////            "totalWeight", batchWeight
////        )
//        );
//    }
//
//    @Override
//    public boolean supports(WmsOutOrders order) {
//        return "SPLIT_BY_WEIGHT".equals(order.getOrderType());
//    }
//
//    @Override
//    public String getStrategyName() {
//        return "SPLIT_BY_WEIGHT_STRATEGY";
//    }
////    private WmsShipment createBaseShipment(WmsOutOrders order) {
////        // 生成出库单号
////        String shipmentNumber = shipmentService.generateShipmentNumber();
////        WmsShipment shipment = new WmsShipment();
////        shipment.setShipmentNo(shipmentNumber);
////        shipment.setOrderId(order.getId());
////        shipment.setWaveId(order.getWaveId());
////        shipment.setStatus("CREATED");
////        return shipment;
////    }
//
//}
