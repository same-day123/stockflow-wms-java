package org.jeecg.modules.wms.pickroute;


import org.jeecg.modules.wms.pickroute.dto.GridType;
import org.jeecg.modules.wms.pickroute.dto.Node;

import java.util.*;

/**
 * A*算法路径规划
 *
 * @author muht
 * @date 2025/8/9 16:38
 */
public class AStarFinder {

    /**
     * A* 算法搜索
     */
    public static List<Node> aStarSearch(int[][] grid, Node start, Node end) {
        // 开启队列，通过优先级队列可以快速获取路径估值最短的节点，时间复杂度 O(1)
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.getF()));
        Map<String, Node> nodeCache = new HashMap<>();
        // 关闭集合，利用Set的去重特性存储探索过的节点，避免重复
        Set<String> closed = new HashSet<>();

        start.setG(0.0);
        start.setH(heuristic(start, end));
        start.setF(start.getG() + start.getH());
        open.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();

            // 到达终点
            if (current.getX() == end.getX() && current.getY() == end.getY()){
                current.setLocation(end.getLocation());
                return reconstructPath(current);

            }

            closed.add(current.getX() + "," + current.getY());

            // 遍历上下左右四个方向
            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int nx = current.getX() + dir[0];
                int ny = current.getY() + dir[1];
                String nodeKey = nx + "," + ny;

                // 检查边界和障碍物
                if (nx < 0 || ny < 0 || nx >= grid.length || ny >= grid[0].length || grid[nx][ny] == GridType.SHELF) {
                    continue;
                }

                // 已经探索过则跳过此节点
                if (closed.contains(nx + "," + ny)) {
                    continue;
                }

                Node neighbor;
                if (nodeCache.containsKey(nodeKey)) {
                    neighbor = nodeCache.get(nodeKey);
                } else {
                    neighbor = new Node(nx, ny);
                    nodeCache.put(nodeKey, neighbor);
                }

                // 计算此方向相邻节点的预估实际成本，即实际成本G + 距离(相邻节点始终为1)
                double tentativeG = current.getG() + distance(current, neighbor);

                // 更新相邻节点
                if (!open.contains(neighbor) || tentativeG < neighbor.getG()) {
                    neighbor.setParent(current);
                    neighbor.setG(tentativeG);
                    neighbor.setH(heuristic(neighbor, end));
                    neighbor.setF(neighbor.getG() + neighbor.getH());

                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    } else {
                        //当邻居节点已在开启队列中但当前路径更优时，先将其从队列中移除，再重新加入，以更新其在优先队列中的排序位置。
                        open.remove(neighbor);
                        open.add(neighbor);
                    }
                }
            }
        }
        return Collections.emptyList(); // 无路径
    }

    /**
     * 启发函数，表示无障碍情况下的距离目标节点的最短距离
     * 网格地图场景=|△x|+|△y|
     *
     * @param a
     * @param b
     * @return
     */
    private static double heuristic(Node a, Node b) {
        return distance(a, b);
    }

    /**
     * 曼哈顿距离它通过计算两个节点在x轴和y轴上的坐标差值绝对值之和，来得到两点之间的距离。这是A寻路算法中常用的启发式函数，用于估算从当前节点到目标节点的代价。
     * 网格地图场景
     *
     * @param a
     * @param b
     * @return
     */
    public static double distance(Node a, Node b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * 构建规划路径
     *
     * @param node
     * @return
     */
    private static List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * 输出最优路径
     *
     * @param path
     */
    public static void printPath(List<Node> path) {
        // 输出路径
        System.out.println("最优路径：");
        for (Node node : path) {
            System.out.printf("-> (%d,%d) "+(node.getLocation()!=null?node.getLocation():""), node.getX(), node.getY());
        }
    }
}
