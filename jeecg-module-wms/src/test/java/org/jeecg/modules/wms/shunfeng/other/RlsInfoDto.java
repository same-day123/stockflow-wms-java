package org.jeecg.modules.wms.shunfeng.other;

public class RlsInfoDto implements Cloneable {
    private String waybillNo;
    private String abFlag;
    private String xbFlag;
    private String proCode;
    private String destRouteLabel;
    private String destTeamCode;
    private String codingMapping;
    private String codingMappingOut;
    private String sourceTransferCode;
    private String printIcon;
    private String qrcode;


    public String getWaybillNo() {
        return this.waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getAbFlag() {
        return this.abFlag;
    }

    public void setAbFlag(String abFlag) {
        this.abFlag = abFlag;
    }

    public String getXbFlag() {
        return this.xbFlag;
    }

    public void setXbFlag(String xbFlag) {
        this.xbFlag = xbFlag;
    }

    public String getProCode() {
        return this.proCode;
    }

    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    public String getDestRouteLabel() {
        return this.destRouteLabel;
    }

    public void setDestRouteLabel(String destRouteLabel) {
        this.destRouteLabel = destRouteLabel;
    }

    public String getDestTeamCode() {
        return this.destTeamCode;
    }

    public void setDestTeamCode(String destTeamCode) {
        this.destTeamCode = destTeamCode;
    }

    public String getCodingMapping() {
        return this.codingMapping;
    }

    public void setCodingMapping(String codingMapping) {
        this.codingMapping = codingMapping;
    }

    public String getCodingMappingOut() {
        return this.codingMappingOut;
    }

    public void setCodingMappingOut(String codingMappingOut) {
        this.codingMappingOut = codingMappingOut;
    }

    public String getSourceTransferCode() {
        return this.sourceTransferCode;
    }

    public void setSourceTransferCode(String sourceTransferCode) {
        this.sourceTransferCode = sourceTransferCode;
    }

    public String getPrintIcon() {
        return this.printIcon;
    }

    public void setPrintIcon(String printIcon) {
        this.printIcon = printIcon;
    }

    public String getQrcode() {
        return this.qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Override
    public Object clone() {
        RlsInfoDto obj = null;
        try {
            obj = (RlsInfoDto) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString() {
        return "{" +
                "waybillNo='" + waybillNo + '\'' +
                ", abFlag='" + abFlag + '\'' +
                ", xbFlag='" + xbFlag + '\'' +
                ", proCode='" + proCode + '\'' +
                ", destRouteLabel='" + destRouteLabel + '\'' +
                ", destTeamCode='" + destTeamCode + '\'' +
                ", codingMapping='" + codingMapping + '\'' +
                ", codingMappingOut='" + codingMappingOut + '\'' +
                ", sourceTransferCode='" + sourceTransferCode + '\'' +
                ", printIcon='" + printIcon + '\'' +
                ", qrcode='" + qrcode + '\'' +
                '}';
    }
}
