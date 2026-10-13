package org.jeecg.modules.wms.waybill.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.wms.shipment.service.IWmsShipmentService;
import org.jeecg.modules.wms.shipment.vo.PrintWaybillResult;
import org.jeecg.modules.wms.waybill.sf.SfExpressUtil;
import org.jeecg.modules.wms.waybill.service.IWmsWaybillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
* @Description: 运单接口类
* @Author: jeecg-boot
* @Date:   2025-06-18
* @Version: V1.0
*/
@Tag(name="运单接口类")
@RestController
@RequestMapping("/shipment/waybill")
@Slf4j
public class WmsWaybillController {
   @Autowired
   private IWmsShipmentService wmsShipmentService;
   @Autowired
   private IWmsWaybillService wmsWaybillService;


    /**
     * 根据波次打印电子面单
     * @param waveId 波次id
     *
     */
    @AutoLog(value = "波次-打印电子面单")
    @Operation(summary="波次-打印电子面单")
    @GetMapping (value = "/printWaybill")
    public Result<PrintWaybillResult> printWaybill(@RequestParam(name="waveId",required=true) String waveId) throws UnsupportedEncodingException {
        //查询波次下所有出库单的运单号
//        List<String> strings = wmsWaybillService.selectWaybillNosByWaveId(waveId);
        //请求顺丰拿token
//        String token = SfExpressUtil.getToken();
        PrintWaybillResult printWaybillResult = new PrintWaybillResult();
//        printWaybillResult.setToken(token);
//        printWaybillResult.setWaybillNos(strings);

        //从数据库拿运单号
        return Result.OK(printWaybillResult);
    }
    /**
     * 根据波次进行发货
     */
    @AutoLog(value = "波次-发货")
    @Operation(summary="波次-发货")
    @GetMapping(value = "/send")
    public Result<String> send(@RequestParam(name="waveId",required=true) String waveId) {
//        wmsWaybillService.send(waveId);
        return Result.OK("发货成功！");
    }



}
