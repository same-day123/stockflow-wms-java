package org.jeecg.modules.wms.wave.service;

import org.jeecg.modules.wms.wave.entity.WmsWaveSkuSummary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 波次拣货明细表
 * @Author: jeecg-boot
 * @Date:   2025-06-05
 * @Version: V1.0
 */
public interface IWmsWaveSkuSummaryService extends IService<WmsWaveSkuSummary> {
    /**
     * 根据波次id查询
     */
    public List<WmsWaveSkuSummary> selectByWaveId(String waveId);

    /**
     * 更新波次下拣货明细表的状态为已拣货
     */
    public void updatePickedStatus(String waveId);
}
