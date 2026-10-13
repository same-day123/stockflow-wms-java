package org.jeecg.modules.iot.manage.demo.route;

import org.jeecg.modules.iot.manage.demo.route.dto.Grid;
import org.jeecg.modules.iot.manage.demo.route.dto.Location;
import org.jeecg.modules.iot.manage.demo.route.dto.Node;
import org.jeecg.modules.iot.manage.demo.route.dto.Product;
import org.jeecg.modules.iot.manage.demo.route.service.WarehouseLayoutService;

import java.util.*;

public class MultiProductPickingRouteOptimized3 {

    /**
     * 计算多个商品的最优拣货路径（优化版本）
     */
    public static List<Node> calculateOptimalPickingRoute(Node startNode, List<Product> products) {
        // 1. 按货架列和相邻行分组商品
        List<List<Product>> productGroups = groupProductsByAdjacentRows(products);

        // 2. 为每个商品组获取一个代表拣货点
        List<Grid> pickingPoints = new ArrayList<>();
        for (List<Product> productGroup : productGroups) {
            // 为每组商品找到代表位置并计算拣货点
            Location representativeLocation = productGroup.get(0).getLocation();
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
     * 按货架列和相邻行分组商品
     * 同一列中相邻行的商品可以共用拣货位
     */
    private static List<List<Product>> groupProductsByAdjacentRows(List<Product> products) {
        // 先按区域和列分组
        Map<String, List<Product>> productsByColumn = new HashMap<>();
        for (Product product : products) {
            Location location = product.getLocation();
            // 用区域名和列号作为键
            String columnKey = location.getZoneName() + "-" + location.getCol();
            productsByColumn.computeIfAbsent(columnKey, k -> new ArrayList<>()).add(product);
        }

        // 对每列中的商品按行号排序，并分组相邻行的商品
        List<List<Product>> result = new ArrayList<>();
        for (List<Product> columnProducts : productsByColumn.values()) {
            // 按照行号排序
            columnProducts.sort(Comparator.comparingInt(p -> p.getLocation().getLine()));

            // 分组相邻行的商品
            List<Product> currentGroup = new ArrayList<>();
            int lastRow = -1;

            for (Product product : columnProducts) {
                int currentRow = product.getLocation().getLine();

                // 如果是第一个商品或者与上一个商品相邻行，则加入当前组
                if (currentGroup.isEmpty() || currentRow == lastRow + 1) {
                    currentGroup.add(product);
                } else {
                    // 否则，结束当前组，开始新组
                    result.add(new ArrayList<>(currentGroup));
                    currentGroup.clear();
                    currentGroup.add(product);
                }
                lastRow = currentRow;
            }

            // 添加最后一个组
            if (!currentGroup.isEmpty()) {
                result.add(currentGroup);
            }
        }

        return result;
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

        // 多个商品 - 测试相邻行和非相邻行的情况
        List<Product> products = Arrays.asList(
                new Product("P003", "商品3", new Location("A", 1, 4)),
                new Product("P004", "商品4", new Location("A", 3, 4)),
                new Product("P005", "商品5", new Location("A", 4, 4)) // 与前两个商品不相邻
        );

        // 计算最优路径
        List<Node> optimalRoute = MultiProductPickingRouteOptimized3.calculateOptimalPickingRoute(startNode, products);

        // 打印路径
        System.out.println("优化后的拣货路径:");
        AStarFinder.printPath(optimalRoute);

        // 对比原始方法
        System.out.println("\n原始方法的拣货路径:");
        List<Node> originalRoute = MultiProductPickingRoute.calculateOptimalPickingRoute(startNode, products);
        AStarFinder.printPath(originalRoute);

        // 显示分组结果
        System.out.println("\n商品分组结果:");
        List<List<Product>> groups = groupProductsByAdjacentRows(products);
        for (int i = 0; i < groups.size(); i++) {
            System.out.println("组 " + (i+1) + ": ");
            for (Product p : groups.get(i)) {
                Location loc = p.getLocation();
                System.out.println("  " + p.getName() + " (" + loc.getZoneName() +
                        "区, 第" + loc.getLine() + "行, 第" + loc.getCol() + "列)");
            }
        }
    }
}
