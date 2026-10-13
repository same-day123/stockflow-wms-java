package org.jeecg.modules.iot.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.iot.manage.dto.dashboard.ProductRatio;
import org.jeecg.modules.iot.manage.entity.IotProduct;

import java.util.List;

/**
 * @Description: 设备产品表
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
public interface IotProductMapper extends BaseMapper<IotProduct> {

    /**
     * 查询产品分布
     *
     * @return
     */
    List<ProductRatio> selectProductRatio( @Param("warehouseCodeList") List<String> warehouseCodeList);

}
