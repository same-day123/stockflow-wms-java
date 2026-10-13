package org.jeecg.modules.wms.pickroute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 货位（商品位置信息描述）
 * 可根据实际业务场景定制货位模型，
 * 如(A, 6, 1.2) 表示 存储区A-6号货架-1.2米高，然后再将货位与实际仓库网格建立映射关系
 * 此处不做太复杂抽象，仅直接与网格 x,y 简单对应
 *
 * @author muht
 * @date 2025/8/9 20:28
 */
@Data
@AllArgsConstructor
public class Location {
    /**
     * 储区名称
     */
    private String zoneName;

    /**
     * 行
     */
    private int line;
    /**
     * 列
     */
    private int col;
    /**
     * 储位名称
     */
    private String locationName;
}
