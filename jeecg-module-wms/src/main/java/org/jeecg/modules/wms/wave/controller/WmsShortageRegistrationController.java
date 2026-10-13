package org.jeecg.modules.wms.wave.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.wave.entity.WmsShortageRegistration;
import org.jeecg.modules.wms.wave.service.IWmsShortageRegistrationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 缺货登记表
 * @Author: jeecg-boot
 * @Date:   2025-08-09
 * @Version: V1.0
 */
@Tag(name="缺货登记表")
@RestController
@RequestMapping("/wave/wmsShortageRegistration")
@Slf4j
public class WmsShortageRegistrationController extends JeecgController<WmsShortageRegistration, IWmsShortageRegistrationService> {
	@Autowired
	private IWmsShortageRegistrationService wmsShortageRegistrationService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsShortageRegistration
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "缺货登记表-分页列表查询")
	@Operation(summary="缺货登记表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsShortageRegistration>> queryPageList(WmsShortageRegistration wmsShortageRegistration,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsShortageRegistration> queryWrapper = QueryGenerator.initQueryWrapper(wmsShortageRegistration, req.getParameterMap());
		Page<WmsShortageRegistration> page = new Page<WmsShortageRegistration>(pageNo, pageSize);
		IPage<WmsShortageRegistration> pageList = wmsShortageRegistrationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsShortageRegistration
	 * @return
	 */
	@AutoLog(value = "缺货登记表-添加")
	@Operation(summary="缺货登记表-添加")
	@RequiresPermissions("wave:wms_shortage_registration:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsShortageRegistration wmsShortageRegistration) {
		wmsShortageRegistrationService.add(wmsShortageRegistration);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsShortageRegistration
	 * @return
	 */
	@AutoLog(value = "缺货登记表-编辑")
	@Operation(summary="缺货登记表-编辑")
	@RequiresPermissions("wave:wms_shortage_registration:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsShortageRegistration wmsShortageRegistration) {
		wmsShortageRegistrationService.updateById(wmsShortageRegistration);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "缺货登记表-通过id删除")
	@Operation(summary="缺货登记表-通过id删除")
	@RequiresPermissions("wave:wms_shortage_registration:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsShortageRegistrationService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "缺货登记表-批量删除")
	@Operation(summary="缺货登记表-批量删除")
	@RequiresPermissions("wave:wms_shortage_registration:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsShortageRegistrationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "缺货登记表-通过id查询")
	@Operation(summary="缺货登记表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsShortageRegistration> queryById(@RequestParam(name="id",required=true) String id) {
		WmsShortageRegistration wmsShortageRegistration = wmsShortageRegistrationService.getById(id);
		if(wmsShortageRegistration==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsShortageRegistration);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsShortageRegistration
    */
    @RequiresPermissions("wave:wms_shortage_registration:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsShortageRegistration wmsShortageRegistration) {
        return super.exportXls(request, wmsShortageRegistration, WmsShortageRegistration.class, "缺货登记表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wave:wms_shortage_registration:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsShortageRegistration.class);
    }

}
