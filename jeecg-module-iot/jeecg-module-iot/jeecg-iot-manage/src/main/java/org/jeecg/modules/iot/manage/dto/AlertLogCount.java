package org.jeecg.modules.iot.manage.dto;

import lombok.Data;

/**
 * @author muht
 * @create 2025/5/9 16:22
 */
@Data
public class AlertLogCount {
    /**
     * 处理进度
     */
    private String processStatus;
    /**
     * 数量
     */
    private Integer count;
}
