package org.jeecg.modules.wms.pickroute.dto;

/**
 * 网格类型常量
 *
 * @author muht
 * @date 2025/8/10 09:38
 */
public interface GridType {
    /**
     * 货架/障碍物
     */
    int SHELF = 0;
    /**
     * 通道
     */
    int AISLE = 1;
    /**
     * 拣货点
     */
    int POINT = 2;
    /**
     * 工作站
     */
    int STATN = 3;
}
