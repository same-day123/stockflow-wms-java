package org.jeecg.modules.wms.waybill.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import org.jeecg.modules.wms.waybill.service.IWmsSfService;
import org.jeecg.modules.wms.waybill.sf.SfApiResponse;
import org.jeecg.modules.wms.waybill.sf.SfExpressUtil;
import org.jeecg.modules.wms.waybill.sf.SfOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.M
 * @version 1.0
 * @description 顺丰对象service
 * @date 2025/7/16 8:26
 */
@Service
@Slf4j
public class IWmsSfServiceImpl implements IWmsSfService {

    @Autowired
    private ProvinceCityArea provinceCityArea;

    @Autowired
    private IWmsCargoOwnersService wmsCargoOwnersService;

    @Autowired
    private IWmsProductsService wmsProductsService;

    @Autowired
    private IWmsOutOrdersItemsService wmsOutOrdersItemsService;

    /**
     * 请求快递平台获取运单号
     */
    @Override
    public List<String> requestSfCreateOrder(WmsOutOrders order,List<WmsShipment> shipments) throws Exception {
        int shipmentSize = shipments.size();
        //请求顺丰下单
        //创建参数对象
        SfOrderInfo sfOrderInfo = new SfOrderInfo();
        /**
         * {
         * 	"cargoDetails": [{
         * 		"amount": 308.0,
         * 		"count": 1.0,
         * 		"name": "君宝牌地毯",
         * 		"unit": "个",
         * 		"volume": 0.0,
         * 		"weight": 0.1
         *        }],
         * 	"contactInfoList": [{
         * 		"address": "十堰市丹江口市公园路155号",
         * 		"city": "十堰市",
         * 		"company": "清雅轩保健品专营店",
         * 		"contact": "张三丰",
         * 		"contactType": 1,
         * 		"county": "武当山风景区",
         * 		"mobile": "17006805888",
         * 		"province": "湖北省"
         *    }, {
         * 		"address": "湖北省襄阳市襄城区环城东路122号",
         * 		"city": "襄阳市",
         * 		"contact": "郭襄阳",
         * 		"county": "襄城区",
         * 		"contactType": 2,
         * 		"mobile": "18963828829",
         * 		"province": "湖北省"
         *    }],
         * 	"customsInfo": {},
         * 	"expressTypeId": 2,
         * 	"extraInfoList": [],
         * 	"isOneselfPickup": 0,
         * 	"language": "zh-CN",
         * 	"orderId": "QIAO-20200618-00522",
         * 	"parcelQty": 3,
         * 	"payMethod": 1,
         * 	"totalWeight": 6,
         * 	"monthlyCard":7551234567
         *    }
         */
        sfOrderInfo.setOrderId(order.getOrderNo());
        sfOrderInfo.setPayMethod(1);
        sfOrderInfo.setParcelQty(shipments.size());//包裹数量
        sfOrderInfo.setTotalWeight(6);//测试数据
        sfOrderInfo.setMonthlyCard(SfExpressUtil.getMonthly_card());
        sfOrderInfo.setExpressTypeId(2);
        sfOrderInfo.setLanguage("zh-CN");
        sfOrderInfo.setIsOneselfPickup(0);
        //联系人信息
        List<SfOrderInfo.ContactInfo> contactInfoList = new ArrayList<>();
        //收件人信息
        SfOrderInfo.ContactInfo contactInfo = new SfOrderInfo.ContactInfo();
        contactInfo.setContactType(2);
        contactInfo.setContact(order.getConsignee());
        contactInfo.setMobile(order.getContact());
        contactInfo.setProvince(order.getShippingProvince());
        contactInfo.setCity(order.getShippingCity());
        contactInfo.setCounty(order.getShippingCounty());
        contactInfo.setAddress(order.getShippingAddress());
        contactInfoList.add(contactInfo);
        //发件人信息即货主信息
        //货主id
        String ownerId = order.getOwnerId();
        //查询货主信息
        WmsCargoOwners cargoOwner = wmsCargoOwnersService.getById(ownerId);
        SfOrderInfo.ContactInfo senderInfo = new SfOrderInfo.ContactInfo();
        senderInfo.setContactType(1);
        senderInfo.setContact(cargoOwner.getOwnerName());
        senderInfo.setMobile(cargoOwner.getPhone());
        senderInfo.setProvince(cargoOwner.getCity());
        senderInfo.setCity(cargoOwner.getCity());
        senderInfo.setCounty(cargoOwner.getCountry());
        senderInfo.setAddress(cargoOwner.getAddress());
        contactInfoList.add(senderInfo);
        //添加联系人信息
        sfOrderInfo.setContactInfoList(contactInfoList);
        //商品信息
        List<SfOrderInfo.CargoDetail> cargoDetails = new ArrayList<>();
        List<WmsOutOrdersItems> wmsOutOrdersItems = wmsOutOrdersItemsService.selectByMainId(order.getId());
        // 遍历
        for (WmsOutOrdersItems item : wmsOutOrdersItems) {
            //查询商品
            WmsProducts product = wmsProductsService.getById(item.getSkuId());
            SfOrderInfo.CargoDetail cargoDetail = new SfOrderInfo.CargoDetail();
            cargoDetail.setName(product.getProductName());
            cargoDetail.setWeight(product.getNetWeight());//单位重量
            cargoDetail.setCount(item.getPackedQuantity());
            cargoDetail.setVolume(0.0);//体积
            cargoDetail.setAmount(308.0);//价格
            cargoDetails.add(cargoDetail);
            sfOrderInfo.setCargoDetails(cargoDetails);
        }
        //测试数据
//        SfOrderInfo.CargoDetail cargoDetail = new SfOrderInfo.CargoDetail();
//        cargoDetail.setName("君宝牌地毯");
//        cargoDetail.setWeight(0.1);
//        cargoDetail.setCount(1.0);
//        cargoDetail.setVolume(0.0);
//        cargoDetail.setAmount(308.0);
//        cargoDetails.add(cargoDetail);
//        sfOrderInfo.setCargoDetails(cargoDetails);
        sfOrderInfo.setMonthlyCard(SfExpressUtil.getMonthly_card());

        //生成json
        String sfOrderInfoJson = JSON.toJSONString(sfOrderInfo);
        String responseBody = SfExpressUtil.post(SfExpressUtil.EXP_RECE_CREATE_ORDER, sfOrderInfoJson);
        /**
         * {"apiErrorMsg":"","apiResponseID":"0001980D3635003FCDC966C657C6883F","apiResultCode":"A1000","apiResultData":"{\"success\":false,\"errorCode\":\"8016\",\"errorMsg\":\"重复下单\",\"msgData\":null}"}
         */
//		HashMap hashMap = JSON.parseObject("{\"success\":false,\"errorCode\":\"8016\",\"errorMsg\":\"重复下单\",\"msgData\":null}", HashMap.class);
        //将响应结果转换成SfApiResponse
        SfApiResponse sfApiResponse = JSON.parseObject(responseBody, SfApiResponse.class);
        String apiResultDataJson = sfApiResponse.getApiResultData();
        SfApiResponse.ApiResultData1 apiResultData11 = JSON.parseObject(apiResultDataJson, SfApiResponse.ApiResultData1.class);
        //如果重复下单则调用查询订单接口
        if ("重复下单".equals(apiResultData11.getErrorMsg())) {
            /**
             * {
             * 	"searchType": "1",
             * 	"orderId": "QIAO-20200618-00522",
             * 	"language": "zh-cn"
             * }
             */
            //查询订单请求对象
            SfExpressUtil.SearchOrderParam searchOrderParam = new SfExpressUtil.SearchOrderParam();
            searchOrderParam.setOrderId(order.getOrderNo());
            searchOrderParam.setSearchType("1");
            searchOrderParam.setLanguage("zh-cn");
            String searchOrderResponseBody = SfExpressUtil.post(SfExpressUtil.EXP_RECE_SEARCH_ORDER_RESP, JSON.toJSONString(searchOrderParam));
            /**
             * {"apiErrorMsg":"","apiResponseID":"0001980D4990BD3FE464FACE9440A73F","apiResultCode":"A1000","apiResultData":"{\"success\":true,\"errorCode\":\"S0000\",\"errorMsg\":null,\"msgData\":{\"orderId\":\"OBD202506110007\",\"returnExtraInfoList\":null,\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444497657631\"}],\"origincode\":\"710\",\"destcode\":\"719\",\"filterResult\":\"2\",\"remark\":null,\"routeLabelInfo\":[{\"code\":\"1000\",\"routeLabelData\":{\"waybillNo\":\"SF7444497657631\",\"sourceTransferCode\":\"710\",\"sourceCityCode\":\"710\",\"sourceDeptCode\":\"710\",\"sourceTeamCode\":\"\",\"destCityCode\":\"719\",\"destDeptCode\":\"719J\",\"destDeptCodeMapping\":\"\",\"destTeamCode\":\"009\",\"destTeamCodeMapping\":\"\",\"destTransferCode\":\"719\",\"destRouteLabel\":\"719-719J-011\",\"proName\":\"顺丰标快\",\"cargoTypeCode\":\"T6\",\"limitTypeCode\":\"T6\",\"expressTypeCode\":\"B1\",\"codingMapping\":\"K14\",\"codingMappingOut\":\"\",\"xbFlag\":\"0\",\"printFlag\":\"000000000\",\"twoDimensionCode\":\"MMM={'k1':'719','k2':'719J','k3':'009','k4':'T801','k5':'SF7444497657631','k6':'','k7':'de29da16'}\",\"proCode\":\"T801\",\"printIcon\":\"00000000\",\"abFlag\":\"\",\"destPortCode\":\"\",\"destCountry\":\"\",\"destPostCode\":\"\",\"goodsValueTotal\":\"\",\"currencySymbol\":\"\",\"cusBatch\":\"\",\"goodsNumber\":\"\",\"errMsg\":\"\",\"checkCode\":\"de29da16\",\"proIcon\":\"\",\"fileIcon\":\"\",\"fbaIcon\":\"\",\"icsmIcon\":\"\",\"destGisDeptCode\":\"719J\",\"newIcon\":null},\"message\":\"SF7444497657631:\"}],\"contactInfo\":null,\"clientCode\":\"Y2VL6F82\",\"serviceList\":null}}"}
             */
            SfApiResponse sfApiResponse1 = JSON.parseObject(searchOrderResponseBody, SfApiResponse.class);
            String apiResultDataJson2 = sfApiResponse1.getApiResultData();
            SfApiResponse.ApiResultData1 apiResultData12 = JSON.parseObject(apiResultDataJson2, SfApiResponse.ApiResultData1.class);
            String msgData = apiResultData12.getMsgData();
            SfApiResponse.MsgData apiResultData1 = JSON.parseObject(msgData, SfApiResponse.MsgData.class);
            List<SfApiResponse.WaybillNoInfo> waybillNoInfoList = apiResultData1.getWaybillNoInfoList();
            if (waybillNoInfoList != null && waybillNoInfoList.size() == shipmentSize) {
                //取出waybillNoInfoList中的运单号,得到List<String>
                List<String> waybillNos = waybillNoInfoList.stream().map(SfApiResponse.WaybillNoInfo::getWaybillNo).collect(Collectors.toList());
                return waybillNos;
            }

        }
        return null;
    }

