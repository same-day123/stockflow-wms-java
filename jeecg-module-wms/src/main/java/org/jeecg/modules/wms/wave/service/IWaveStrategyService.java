package org.jeecg.modules.wms.wave.service;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.wave.entity.WmsWaveStrategy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 波次策略表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
public interface IWaveStrategyService extends IService<WmsWaveStrategy> {

    /**
     * 根据策略编码获取策略
     * @param strategyCode
     */
    WmsWaveStrategy getStrategyByCode(String strategyCode);
}
