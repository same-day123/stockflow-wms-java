package org.jeecg.modules.iot.manage.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;

/**
 * @author muht
 * @create 2025/5/9 16:32
 */
@Data
public class AlertLogPageResp extends Page<IotAlertLog> {

    public AlertLogPageResp(Page<IotAlertLog> page) {
        super(page.getCurrent(), page.getSize(), page.getTotal());
        this.setRecords(page.getRecords());
        this.setPages(page.getPages());
//        this.setOrders(page.getOrders());
//        this.setMaxLimit(page.getMaxLimit());
    }
    /**
     * 全部数量
     */
    private Integer allCount = 0;
    /**
     * 未解决数量
     */
    private Integer unsolvedCount = 0;
    /**
     * 已解决数量
     */
    private Integer solvedCount = 0;
    /**
     * 不予解决数量
     */
    private Integer ignoreCount = 0;
}
