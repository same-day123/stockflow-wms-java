package org.jeecg.modules.wms.wave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.wave.entity.WmsWaveSkuSummary;
import org.jeecg.modules.wms.wave.mapper.WmsWaveSkuSummaryMapper;
import org.jeecg.modules.wms.wave.service.IWmsWaveSkuSummaryService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 波次拣货明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-05
 * @Version: V1.0
 */
@Service
public class WmsWaveSkuSummaryServiceImpl extends ServiceImpl<WmsWaveSkuSummaryMapper, WmsWaveSkuSummary> implements IWmsWaveSkuSummaryService {
    /**
     * 根据波次id查询
     */
    public List<WmsWaveSkuSummary> selectByWaveId(String waveId){
        LambdaQueryWrapper<WmsWaveSkuSummary> eq = new LambdaQueryWrapper<WmsWaveSkuSummary>()
                .eq(WmsWaveSkuSummary::getWaveId, waveId);
        return list(eq);
    }
    /**
     * 更新波次下拣货明细表的状态为已拣货
     */
    public void updatePickedStatus(String waveId){
        LambdaUpdateWrapper<WmsWaveSkuSummary> updateWrapper = new LambdaUpdateWrapper<WmsWaveSkuSummary>()
                .eq(WmsWaveSkuSummary::getWaveId, waveId)
                .eq(WmsWaveSkuSummary::getStatus, WarehouseDictEnum.WAVESKU_PICKING.getCode())
                        .set(WmsWaveSkuSummary::getStatus, WarehouseDictEnum.WAVESKU_PICKED.getCode());
        update(null, updateWrapper);
    }
}
