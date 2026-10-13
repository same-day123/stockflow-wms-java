package org.jeecg.modules.iot.manage.demo.route;

import org.jeecg.modules.iot.manage.demo.route.dto.Grid;
import org.jeecg.modules.iot.manage.demo.route.dto.Location;
import org.jeecg.modules.iot.manage.demo.route.dto.Node;
import org.jeecg.modules.iot.manage.demo.route.dto.Product;
import org.jeecg.modules.iot.manage.demo.route.service.WarehouseLayoutService;

import java.util.List;

/**
 * @author muht
 * @date 2025/8/9 17:58
 */
public class Test {

    public static void main(String[] args) {
        // 模拟拣货商品位置信息
        Location location = new Location("B", 2, 2);
        Product product = new Product("P001", "商品1", location);

        // 分拣车当前位置
        Node currentNode = new Node(13, 12);

        // 通过商品位置信息定位拣货点网格
        Grid shelfGrid = WarehouseLayoutService.findShelfGrid(product.getLocation());
        Grid pickingPointGrid = WarehouseLayoutService.findNearestPickingPoint(shelfGrid, currentNode);
        System.out.println("最近拣货点位置 grid=" + pickingPointGrid);

        // 路径终点
        Node endNode = new Node(pickingPointGrid.getX(), pickingPointGrid.getY());
        endNode.setLocation(pickingPointGrid.getLocation());

        // 找到最优路径
        List<Node> nodes = AStarFinder.aStarSearch(WarehouseLayoutService.WAREHOUSE_GRID, currentNode, endNode);

        // 打印最优路径
        AStarFinder.printPath(nodes);
    }
}
