package org.jeecg.modules.wms.shipment.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 打印电子面单结果
 * @date 2025/7/16 16:32
 */
@Data
public class PrintWaybillResult {
 //运单号
 private List<String> waybillNos;
 //token
 private String token;
}
