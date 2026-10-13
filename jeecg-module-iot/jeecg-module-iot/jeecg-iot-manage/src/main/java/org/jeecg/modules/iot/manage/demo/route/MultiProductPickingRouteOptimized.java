package org.jeecg.modules.iot.manage.demo.route;

import org.jeecg.modules.iot.manage.demo.route.dto.Grid;
import org.jeecg.modules.iot.manage.demo.route.dto.Location;
import org.jeecg.modules.iot.manage.demo.route.dto.Node;
import org.jeecg.modules.iot.manage.demo.route.dto.Product;
import org.jeecg.modules.iot.manage.demo.route.service.WarehouseLayoutService;

import java.util.*;

public class MultiProductPickingRouteOptimized {

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

    /**
     * 计算两个节点间的距离
     */
    private static double calculateDistance(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
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

    public static void main(String[] args) {
        // 起始位置
        Node startNode = new Node(13, 12);

        // 多个商品 - 测试相邻行和非相邻行的情况
        List<Product> products = Arrays.asList(
                new Product("P003", "商品3", new Location("A", 1, 4)),
                new Product("P004", "商品4", new Location("A", 3, 4)),
                new Product("P005", "商品5", new Location("A", 3, 5)),
                new Product("P006", "商品6", new Location("C", 4, 5)) // 与前两个商品不相邻
        );

        // 计算带商品信息的最优路径
        RouteWithProducts routeWithProducts = calculateOptimalPickingRouteWithProducts(startNode, products);

        // 打印带商品信息的路径
        System.out.println("优化后的拣货路径（带商品信息）:");
        printPathWithProducts(routeWithProducts);

        // 生成图形数据
        System.out.println("\nSVG图形数据:");
        String svgData = generateSVGData(routeWithProducts, startNode, products);
        System.out.println(svgData);

        // 保存SVG到文件
        saveSVGToFile(svgData, "picking_route.svg");

        // 生成坐标点数据
        System.out.println("\n坐标点数据:");
        List<PointWithInfo> pointsData = generatePointsData(routeWithProducts, startNode, products);
        for (PointWithInfo point : pointsData) {
            System.out.println(point);
        }

        // 生成文本形式的简单可视化图
        System.out.println("\n文本形式可视化:");
        String textVisualization = generateTextVisualization(routeWithProducts, startNode, products);
        System.out.println(textVisualization);
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
     * 生成SVG格式的图形数据
     */
    public static String generateSVGData(RouteWithProducts routeWithProducts, Node startNode, List<Product> products) {
        StringBuilder svg = new StringBuilder();
        int width = 1040;  // 26列 * 40px
        int height = 600;  // 15行 * 40px
        int cellWidth = 40;
        int cellHeight = 40;

        svg.append("<svg width=\"").append(width).append("\" height=\"").append(height).append("\" xmlns=\"http://www.w3.org/2000/svg\">\n");

        // 绘制仓库布局网格
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 26; col++) {
                int x = col * cellWidth;
                int y = row * cellHeight;

                // 根据网格类型设置颜色
                String fillColor = "#ffffff"; // 默认白色
                switch (WarehouseLayoutService.WAREHOUSE_GRID[row][col]) {
                    case 0:  // STATN 起点/终点区域
                        fillColor = "#cccccc";
                        break;
                    case 1:  // AISLE 通道
                        fillColor = "#f0f0f0";
                        break;
                    case 2:  // SHELF 货架
                        fillColor = "#87CEEB";
                        break;
                    case 3:  // POINT 拣货点
                        fillColor = "#FFD700";
                        break;
                }

                svg.append(String.format("  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\" stroke=\"#ccc\" stroke-width=\"1\" />\n",
                        x, y, cellWidth, cellHeight, fillColor));
            }
        }

        // 绘制网格线
        for (int row = 0; row <= 15; row++) {
            svg.append(String.format("  <line x1=\"0\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#999\" stroke-width=\"1\" />\n",
                    row * cellHeight, 26 * cellWidth, row * cellHeight));
        }
        for (int col = 0; col <= 26; col++) {
            svg.append(String.format("  <line x1=\"%d\" y1=\"0\" x2=\"%d\" y2=\"%d\" stroke=\"#999\" stroke-width=\"1\" />\n",
                    col * cellWidth, col * cellWidth, 15 * cellHeight));
        }

        // 绘制路径
        List<Node> path = routeWithProducts.getPath();
        if (!path.isEmpty()) {
            svg.append("  <!-- 拣货路径 -->\n");
            svg.append("  <polyline points=\"");
            // 添加起始点
            svg.append(String.format("%d,%d ", startNode.getY() * cellWidth + cellWidth / 2, startNode.getX() * cellHeight + cellHeight / 2));
            // 添加路径点
            for (Node node : path) {
                svg.append(String.format("%d,%d ", node.getY() * cellWidth + cellWidth / 2, node.getX() * cellHeight + cellHeight / 2));
            }
            svg.append("\" fill=\"none\" stroke=\"blue\" stroke-width=\"3\" />\n");
        }

        // 绘制起始点
        svg.append(String.format("  <circle cx=\"%d\" cy=\"%d\" r=\"8\" fill=\"green\" />\n",
                startNode.getY() * cellWidth + cellWidth / 2, startNode.getX() * cellHeight + cellHeight / 2));
        svg.append(String.format("  <text x=\"%d\" y=\"%d\" fill=\"black\" font-size=\"12\" font-weight=\"bold\">起点</text>\n",
                startNode.getY() * cellWidth + cellWidth / 2 + 10, startNode.getX() * cellHeight + cellHeight / 2 - 10));

        // 绘制拣货点和商品信息
        List<List<Product>> productsAtEachPoint = routeWithProducts.getProductsAtEachPoint();

        // 跟踪已绘制的拣货点，避免重复绘制
        Set<String> drawnPoints = new HashSet<>();

        for (int i = 0; i < productsAtEachPoint.size(); i++) {
            List<Product> productGroup = productsAtEachPoint.get(i);
            if (!productGroup.isEmpty()) {
                Product firstProduct = productGroup.get(0);
                Location location = firstProduct.getLocation();
                Grid shelfGrid = WarehouseLayoutService.findShelfGrid(location);
                Grid pickingPoint = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, startNode);

                // 创建拣货点的唯一标识
                String pointKey = pickingPoint.getX() + "," + pickingPoint.getY();

                // 只绘制尚未绘制的拣货点
                if (!drawnPoints.contains(pointKey)) {
                    // 绘制拣货点
                    svg.append(String.format("  <circle cx=\"%d\" cy=\"%d\" r=\"8\" fill=\"red\" />\n",
                            pickingPoint.getY() * cellWidth + cellWidth / 2, pickingPoint.getX() * cellHeight + cellHeight / 2));

                    // 收集该拣货点的所有商品信息
                    StringBuilder productInfo = new StringBuilder();
                    // 查找所有在同一个拣货点的商品
                    for (List<Product> group : productsAtEachPoint) {
                        if (!group.isEmpty()) {
                            Product firstP = group.get(0);
                            Location loc = firstP.getLocation();
                            Grid shelf = WarehouseLayoutService.findShelfGrid(loc);
                            Grid point = WarehouseLayoutService.findNearestPickingPoint(shelf, startNode);

                            // 如果是同一个拣货点
                            if (point.getX() == pickingPoint.getX() && point.getY() == pickingPoint.getY()) {
                                for (int j = 0; j < group.size(); j++) {
                                    if (productInfo.length() > 0) productInfo.append(", ");
                                    Product p = group.get(j);
                                    productInfo.append(p.getName()).append("(行").append(p.getLocation().getLine())
                                            .append(",列").append(p.getLocation().getCol()).append(")");
                                }
                            }
                        }
                    }

                    svg.append(String.format("  <text x=\"%d\" y=\"%d\" fill=\"black\" font-size=\"10\">%s</text>\n",
                            pickingPoint.getY() * cellWidth + cellWidth / 2 + 10, pickingPoint.getX() * cellHeight + cellHeight / 2 - 10, productInfo.toString()));

                    drawnPoints.add(pointKey);
                }
            }
        }

