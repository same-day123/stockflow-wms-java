package org.jeecg.modules.iot.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.iot.manage.dto.dashboard.WorkbenchDashboard;
import org.jeecg.modules.iot.manage.service.IIotWorkbenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 工作台接口
 *
 * @author muht
 * @create 2025/5/13 13:57
 */
@Tag(name = "工作台")
@Slf4j
@RestController
@RequestMapping("/iot/manage/workbench")
public class IotWorkbenchController {

    @Autowired
    private IIotWorkbenchService workbenchService;

    /**
     * 工作台仪表盘
     */
    @Operation(summary = "工作台仪表盘")
    @GetMapping("/dashboard")
    public Result<WorkbenchDashboard> getDashboard(HttpServletRequest request) {
        //当前仓库 id
        String warehouseCodes = request.getHeader("X-Warehouse-Id");
        if(StringUtils.isEmpty(warehouseCodes)){
            return Result.error("请选择仓库");
        }
        WorkbenchDashboard dashboard = workbenchService.getDashboard(warehouseCodes);
        return Result.OK(dashboard);
    }

    /**
     * 导出产品分布
     */
    @GetMapping("/exportXls/productRatio")
    public ModelAndView exportXlsProductRatio(HttpServletRequest request) {
        return workbenchService.exportXlsProductRatio(request);
    }
}
