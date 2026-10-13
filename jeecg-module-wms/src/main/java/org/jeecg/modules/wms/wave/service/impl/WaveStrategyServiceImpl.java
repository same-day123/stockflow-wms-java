package org.jeecg.modules.wms.wave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.wave.entity.WmsWaveStrategy;
import org.jeecg.modules.wms.wave.mapper.WaveStrategyMapper;
import org.jeecg.modules.wms.wave.service.IWaveStrategyService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 波次策略表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Service
public class WaveStrategyServiceImpl extends ServiceImpl<WaveStrategyMapper, WmsWaveStrategy> implements IWaveStrategyService {

    @Override
    public WmsWaveStrategy getStrategyByCode(String strategyCode) {

        LambdaQueryWrapper<WmsWaveStrategy> eq = new LambdaQueryWrapper<WmsWaveStrategy>()
                .eq(WmsWaveStrategy::getStrategyCode, strategyCode);
        return getOne(eq);
    }


}
