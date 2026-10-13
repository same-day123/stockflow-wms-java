package org.jeecg.modules.wms.wave.service.impl;

import org.jeecg.modules.wms.wave.entity.WmsShortageRegistration;
import org.jeecg.modules.wms.wave.entity.WmsWaveSkuSummary;
import org.jeecg.modules.wms.wave.mapper.WmsShortageRegistrationMapper;
import org.jeecg.modules.wms.wave.service.IPickingTasksService;
import org.jeecg.modules.wms.wave.service.IWmsShortageRegistrationService;
import org.jeecg.modules.wms.wave.service.IWmsWaveSkuSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 缺货登记表
 * @Author: jeecg-boot
 * @Date:   2025-08-09
 * @Version: V1.0
 */
@Service
public class WmsShortageRegistrationServiceImpl extends ServiceImpl<WmsShortageRegistrationMapper, WmsShortageRegistration> implements IWmsShortageRegistrationService {

    @Autowired
    private IPickingTasksService pickingTasksService;

    /**
     * 新增缺货登记
     * @param wmsShortageRegistration
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WmsShortageRegistration wmsShortageRegistration) {
        //保存缺货登记记录
        saveOrUpdate(wmsShortageRegistration);
        //校验拣货数量和缺货数量
        pickingTasksService.checkPickedQuantity(wmsShortageRegistration.getTaskId());
    }
}
