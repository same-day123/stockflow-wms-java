package org.jeecg.modules.wms.inorder.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.vo.WmsStockInOrdersPage;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Tag(name="入库单主表")
@RestController
@RequestMapping("/inorder/wmsStockInOrders")
@Slf4j
public class WmsStockInOrdersController {
	@Autowired
	private IWmsStockInOrdersService wmsStockInOrdersService;
	@Autowired
	private IWmsStockInOrderItemsService wmsStockInOrderItemsService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsStockInOrders
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "入库单主表-分页列表查询")
	@Operation(summary="入库单主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsStockInOrders>> queryPageList(WmsStockInOrders wmsStockInOrders,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsStockInOrders> queryWrapper = QueryGenerator.initQueryWrapper(wmsStockInOrders, req.getParameterMap());
		Page<WmsStockInOrders> page = new Page<WmsStockInOrders>(pageNo, pageSize);
		IPage<WmsStockInOrders> pageList = wmsStockInOrdersService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsStockInOrdersPage
	 * @return
	 */
	@AutoLog(value = "入库单主表-添加")
	@Operation(summary="入库单主表-添加")
    @RequiresPermissions("inorder:wms_stock_in_orders:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsStockInOrdersPage wmsStockInOrdersPage) {
		WmsStockInOrders wmsStockInOrders = new WmsStockInOrders();
		BeanUtils.copyProperties(wmsStockInOrdersPage, wmsStockInOrders);
		wmsStockInOrdersService.add(wmsStockInOrders);
//		wmsStockInOrdersService.saveMain(wmsStockInOrders, wmsStockInOrdersPage.getWmsStockInOrderItemsList());
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsStockInOrdersPage
	 * @return
	 */
	@AutoLog(value = "入库单主表-编辑")
	@Operation(summary="入库单主表-编辑")
    @RequiresPermissions("inorder:wms_stock_in_orders:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsStockInOrdersPage wmsStockInOrdersPage) {
		WmsStockInOrders wmsStockInOrders = new WmsStockInOrders();
		BeanUtils.copyProperties(wmsStockInOrdersPage, wmsStockInOrders);
		WmsStockInOrders wmsStockInOrdersEntity = wmsStockInOrdersService.getById(wmsStockInOrders.getId());
		if(wmsStockInOrdersEntity==null) {
			return Result.error("未找到对应数据");
		}
		wmsStockInOrdersService.updateMain(wmsStockInOrders, wmsStockInOrdersPage.getWmsStockInOrderItemsList());
		return Result.OK("编辑成功!");
	}
	 /**
	  * 提交审核
	  */
	 @AutoLog(value = "入库单主表-提交审核")
	 @Operation(summary="入库单主表-提交审核")
//	 @RequiresPermissions("inorder:wms_stock_in_orders:submitaudit")
	 @PostMapping(value = "/submitAudit")
	 public Result<String> submit(@RequestBody WmsStockInOrdersPage wmsStockInOrdersPage) {
		 wmsStockInOrdersService.submitAudit(wmsStockInOrdersPage);
		 return Result.OK("提交成功！");
	 }
	 /**
	  *  审核
	  *
	  * @param wmsStockInOrdersPage
	  * @return
	  */
	 @AutoLog(value = "入库单主表-审核")
	 @Operation(summary="入库单主表-审核")
	 @RequiresPermissions("inorder:wms_stock_in_orders:audit")
	 @RequestMapping(value = "/audit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> audit(@RequestBody WmsStockInOrdersPage wmsStockInOrdersPage) {
		 wmsStockInOrdersService.audit(wmsStockInOrdersPage);
		 return Result.OK("审核完成!");
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "入库单主表-通过id删除")
	@Operation(summary="入库单主表-通过id删除")
    @RequiresPermissions("inorder:wms_stock_in_orders:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsStockInOrdersService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "入库单主表-批量删除")
	@Operation(summary="入库单主表-批量删除")
    @RequiresPermissions("inorder:wms_stock_in_orders:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsStockInOrdersService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "入库单主表-通过id查询")
	@Operation(summary="入库单主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsStockInOrders> queryById(@RequestParam(name="id",required=true) String id) {
		WmsStockInOrders wmsStockInOrders = wmsStockInOrdersService.getById(id);
		if(wmsStockInOrders==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsStockInOrders);

	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "入库单明细通过主表ID查询")
	@Operation(summary="入库单明细主表ID查询")
	@GetMapping(value = "/queryWmsStockInOrderItemsByMainId")
	public Result<List<WmsStockInOrderItems>> queryWmsStockInOrderItemsListByMainId(@RequestParam(name="id",required=true) String id) {
		List<WmsStockInOrderItems> wmsStockInOrderItemsList = wmsStockInOrderItemsService.selectByMainId(id);
		return Result.OK(wmsStockInOrderItemsList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsStockInOrders
    */
    @RequiresPermissions("inorder:wms_stock_in_orders:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsStockInOrders wmsStockInOrders) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<WmsStockInOrders> queryWrapper = QueryGenerator.initQueryWrapper(wmsStockInOrders, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<WmsStockInOrders> wmsStockInOrdersList = wmsStockInOrdersService.list(queryWrapper);

      // Step.3 组装pageList
      List<WmsStockInOrdersPage> pageList = new ArrayList<WmsStockInOrdersPage>();
      for (WmsStockInOrders main : wmsStockInOrdersList) {
          WmsStockInOrdersPage vo = new WmsStockInOrdersPage();
          BeanUtils.copyProperties(main, vo);
          List<WmsStockInOrderItems> wmsStockInOrderItemsList = wmsStockInOrderItemsService.selectByMainId(main.getId());
          vo.setWmsStockInOrderItemsList(wmsStockInOrderItemsList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "入库单主表列表");
      mv.addObject(NormalExcelConstants.CLASS, WmsStockInOrdersPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库单主表数据", "导出人:"+sysUser.getRealname(), "入库单主表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("inorder:wms_stock_in_orders:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<WmsStockInOrdersPage> list = ExcelImportUtil.importExcel(file.getInputStream(), WmsStockInOrdersPage.class, params);
              for (WmsStockInOrdersPage page : list) {
                  WmsStockInOrders po = new WmsStockInOrders();
                  BeanUtils.copyProperties(page, po);
                  wmsStockInOrdersService.saveMain(po, page.getWmsStockInOrderItemsList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

}
