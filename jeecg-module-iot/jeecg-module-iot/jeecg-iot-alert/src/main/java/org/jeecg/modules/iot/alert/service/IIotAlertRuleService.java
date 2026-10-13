package org.jeecg.modules.iot.alert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.alert.entity.IotAlertRule;

import java.util.List;

/**
 * @Description: 报警规则表
 * @Author: jeecg-boot
 * @Date: 2025-05-03
 * @Version: V1.0
 */
public interface IIotAlertRuleService extends IService<IotAlertRule> {

    /**
     * 从缓存中查询报警规则配置
     * 如果未找到，请求DB并缓存
     *
     * @param deviceCode
     * @return
     */
    List<IotAlertRule> queryAlertRules(String deviceCode);
}
