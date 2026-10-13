package org.jeecg.modules.iot.manage.demo.route.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品类
 *
 * @author muht
 * @date 2025/8/9 20:39
 */
@Data
@AllArgsConstructor
public class Product {
    /**
     * 商品ID
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品存放货位
     */
    private Location location;
}
