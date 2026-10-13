package org.jeecg.modules.wms.shipment.strategy;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.shipment.entity.WmsShipmentDetail;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
import org.jeecg.modules.wms.shipment.vo.ShipmentGenerationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 抽象打包策略
 * @date 2025/6/17 15:34
 */
public abstract class AbstractShipmentStrategy implements ShipmentGenerationStrategy {

    protected IWmsShipmentService shipmentService;

    protected IWmsProductsService productsService;

    public AbstractShipmentStrategy(IWmsShipmentService shipmentService, IWmsProductsService productsService) {
        this.shipmentService = shipmentService;
        this.productsService = productsService;
    }

    @Override
    public abstract List<ShipmentGenerationResult> generateShipments(WmsOutOrders order, List<WmsOutOrdersItems> items);

    @Override
    public abstract boolean supports(WmsOutOrders order);

    @Override
    public abstract String getStrategyName();

    protected WmsShipment createBaseShipment(WmsOutOrders order) {
        //生成包裹号
        String shipmentNumber = shipmentService.generateShipmentNumber();
        WmsShipment shipment = new WmsShipment();
        //主键用雪花算法
        shipment.setId(String.valueOf(IdWorker.getId()));
        shipment.setShipmentNo(shipmentNumber);
        shipment.setOrderId(order.getId());
        shipment.setWaveId(order.getWaveId());
        shipment.setStatus(WarehouseDictEnum.PACKAGE_STATUS_CREATED.getCode());//已创建
        return shipment;
    }
    protected Double getItemWeight(String skuId){
        WmsProducts products = productsService.getById(skuId);
        if(products == null){
            //抛出异常
            throw new RuntimeException("商品不存在");
        }
        return products.getNetWeight();
    }
    /**
     * 计算总重量
     * @param items
     * @return
     */
    protected double calculateTotalWeight(List<WmsOutOrdersItems> items) {
        double sum = items.stream().mapToDouble(item -> {
            String skuId = item.getSkuId();
            WmsProducts products = productsService.getById(skuId);
            //重量
            double weight = products.getNetWeight();
            //重量乘以数量
            return weight * item.getPickedQuantity();
        }).sum();
        return sum;
    }

    /**
     * 生成包裹明细
     */
    protected List<WmsShipmentDetail> createShipmentDetail(WmsShipment shipment,List<WmsOutOrdersItems> outOrdersItems) {
        List<WmsShipmentDetail> allDetails = new ArrayList<>();
        List<WmsOutOrdersItems> items = new ArrayList<>();
            for (WmsOutOrdersItems item : outOrdersItems) {
                WmsShipmentDetail detail = convertToDetail(shipment, item);
                allDetails.add(detail);

                // 出库单明细设置打包数量
                item.setPackedQuantity(item.getPickedQuantity());
                // 更新出库单明细状态为已打包
                item.setStatus(WarehouseDictEnum.OUTBOUND_DETAIL_PACKED.getCode());
                items.add(item);
            }
        return allDetails;
    }
    private WmsShipmentDetail convertToDetail(WmsShipment shipment, WmsOutOrdersItems item) {
        WmsShipmentDetail detail = new WmsShipmentDetail();
        detail.setShipmentId(shipment.getId());
        detail.setOrderId(shipment.getOrderId());
        detail.setOrderItemId(item.getId());
        detail.setSkuId(item.getSkuId());
        detail.setQuantity(item.getPickedQuantity());
        return detail;
    }
}
