package org.jeecg.modules.wms.wmstask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 任务执行记录表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
public interface IWmsTasksRecordsService extends IService<WmsTasksRecords> {
    /**
     * 收货记录查询
     */
    public IPage<WmsTasksRecords> pageList(WmsTasksRecords wmsTasksRecords,
                                           Integer pageNo,
                                           Integer pageSize);
}
