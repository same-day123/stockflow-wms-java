package org.jeecg.modules.wms.pickroute;


import org.jeecg.modules.wms.pickroute.dto.Grid;
import org.jeecg.modules.wms.pickroute.dto.Location;
import org.jeecg.modules.wms.pickroute.dto.Node;
import org.jeecg.modules.wms.pickroute.dto.Product;
import org.jeecg.modules.wms.pickroute.service.WarehouseLayoutService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiProductPickingRoute {

    /**
     * 计算多个商品的最优拣货路径
     */
    public static List<Node> calculateOptimalPickingRoute(Node startNode, List<Product> products) {
        // 1. 获取所有拣货点
        List<Grid> pickingPoints = new ArrayList<>();
        for (Product product : products) {
            Grid shelfGrid = WarehouseLayoutService.findShelfGrid(product.getLocation());
            Grid pickingPointGrid = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, startNode);
            pickingPoints.add(pickingPointGrid);
        }

        // 2. 使用贪心算法或遗传算法优化拣货点访问顺序
        List<Grid> optimizedOrder = optimizePickingOrder(startNode, pickingPoints);

        // 3. 计算完整路径
        List<Node> fullPath = new ArrayList<>();
        Node current = startNode;

        for (Grid pickingPoint : optimizedOrder) {
            Node target = new Node(pickingPoint.getX(), pickingPoint.getY());
            target.setLocation(pickingPoint.getLocation());
            List<Node> segment = AStarFinder.aStarSearch(WarehouseLayoutService.WAREHOUSE_GRID, current, target);

            // 添加路径段（除去起点，避免重复）
            if (segment.size() > 1) {
                fullPath.addAll(segment.subList(1, segment.size()));
            } else {
                fullPath.addAll(segment);
            }

            current = target;
        }

        return fullPath;
    }

    /**
     * 优化拣货点访问顺序（这里使用简单的最近邻算法）
     */
    private static List<Grid> optimizePickingOrder(Node startNode, List<Grid> pickingPoints) {
        List<Grid> optimized = new ArrayList<>();
        List<Grid> remaining = new ArrayList<>(pickingPoints);
        Node current = startNode;

        while (!remaining.isEmpty()) {
            // 找到距离当前节点最近的拣货点
            Grid nearest = findNearestPoint(current, remaining);
            optimized.add(nearest);
            remaining.remove(nearest);
            current = new Node(nearest.getX(), nearest.getY());
        }

        return optimized;
    }

    /**
     * 找到距离指定节点最近的拣货点
     */
    private static Grid findNearestPoint(Node current, List<Grid> points) {
        Grid nearest = points.get(0);
        double minDistance = calculateDistance(current, new Node(nearest.getX(), nearest.getY()));

        for (int i = 1; i < points.size(); i++) {
            Grid point = points.get(i);
            double distance = calculateDistance(current, new Node(point.getX(), point.getY()));
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }

        return nearest;
    }

    /**
     * 计算两个节点间的距离
     */
    private static double calculateDistance(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public static void main(String[] args) {
        // 起始位置
        Node startNode = new Node(13, 12);

        // 多个商品
        List<Product> products = Arrays.asList(
                new Product("P001", "商品1", new Location("B", 2, 2,"Z1B-2-2")),
                new Product("P002", "商品2", new Location("C", 5, 3,"Z1C-5-3")),
                new Product("P003", "商品3", new Location("A", 1, 4,"Z1A-1-4")),
                new Product("P003", "商品3", new Location("A", 2, 4,"Z1A-2-4"))
        );

        // 计算最优路径
        List<Node> optimalRoute = MultiProductPickingRoute.calculateOptimalPickingRoute(startNode, products);

        // 打印路径
        AStarFinder.printPath(optimalRoute);
    }
}
