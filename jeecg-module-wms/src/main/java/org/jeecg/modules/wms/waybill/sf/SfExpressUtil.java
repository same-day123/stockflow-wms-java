package org.jeecg.modules.wms.waybill.sf;

import com.alibaba.fastjson.JSON;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mr.M
 * @version 1.0
 * @description 顺丰接口工具类
 * @date 2025/7/14 20:32
 */
public class SfExpressUtil {
    private static String client_code;// 客户编码
    private static String check_word;// 校验密码
    private static String call_url;// 丰桥地址
    private static String monthly_card;// 月结卡号

    //请求token接口地址
    private static String token_url="https://sfapi-sbox.sf-express.com/oauth2/accessToken";

    /**
     * service code
     */
    public static final String EXP_RECE_CREATE_ORDER = "EXP_RECE_CREATE_ORDER";//下单
    public static final String EXP_RECE_SEARCH_ORDER_RESP = "EXP_RECE_SEARCH_ORDER_RESP";//查询订单
    public static final String EXP_RECE_SEARCH_ROUTES = "EXP_RECE_SEARCH_ROUTES";//查询路由
    public static final String COM_RECE_CLOUD_PRINT_WAYBILLS = "COM_RECE_CLOUD_PRINT_WAYBILLS";//面单转pdf

    /**
     * 请求获取token
     *
     */
    public static String getToken() throws UnsupportedEncodingException {
        requireCommonConfig();
        CallExpressServiceTools tools=CallExpressServiceTools.getInstance();
        // set common header
        Map<String, String> params = new HashMap<String, String>();
        params.put("partnerID", client_code);  // 顾客编码 ，对应丰桥上获取的clientCode
        params.put("secret", check_word);
        params.put("grantType", "password");

        // System.out.println(params.get("requestID"));
        long startTime = System.currentTimeMillis();

        //  System.out.println("====调用请求：" + params.get("msgData"));
        printTokenRequestDiagnostics();
        String result = HttpClientUtil.post(token_url, params);

        System.out.println("===调用地址 ===" + token_url);
        System.out.println("===顾客编码 ===" + client_code);
        System.out.println("===返回结果：" + result);
        //将result转为map
        Map<String, String> resultMap = JSON.parseObject(result, Map.class);
        //从map中获取key为”accessToken“的值
        String accessToken = resultMap.get("accessToken");
        return accessToken;
    }

    /**
     * post请求丰桥的接口
     *
     * @return
     */
    public static String post(String serviceCode, String requestBody) throws UnsupportedEncodingException {
        requirePostConfig(serviceCode, requestBody);
        CallExpressServiceTools tools=CallExpressServiceTools.getInstance();
        // set common header
        Map<String, String> params = new HashMap<String, String>();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        params.put("partnerID", client_code);  // 顾客编码 ，对应丰桥上获取的clientCode
        params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
        params.put("serviceCode", serviceCode);// 接口服务码
        params.put("timestamp", timeStamp);
        params.put("msgData", requestBody);
        params.put("msgDigest", tools.getMsgDigest(requestBody, timeStamp, check_word));

        // System.out.println(params.get("requestID"));
        long startTime = System.currentTimeMillis();

        //  System.out.println("====调用请求：" + params.get("msgData"));
        printRequestDiagnostics(serviceCode, requestBody, params.get("msgDigest"));
        String result = HttpClientUtil.post(call_url, params);

        System.out.println("====调用丰桥的接口服务代码：" + serviceCode + " 接口耗时：" + String.valueOf(System.currentTimeMillis() - startTime) + "====");
        System.out.println("===调用地址 ===" + call_url);
        System.out.println("===顾客编码 ===" + client_code);
        System.out.println("===返回结果：" + result);
        return result;
    }


    private static void requirePostConfig(String serviceCode, String requestBody) {
        requireCommonConfig();
        if (StringUtils.isBlank(call_url)) {
            throw new IllegalStateException("SF call url is blank. Set SfExpressUtil.call_url before calling SF API.");
        }
        if (StringUtils.isBlank(monthly_card)) {
            throw new IllegalStateException("SF monthly card is blank. Set SfExpressUtil.monthly_card before creating SF order.");
        }
        if (StringUtils.isBlank(serviceCode)) {
            throw new IllegalArgumentException("SF serviceCode must not be blank.");
        }
        if (StringUtils.isBlank(requestBody)) {
            throw new IllegalArgumentException("SF requestBody must not be blank.");
        }
    }