    @Override
    public void requestSfGenerateWaybillPdf(WmsOutOrders order, List<WmsShipment> shipments, List<String> waybillNoList) throws Exception {
        //调用运单pdf接口
        /**
         * {
         * 	"templateCode": "fm_150_standard_Y2VL6F82",
         * 	"version":"2.0",
         *     	"fileType":"pdf",
         *         "sync":true,
         *
         *  	"documents": [{
         * 	"masterWaybillNo":"SF7444497619913",
         * 	"seq":"1",
         * 	"sum":"3",
         * 	"remark":"有需要可以传自己需要备注的简短内容，没有可以不传该字段"},
         *        {
         * 	"masterWaybillNo":"SF7444497619913",
         * 	"branchWaybillNo":"SF7444516115096",
         * 	"seq":"2",
         * 	"sum":"3",
         * 	"remark":"有需要可以传自己需要备注的简短内容，没有可以不传该字段"},
         *    {
         * 	"masterWaybillNo":"SF7444497619913",
         * 	"branchWaybillNo":"SF7444516115102",
         * 	"seq":"3",
         * 	"sum":"3",
         * 	"remark":"有需要可以传自己需要备注的简短内容，没有可以不传该字段"}
         *
         * ]
         * }
         */
        //准备请求参数
        SfExpressUtil.SfWayBillPdfRequest sfPrintWaybillRequest = new SfExpressUtil.SfWayBillPdfRequest();
        sfPrintWaybillRequest.setTemplateCode("fm_150_standard_Y2VL6F82");
        sfPrintWaybillRequest.setVersion("2.0");
        sfPrintWaybillRequest.setFileType("pdf");
        sfPrintWaybillRequest.setSync(true);
        List<SfExpressUtil.SfWayBillPdfRequest.Document> documents = new ArrayList<>();
        int i=0;
        for (String waybillNo : waybillNoList) {
            SfExpressUtil.SfWayBillPdfRequest.Document document = new SfExpressUtil.SfWayBillPdfRequest.Document();
            document.setMasterWaybillNo(waybillNo);
            document.setSeq(String.valueOf(++i));
            document.setSum(String.valueOf(waybillNoList.size()));
            document.setRemark("");
            documents.add(document);
        }
        sfPrintWaybillRequest.setDocuments(documents);
        String waybillNo = JSON.toJSONString(sfPrintWaybillRequest);
        //调用面单pdf接口
        String waybillResponseBody = SfExpressUtil.post(SfExpressUtil.COM_RECE_CLOUD_PRINT_WAYBILLS, waybillNo);
        SfApiResponse sfApiResponse2 = JSON.parseObject(waybillResponseBody, SfApiResponse.class);
        String apiResultDataJson3 = sfApiResponse2.getApiResultData();
        SfApiResponse.ApiResultData2 apiResultDataTemp = JSON.parseObject(apiResultDataJson3, SfApiResponse.ApiResultData2.class);
        List<SfApiResponse.FileInfo> files = apiResultDataTemp.getObj().getFiles();
        for (SfApiResponse.FileInfo file : files) {
            String token = file.getToken();
            String fileUrl = file.getUrl();
            System.out.println(token);
            System.out.println(fileUrl);
            downloadFile(fileUrl, token, "D:\\develop\\upload\\1.pdf");


        }
    }
    public  void downloadFile(String fileUrl, String token, String savePath) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            // 创建 HTTP GET 请求
            HttpGet httpGet = new HttpGet(fileUrl);

            // 设置请求头 X-Auth-Token
            httpGet.setHeader("X-Auth-Token", token);

            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpGet);

            try {
                // 检查 HTTP 状态码
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("Failed to download file. HTTP status code: " + statusCode);
                }

                // 获取响应内容
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 获取输入流
                    try (InputStream inputStream = entity.getContent()) {
                        // 创建输出文件
                        File outputFile = new File(savePath);
                        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            IOUtils.copy(inputStream, outputStream);
//							// 缓冲区
//							byte[] buffer = new byte[4096];
//							int bytesRead;
//
//							// 读取并写入文件
//							while ((bytesRead = inputStream.read(buffer)) != -1) {
//								outputStream.write(buffer, 0, bytesRead);
//							}
                        }
                    }
                }

                // 确保资源释放
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }
}
