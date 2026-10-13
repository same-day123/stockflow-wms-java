package org.jeecg.modules.wms.waybill.service;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 顺丰对接service
 * @date 2025/7/16 8:23
 */
public interface IWmsSfService {
    /**
     * 请求快递平台获取运单号
     * @param order
     * @param shipments
     * @return 运单号
     * @throws Exception
     */
    public List<String> requestSfCreateOrder(WmsOutOrders order, List<WmsShipment> shipments) throws Exception;

    /**
     * 请求顺丰生成运单pdf
     */
    public void requestSfGenerateWaybillPdf(WmsOutOrders order, List<WmsShipment> shipments,List<String> waybillNoList) throws Exception;

}