    private static void requireCommonConfig() {
        if (StringUtils.isBlank(client_code)) {
            throw new IllegalStateException("SF client code is blank. Set SfExpressUtil.client_code before calling SF API.");
        }
        if (StringUtils.isBlank(check_word)) {
            throw new IllegalStateException("SF check word is blank. Set SfExpressUtil.check_word before calling SF API.");
        }
    }


    private static void printTokenRequestDiagnostics() {
        System.out.println("==== SF token request diagnostics ====");
        System.out.println("partnerID    = " + client_code);
        System.out.println("partnerIDChars = " + charCodes(client_code));
        System.out.println("checkWordLen = " + check_word.length());
    }
    private static void printRequestDiagnostics(String serviceCode, String requestBody, String msgDigest) {
        System.out.println("==== SF request diagnostics ====");
        System.out.println("serviceCode    = " + serviceCode);
        System.out.println("partnerID      = " + client_code);
        System.out.println("partnerIDChars = " + charCodes(client_code));
        System.out.println("checkWordLen   = " + check_word.length());
        System.out.println("monthlyCard    = " + monthly_card);
        System.out.println("msgDataChars   = " + requestBody.length());
        System.out.println("msgDigest      = " + msgDigest);
    }

    private static String charCodes(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            char ch = value.charAt(i);
            builder.append(ch).append("=").append((int) ch);
        }
        return builder.toString();
    }

    private static void printSfResultHint(String result) {
        if (result == null) {
            return;
        }
        if (result.contains("\"apiResultCode\":\"A1006\"")) {
            System.out.println("SF hint = A1006 invalid signature. Check that partnerID, checkWord, timestamp, and msgData are exactly the same inputs used to generate msgDigest.");
        } else if (result.contains("\"apiResultCode\":\"A1004\"")) {
            System.out.println("SF hint = A1004 no permission for this serviceCode. Check that the API association belongs to the same SF app credentials used by this backend request.");
        }
    }
    public static String getClient_code() {
        return client_code;
    }

    public static void setClient_code(String client_code) {
        SfExpressUtil.client_code = StringUtils.trim(client_code);
    }

    public static String getCheck_word() {
        return check_word;
    }

    public static void setCheck_word(String check_word) {
        SfExpressUtil.check_word = StringUtils.trim(check_word);
    }

    public static String getCall_url() {
        return call_url;
    }

    public static void setCall_url(String call_url) {
        SfExpressUtil.call_url = StringUtils.trim(call_url);
    }

    public static String getMonthly_card() {
        return monthly_card;
    }

    public static void setMonthly_card(String monthly_card) {
        SfExpressUtil.monthly_card = StringUtils.trim(monthly_card);
    }

    /**
     * {
     * 	"searchType": "1",
     * 	"orderId": "OrderNummmrt20200612223",
     * 	"language": "zh-cn",
     * }
     * 查询订单参数内部类
     */
    @Data
    public static class SearchOrderParam {
        private String searchType;
        private String orderId;
        private String language;
    }

    /**
     * 面单 pdf 接口参数内部类
     */
    @Data
    public static class SfWayBillPdfRequest {
        /**
         * 模板代码
         */
        private String templateCode;

        /**
         * 版本号
         */
        private String version;

        /**
         * 文件类型
         */
        private String fileType;

        /**
         * 是否同步请求
         */
        private boolean sync;

        /**
         * 面单文档列表
         */
        private List<Document> documents;

        // 静态内部类，表示单个面单文档
        @Data
        public static class Document {
            /**
             * 主运单号
             */
            private String masterWaybillNo;

            /**
             * 子运单号（可选）
             */
            private String branchWaybillNo;

            /**
             * 序号
             */
            private String seq;

            /**
             * 总件数
             */
            private String sum;

            /**
             * 备注（可选）
             */
            private String remark;


        }

    }
}
