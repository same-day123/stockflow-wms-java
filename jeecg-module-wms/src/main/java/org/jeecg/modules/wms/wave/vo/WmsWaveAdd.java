package org.jeecg.modules.wms.wave.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 波次创建参数对象
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="波次创建参数")
public class WmsWaveAdd implements Serializable {
    private static final long serialVersionUID = 1L;

    /**策略ids，中间用逗号分隔*/
    @TableField(exist = false)
    private String strategyCodes;
    /**仓库id*/
    @Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private String warehouseId;
}
