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
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.iot.manage.dto.CtrlDeviceReq;
import org.jeecg.modules.iot.manage.dto.DeviceHistoryData;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.service.IIotDeviceService;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.jeecg.modules.iot.manage.vo.DeviceFieldModel;
import org.jeecg.modules.iot.manage.vo.DeviceServiceModel;
import org.jeecg.modules.iot.manage.vo.IotDeviceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/iot/manage/device")
@Slf4j
public class IotDeviceController extends JeecgController<IotDevice, IIotDeviceService> {
    @Autowired
    private IIotDeviceService iotDeviceService;

    @Autowired
    private IWarehouseService warehouseService;

    /**
     * 分页列表查询
     *
     * @param iotDevice
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "设备表-分页列表查询")
    @Operation(summary = "设备表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<IotDevice>> queryPageList(IotDevice iotDevice,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<IotDevice> queryWrapper = QueryGenerator.initQueryWrapper(iotDevice, req.getParameterMap());
        List<String> warehouseCodes = warehouseService.getWarehouseCodes(iotDevice.getWarehouseCodes());
        if (StringUtils.hasText(iotDevice.getDeviceName())) {
            queryWrapper.like("device_name", iotDevice.getDeviceName());
        }
        Page<IotDevice> page = new Page<>(pageNo, pageSize);
        IPage<IotDevice> pageList = iotDeviceService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param iotDevice
     * @return
     */
    @AutoLog(value = "设备表-添加")
    @Operation(summary = "设备表-添加")
//	@RequiresPermissions("manage:wms_iot_device:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody IotDevice iotDevice) {
        iotDeviceService.save(iotDevice);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param iotDevice
     * @return
     */
    @AutoLog(value = "设备表-编辑")
    @Operation(summary = "设备表-编辑")
//	@RequiresPermissions("manage:wms_iot_device:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody IotDevice iotDevice) {
        iotDeviceService.updateById(iotDevice);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "设备表-通过id删除")
    @Operation(summary = "设备表-通过id删除")
//    @RequiresPermissions("manage:wms_iot_device:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        iotDeviceService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "设备表-批量删除")
    @Operation(summary = "设备表-批量删除")
    @RequiresPermissions("manage:wms_iot_device:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.iotDeviceService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "设备表-通过id查询")
    @Operation(summary = "设备表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<IotDeviceDetail> queryById(@RequestParam(name = "id", required = true) String id) {
        IotDeviceDetail iotDevice = iotDeviceService.getDetailById(id);
        if (iotDevice == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(iotDevice);
    }

    /**
     * 查询设备属性物模型
     *
     * @return
     */
    @Operation(summary = "查询设备属性物模型")
    @GetMapping(value = "/queryFields")
    public Result<IPage<DeviceFieldModel>> queryDeviceFields(@RequestParam(name = "id") String id,
                                                             @RequestParam(name = "name", required = false) String name,
                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<DeviceFieldModel> page = iotDeviceService.queryDeviceFieldsPage(id, name, pageNo, pageSize);
        return Result.OK(page);
    }

    /**
     * 查询设备服务物模型
     *
     * @param id
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Operation(summary = "查询设备服务物模型")
    @GetMapping(value = "/queryServices")
    public Result<IPage<DeviceServiceModel>> queryDeviceServices(@RequestParam(name = "id") String id,
                                                                 @RequestParam(name = "name", required = false) String name,
                                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<DeviceServiceModel> page = iotDeviceService.queryDeviceServicesPage(id, name, pageNo, pageSize);
        return Result.OK(page);
    }

    /**
     * 查询设备下，某指标的数据历史
     * 默认返回一周数据
     */
    @GetMapping(value = "/data/history")
    public Result<IPage<DeviceHistoryData>> queryHistoryData(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "modelCode") String modelCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<DeviceHistoryData> pageList = iotDeviceService.queryHistoryData(id, modelCode, pageNo, pageSize);
        return Result.OK(pageList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param iotDevice
     */
//    @RequiresPermissions("manage:wms_iot_device:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IotDevice iotDevice) {
        return super.exportXls(request, iotDevice, IotDevice.class, "设备表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
//    @RequiresPermissions("manage:wms_iot_device:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IotDevice.class);
    }

    /**
     * 设备控制
     *
     * @return
     */
    @PostMapping(value = "/ctrl")
    public Result<?> ctrlDevice(@RequestBody CtrlDeviceReq ctrlDeviceReq) {
        String res = iotDeviceService.ctrlDevice(ctrlDeviceReq);
        return Result.OK(res);
    }
}
