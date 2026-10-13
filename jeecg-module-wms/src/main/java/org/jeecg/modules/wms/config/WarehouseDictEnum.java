package org.jeecg.modules.wms.config;

/**
 * @author Mr.M
 * @version 1.0
 * @description 仓储管理系统数据字典枚举
 * @date 2025/5/20 18:01
 */
public enum WarehouseDictEnum {

    /* 通用状态  */
    STATUS_CREATED("CREATED", "创建"),
    STATUS_ACTIVE("ACTIVE", "启用"),
    STATUS_INACTIVE("INACTIVE", "禁用"),

    /* 出库单状态 */
    OUTBOUND_CREATED("CREATED", "已创建"),
    OUTBOUND_SUBMIT_AUDIT("SUBMITTED", "提交审核"),
    OUTBOUND_APPROVED("APPROVED", "审核通过"),
    OUTBOUND_REJECTED("REJECTED", "审核失败"),
    OUTBOUND_ALLOCATED("ALLOCATED", "已分配"),
    OUTBOUND_FAILED("ALLOCATED_FAILED", "分配失败"),
    OUTBOUND_PICKING("PICKING", "拣货中"),
    OUTBOUND_PICKED("PICKED", "已拣货"),

    //打包中
//    OUTBOUND_PACKING("PACKING", "打包中"),
    OUTBOUND_PACKED("PACKED", "已打包"),
    OUTBOUND_SHIPPED("SHIPPED", "已发货"),
    OUTBOUND_CANCELED("CANCELED", "已取消"),

    /*出库单明细状态*/
    OUTBOUND_DETAIL_CREATED("CREATED", "已创建"),
    OUTBOUND_DETAIL_ALLOCATED("ALLOCATED", "已分配"),
    //分配失败
    OUTBOUND_DETAIL_FAILED("ALLOCATED_FAILED", "分配失败"),
    OUTBOUND_DETAIL_PICKING("PICKING", "拣货中"),
    OUTBOUND_DETAIL_PICKED("PICKED", "已拣货"),
    //打包中
//    OUTBOUND_DETAIL_PACKING("PACKING", "打包中"),
    OUTBOUND_DETAIL_PACKED("PACKED", "已打包"),
    OUTBOUND_DETAIL_SHIPPED("SHIPPED", "已发货"),
    OUTBOUND_DETAIL_CANCELED("CANCELED", "已取消"),

    /* 入库单状态 */
    INBOUND_INITIAL("INITIAL", "初始"),
    INBOUND_SUBMIT_AUDIT("SUBMITTED", "提交审核"),
    INBOUND_APPROVED("APPROVED", "审核通过"),
    INBOUND_REJECTED("REJECTED", "审核失败"),
    INBOUND_RECEIVING("RECEIVING", "收货中"),
    INBOUND_RECEIVED("RECEIVED", "收货完成"),
    INBOUND_PUTAWAYING("PUTAWAYING", "上架中"),
    INBOUND_PUTAWAYED("PUTAWAYED", "上架完成"),
    INBOUND_CLOSED("CLOSED", "已关闭"),
    INBOUND_CANCELED("CANCELED", "已作废"),

    /* 入库单明细状态 */
    INBOUND_DETAIL_INITIAL("INITIAL", "初始"),
    INBOUND_DETAIL_RECEIVING("RECEIVING", "收货中"),
    INBOUND_DETAIL_RECEIVED("RECEIVED", "收货完成"),
    //上架完成
    INBOUND_DETAIL_PUTAWAYED("PUTAWAYED", "上架完成"),
    INBOUND_DETAIL_CLOSED("CLOSED", "已关闭"),
    INBOUND_DETAIL_CANCELED("CANCELED", "已作废"),

    /* 储区类型 */
    AREA_TEMPORARY("TEMPORARY_AREA", "暂存区"),
    AREA_STORAGE("STORAGE_AREA", "存储区"),
    AREA_PICKING("PICKING_AREA", "拣选区"),

    /* 仓库属性 */
    WAREHOUSE_FINISHED_GOODS("FINISHED_GOODS", "成品仓"),
    WAREHOUSE_RAW_MATERIAL("RAW_MATERIAL", "原料仓"),
    WAREHOUSE_SEMI_FINISHED("SEMI_FINISHED", "半成品仓"),


    /* 储位类别 */
    LOCATION_GROUND("GROUND_LOCATION", "地摊储位"),
    LOCATION_LIGHT_SHELF("LIGHT_SHELF", "轻型货架"),
    LOCATION_HEAVY_SHELF("HEAVY_SHELF", "重型货架"),

