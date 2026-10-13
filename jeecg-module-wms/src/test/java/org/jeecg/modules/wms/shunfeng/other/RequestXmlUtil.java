package org.jeecg.modules.wms.shunfeng.other;

import org.apache.http.util.TextUtils;
/**
 * 顺丰请求的工具类
 * @author lx
 * @date
 */
public class RequestXmlUtil {

    /**
     * 顾客编码
     */
    public static final String CLIENT_CODE = "Y2VL6F82";

    /**
     * 获取顺丰路由查询接口xml
     * @param mailNo 快递运单号
     * @return
     */
    public static String getRouteServiceRequestXml(String mailNo){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<?xml version='1.0' encoding='UTF-8'?>");
        strBuilder.append("<Request service='RouteService' lang='zh-CN'>");
        strBuilder.append("<Head>" + CLIENT_CODE + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<RouteRequest").append(" ");
        strBuilder.append("tracking_type='1'").append(" ");
        strBuilder.append("method_type='1'").append(" ");
        strBuilder.append("tracking_number='" + mailNo + "'").append("/>");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");
        return strBuilder.toString();
    }

    /**
     * 获取下单请求体
     * @param expressOrder
     * @return
     */
    public static String getOrderServiceRequestXml(ExpressOrder expressOrder) {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<?xml version='1.0' encoding='UTF-8'?>");
        strBuilder.append("<Request service='OrderService' lang='zh-CN'>");
        strBuilder.append("<Head>" + CLIENT_CODE + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<Order").append(" ");
        strBuilder.append("orderid='" + expressOrder.getOrderId() + "'").append(" ");
        //返回顺丰运单号
        strBuilder.append("express_type='1'").append(" ");
        //寄件方信息
        strBuilder.append("j_province='" + expressOrder.getjProvince() + "'").append(" ");
        strBuilder.append("j_city='" + expressOrder.getjCity() + "'").append(" ");
        strBuilder.append("j_county='" + expressOrder.getjCounty() + "'").append(" ");
        strBuilder.append("j_company='" + expressOrder.getjCompany() + "'").append(" ");
        strBuilder.append("j_contact='" + expressOrder.getjContact() + "'").append(" ");
        strBuilder.append("j_tel='" + expressOrder.getjTel() + "'").append(" ");
        strBuilder.append("j_address='" + expressOrder.getjAddress() + "'").append(" ");
        //收件方信息
        strBuilder.append("d_province='" + expressOrder.getdProvince() + "'").append(" ");
        strBuilder.append("d_city='" + expressOrder.getdCity() + "'").append(" ");
        strBuilder.append("d_county='" + expressOrder.getdCounty() + "'").append(" ");
        strBuilder.append("d_county=''").append(" ");
        strBuilder.append("d_company='" + expressOrder.getdCompany() + "'").append(" ");
        strBuilder.append("d_tel='" + expressOrder.getdTel() + "'").append(" ");
        strBuilder.append("d_contact='" + expressOrder.getdContact() + "'").append(" ");
        strBuilder.append("d_address='" + expressOrder.getdAddress() + "'").append(" ");
        if (!TextUtils.isEmpty(expressOrder.getRemark())) {
            strBuilder.append("remark='" + expressOrder.getRemark() + "'").append(" ");
        }
        //货物信息
        strBuilder.append("parcel_quantity='1'").append(" ");
        strBuilder.append("pay_method='1'").append(" ");
        strBuilder.append("custid ='" + CallExpressServiceTools.CUST_ID + "'").append(" ");
        strBuilder.append("customs_batchs=''").append(" ");
        strBuilder.append("cargo='医药'").append(">");
//        strBuilder.append("<AddedService name='COD' value='1.01' value1='7551234567' />");
        strBuilder.append("</Order>");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");
        return strBuilder.toString();
    }

    /**
     * 查询下单请求体
     * @param orderNo 订单号
     * @return
     */
    public static String getOrderSearchServiceRequestXml(String orderNo) {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<Request service='OrderSearchService' lang='zh-CN'>");
        strBuilder.append("<Head>" + CLIENT_CODE + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<OrderSearch").append(" ");
        strBuilder.append("orderid='" + orderNo + "'").append(" /> ");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");
        return strBuilder.toString();
    }

    /**
     * 取消顺丰订单请求体
     * @param orderNo 订单号
     * @return
     */
    public static String getOrderCancelServiceRequestXml(String orderNo) {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<Request service='OrderConfirmService' lang='zh-CN'>");
        strBuilder.append("<Head>" + CLIENT_CODE + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<OrderConfirm").append(" ");
        strBuilder.append("orderid='" + orderNo + "'").append(" ");
        strBuilder.append("dealtype='2'");
        strBuilder.append(" /> ");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");
        return strBuilder.toString();
    }
}
