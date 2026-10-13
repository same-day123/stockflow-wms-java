package org.jeecg.modules.wms.analysis.vo;

import lombok.Data;

import java.util.List;

@Data
public class TrendDataVO {
    private List<TrendPoint> inbound;
    private List<TrendPoint> outbound;
}
