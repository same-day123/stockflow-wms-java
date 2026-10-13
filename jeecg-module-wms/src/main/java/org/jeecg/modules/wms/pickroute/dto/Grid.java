package org.jeecg.modules.wms.pickroute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 网格坐标
 *
 * @author muht
 * @create 2025/8/11 12:00
 */
@Data
@AllArgsConstructor
public class Grid {
    /**
     * 商品存放货位
     */
    private Location location;

    /**
     * 网格坐标x
     */
    private int x;
    /**
     * 网格坐标y
     */
    private int y;
    /**
     * 类型，GridType
     */
    private int type;

    public Grid(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    /**
     * 工厂方法
     */
    public static Grid of(int x, int y) {
        return new Grid(x, y, GridType.SHELF);
    }

    public static Grid of(int x, int y, int type) {
        return new Grid(x, y, type);
    }
}
