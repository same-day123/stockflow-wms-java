package org.jeecg.modules.wms.shunfeng.other;


import org.junit.jupiter.api.Test;

public class ShunFengTest{

    /**
     * 顺丰下单
     */
    @Test
    public void testShunfengOrderService() {

        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setOrderId("20190121181653954019");
        expressOrder.setjProvince("广东省");
        expressOrder.setjCity("深圳市");
        expressOrder.setjCounty("南山区");
        expressOrder.setjCompany("金草中医");
        expressOrder.setjContact("李大宝");
        expressOrder.setjTel("18777276920");
        expressOrder.setjAddress("龙珠四路金谷创业园c座611");

        expressOrder.setdProvince("广东省");
        expressOrder.setdCity("广州市");
        expressOrder.setdCounty("海珠区");
        expressOrder.setdCompany("个人");
        expressOrder.setdContact("滕大宝");
        expressOrder.setdTel("18938905541");
        expressOrder.setdAddress("宝芝林大厦701室");
        System.out.println("顺丰下单");
        String requestXml = RequestXmlUtil.getOrderServiceRequestXml(expressOrder);
        System.out.println("请求报文：" + requestXml);
        String respXml= CallExpressServiceTools.callSfExpressServiceByCSIM(requestXml);
        System.out.println("响应报文：" + respXml);
    }

    /**
     * 下单结果查询
     */
    @Test
    public void testShunfengOrderSearcheService() {
    	  System.out.println("下单结果查询");
        String requestXml = RequestXmlUtil.getOrderSearchServiceRequestXml("20190121181653954019");
        System.out.println("请求报文：" + requestXml);
        String respXml= CallExpressServiceTools.callSfExpressServiceByCSIM(requestXml);
        System.out.println("响应报文：" + respXml);
    }

    /**
     * 路由查询
     */
    @Test
    public void testShunfengRouteSearch() {
    	 System.out.println("路由查询");
        String requestXml = RequestXmlUtil.getRouteServiceRequestXml("444010274754");
        System.out.println("请求报文：" + requestXml);
        String respXml= CallExpressServiceTools.callSfExpressServiceByCSIM(requestXml);
        System.out.println("响应报文：" + respXml);
    }

    /**
     * 订单取消
     */
    @Test
    public void testShunfengOrderCancelService() {
    	 System.out.println("订单取消");
        String requestXml = RequestXmlUtil.getOrderCancelServiceRequestXml("SF7444403973412");
        System.out.println("请求报文：" + requestXml);
        String respXml= CallExpressServiceTools.callSfExpressServiceByCSIM(requestXml);
        System.out.println("响应报文：" + respXml);
    }
}
