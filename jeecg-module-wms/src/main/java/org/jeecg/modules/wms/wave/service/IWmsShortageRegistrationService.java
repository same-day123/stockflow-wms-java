package org.jeecg.modules.wms.wave.service;

import org.jeecg.modules.wms.wave.entity.WmsShortageRegistration;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 缺货登记表
 * @Author: jeecg-boot
 * @Date:   2025-08-09
 * @Version: V1.0
 */
public interface IWmsShortageRegistrationService extends IService<WmsShortageRegistration> {

    /**
     * 添加缺货登记
     */
    void add(WmsShortageRegistration wmsShortageRegistration);
}
