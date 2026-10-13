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
import org.jeecg.modules.wms.goods.entity.WmsPackagingMaterial;
import org.jeecg.modules.wms.goods.service.IWmsPackagingMaterialService;

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
 * @Description: 包材表
 * @Author: jeecg-boot
 * @Date:   2025-06-27
 * @Version: V1.0
 */
@Tag(name="包材表")
@RestController
@RequestMapping("/goods/wmsPackagingMaterial")
@Slf4j
public class WmsPackagingMaterialController extends JeecgController<WmsPackagingMaterial, IWmsPackagingMaterialService> {
	@Autowired
	private IWmsPackagingMaterialService wmsPackagingMaterialService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wmsPackagingMaterial
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "包材表-分页列表查询")
	@Operation(summary="包材表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsPackagingMaterial>> queryPageList(WmsPackagingMaterial wmsPackagingMaterial,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsPackagingMaterial> queryWrapper = QueryGenerator.initQueryWrapper(wmsPackagingMaterial, req.getParameterMap());
		Page<WmsPackagingMaterial> page = new Page<WmsPackagingMaterial>(pageNo, pageSize);
		IPage<WmsPackagingMaterial> pageList = wmsPackagingMaterialService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param wmsPackagingMaterial
	 * @return
	 */
	@AutoLog(value = "包材表-添加")
	@Operation(summary="包材表-添加")
	@RequiresPermissions("goods:wms_packaging_material:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsPackagingMaterial wmsPackagingMaterial) {
		wmsPackagingMaterialService.save(wmsPackagingMaterial);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wmsPackagingMaterial
	 * @return
	 */
	@AutoLog(value = "包材表-编辑")
	@Operation(summary="包材表-编辑")
	@RequiresPermissions("goods:wms_packaging_material:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsPackagingMaterial wmsPackagingMaterial) {
		wmsPackagingMaterialService.updateById(wmsPackagingMaterial);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "包材表-通过id删除")
	@Operation(summary="包材表-通过id删除")
	@RequiresPermissions("goods:wms_packaging_material:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsPackagingMaterialService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "包材表-批量删除")
	@Operation(summary="包材表-批量删除")
	@RequiresPermissions("goods:wms_packaging_material:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsPackagingMaterialService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "包材表-通过id查询")
	@Operation(summary="包材表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsPackagingMaterial> queryById(@RequestParam(name="id",required=true) String id) {
		WmsPackagingMaterial wmsPackagingMaterial = wmsPackagingMaterialService.getById(id);
		if(wmsPackagingMaterial==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsPackagingMaterial);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsPackagingMaterial
    */
    @RequiresPermissions("goods:wms_packaging_material:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsPackagingMaterial wmsPackagingMaterial) {
        return super.exportXls(request, wmsPackagingMaterial, WmsPackagingMaterial.class, "包材表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("goods:wms_packaging_material:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsPackagingMaterial.class);
    }

}
