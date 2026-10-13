package org.jeecg.modules.iot.manage.demo.route;

import org.jeecg.modules.iot.manage.demo.route.dto.Grid;
import org.jeecg.modules.iot.manage.demo.route.dto.Location;
import org.jeecg.modules.iot.manage.demo.route.dto.Node;
import org.jeecg.modules.iot.manage.demo.route.dto.Product;
import org.jeecg.modules.iot.manage.demo.route.service.WarehouseLayoutService;

import java.util.*;

public class MultiProductPickingRouteOptimized4 {

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

    /**
     * 计算多个商品的最优拣货路径并返回带商品信息的路径
     */
    public static RouteWithProducts calculateOptimalPickingRouteWithProducts(Node startNode, List<Product> products) {
        // 1. 按货架列和相邻行分组商品
        List<List<Product>> productGroups = groupProductsByAdjacentRows(products);

        // 2. 为每个商品组获取一个代表拣货点及对应的商品列表
        List<PickingPointWithProducts> pickingPointsWithProducts = new ArrayList<>();
        for (List<Product> productGroup : productGroups) {
            // 为每组商品找到代表位置并计算拣货点
            Location representativeLocation = productGroup.get(0).getLocation();
            Grid shelfGrid = WarehouseLayoutService.findShelfGrid(representativeLocation);
            Grid pickingPointGrid = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, startNode);
            pickingPointsWithProducts.add(new PickingPointWithProducts(pickingPointGrid, productGroup));
        }

        // 3. 使用贪心算法优化拣货点访问顺序
        List<PickingPointWithProducts> optimizedOrder = optimizePickingOrderWithProducts(startNode, pickingPointsWithProducts);

        // 4. 计算完整路径
        List<Node> fullPath = new ArrayList<>();
        List<List<Product>> productsAtEachPoint = new ArrayList<>();
        Node current = startNode;

        for (PickingPointWithProducts pickingPointWithProducts : optimizedOrder) {
            Grid pickingPoint = pickingPointWithProducts.getPickingPoint();
            Node target = new Node(pickingPoint.getX(), pickingPoint.getY());
            target.setLocation(pickingPoint.getLocation());
            List<Node> segment = AStarFinder.aStarSearch(WarehouseLayoutService.WAREHOUSE_GRID, current, target);

            // 添加路径段（除去起点，避免重复）
            if (segment.size() > 1) {
                fullPath.addAll(segment.subList(1, segment.size()));
            } else {
                fullPath.addAll(segment);
            }

            // 记录在该拣货点需要拣选的商品
            productsAtEachPoint.add(pickingPointWithProducts.getProducts());

            current = target;
        }

