package org.jeecg.modules.iot.manage.service;

import java.util.List;

/**
 * 仓库服务
 */
public interface IWarehouseService {
    /**
     * 获取仓库编码列表
     *
     * @param specifiedWarehouseCodes 指定的仓库编码
     * @return
     */
    List<String> getWarehouseCodes(String specifiedWarehouseCodes);
}
