package org.jeecg.modules.iot.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.iot.manage.entity.IotMonitorConfig;
import org.jeecg.modules.iot.manage.service.IIotMonitorConfigService;
import org.jeecg.modules.iot.manage.vo.MonitorChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

/**
 * @Description: 监控配置表
 * @Author: jeecg-boot
 * @Date: 2025-05-01
 * @Version: V1.0
 */
@Tag(name = "监控配置")
@RestController
@RequestMapping("/iot/manage/monitor")
@Slf4j
public class IotMonitorController extends JeecgController<IotMonitorConfig, IIotMonitorConfigService> {
    @Autowired
    private IIotMonitorConfigService iotMonitorConfigService;

    /**
     * 分页列表查询
     *
     * @param iotMonitorConfig
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "监控配置表-分页列表查询")
    @Operation(summary = "监控配置表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<MonitorChart>> queryPageList(IotMonitorConfig iotMonitorConfig,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        QueryWrapper<IotMonitorConfig> queryWrapper = QueryGenerator.initQueryWrapper(iotMonitorConfig, req.getParameterMap());
        Page<IotMonitorConfig> page = new Page<>(pageNo, pageSize);
        IPage<MonitorChart> monitorChartPage = iotMonitorConfigService.queryMonitorPoint(iotMonitorConfig, page, queryWrapper);
        return Result.OK(monitorChartPage);
    }

    /**
     * 添加
     *
     * @param iotMonitorConfig
     * @return
     */
    @AutoLog(value = "监控配置表-添加")
    @Operation(summary = "监控配置表-添加")
    // @RequiresPermissions("manage:wms_iot_monitor_config:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody IotMonitorConfig iotMonitorConfig) {
        iotMonitorConfigService.save(iotMonitorConfig);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param iotMonitorConfig
     * @return
     */
    @AutoLog(value = "监控配置表-编辑")
    @Operation(summary = "监控配置表-编辑")
    // @RequiresPermissions("manage:wms_iot_monitor_config:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody IotMonitorConfig iotMonitorConfig) {
        iotMonitorConfigService.updateById(iotMonitorConfig);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "监控配置表-通过id删除")
    @Operation(summary = "监控配置表-通过id删除")
    // @RequiresPermissions("manage:wms_iot_monitor_config:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        iotMonitorConfigService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "监控配置表-批量删除")
    @Operation(summary = "监控配置表-批量删除")
    // @RequiresPermissions("manage:wms_iot_monitor_config:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.iotMonitorConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "监控配置表-通过id查询")
    @Operation(summary = "监控配置表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<IotMonitorConfig> queryById(@RequestParam(name = "id", required = true) String id) {
        IotMonitorConfig iotMonitorConfig = iotMonitorConfigService.getById(id);
        if (iotMonitorConfig == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(iotMonitorConfig);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param iotMonitorConfig
     */
    @RequiresPermissions("manage:wms_iot_monitor_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IotMonitorConfig iotMonitorConfig) {
        return super.exportXls(request, iotMonitorConfig, IotMonitorConfig.class, "监控配置表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("manage:wms_iot_monitor_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IotMonitorConfig.class);
    }

}
