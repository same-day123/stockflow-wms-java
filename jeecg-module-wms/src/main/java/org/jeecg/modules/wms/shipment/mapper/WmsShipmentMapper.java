package org.jeecg.modules.wms.shipment.mapper;

import org.jeecg.modules.wms.shipment.entity.WmsShipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 包裹主表
 * @Author: jeecg-boot
 * @Date:   2025-06-13
 * @Version: V1.0
 */
public interface WmsShipmentMapper extends BaseMapper<WmsShipment> {
    /**
     * 综合查询包裹信息
     */
    List<WmsShipment> queryShipmentList(WmsShipment wmsShipment);
}
