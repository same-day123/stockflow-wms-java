package org.jeecg.modules.iot.manage.demo.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品信息
 *
 * @author muht
 * @create 2025/8/8 11:32
 */
@Data
public class GoodsInfo {
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品类型
     */
    private String goodsType;
    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;
}
