package org.jeecg.modules.wms.waybill.sf;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 顺丰接口返回结果
 */
@Data
public class SfApiResponse {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;

    // Getters and Setters
    public String getApiErrorMsg() {
        return apiErrorMsg;
    }

    public void setApiErrorMsg(String apiErrorMsg) {
        this.apiErrorMsg = apiErrorMsg;
    }

    public String getApiResponseID() {
        return apiResponseID;
    }

    public void setApiResponseID(String apiResponseID) {
        this.apiResponseID = apiResponseID;
    }

    public String getApiResultCode() {
        return apiResultCode;
    }

    public void setApiResultCode(String apiResultCode) {
        this.apiResultCode = apiResultCode;
    }

    public String getApiResultData() {
        return apiResultData;
    }

    public void setApiResultData(String apiResultData) {
        this.apiResultData = apiResultData;
    }

    @Data
    public static class ApiResultData1 {
        private String success;
        private String errorCode;
        private String errorMsg;
        private String msgData;
    }
    @Data
    public static class ApiResultData2 {
        private Obj obj;
        private String requestId;
        private boolean success;

    }

    // 对象数据类
    @Data
    public class Obj {
        private String clientCode;
        private String fileType;
        private List<FileInfo> files;
        private String templateCode;

    }

    // 文件信息类
    @Data
    public class FileInfo {
        private int areaNo;
        private int documentSize;
        private int pageCount;
        private int pageNo;
        private int seqNo;
        private String token;
        private String url;
        private String waybillNo;

    }
    @Data
    public static class MsgData {
        private String orderId;
        private String origincode;
        private String destcode;
        private String filterResult;
        private String remark;
        private List<WaybillNoInfo> waybillNoInfoList;
        private List<RouteLabelInfo> routeLabelInfo;
        private Object contactInfo;
        private String clientCode;
        private Object serviceList;
        private Object returnExtraInfoList;
    }

    public static class WaybillNoInfo {
        private int waybillType;
        private String waybillNo;

        // Getters and Setters
        public int getWaybillType() {
            return waybillType;
        }

        public void setWaybillType(int waybillType) {
            this.waybillType = waybillType;
        }

        public String getWaybillNo() {
            return waybillNo;
        }

        public void setWaybillNo(String waybillNo) {
            this.waybillNo = waybillNo;
        }
    }

    public static class RouteLabelInfo {
        private String code;
        private RouteLabelData routeLabelData;
        private String message;

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public RouteLabelData getRouteLabelData() {
            return routeLabelData;
        }

        public void setRouteLabelData(RouteLabelData routeLabelData) {
            this.routeLabelData = routeLabelData;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Data
    public static class RouteLabelData {
        private String waybillNo;
        private String sourceTransferCode;
        private String sourceCityCode;
        private String sourceDeptCode;
        private String sourceTeamCode;
        private String destCityCode;
        private String destDeptCode;
        private String destDeptCodeMapping;
        private String destTeamCode;
        private String destTeamCodeMapping;
        private String destTransferCode;
        private String destRouteLabel;
        private String proName;
        private String cargoTypeCode;
        private String limitTypeCode;
        private String expressTypeCode;
        private String codingMapping;
        private String codingMappingOut;
        private String xbFlag;
        private String printFlag;
        private String twoDimensionCode;
        private String proCode;
        private String printIcon;
        private String abFlag;
        private String destPortCode;
        private String destCountry;
        private String destPostCode;
        private String goodsValueTotal;
        private String currencySymbol;
        private String cusBatch;
        private String goodsNumber;
        private String errMsg;
        private String checkCode;
        private String proIcon;
        private String fileIcon;
        private String fbaIcon;
        private String icsmIcon;
        private String destGisDeptCode;
        private Object newIcon;
    }
}
