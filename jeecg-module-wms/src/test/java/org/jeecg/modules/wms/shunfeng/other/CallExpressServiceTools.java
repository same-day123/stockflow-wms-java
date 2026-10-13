package org.jeecg.modules.wms.shunfeng.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallExpressServiceTools {
    private static Logger logger = LoggerFactory.getLogger(CallExpressServiceTools.class);
    /**
     * 顺丰快递请求url
     */
    //沙箱https://sfapi-sbox.sf-express.com/std/service
    public static final String REQUEST_URL = "https://sfapi-sbox.sf-express.com/std/service";
//    public static final String REQUEST_URL = "http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService";
    /**
     * 丰桥平台获取的校验码
     */
    public static final String CHECKWORD = "jy1HZyiDinIuipRALDjdKksUtstLveBy";
    /**
     * 顺丰月结卡号
     */
    public static final String CUST_ID = "7551234567";
    /**
     * 快递公司名称
     */
    public static final String COMPANY = "shunfeng";

    private CallExpressServiceTools() {
    }

    public static String callSfExpressServiceByCSIM(String reqXML) {
        String verifyCode = VerifyCodeUtil.md5EncryptAndBase64(reqXML + CHECKWORD);
        String result = querySFAPIservice(reqXML, verifyCode);
        return result;
    }

    public static String querySFAPIservice(String xml, String verifyCode) {
        HttpClientUtil httpclient = new HttpClientUtil();
        try {
            String result = httpclient.postSFAPI(REQUEST_URL, xml, verifyCode);
            return result;
        } catch (Exception var6) {
            logger.warn(" " + var6);
            return null;
        }
    }
}
