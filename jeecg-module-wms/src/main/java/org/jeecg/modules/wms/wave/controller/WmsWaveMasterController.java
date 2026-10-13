package org.jeecg.modules.wms.wave.controller;

import jakarta.annotation.Resource;
import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryRuleEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.wave.service.IPickingTasksService;
import org.jeecg.modules.wms.wave.vo.WmsWaveAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.HashMap;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 波次主表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Tag(name="波次主表")
@RestController
@RequestMapping("/wave/wmsWaveMaster")
@Slf4j
public class WmsWaveMasterController extends JeecgController<WmsWaveMaster, IWmsWaveMasterService> {

	@Autowired
	private IWmsWaveMasterService wmsWaveMasterService;

	@Autowired
	private IWmsOutOrdersService wmsOutOrdersService;

	 @Autowired
	private IPickingTasksService pickingTasksService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param wmsWaveMaster
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "波次主表-分页列表查询")
	@Operation(summary="波次主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsWaveMaster>> queryPageList(WmsWaveMaster wmsWaveMaster,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
//      	QueryWrapper<WmsWaveMaster> queryWrapper = QueryGenerator.initQueryWrapper(wmsWaveMaster, req.getParameterMap());
//		Page<WmsWaveMaster> page = new Page<WmsWaveMaster>(pageNo, pageSize);
//		IPage<WmsWaveMaster> pageList = wmsWaveMasterService.page(page, queryWrapper);


		IPage<WmsWaveMaster> wmsWaveMasterIPage = wmsWaveMasterService.queryList(wmsWaveMaster, pageNo, pageSize);
		return Result.OK(wmsWaveMasterIPage);
	}

	/**
     *   添加
     * @param wmsWaveMaster
     * @return
     */
    @AutoLog(value = "波次主表-添加")
    @Operation(summary="波次主表-添加")
    @RequiresPermissions("wave:wms_wave_master:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WmsWaveMaster wmsWaveMaster) {
        wmsWaveMasterService.save(wmsWaveMaster);
        return Result.OK("添加成功！");
    }

	 /**
	  * 创建波次
	  */
	 @AutoLog(value = "波次主表-创建波次")
	 @Operation(summary="波次主表-创建波次")
	 @PostMapping(value = "/createWave")
	 public Result<String> createWave(@RequestBody WmsWaveAdd wmsWaveAdd) {
		 //获取波次策略
		 String strategyCodes = wmsWaveAdd.getStrategyCodes();
		 //仓库
		 String warehouseId = wmsWaveAdd.getWarehouseId();
		 if (oConvertUtils.isEmpty(strategyCodes)) {
			 return Result.error("请选择波次策略！");
		 }
		 String[] strategyCodesArr = strategyCodes.split(",");
		 //创建波次
		 wmsWaveMasterService.createWave(warehouseId,Arrays.asList(strategyCodesArr));
		 return Result.OK("创建成功！");
    }



    /**
     *  编辑
     * @param wmsWaveMaster
     * @return
     */
    @AutoLog(value = "波次主表-编辑")
    @Operation(summary="波次主表-编辑")
    @RequiresPermissions("wave:wms_wave_master:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody WmsWaveMaster wmsWaveMaster) {
        wmsWaveMasterService.updateById(wmsWaveMaster);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "波次主表-通过id删除")
    @Operation(summary="波次主表-通过id删除")
    @RequiresPermissions("wave:wms_wave_master:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        wmsWaveMasterService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "波次主表-批量删除")
    @Operation(summary="波次主表-批量删除")
    @RequiresPermissions("wave:wms_wave_master:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.wmsWaveMasterService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequiresPermissions("wave:wms_wave_master:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsWaveMaster wmsWaveMaster) {
        return super.exportXls(request, wmsWaveMaster, WmsWaveMaster.class, "波次主表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("wave:wms_wave_master:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsWaveMaster.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/


    /*--------------------------------子表处理-出库单-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "出库单-通过主表ID查询")
	@Operation(summary="出库单-通过主表ID查询")
	@GetMapping(value = "/listWmsOutOrdersByMainId")
    public Result<IPage<WmsOutOrders>> listWmsOutOrdersByMainId(WmsOutOrders wmsOutOrders,
																@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
																HttpServletRequest req) {
        QueryWrapper<WmsOutOrders> queryWrapper = QueryGenerator.initQueryWrapper(wmsOutOrders, req.getParameterMap());
        Page<WmsOutOrders> page = new Page<WmsOutOrders>(pageNo, pageSize);
        IPage<WmsOutOrders> pageList = wmsOutOrdersService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

//	/**
//	 * 添加
//	 * @param wmsOutOrders
//	 * @return
//	 */
//	@AutoLog(value = "出库单-添加")
//	@Operation(summary="出库单-添加")
//	@PostMapping(value = "/addWmsOutOrders")
//	public Result<String> addWmsOutOrders(@RequestBody WmsOutOrders wmsOutOrders) {
//		wmsOutOrdersService.save(wmsOutOrders);
//		return Result.OK("添加成功！");
//	}
//
//    /**
//	 * 编辑
//	 * @param wmsOutOrders
//	 * @return
//	 */
//	@AutoLog(value = "出库单-编辑")
//	@Operation(summary="出库单-编辑")
//	@RequestMapping(value = "/editWmsOutOrders", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> editWmsOutOrders(@RequestBody WmsOutOrders wmsOutOrders) {
//		wmsOutOrdersService.updateById(wmsOutOrders);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 * 通过id删除
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "出库单-通过id删除")
//	@Operation(summary="出库单-通过id删除")
//	@DeleteMapping(value = "/deleteWmsOutOrders")
//	public Result<String> deleteWmsOutOrders(@RequestParam(name="id",required=true) String id) {
//		wmsOutOrdersService.removeById(id);
//		return Result.OK("删除成功!");
//	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "出库单-批量删除")
	@Operation(summary="出库单-批量删除")
	@DeleteMapping(value = "/deleteBatchWmsOutOrders")
	public Result<String> deleteBatchWmsOutOrders(@RequestParam(name="ids",required=true) String ids) {
	    this.wmsOutOrdersService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportWmsOutOrders")
    public ModelAndView exportWmsOutOrders(HttpServletRequest request, WmsOutOrders wmsOutOrders) {
		 // Step.1 组装查询条件
		 QueryWrapper<WmsOutOrders> queryWrapper = QueryGenerator.initQueryWrapper(wmsOutOrders, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<WmsOutOrders> pageList = wmsOutOrdersService.list(queryWrapper);
		 List<WmsOutOrders> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "出库单");
		 mv.addObject(NormalExcelConstants.CLASS, WmsOutOrders.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库单报表", "导出人:" + sysUser.getRealname(), "出库单"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importWmsOutOrders/{mainId}")
    public Result<?> importWmsOutOrders(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
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
				 List<WmsOutOrders> list = ExcelImportUtil.importExcel(file.getInputStream(), WmsOutOrders.class, params);
				 for (WmsOutOrders temp : list) {
                    temp.setWaveId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 wmsOutOrdersService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-出库单-end----------------------------------------------*/




}
