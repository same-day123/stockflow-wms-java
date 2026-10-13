package org.jeecg.modules.wms.shunfeng.other;


import java.util.List;

/**
 * 顺丰响应实体
 * @author lx
 * @date
 */
public class ShunFengResponse {

    /** 响应成功标志 */
    private boolean resultFlag;
    /** 错误信息 */
    private String errorMsg;
    /** 顺丰运单号 */
    private String mailNo;
    /** 快递公司拼音 */
    private ExpressCompanyType com;
    /** 快递公司中文 */
    private String comCN;
    /** 下单返回的信息封装的实体*/
    private CreateExpressOrderDTO orderResponse;
    /** 顺丰路由节点操作码 */
    private String opCode;
    /** 路由查询返回实体节点 */
    private List<DataBean> data;
    /** 路由推送返回的节点实体 */
    private List<WaybillRoute> waybillRouteList;

    public boolean isResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(boolean resultFlag) {
        this.resultFlag = resultFlag;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public ExpressCompanyType getCom() {
        return com;
    }

    public void setCom(ExpressCompanyType com) {
        this.com = com;
    }

    public String getComCN() {
        return com == null ? "" : com.getChName();
    }

    public CreateExpressOrderDTO getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(CreateExpressOrderDTO orderResponse) {
        this.orderResponse = orderResponse;
    }

    public static class DataBean {
        /**
         * time : 2018-05-21 17:48:44
         * ftime : 2018-05-21 17:48:44
         * context : [深圳市]已签收,感谢使用顺丰,期待再次为您服务
         */

        private String time;
        private String ftime;
        private String context;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFtime() {
            return ftime;
        }

        public void setFtime(String ftime) {
            this.ftime = ftime;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        @Override
        public String toString() {
            return "{" +
                    "time='" + time + '\'' +
                    ", ftime='" + ftime + '\'' +
                    ", context='" + context + '\'' +
                    '}';
        }
    }

    public static class WaybillRoute {
        /** 顺丰运单号 */
        private String mailNo;
        /** 顺丰路由节点操作码 */
        private String opCode;

        public String getMailNo() {
            return mailNo;
        }

        public void setMailNo(String mailNo) {
            this.mailNo = mailNo;
        }

        public String getOpCode() {
            return opCode;
        }

        public void setOpCode(String opCode) {
            this.opCode = opCode;
        }

        @Override
        public String toString() {
            return "WaybillRoute{" +
                    "mailNo='" + mailNo + '\'' +
                    ", opCode='" + opCode + '\'' +
                    '}';
        }
    }

    public void setComCN(String comCN) {
        this.comCN = comCN;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public List<WaybillRoute> getWaybillRouteList() {
        return waybillRouteList;
    }

    public void setWaybillRouteList(List<WaybillRoute> waybillRouteList) {
        this.waybillRouteList = waybillRouteList;
    }

    @Override
    public String toString() {
        return "ShunFengResponse{" +
                "resultFlag=" + resultFlag +
                ", errorMsg='" + errorMsg + '\'' +
                ", mailNo='" + mailNo + '\'' +
                ", com=" + com +
                ", orderResponse=" + orderResponse.toString() +
                ", comCN='" + comCN + '\'' +
                ", opCode='" + opCode + '\'' +
                ", data=" + data +
                '}';
    }
}
