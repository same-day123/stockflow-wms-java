package org.jeecg.modules.wms.goods.controller;

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
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;

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
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
@Tag(name="货主表")
@RestController
@RequestMapping("/goods/wmsCargoOwners")
@Slf4j
public class WmsCargoOwnersController extends JeecgController<WmsCargoOwners, IWmsCargoOwnersService> {
	@Autowired
	private IWmsCargoOwnersService wmsCargoOwnersService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsCargoOwners
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "货主表-分页列表查询")
	@Operation(summary="货主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsCargoOwners>> queryPageList(WmsCargoOwners wmsCargoOwners,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsCargoOwners> queryWrapper = QueryGenerator.initQueryWrapper(wmsCargoOwners, req.getParameterMap());
		Page<WmsCargoOwners> page = new Page<WmsCargoOwners>(pageNo, pageSize);
		IPage<WmsCargoOwners> pageList = wmsCargoOwnersService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsCargoOwners
	 * @return
	 */
	@AutoLog(value = "货主表-添加")
	@Operation(summary="货主表-添加")
	@RequiresPermissions("goods:wms_cargo_owners:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsCargoOwners wmsCargoOwners) {
		wmsCargoOwnersService.add(wmsCargoOwners);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsCargoOwners
	 * @return
	 */
	@AutoLog(value = "货主表-编辑")
	@Operation(summary="货主表-编辑")
	@RequiresPermissions("goods:wms_cargo_owners:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsCargoOwners wmsCargoOwners) {
		wmsCargoOwnersService.edit(wmsCargoOwners);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "货主表-通过id删除")
	@Operation(summary="货主表-通过id删除")
	@RequiresPermissions("goods:wms_cargo_owners:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsCargoOwnersService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "货主表-批量删除")
	@Operation(summary="货主表-批量删除")
	@RequiresPermissions("goods:wms_cargo_owners:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsCargoOwnersService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "货主表-通过id查询")
	@Operation(summary="货主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsCargoOwners> queryById(@RequestParam(name="id",required=true) String id) {
		WmsCargoOwners wmsCargoOwners = wmsCargoOwnersService.getById(id);
		if(wmsCargoOwners==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsCargoOwners);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsCargoOwners
    */
    @RequiresPermissions("goods:wms_cargo_owners:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsCargoOwners wmsCargoOwners) {
        return super.exportXls(request, wmsCargoOwners, WmsCargoOwners.class, "货主表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("goods:wms_cargo_owners:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsCargoOwners.class);
    }

}