    /* 储位类型 */
    LOCATION_TYPE_RECEIVING("RECEIVING_LOC", "收货位"),
    LOCATION_TYPE_STORAGE("STORAGE_LOC", "存储位"),
    LOCATION_TYPE_PICKING("PICKING_LOC", "拣选位"),
    LOCATION_TYPE_EXCEPTION("EXCEPTION_LOC", "异常位"),

    /* 入库类型 */
    INBOUND_TYPE_PURCHASE("PURCHASE_IN", "采购入库"),
    INBOUND_TYPE_FBA_RETURN("FBA_RETURN_IN", "FBA退货入库"),
    INBOUND_TYPE_OTHER("OTHER_IN", "其它入库"),

    /* 收货类型 */
    RECEIVING_GOOD("GOOD_PRODUCT", "良品"),
    RECEIVING_DEFECTIVE("DEFECTIVE_PRODUCT", "不良品"),
    RECEIVING_SAMPLE("SAMPLE_PRODUCT", "样品"),
    /*库存属性*/
    INVENTORY_ATTRIBUTE_GOOD("GOOD_PRODUCT", "良品"),
    INVENTORY_ATTRIBUTE_DEFECTIVE("DEFECTIVE_PRODUCT", "不良品"),

    /* 任务类型 */
    TASK_TYPE_RECEIVING("RECEIVING_TASK", "收货任务"),
    TASK_TYPE_PUTAWAY("PUTAWAY_TASK", "上架任务"),
    TASK_TYPE_PICKING("PICKING_TASK", "分拣任务"),
    TASK_TYPE_PACKING("PACKING_TASK", "打包任务"),

    /* 任务状态 */
    TASK_STATUS_CREATED("CREATED", "已创建"),
    TASK_STATUS_ASSIGNED("ASSIGNED", "已指派"),
    TASK_STATUS_EXECUTING("EXECUTING", "执行中"),
    TASK_STATUS_COMPLETED("COMPLETED", "已完成"),
    TASK_STATUS_CANCELED("CANCELED", "作废"),

    /* 库存变更类型 */
    INVENTORY_RECEIVING("RECEIVING", "收货"),
    INVENTORY_PUTAWAY("PUTAWAY", "上架"),
    INVENTORY_ALLOCATION("ALLOCATION", "分配"),
    INVENTORY_PICKING("PICKING", "拣货"),
    INVENTORY_OUTBOUND("OUTBOUND", "出库"),
    INVENTORY_ADJUSTMENT("ADJUSTMENT", "调整"),
    INVENTORY_TRANSFER("TRANSFER", "调拨"),

    /**
     * 波次状态
     * 已创建	CREATED
     * 已分配	ASSIGNED
     * 拣货中	PICKING
     * 包装中	PACKING
     * 已完成	COMPLETED
     * 已取消	CANCELED
     */
    WAVE_CREATED("CREATED", "已创建"),
//    WAVE_ASSIGNED("ASSIGNED", "已分配"),
    WAVE_PICKING("PICKING", "拣货中"),
    WAVE_PICKED("PICKED", "拣货完成"),
//    WAVE_PACKING("PACKING", "打包中"),
    //打包完成
    WAVE_PACKED("PACKED", "打包完成"),
    //已发货
    WAVE_SHIPPED("SHIPPED", "已发货"),
    WAVE_COMPLETED("COMPLETED", "已完成"),
    WAVE_CANCELED("CANCELED", "已取消"),

    /**
     * 波次拣货明细状态
     */
    WAVESKU_PICKING("PICKING", "拣货中"),
    WAVESKU_PICKED("PICKED", "拣货完成"),

    /**
     * 包裹状态
     * 已创建	CREATED
     * 已打包	PACKED
     * 已发货	SHIPPED
     * 已签收	DELIVERED
     * 已取消	CANCELED
     */
    PACKAGE_STATUS_CREATED("CREATED", "已创建"),
    PACKAGE_STATUS_PACKED("PACKED", "已打包"),
    PACKAGE_STATUS_SHIPPED("SHIPPED", "已发货"),
    PACKAGE_STATUS_DELIVERED("DELIVERED", "已签收"),
    PACKAGE_STATUS_CANCELED("CANCELED", "已取消"),
    /**
     * 包裹类型
     * 标准	STANDARD
     * 大宗	BULK
     */
    PACKAGE_TYPE_STANDARD("STANDARD", "标准"),
    PACKAGE_TYPE_BULK("BULK", "大宗");


    private final String code;
    private final String desc;

    WarehouseDictEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WarehouseDictEnum fromCode(String code) {
        for (WarehouseDictEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知编码: " + code);
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return code + ":" + desc;
    }
}
