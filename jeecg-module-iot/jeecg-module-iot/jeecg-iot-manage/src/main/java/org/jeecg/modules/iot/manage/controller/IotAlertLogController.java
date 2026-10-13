package org.jeecg.modules.iot.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.iot.manage.dto.AlertLogPageResp;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;
import org.jeecg.modules.iot.manage.service.IIotAlertLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

/**
 * @Description: 报警日志表
 * @Author: jeecg-boot
 * @Date: 2025-05-06
 * @Version: V1.0
 */
@Tag(name = "报警日志管理")
@RestController
@RequestMapping("/iot/manage/alertLog")
@Slf4j
public class IotAlertLogController extends JeecgController<IotAlertLog, IIotAlertLogService> {
    @Autowired
    private IIotAlertLogService iotAlertLogService;

    /**
     * 分页列表查询
     *
     * @param iotAlertLog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "报警日志表-分页列表查询")
    @Operation(summary = "报警日志表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<IotAlertLog>> queryPageList(IotAlertLog iotAlertLog,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<IotAlertLog> queryWrapper = QueryGenerator.initQueryWrapper(iotAlertLog, req.getParameterMap());
        Page<IotAlertLog> page = new Page<>(pageNo, pageSize);
        AlertLogPageResp logPageResp = iotAlertLogService.pageWithProcessStatusCount(page, iotAlertLog, queryWrapper);
        return Result.OK(logPageResp);
    }

    /**
     * 编辑
     *
     * @param iotAlertLog
     * @return
     */
    @AutoLog(value = "报警日志表-编辑")
    @Operation(summary = "报警日志表-编辑")
//	@RequiresPermissions("manage:wms_iot_alert_log:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody IotAlertLog iotAlertLog) {
        iotAlertLogService.updateById(iotAlertLog);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报警日志表-通过id删除")
    @Operation(summary = "报警日志表-通过id删除")
//	@RequiresPermissions("manage:wms_iot_alert_log:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        iotAlertLogService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "报警日志表-批量删除")
    @Operation(summary = "报警日志表-批量删除")
//	@RequiresPermissions("manage:wms_iot_alert_log:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.iotAlertLogService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "报警日志表-通过id查询")
    @Operation(summary = "报警日志表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<IotAlertLog> queryById(@RequestParam(name = "id", required = true) String id) {
        IotAlertLog iotAlertLog = iotAlertLogService.getById(id);
        if (iotAlertLog == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(iotAlertLog);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param iotAlertLog
     */
//    @RequiresPermissions("manage:wms_iot_alert_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IotAlertLog iotAlertLog) {
        return super.exportXls(request, iotAlertLog, IotAlertLog.class, "报警日志表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
//    @RequiresPermissions("manage:wms_iot_alert_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IotAlertLog.class);
    }

}