        return new RouteWithProducts(fullPath, productsAtEachPoint);
    }

    /**
     * 优化带商品信息的拣货点访问顺序
     */
    private static List<PickingPointWithProducts> optimizePickingOrderWithProducts(Node startNode,
        List<PickingPointWithProducts> pickingPointsWithProducts) {
        List<PickingPointWithProducts> optimized = new ArrayList<>();
        List<PickingPointWithProducts> remaining = new ArrayList<>(pickingPointsWithProducts);
        Node current = startNode;

        while (!remaining.isEmpty()) {
            // 找到距离当前节点最近的拣货点
            PickingPointWithProducts nearest = findNearestPointWithProducts(current, remaining);
            optimized.add(nearest);
            remaining.remove(nearest);
            current = new Node(nearest.getPickingPoint().getX(), nearest.getPickingPoint().getY());
        }

        return optimized;
    }

    /**
     * 找到距离指定节点最近的带商品信息的拣货点
     */
    private static PickingPointWithProducts findNearestPointWithProducts(Node current,
        List<PickingPointWithProducts> points) {
        PickingPointWithProducts nearest = points.get(0);
        double minDistance = calculateDistance(current,
            new Node(nearest.getPickingPoint().getX(), nearest.getPickingPoint().getY()));

        for (int i = 1; i < points.size(); i++) {
            PickingPointWithProducts point = points.get(i);
            double distance = calculateDistance(current,
                new Node(point.getPickingPoint().getX(), point.getPickingPoint().getY()));
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }

        return nearest;
    }

    public static void main(String[] args) {
        // 起始位置
        Node startNode = new Node(13, 12);

        // 多个商品 - 测试相邻行和非相邻行的情况
        List<Product> products = Arrays.asList(
                new Product("P003", "商品3", new Location("A", 1, 4)),
                new Product("P004", "商品4", new Location("A", 3, 4)),
                new Product("P004", "商品4", new Location("A", 3, 5)),
                new Product("P005", "商品5", new Location("A", 4, 5)) // 与前两个商品不相邻
        );

        // 计算带商品信息的最优路径
        RouteWithProducts routeWithProducts = calculateOptimalPickingRouteWithProducts(startNode, products);

        // 打印带商品信息的路径
        System.out.println("优化后的拣货路径（带商品信息）:");
        printPathWithProducts(routeWithProducts);

        // 对比原始方法
        System.out.println("\n原始方法的拣货路径:");
        List<Node> originalRoute = MultiProductPickingRoute.calculateOptimalPickingRoute(startNode, products);
        AStarFinder.printPath(originalRoute);
    }

    /**
     * 打印带商品信息的路径
     */
    public static void printPathWithProducts(RouteWithProducts routeWithProducts) {
        List<Node> path = routeWithProducts.getPath();
        List<List<Product>> productsAtEachPoint = routeWithProducts.getProductsAtEachPoint();

        int productPointIndex = 0;
        List<Product> currentProducts = productsAtEachPoint.isEmpty() ?
            new ArrayList<>() : productsAtEachPoint.get(0);

        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            System.out.print("(" + node.getX() + "," + node.getY() + ")");

            // 检查是否是拣货点
            boolean isPickingPoint = false;
            for (Product product : currentProducts) {
                Location location = product.getLocation();
                Grid shelfGrid = WarehouseLayoutService.findShelfGrid(location);
                Grid expectedPickingPoint = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, node);
                if (expectedPickingPoint.getX() == node.getX() && expectedPickingPoint.getY() == node.getY()) {
                    isPickingPoint = true;
                    break;
                }
            }

            if (isPickingPoint && !currentProducts.isEmpty()) {
                System.out.print(" Location(");
                for (int j = 0; j < currentProducts.size(); j++) {
                    if (j > 0) System.out.print(",");
                    Product product = currentProducts.get(j);
                    Location loc = product.getLocation();
                    System.out.print("zoneName=" + loc.getZoneName() + ", line=" + loc.getLine() +
                        ", col=" + loc.getCol());
                }
                System.out.print(")");

                productPointIndex++;
                if (productPointIndex < productsAtEachPoint.size()) {
                    currentProducts = productsAtEachPoint.get(productPointIndex);
                } else {
                    currentProducts = new ArrayList<>();
                }
            }

            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    /**
     * 表示拣货点及其对应商品的类
     */
    public static class PickingPointWithProducts {
        private Grid pickingPoint;
        private List<Product> products;

        public PickingPointWithProducts(Grid pickingPoint, List<Product> products) {
            this.pickingPoint = pickingPoint;
            this.products = products;
        }

        public Grid getPickingPoint() {
            return pickingPoint;
        }

        public List<Product> getProducts() {
            return products;
        }
    }

    /**
     * 表示带商品信息的路径类
     */
    public static class RouteWithProducts {
        private List<Node> path;
        private List<List<Product>> productsAtEachPoint;

        public RouteWithProducts(List<Node> path, List<List<Product>> productsAtEachPoint) {
            this.path = path;
            this.productsAtEachPoint = productsAtEachPoint;
        }

        public List<Node> getPath() {
            return path;
        }

        public List<List<Product>> getProductsAtEachPoint() {
            return productsAtEachPoint;
        }
    }
}
