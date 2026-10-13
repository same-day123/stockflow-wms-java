package org.jeecg.modules.wms.shunfeng.other;

import java.util.ArrayList;
import java.util.List;

/**
 * 顺丰下单成功返回的信息封装的实体
 * @author lx
 * @date
 */
public class CreateExpressOrderDTO implements Cloneable{

    /** 运单号 */
    private String mailNo;
    /** 原寄递地代码 */
    private String zipCode;
    /** 目的地的代码 */
    private String destCode;
    /** 备注 */
    private String mainRemark;
    /** 寄件人姓名 */
    private String deliverName;
    /** 寄件人电话*/
    private String deliverMobile;
    /** 寄件人省份 */
    private String deliverProvince;
    /** 寄件人城市 */
    private String deliverCity;
    /** 寄件人区县 */
    private String deliverCounty;
    /** 寄件人地址 */
    private String deliverAddress;
    /** 寄件人公司 */
    private String deliverCompany;
    /** 收件人姓名 */
    private String consignerName;
    /** 收件人电话 */
    private String consignerMobile;
    /** 收件人省份 */
    private String consignerProvince;
    /** 收件人城市 */
    private String consignerCity;
    /** 收件人区县 */
    private String consignerCounty;
    /** 收件人地址 */
    private String consignerAddress;
    /** 月结卡号 */
    private String custId;
    /** 快递类别 1：顺丰标快 */
    private String expressType;
    /** 包裹数量 大于1则会生成子单号*/
    private Integer parcelQuantity;
    /** 丰密运单相关设置 */
    private List<RlsInfoDto> rlsInfoDtoList;

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDestCode() {
        return destCode;
    }

    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }

    public String getMainRemark() {
        return mainRemark;
    }

    public void setMainRemark(String mainRemark) {
        this.mainRemark = mainRemark;
    }

    public String getDeliverName() {
        return deliverName;
    }

    public void setDeliverName(String deliverName) {
        this.deliverName = deliverName;
    }

    public String getDeliverMobile() {
        return deliverMobile;
    }

    public void setDeliverMobile(String deliverMobile) {
        this.deliverMobile = deliverMobile;
    }

    public String getDeliverProvince() {
        return deliverProvince;
    }

    public void setDeliverProvince(String deliverProvince) {
        this.deliverProvince = deliverProvince;
    }

    public String getDeliverCity() {
        return deliverCity;
    }

    public void setDeliverCity(String deliverCity) {
        this.deliverCity = deliverCity;
    }

    public String getDeliverCounty() {
        return deliverCounty;
    }

    public void setDeliverCounty(String deliverCounty) {
        this.deliverCounty = deliverCounty;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public String getDeliverCompany() {
        return deliverCompany;
    }

    public void setDeliverCompany(String deliverCompany) {
        this.deliverCompany = deliverCompany;
    }

    public String getConsignerName() {
        return consignerName;
    }

    public void setConsignerName(String consignerName) {
        this.consignerName = consignerName;
    }

    public String getConsignerMobile() {
        return consignerMobile;
    }

    public void setConsignerMobile(String consignerMobile) {
        this.consignerMobile = consignerMobile;
    }

    public String getConsignerProvince() {
        return consignerProvince;
    }

    public void setConsignerProvince(String consignerProvince) {
        this.consignerProvince = consignerProvince;
    }

    public String getConsignerCity() {
        return consignerCity;
    }

    public void setConsignerCity(String consignerCity) {
        this.consignerCity = consignerCity;
    }

    public String getConsignerCounty() {
        return consignerCounty;
    }

    public void setConsignerCounty(String consignerCounty) {
        this.consignerCounty = consignerCounty;
    }

    public String getConsignerAddress() {
        return consignerAddress;
    }

    public void setConsignerAddress(String consignerAddress) {
        this.consignerAddress = consignerAddress;
    }

    public List<RlsInfoDto> getRlsInfoDtoList() {
        return rlsInfoDtoList;
    }

    public void setRlsInfoDtoList(List<RlsInfoDto> rlsInfoDtoList) {
        this.rlsInfoDtoList = rlsInfoDtoList;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getExpressType() {
        return expressType;
    }

    public void setExpressType(String expressType) {
        this.expressType = expressType;
    }

    public Integer getParcelQuantity() {
        return parcelQuantity;
    }

    public void setParcelQuantity(Integer parcelQuantity) {
        this.parcelQuantity = parcelQuantity;
    }

    @Override
    public Object clone() {
        CreateExpressOrderDTO obj = null;
        try {
            obj = (CreateExpressOrderDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        obj.rlsInfoDtoList = new ArrayList<>(1);
        obj.rlsInfoDtoList.add((RlsInfoDto) rlsInfoDtoList.get(0).clone());
        return obj;
    }

    @Override
    public String toString() {
        return "CreateExpressOrderDTO{" +
                "mailNo='" + mailNo + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", destCode='" + destCode + '\'' +
                ", mainRemark='" + mainRemark + '\'' +
                ", deliverName='" + deliverName + '\'' +
                ", deliverMobile='" + deliverMobile + '\'' +
                ", deliverProvince='" + deliverProvince + '\'' +
                ", deliverCity='" + deliverCity + '\'' +
                ", deliverCounty='" + deliverCounty + '\'' +
                ", deliverAddress='" + deliverAddress + '\'' +
                ", deliverCompany='" + deliverCompany + '\'' +
                ", consignerName='" + consignerName + '\'' +
                ", consignerMobile='" + consignerMobile + '\'' +
                ", consignerProvince='" + consignerProvince + '\'' +
                ", consignerCity='" + consignerCity + '\'' +
                ", consignerCounty='" + consignerCounty + '\'' +
                ", consignerAddress='" + consignerAddress + '\'' +
                ", custId='" + custId + '\'' +
                ", expressType='" + expressType + '\'' +
                ", parcelQuantity='" + parcelQuantity + '\'' +
                ", rlsInfoDtoList=" + rlsInfoDtoList +
                '}';
    }
}
