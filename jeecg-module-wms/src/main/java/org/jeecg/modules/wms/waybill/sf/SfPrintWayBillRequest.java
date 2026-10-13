package org.jeecg.modules.wms.waybill.sf;

import lombok.Data;

import java.util.List;

/**
 * 顺丰打印电子面单请求参数
 */
@Data
public class SfPrintWayBillRequest {
    private String templateCode;
    private String version;
    private String fileType;
    private boolean sync;
    private List<Document> documents;


    @Data
    public static class Document {
        private String masterWaybillNo;
        private String branchWaybillNo; // 可选字段
        private String seq;
        private String sum;
        private String remark; // 可选字段


    }
}