        // 添加图例
        svg.append("  <!-- 图例 -->\n");
        svg.append("  <rect x=\"850\" y=\"20\" width=\"15\" height=\"15\" fill=\"#cccccc\" stroke=\"#999\" />\n");
        svg.append("  <text x=\"870\" y=\"32\" font-size=\"12\">起点/终点区域</text>\n");

        svg.append("  <rect x=\"850\" y=\"40\" width=\"15\" height=\"15\" fill=\"#f0f0f0\" stroke=\"#999\" />\n");
        svg.append("  <text x=\"870\" y=\"52\" font-size=\"12\">通道</text>\n");

        svg.append("  <rect x=\"850\" y=\"60\" width=\"15\" height=\"15\" fill=\"#87CEEB\" stroke=\"#999\" />\n");
        svg.append("  <text x=\"870\" y=\"72\" font-size=\"12\">货架</text>\n");

        svg.append("  <rect x=\"850\" y=\"80\" width=\"15\" height=\"15\" fill=\"#FFD700\" stroke=\"#999\" />\n");
        svg.append("  <text x=\"870\" y=\"92\" font-size=\"12\">拣货点</text>\n");

        svg.append("  <circle cx=\"857\" y=\"110\" r=\"6\" fill=\"green\" />\n");
        svg.append("  <text x=\"870\" y=\"114\" font-size=\"12\">起点</text>\n");

