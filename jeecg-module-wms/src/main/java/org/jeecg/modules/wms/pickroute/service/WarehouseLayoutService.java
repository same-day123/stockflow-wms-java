package org.jeecg.modules.wms.pickroute.service;

import org.jeecg.modules.wms.pickroute.AStarFinder;
import org.jeecg.modules.wms.pickroute.dto.Grid;
import org.jeecg.modules.wms.pickroute.dto.Location;
import org.jeecg.modules.wms.pickroute.dto.Node;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.jeecg.modules.wms.pickroute.dto.GridType.*;


/**
 * 仓库布局服务
 *
 * @author muht
 * @date 2025/8/9 19:00
 */
public class WarehouseLayoutService {
    /**
     * 静态仓库网格（示例）
     * 地图精度选择
     * 人工拣货：0.5-1米网格
     * AGV拣货：0.1-0.3米网格
     * 密集仓储：0.05米高精度网格
     */
    public static final int[][] WAREHOUSE_GRID = new int[][]{
                 //0     1      2      3      4      5      6      7      8      9      10     11     12     13     14     15     16     17     18     19     20     21     22     23     24     25
            /*0*/{STATN, STATN, STATN, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE},
            /*1*/{STATN, STATN, STATN, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE},
            /*2*/{AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE},
            /*3*/{AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE},
            /*4*/{AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE},
            /*5*/{AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE},
            /*6*/{AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE},
            /*7*/{AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE},
            /*8*/{AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE},
            /*9*/{AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE},
            /*10*/{AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE, AISLE, POINT, AISLE, AISLE, POINT, AISLE, AISLE, AISLE},
            /*11*/{AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE, SHELF, SHELF, SHELF, SHELF, SHELF, SHELF, AISLE, AISLE},
            /*12*/{AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE},
            /*13*/{AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, STATN, STATN, STATN},
            /*14*/{AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, AISLE, STATN, STATN, STATN}
    };

    /**
     * 通过逻辑位置找到网格坐标
     *
     * @param location 存放位置
     */
    public static Grid findShelfGrid(Location location) {
        String zoneName = location.getZoneName();
        int line = location.getLine();
        int col = location.getCol();

        int gridX = 2 * line + 1;
        int gridY = 0;
        if ("A".equals(zoneName)) {
            gridY = col + 1;
        } else if ("B".equals(zoneName)) {
            gridY = col + 9;
        } else if ("C".equals(zoneName)) {
            gridY = col + 17;
        }
        Grid of = Grid.of(gridX, gridY);
        of.setLocation( location);
        return of;
    }

    /**
     * 根据储位找到最近拣货点
     *
     * @param shelfGrid 货位位置
     * @return startGrid 分拣车当前位置
     */
    public static Grid findNearestPickingPoint(Grid shelfGrid, Node startNode) {
        // 储位周围所有的拣货点
        List<Grid> allPickingPoints = new ArrayList<>();
        // 上下左右四个方向，以及4个斜角网格
        for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {-1, 1}, {1, -1}}) {
            int x = shelfGrid.getX() + dir[0];
            int y = shelfGrid.getY() + dir[1];
            if (WAREHOUSE_GRID[x][y] == POINT) {
                allPickingPoints.add(Grid.of(x, y, POINT));
            }
        }

        if (!CollectionUtils.isEmpty(allPickingPoints)) {
            Grid grid = allPickingPoints.stream()
                    .sorted(Comparator.comparingDouble(
                            pointGrid -> AStarFinder.distance(
                                    startNode,
                                    new Node(pointGrid.getX(), pointGrid.getY())
                            )
                    ))
                    .toList()
                    .get(0);
            grid.setLocation( shelfGrid.getLocation());
            return grid;
        }

        return null;
    }


    /**
     * 获取所有拣货点位置
     *
     * @return
     */
    public static List<Node> getPickingPoints() {
        int[][] grid = WAREHOUSE_GRID;
        List<Node> points = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == POINT) {
                    points.add(new Node(i, j));
                }
            }
        }
        return points;
    }

    /**
     * 获取动态仓库网格
     * 用于通道临时障碍物摆放后，自动触发地图刷新
     *
     * @return
     */
    public static int[][] getDynamicGrid(int x, int y, int type) {
        WAREHOUSE_GRID[x][y] = type;
        return WAREHOUSE_GRID;
    }

}
