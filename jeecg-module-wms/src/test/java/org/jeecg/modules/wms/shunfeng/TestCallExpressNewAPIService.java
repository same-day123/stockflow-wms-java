package org.jeecg.modules.wms.shunfeng;


import cn.hutool.core.io.IoUtil;
import com.sf.csim.express.service.CallExpressServiceTools;
import org.jeecg.modules.wms.shunfeng.other.HttpClientUtil;
import com.sf.csim.express.service.IServiceCodeStandard;
import com.sf.csim.express.service.code.ExpressServiceCodeEnum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestCallExpressNewAPIService {

	/**丰桥新沙箱测试顾客编码  Yg4Zf06w_sxZs3A5D
                      校验码  3Xdk1jqeG1Xod9nUXus8Op7DNOkchTnw
    **/
	private static final String CLIENT_CODE = "Y0KOQXFQ";  //此处替换为您在丰桥平台获取的顾客编码
	private static final String CHECK_WORD = "cpBFY0d9VBoo5SYFh59D6xLHvJURO8Mw";//此处替换为您在丰桥平台获取的校验码

	//沙箱环境的地址 -PRO
	 	private static final String CALL_URL_BOX = "https://sfapi-sbox.sf-express.com/std/service";
 	//生产环境的地址 -PRO
//	 	private static final String CALL_URL_PROD = "https://sfapi.sf-express.com/std/service";

	public static void main(String[] args) throws Exception {
		/**ExpressServiceCodeEnum     对应速运类-快递APIs
		   POSTServiceCodeEnum        对应速运类-驿站APIs
		   YJTServiceCodeEnum         对应解决方案-医寄通APIs
		   EPSServiceCodeEnum         对应解决方案-快递管家APIs
		   HZTServiceCodeEnum         对应解决方案-函证通APIs    2022-2-24 新增
		   详情见code目录下枚举类，客户可自行修改引用的该类
		**/

		    IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_CREATE_ORDER; //下订单
//			IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_SEARCH_ORDER_RESP; //查订单
		//  IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_UPDATE_ORDER;//订单取消
		// 	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_FILTER_ORDER_BSP;//订单筛选
//		  IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_SEARCH_ROUTES;//查路由
		//	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_GET_SUB_MAILNO;//子单号
		//	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_QUERY_SFWAYBILL;//查运费
		//	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_REGISTER_ROUTE;//注册路由
		//	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_CREATE_REVERSE_ORDER;//退货下单
		//	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_CANCEL_REVERSE_ORDER;//退货消单
        //	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_WANTED_INTERCEPT;//截单转寄
		// 	IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_QUERY_DELIVERTM;//时效标准及价格查询
//		  IServiceCodeStandard standardService = ExpressServiceCodeEnum.COM_RECE_CLOUD_PRINT_WAYBILLS;//面单打印
		//  IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_UPLOAD_ROUTE;//路由上传
       //   IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_SEARCH_PROMITM;//预计派送时间查询
        //  IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_EXCE_CHECK_PICKUP_TIME;//揽件服务时间查询
        //  IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_VALIDATE_WAYBILLNO;//运单号合法性校验

        // 1. 初始化SDK工具
        CallExpressServiceTools tools=CallExpressServiceTools.getInstance();

        // set common header
        Map<String, String> params = new HashMap<String, String>();


        // 2. 读取订单JSON文件，强制UTF-8解析

        // 3. 生成接口公共参数
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String requestID = UUID.randomUUID().toString().replace("-", "");

//        String msgData =tools.packageMsgData(standardService);
        //找到下载sdk下json文件的路径
        byte[] bytes = IoUtil.readBytes(
                TestCallExpressNewAPIService.class.getClassLoader().getResourceAsStream("01.order.json")
        );
// 临时替换掉文件读取，用一行极简JSON测试
        String msgData = "{\"language\":\"zh-CN\",\"orderId\":\"TEST-20260815-002\",\"monthlyCard\":\"7551234567\"}";
//        String msgData = new String(bytes, StandardCharsets.UTF_8);
        // 临时替换掉文件读取逻辑，用固定字符串测试
//        String msgData = "{\"language\":\"zh-CN\",\"orderId\":\"TEST-20260815-001\",\"monthlyCard\":\"7551234567\"}";

        // 4. 核心校验：双渠道计算签名，对比是否一致
        String sdkSign = tools.getMsgDigest(msgData, timeStamp, CHECK_WORD);
        String selfSign = selfGetMsgDigest(msgData, timeStamp, CHECK_WORD);


        params.put("partnerID", CLIENT_CODE);  // 顾客编码 ，对应丰桥上获取的clientCode
        params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
        params.put("serviceCode","EXP_RECE_CREATE_ORDER");// 接口服务码
        params.put("timestamp", timeStamp);
        params.put("msgData", msgData);
        params.put("msgDigest", tools.getMsgDigest(msgData,timeStamp,CHECK_WORD));

       // System.out.println(params.get("requestID"));
        long startTime = System.currentTimeMillis();

      //  System.out.println("====调用请求：" + params.get("msgData"));
        System.out.println("====调用实际请求：" + params);
        String result = HttpClientUtil.post(CALL_URL_BOX, params);

        System.out.println("====调用丰桥的接口服务代码：" + String.valueOf("EXP_RECE_SEARCH_ROUTES") + " 接口耗时："+ String.valueOf(System.currentTimeMillis()-startTime)+"====");
        System.out.println("===调用地址 ==="+CALL_URL_BOX);
        System.out.println("===顾客编码 ==="+CLIENT_CODE);
        System.out.println("===返回结果：" +result);

        System.out.println("SDK计算签名：" + sdkSign);
        System.out.println("手写计算签名：" + selfSign);
        System.out.println("两者是否一致：" + sdkSign.equals(selfSign));

        String testSign = tools.getMsgDigest(
                "{\"language\":\"zh-CN\",\"orderId\":\"QIAO-20200618-004\"}",
                "12312334453453",
                "fjcg5PGKaNpPSHFAZ4QsCOkV71R3zVci");
        System.out.println("官方示例签名：" + testSign);
	}
    /**
     * 丰桥简易MD5签名（自己实现，用来和SDK对比）
     */
    public static String selfGetMsgDigest(String msgData, String timestamp, String checkWord) throws Exception {
        // 1. 严格按 msgData + timestamp + checkWord 顺序拼接
        String raw = msgData + timestamp + checkWord;
        // 2. 核心步骤：URL编码（UTF-8）
        String encoded = URLEncoder.encode(raw, StandardCharsets.UTF_8.name());
        // 若空格编码为+导致签名失败，打开此行替换为%20
        // encoded = encoded.replace("+", "%20");
        // 3. MD5二进制摘要 + Base64编码
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md5.digest(encoded.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(md5Bytes);
    }

}
