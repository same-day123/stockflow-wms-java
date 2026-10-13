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
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.iot.manage.entity.IotAlertRule;
import org.jeecg.modules.iot.manage.service.IIotAlertRuleService;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 报警规则表
 * @Author: jeecg-boot
 * @Date:   2025-05-03
 * @Version: V1.0
 */
@Tag(name="报警规则管理")
@RestController
@RequestMapping("/iot/manage/alertRule")
@Slf4j
public class IotAlertRuleController extends JeecgController<IotAlertRule, IIotAlertRuleService> {
	@Autowired
	private IIotAlertRuleService iotAlertRuleService;

	 @Autowired
	 private IWarehouseService warehouseService;

	/**
	 * 分页列表查询
	 *
	 * @param iotAlertRule
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "报警规则表-分页列表查询")
	@Operation(summary="报警规则表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IotAlertRule>> queryPageList(IotAlertRule iotAlertRule,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<IotAlertRule> queryWrapper = QueryGenerator.initQueryWrapper(iotAlertRule, req.getParameterMap());
		List<String> warehouseCodes = warehouseService.getWarehouseCodes(iotAlertRule.getWarehouseCodes());
		Page<IotAlertRule> page = new Page<IotAlertRule>(pageNo, pageSize);
		IPage<IotAlertRule> pageList = iotAlertRuleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param iotAlertRule
	 * @return
	 */
	@AutoLog(value = "报警规则表-添加")
	@Operation(summary="报警规则表-添加")
	// @RequiresPermissions("manage:wms_iot_alert_rule:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IotAlertRule iotAlertRule) {
		iotAlertRuleService.save(iotAlertRule);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param iotAlertRule
	 * @return
	 */
	@AutoLog(value = "报警规则表-编辑")
	@Operation(summary="报警规则表-编辑")
	// @RequiresPermissions("manage:wms_iot_alert_rule:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IotAlertRule iotAlertRule) {
		iotAlertRuleService.updateById(iotAlertRule);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "报警规则表-通过id删除")
	@Operation(summary="报警规则表-通过id删除")
	// @RequiresPermissions("manage:wms_iot_alert_rule:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		iotAlertRuleService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "报警规则表-批量删除")
	@Operation(summary="报警规则表-批量删除")
	// @RequiresPermissions("manage:wms_iot_alert_rule:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.iotAlertRuleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报警规则表-通过id查询")
	@Operation(summary="报警规则表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IotAlertRule> queryById(@RequestParam(name="id",required=true) String id) {
		IotAlertRule iotAlertRule = iotAlertRuleService.getById(id);
		if(iotAlertRule==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(iotAlertRule);
	}

    /**
    * 导出 excel
    *
    * @param request
    * @param iotAlertRule
    */
    // @RequiresPermissions("manage:wms_iot_alert_rule:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IotAlertRule iotAlertRule) {
        return super.exportXls(request, iotAlertRule, IotAlertRule.class, "报警规则表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    // @RequiresPermissions("manage:wms_iot_alert_rule:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IotAlertRule.class);
    }

}
