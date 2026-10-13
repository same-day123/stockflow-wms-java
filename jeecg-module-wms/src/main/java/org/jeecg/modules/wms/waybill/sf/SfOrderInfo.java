package org.jeecg.modules.wms.waybill.sf;

import lombok.Data;

import java.util.List;

/**
 * 顺丰下单参数
 */
@Data
public class SfOrderInfo {
    private List<CargoDetail> cargoDetails;
    private List<ContactInfo> contactInfoList;
    private Object customsInfo; // 由于JSON中为空对象，使用Object或创建具体类
    private int expressTypeId;
    private List<?> extraInfoList; // JSON中为空数组，不确定类型
    private int isOneselfPickup;
    private String language;
    private String orderId;
    private int parcelQty;
    private int payMethod;
    private double totalWeight;
    private String monthlyCard;


    @Data
    public static class CargoDetail {
        private double amount;
        private double count;
        private String name;
        private String unit;
        private double volume;
        private double weight;

    }

    @Data
    public static class ContactInfo {
        private String address;
        private String city;
        private String company; // 可能为空
        private String contact;
        private int contactType;
        private String county;
        private String mobile;
        private String province;


    }
}
