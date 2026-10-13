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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 按重量拆分包裹策略
 * @date 2025/6/18 15:55
 */
@Component
@Order(300)
public class SplitByWeightShipmentStrategy extends AbstractShipmentStrategy {

    private IWmsShipmentService shipmentService;
    private IWmsProductsService productsService;

    @Autowired
    public SplitByWeightShipmentStrategy(IWmsShipmentService shipmentService, IWmsProductsService productsService) {
        super(shipmentService,productsService);
        this.shipmentService = shipmentService;
        this.productsService = productsService;
    }

    // 最大重量20kg
    private static final double MAX_WEIGHT = 20.0;

    @Override
    public List<ShipmentGenerationResult> generateShipments(WmsOutOrders order,
                                                            List<WmsOutOrdersItems> items) {
        // 按重量拆分后的包裹信息
        List<ShipmentGenerationResult> results = new ArrayList<>();
        // 当前批次
        List<WmsOutOrdersItems> currentBatch = new ArrayList<>();
        // 当前批次总重量
        double currentWeight = 0;
        // 批次号
        int batchNumber = 1;

        for (WmsOutOrdersItems item : items) {
            // 获取商品拣货数量
            Integer pickedQuantity = item.getPickedQuantity();
            // 获取商品已打包数量
            Integer packedQuantity = item.getPackedQuantity();
            //剩余打包数量
            int surplusQuantity = pickedQuantity- ObjectUtil.defaultIfNull(packedQuantity,0);
            if(surplusQuantity<=0){
                continue;
            }
            // 获取商品单位重量
            double unitWeight = getItemWeight(item.getSkuId());
            // 商品总重量
            double totalItemWeight = unitWeight * surplusQuantity;

            // 如果单个商品总重量超过最大限制，需要拆分
            if (totalItemWeight > MAX_WEIGHT) {
                // 先处理当前批次
                if (!currentBatch.isEmpty()) {
                    results.add(createBatchResult(order, currentBatch, batchNumber++));
                    currentBatch = new ArrayList<>();
                    currentWeight = 0;
                }

                // 拆分该商品到多个包裹
                results.addAll(splitSingleItem(order, item, unitWeight, batchNumber));
                batchNumber += Math.ceil(totalItemWeight / MAX_WEIGHT);
            } else {
                // 如果加入当前商品会超重，先完成当前批次
                if (currentWeight + totalItemWeight > MAX_WEIGHT && !currentBatch.isEmpty()) {
                    results.add(createBatchResult(order, currentBatch, batchNumber++));
                    currentBatch = new ArrayList<>();
                    currentWeight = 0;
                }

                // 添加商品到当前批次
                currentBatch.add(item);
                currentWeight += totalItemWeight;
            }
        }

        // 处理最后一批
        if (!currentBatch.isEmpty()) {
            results.add(createBatchResult(order, currentBatch, batchNumber));
        }

        return results;
    }

    /**
     * 拆分单个商品到多个包裹
     * @param order 出库单
     * @param item 需要拆分的商品项
     * @param unitWeight 商品单位重量
     * @param startBatchNumber 起始批次号
     * @return 拆分后的包裹列表
     */
    private List<ShipmentGenerationResult> splitSingleItem(WmsOutOrders order,
                                                           WmsOutOrdersItems item,
                                                           double unitWeight,
                                                           int startBatchNumber) {
        List<ShipmentGenerationResult> results = new ArrayList<>();
        //该商品总的拣货数量
        int remainingQuantity = item.getPickedQuantity();
        int batchNum = startBatchNumber;

        while (remainingQuantity > 0) {
            // 计算当前包裹能装的最大数量
            int maxQuantity = (int) Math.floor(MAX_WEIGHT / unitWeight);
            int quantityInBatch = Math.min(maxQuantity, remainingQuantity);

            // 创建商品副本并设置当前批次数量
            WmsOutOrdersItems splitItem = copyItemWithQuantity(item, quantityInBatch);
            List<WmsOutOrdersItems> batch = new ArrayList<>();
            batch.add(splitItem);

            // 创建包裹
            results.add(createBatchResult(order, batch, batchNum++));

            remainingQuantity -= quantityInBatch;
        }

        return results;
    }

    /**
     * 创建商品项的副本并设置指定数量
     * @param original 原始商品项
     * @param quantity 新数量
     * @return 新商品项
     */
    private WmsOutOrdersItems copyItemWithQuantity(WmsOutOrdersItems original, int quantity) {
        WmsOutOrdersItems copy = new WmsOutOrdersItems();
        BeanUtils.copyProperties(original, copy);
        copy.setPickedQuantity(quantity);
        return copy;
    }

    private ShipmentGenerationResult createBatchResult(WmsOutOrders order,
                                                       List<WmsOutOrdersItems> batch,
                                                       int batchNo) {
        WmsShipment shipment = createBaseShipment(order);
        //包裹类型
        shipment.setShipmentType(WarehouseDictEnum.PACKAGE_TYPE_STANDARD.getCode());

        double batchWeight = calculateTotalWeight(batch);
        shipment.setTotalWeight(batchWeight);
        //生成包裹明细
        List<WmsShipmentDetail> shipmentDetail = createShipmentDetail(shipment, batch);
        return new ShipmentGenerationResult(shipment, batch, shipmentDetail);
    }

    @Override
    public boolean supports(WmsOutOrders order) {
        return "SPLIT_BY_WEIGHT_STRATEGY".equals(order.getShipmentStrategy());
    }

    @Override
    public String getStrategyName() {
        return "SPLIT_BY_WEIGHT_STRATEGY";
    }
}
