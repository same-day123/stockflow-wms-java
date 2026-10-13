package org.jeecg.modules.wms.outorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersAllocationService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersService;
import org.jeecg.modules.wms.outorder.vo.WmsOutOrdersPage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


 /**
 * @Description: 出库单
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
@Tag(name="出库单")
@RestController
@RequestMapping("/outorder/wmsOutOrders")
@Slf4j
public class WmsOutOrdersController {
	@Autowired
	private IWmsOutOrdersService wmsOutOrdersService;
	@Autowired
	private IWmsOutOrdersItemsService wmsOutOrdersItemsService;

	@Autowired
	private IWmsProductsService wmsProductsService;
	@Autowired
	private IWmsOutOrdersAllocationService wmsOutOrdersAllocationService;
	@Autowired
	private IWmsCargoOwnersService wmsCargoOwnersService;

	@Autowired
	private ProvinceCityArea provinceCityArea;

	/**
	 * 分页列表查询
	 *
	 * @param wmsOutOrders
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "出库单-分页列表查询")
	@Operation(summary="出库单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsOutOrders>> queryPageList(WmsOutOrders wmsOutOrders,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		IPage<WmsOutOrders> pageList = wmsOutOrdersService.queryList(wmsOutOrders, pageNo, pageSize);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsOutOrdersPage
	 * @return
	 */
	@AutoLog(value = "出库单-添加")
	@Operation(summary="出库单-添加")
    @RequiresPermissions("outorder:wms_out_orders:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsOutOrdersPage wmsOutOrdersPage) {
		WmsOutOrders wmsOutOrders = new WmsOutOrders();
		BeanUtils.copyProperties(wmsOutOrdersPage, wmsOutOrders);
		//处理省市区
		String region = wmsOutOrdersPage.getRegion();
		if(StringUtils.isNotBlank(region)){
			String[] regionArray = region.split(",");
			if(regionArray.length==3){
				wmsOutOrders.setShippingProvince(provinceCityArea.getText(regionArray[0]));
				String city = provinceCityArea.getText(regionArray[1]);//北京市/北京市
				String county = provinceCityArea.getText(regionArray[2]);//北京市/北京市/东城区
				wmsOutOrders.setShippingCity(city.substring(city.lastIndexOf("/")+1));
				wmsOutOrders.setShippingCounty(county.substring(county.lastIndexOf("/")+1));
			}
		}
		wmsOutOrdersService.add(wmsOutOrders);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsOutOrdersPage
	 * @return
	 */
	@AutoLog(value = "出库单-编辑")
	@Operation(summary="出库单-编辑")
    @RequiresPermissions("outorder:wms_out_orders:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsOutOrdersPage wmsOutOrdersPage) {
		WmsOutOrders wmsOutOrders = new WmsOutOrders();
		BeanUtils.copyProperties(wmsOutOrdersPage, wmsOutOrders);
		//处理省市区
		String region = wmsOutOrdersPage.getRegion();
		if(StringUtils.isNotBlank(region)){
			String[] regionArray = region.split(",");
			if(regionArray.length==3){
				wmsOutOrders.setShippingProvince(provinceCityArea.getText(regionArray[0]));
				String city = provinceCityArea.getText(regionArray[1]);//北京市/北京市
				String county = provinceCityArea.getText(regionArray[2]);//北京市/北京市/东城区
				wmsOutOrders.setShippingCity(city.substring(city.lastIndexOf("/")+1));
				wmsOutOrders.setShippingCounty(county.substring(county.lastIndexOf("/")+1));
			}
		}
		WmsOutOrders wmsOutOrdersEntity = wmsOutOrdersService.getById(wmsOutOrders.getId());
		if(wmsOutOrdersEntity==null) {
			return Result.error("未找到对应数据");
		}
		//出库单状态为创建状态或审核失败状态可以编辑
		if(!(WarehouseDictEnum.OUTBOUND_CREATED.getCode().equals(wmsOutOrdersEntity.getStatus())
				|| WarehouseDictEnum.OUTBOUND_REJECTED.getCode().equals(wmsOutOrdersEntity.getStatus()))){
			throw new JeecgBootException("非创建状态、审核失败状态出库单不允许编辑");
		}
		wmsOutOrdersService.updateMain(wmsOutOrders, wmsOutOrdersPage.getWmsOutOrdersItemsList());
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "出库单-通过id删除")
	@Operation(summary="出库单-通过id删除")
    @RequiresPermissions("outorder:wms_out_orders:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsOutOrdersService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "出库单-批量删除")
	@Operation(summary="出库单-批量删除")
    @RequiresPermissions("outorder:wms_out_orders:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsOutOrdersService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出库单-通过id查询")
	@Operation(summary="出库单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsOutOrders> queryById(@RequestParam(name="id",required=true) String id) {
		WmsOutOrders wmsOutOrders = wmsOutOrdersService.getById(id);
		if(wmsOutOrders==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsOutOrders);

	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出库单明细通过主表ID查询")
	@Operation(summary="出库单明细-通主表ID查询")
	@GetMapping(value = "/queryWmsOutOrdersItemsByMainId")
	public Result<List<WmsOutOrdersItems>> queryWmsOutOrdersItemsListByMainId(@RequestParam(name="id",required=true) String id) {
		List<WmsOutOrdersItems> wmsOutOrdersItemsList = wmsOutOrdersItemsService.selectByMainId(id);
		//根据商品id查询商品名称
		List<String> productIds = wmsOutOrdersItemsList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
		BaseMapper<WmsProducts> baseMapper = wmsProductsService.getBaseMapper();
		List<WmsProducts> wmsProducts = baseMapper.selectBatchIds(productIds);
		wmsOutOrdersItemsList.stream().forEach(item -> {
			item.setProductName(wmsProducts.stream().filter(product -> product.getId().equals(item.getSkuId())).findFirst().orElse(null).getProductName());
		});
		return Result.OK(wmsOutOrdersItemsList);
	}
	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "出库单分配明细通过主表ID查询")
	 @Operation(summary="出库单分配明细-通主表ID查询")
	 @GetMapping(value = "/queryWmsOutOrdersAllocationByMainId")
	 public Result<List<WmsOutOrdersAllocation>> queryWmsOutOrdersAllocationListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<WmsOutOrdersAllocation> wmsOutOrdersAllocationList = wmsOutOrdersAllocationService.selectByMainId(id);
		 return Result.OK(wmsOutOrdersAllocationList);
	 }

	 /**
	  * 提交审核
	  */
	 @AutoLog(value = "出库单主表-提交审核")
	 @Operation(summary="出库单主表-提交审核")
	 @PostMapping(value = "/submitAudit")
	 public Result<String> submit(@RequestParam(name="id",required=true)String id) {
		 wmsOutOrdersService.submitAudit(id);
		 return Result.OK("提交成功！");
	 }
	 /**
	  *  审核
	  *
	  * @param wmsOutOrders
	  * @return
	  */
	 @AutoLog(value = "出库单主表-审核")
	 @Operation(summary="出库单主表-审核")
	 @RequestMapping(value = "/audit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> audit(@RequestBody WmsOutOrders wmsOutOrders) {
		 wmsOutOrdersService.audit(wmsOutOrders);
		 return Result.OK("审核完成!");
	 }

	 /**
	  * 分配库存
	  */
	 @AutoLog(value = "出库单-分配库存")
	 @Operation(summary="出库单-分配库存")
	 @PostMapping(value = "/allocateStock")
	 public Result<String> allocateStock(@RequestParam(name="ids",required=true) String ids) {
		 //非空校验
		 if (StringUtils.isEmpty(ids)) {
			 return Result.error("请选择需要分配库存的订单");
		 }
		 //分配成功数量
		 int allocatedCount = 0;
		 String[] idArray = ids.split(",");
		 for (String id : idArray) {
			 String i = WarehouseDictEnum.OUTBOUND_FAILED.getCode();
			 try {
				 //分配库存
				 i = wmsOutOrdersService.allocateStock(id);
			 } catch (Exception e) {
				 log.info("出库单{}分配库存失败", id);
			 }
			 if (i.equals(WarehouseDictEnum.OUTBOUND_ALLOCATED.getCode())) {
				 //成功数量加1
				 allocatedCount++;
			 }
		 }
		 //失败数量
		 int failNum = idArray.length - allocatedCount;
		 return Result.OK("分配库存成功" + allocatedCount + "条,失败" + failNum + "条");

	 }

    /**
    * 导出excel
    *
    * @param request
    * @param wmsOutOrders
    */
    @RequiresPermissions("outorder:wms_out_orders:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsOutOrders wmsOutOrders) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<WmsOutOrders> queryWrapper = QueryGenerator.initQueryWrapper(wmsOutOrders, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<WmsOutOrders> wmsOutOrdersList = wmsOutOrdersService.list(queryWrapper);

      // Step.3 组装pageList
      List<WmsOutOrdersPage> pageList = new ArrayList<WmsOutOrdersPage>();
      for (WmsOutOrders main : wmsOutOrdersList) {
          WmsOutOrdersPage vo = new WmsOutOrdersPage();
          BeanUtils.copyProperties(main, vo);
          List<WmsOutOrdersItems> wmsOutOrdersItemsList = wmsOutOrdersItemsService.selectByMainId(main.getId());
          vo.setWmsOutOrdersItemsList(wmsOutOrdersItemsList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "出库单列表");
      mv.addObject(NormalExcelConstants.CLASS, WmsOutOrdersPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库单数据", "导出人:"+sysUser.getRealname(), "出库单"));
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
    @RequiresPermissions("outorder:wms_out_orders:importExcel")
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
              List<WmsOutOrdersPage> list = ExcelImportUtil.importExcel(file.getInputStream(), WmsOutOrdersPage.class, params);
              for (WmsOutOrdersPage page : list) {
                  WmsOutOrders po = new WmsOutOrders();
                  BeanUtils.copyProperties(page, po);
                  wmsOutOrdersService.saveMain(po, page.getWmsOutOrdersItemsList());
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
