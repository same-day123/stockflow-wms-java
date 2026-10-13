//package org.jeecg.modules.wms.inventory.controller;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.common.system.query.QueryGenerator;
//import org.jeecg.common.system.query.QueryRuleEnum;
//import org.jeecg.common.util.oConvertUtils;
//import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
//import org.jeecg.modules.wms.inventory.service.IWmsInventoryTransService;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import lombok.extern.slf4j.Slf4j;
//
//import org.jeecgframework.poi.excel.ExcelImportUtil;
//import org.jeecgframework.poi.excel.def.NormalExcelConstants;
//import org.jeecgframework.poi.excel.entity.ExportParams;
//import org.jeecgframework.poi.excel.entity.ImportParams;
//import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
//import org.jeecg.common.system.base.controller.JeecgController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.servlet.ModelAndView;
//import com.alibaba.fastjson.JSON;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.Operation;
//import org.jeecg.common.aspect.annotation.AutoLog;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//
// /**
// * @Description: 库存变更表
// * @Author: jeecg-boot
// * @Date:   2025-08-30
// * @Version: V1.0
// */
//@Tag(name="库存变更表")
//@RestController
//@RequestMapping("/inventory/wmsInventoryTrans")
//@Slf4j
//public class WmsInventoryTransController extends JeecgController<WmsInventoryTrans, IWmsInventoryTransService> {
//	@Autowired
//	private IWmsInventoryTransService wmsInventoryTransService;
//
//	/**
//	 * 分页列表查询
//	 *
//	 * @param wmsInventoryTrans
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	//@AutoLog(value = "库存变更表-分页列表查询")
//	@Operation(summary="库存变更表-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<WmsInventoryTrans>> queryPageList(WmsInventoryTrans wmsInventoryTrans,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//        QueryWrapper<WmsInventoryTrans> queryWrapper = QueryGenerator.initQueryWrapper(wmsInventoryTrans, req.getParameterMap());
//		Page<WmsInventoryTrans> page = new Page<WmsInventoryTrans>(pageNo, pageSize);
//		IPage<WmsInventoryTrans> pageList = wmsInventoryTransService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}
//
//	/**
//	 *   添加
//	 *
//	 * @param wmsInventoryTrans
//	 * @return
//	 */
//	@AutoLog(value = "库存变更表-添加")
//	@Operation(summary="库存变更表-添加")
//	@RequiresPermissions("inventory:wms_inventory_trans:add")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody WmsInventoryTrans wmsInventoryTrans) {
//		wmsInventoryTransService.save(wmsInventoryTrans);
//		return Result.OK("添加成功！");
//	}
//
//	/**
//	 *  编辑
//	 *
//	 * @param wmsInventoryTrans
//	 * @return
//	 */
//	@AutoLog(value = "库存变更表-编辑")
//	@Operation(summary="库存变更表-编辑")
//	@RequiresPermissions("inventory:wms_inventory_trans:edit")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody WmsInventoryTrans wmsInventoryTrans) {
//		wmsInventoryTransService.updateById(wmsInventoryTrans);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "库存变更表-通过id删除")
//	@Operation(summary="库存变更表-通过id删除")
//	@RequiresPermissions("inventory:wms_inventory_trans:delete")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		wmsInventoryTransService.removeById(id);
//		return Result.OK("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "库存变更表-批量删除")
//	@Operation(summary="库存变更表-批量删除")
//	@RequiresPermissions("inventory:wms_inventory_trans:deleteBatch")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.wmsInventoryTransService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "库存变更表-通过id查询")
//	@Operation(summary="库存变更表-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<WmsInventoryTrans> queryById(@RequestParam(name="id",required=true) String id) {
//		WmsInventoryTrans wmsInventoryTrans = wmsInventoryTransService.getById(id);
//		if(wmsInventoryTrans==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(wmsInventoryTrans);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param wmsInventoryTrans
//    */
//    @RequiresPermissions("inventory:wms_inventory_trans:exportXls")
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, WmsInventoryTrans wmsInventoryTrans) {
//        return super.exportXls(request, wmsInventoryTrans, WmsInventoryTrans.class, "库存变更表");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequiresPermissions("inventory:wms_inventory_trans:importExcel")
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, WmsInventoryTrans.class);
//    }
//
//}