        svg.append("  <circle cx=\"857\" y=\"130\" r=\"6\" fill=\"red\" />\n");
        svg.append("  <text x=\"870\" y=\"134\" font-size=\"12\">拣货点</text>\n");

        svg.append("  <line x1=\"850\" y1=\"150\" x2=\"870\" y2=\"150\" stroke=\"blue\" stroke-width=\"3\" />\n");
        svg.append("  <text x=\"875\" y=\"154\" font-size=\"12\">拣货路径</text>\n");

        svg.append("</svg>");
        return svg.toString();
    }

    /**
     * 保存SVG到文件
     */
    public static void saveSVGToFile(String svgData, String filename) {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filename),
                    svgData.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            System.out.println("SVG文件已保存到: " + filename);
        } catch (java.io.IOException e) {
            System.err.println("保存SVG文件时出错: " + e.getMessage());
        }
    }

    /**
     * 生成坐标点数据，包含每个点的类型信息
     */
    public static List<PointWithInfo> generatePointsData(RouteWithProducts routeWithProducts, Node startNode, List<Product> products) {
        List<PointWithInfo> points = new ArrayList<>();

        // 添加起始点
        points.add(new PointWithInfo(startNode.getX(), startNode.getY(), PointType.START, null));

        // 添加路径点
        List<Node> path = routeWithProducts.getPath();
        List<List<Product>> productsAtEachPoint = routeWithProducts.getProductsAtEachPoint();

        int productPointIndex = 0;
        List<Product> currentProducts = productsAtEachPoint.isEmpty() ?
                new ArrayList<>() : productsAtEachPoint.get(0);

        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);

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
                points.add(new PointWithInfo(node.getX(), node.getY(), PointType.PICKING, currentProducts));

                productPointIndex++;
                if (productPointIndex < productsAtEachPoint.size()) {
                    currentProducts = productsAtEachPoint.get(productPointIndex);
                } else {
                    currentProducts = new ArrayList<>();
                }
            } else {
                points.add(new PointWithInfo(node.getX(), node.getY(), PointType.PATH, null));
            }
        }

        return points;
    }

    /**
     * 生成文本形式的简单可视化图
     */
    public static String generateTextVisualization(RouteWithProducts routeWithProducts, Node startNode, List<Product> products) {
        // 创建一个15行26列的文本地图
        char[][] map = new char[15][26];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 26; j++) {
                // 根据网格类型设置字符
                switch (WarehouseLayoutService.WAREHOUSE_GRID[i][j]) {
                    case 0:  // STATN 起点/终点区域
                        map[i][j] = 'S';
                        break;
                    case 1:  // AISLE 通道
                        map[i][j] = '.';
                        break;
                    case 2:  // SHELF 货架
                        map[i][j] = '#';
                        break;
                    case 3:  // POINT 拣货点
                        map[i][j] = 'O';
                        break;
                    default:
                        map[i][j] = '.';
                        break;
                }
            }
        }

        // 标记路径点
        List<Node> path = routeWithProducts.getPath();
        for (Node node : path) {
            if (node.getX() >= 0 && node.getX() < 15 && node.getY() >= 0 && node.getY() < 26) {
                map[node.getX()][node.getY()] = '*'; // 路径用*表示
            }
        }

        // 标记起始点
        if (startNode.getX() >= 0 && startNode.getX() < 15 && startNode.getY() >= 0 && startNode.getY() < 26) {
            map[startNode.getX()][startNode.getY()] = 'S'; // 起点用S表示
        }

        // 标记拣货点
        List<List<Product>> productsAtEachPoint = routeWithProducts.getProductsAtEachPoint();
        for (List<Product> productGroup : productsAtEachPoint) {
            if (!productGroup.isEmpty()) {
                Product firstProduct = productGroup.get(0);
                Location location = firstProduct.getLocation();
                Grid shelfGrid = WarehouseLayoutService.findShelfGrid(location);
                Grid pickingPoint = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, startNode);

                if (pickingPoint.getX() >= 0 && pickingPoint.getX() < 15 &&
                        pickingPoint.getY() >= 0 && pickingPoint.getY() < 26) {
                    map[pickingPoint.getX()][pickingPoint.getY()] = 'P'; // 拣货点用P表示
                }
            }
        }

        // 生成文本表示
        StringBuilder visualization = new StringBuilder();

        // 添加列号
        visualization.append("    ");
        for (int x = 0; x < 26; x++) {
            visualization.append(String.format("%2d", x % 10));
        }
        visualization.append("\n");

        // 添加行号和地图内容
        for (int y = 0; y < 15; y++) {
            visualization.append(String.format("%2d: ", y));
            for (int x = 0; x < 26; x++) {
                visualization.append(" ").append(map[y][x]);
            }
            visualization.append("\n");
        }

        // 添加图例
        visualization.append("\n图例:\n");
        visualization.append("S - 起点/终点区域\n");
        visualization.append(". - 通道\n");
        visualization.append("# - 货架\n");
        visualization.append("O - 拣货点\n");
        visualization.append("* - 路径点\n");
        visualization.append("P - 拣货点(路径中访问)\n");

        return visualization.toString();
    }

    /**
     * 点类型枚举
     */
    public enum PointType {
        START,      // 起始点
        PATH,       // 路径点
        PICKING     // 拣货点
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
     * 带信息的点类
     */
    public static class PointWithInfo {
        private int x;
        private int y;
        private PointType type;
        private List<Product> products;

        public PointWithInfo(int x, int y, PointType type, List<Product> products) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.products = products;
        }

        // Getters
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public PointType getType() {
            return type;
        }

        public List<Product> getProducts() {
            return products;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Point(%d,%d) Type:%s", x, y, type));
            if (products != null && !products.isEmpty()) {
                sb.append(" Products:[");
                for (int i = 0; i < products.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Product p = products.get(i);
                    sb.append(String.format("%s(行%d,列%d)", p.getName(),
                            p.getLocation().getLine(), p.getLocation().getCol()));
                }
                sb.append("]");
            }
            return sb.toString();
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
