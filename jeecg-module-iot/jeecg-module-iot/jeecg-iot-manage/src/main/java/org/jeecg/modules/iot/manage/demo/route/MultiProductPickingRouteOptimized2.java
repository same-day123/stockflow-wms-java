package org.jeecg.modules.iot.manage.demo.route;

import org.jeecg.modules.iot.manage.demo.route.dto.Grid;
import org.jeecg.modules.iot.manage.demo.route.dto.Location;
import org.jeecg.modules.iot.manage.demo.route.dto.Node;
import org.jeecg.modules.iot.manage.demo.route.dto.Product;
import org.jeecg.modules.iot.manage.demo.route.service.WarehouseLayoutService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiProductPickingRouteOptimized2 {

    /**
     * 计算多个商品的最优拣货路径（优化版本）
     */
    public static List<Node> calculateOptimalPickingRoute(Node startNode, List<Product> products) {
        // 1. 按货架行分组商品（同一货架行的商品可以一次性拣选）
        Map<String, List<Product>> productsByShelfRow = groupProductsByShelfRow(products);

        // 2. 为每个货架行获取一个代表拣货点
        List<Grid> pickingPoints = new ArrayList<>();
        for (List<Product> productList : productsByShelfRow.values()) {
            // 为每组商品找到代表位置并计算拣货点
            Location representativeLocation = productList.get(0).getLocation();
            Grid shelfGrid = WarehouseLayoutService.findShelfGrid(representativeLocation);
            Grid pickingPointGrid = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, startNode);
            pickingPoints.add(pickingPointGrid);
        }

        // 3. 使用贪心算法优化拣货点访问顺序
        List<Grid> optimizedOrder = optimizePickingOrder(startNode, pickingPoints);

        // 4. 计算完整路径
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
     * 按货架行分组商品（同一行的商品可以一起拣选）
     * 键格式: "区域名-列号"，例如 "A-4" 表示A区域第4列
     */
    private static Map<String, List<Product>> groupProductsByShelfRow(List<Product> products) {
        Map<String, List<Product>> groupedProducts = new HashMap<>();

        for (Product product : products) {
            Location location = product.getLocation();
            // 用区域名和列号作为键（因为Location中line是行，col是列）
            String shelfRowKey = location.getZoneName() + "-" + location.getCol();
            groupedProducts.computeIfAbsent(shelfRowKey, k -> new ArrayList<>()).add(product);
        }

        return groupedProducts;
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

        // 多个商品 - 同一货架行的两个商品
        List<Product> products = Arrays.asList(
//            new Product("P001", "商品1", new Location("B", 2, 2)),
//            new Product("P002", "商品2", new Location("C", 5, 3)),
            new Product("P003", "商品3", new Location("A", 1, 4)),
            new Product("P004", "商品4", new Location("A", 3, 4))
        );

        // 计算最优路径
        List<Node> optimalRoute = MultiProductPickingRouteOptimized2.calculateOptimalPickingRoute(startNode, products);

        // 打印路径
        System.out.println("优化后的拣货路径:");
        AStarFinder.printPath(optimalRoute);

        // 对比原始方法
        System.out.println("\n原始方法的拣货路径:");
        List<Node> originalRoute = MultiProductPickingRoute.calculateOptimalPickingRoute(startNode, products);
        AStarFinder.printPath(originalRoute);
    }
}
