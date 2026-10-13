package org.jeecg.modules.iot.manage.demo.route.dto;

import lombok.Data;

import java.util.Objects;

/**
 * 路径规划节点（指向父节点的单向链表）
 *
 * @author muht
 * @date 2025/8/9 16:58
 */
@Data
public class Node {

    /**
     * 商品存放货位
     */
    private Location location;

    /**
     * 节点坐标
     */
    private int x, y;
    /**
     * g: 实际代价.从起点到当前节点的实际移动代价
     * h: 启发值。从当前节点到目标节点的预估代价。
     * - 对于网格地图, h=|△x|+|△y|，直角三角两直角边的和
     * - 对于自由移动平面，h=√△x^2+△y^2，直角三角形斜边
     * f: 总估值，节点总代价估计值，f = g + h
     */
    private double f = 0.0, g = 0.0, h = 0.0;
    /**
     * 父节点/路径回溯节点
     */
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * 节点唯一ID
     */
    public String getKey() {
        return x + "," + y;
    }
}
