package org.jeecg.modules.wms.analysis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankItemVO {
    /** 店铺/货主名 */
    private String name;
    /** 出库总量 */
    private Integer total;
}
