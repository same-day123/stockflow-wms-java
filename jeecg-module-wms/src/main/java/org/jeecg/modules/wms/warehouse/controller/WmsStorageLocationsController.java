package org.jeecg.modules.wms.warehouse.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageZones;
import org.jeecg.modules.wms.warehouse.entity.WmsWarehouses;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.wms.warehouse.service.IWmsStorageZonesService;
import org.jeecg.modules.wms.warehouse.service.IWmsWarehousesService;
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
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date:   2025-04-10
 * @Version: V1.0
 */
@Tag(name="库位表")
@RestController
@RequestMapping("/warehouse/wmsStorageLocations")
@Slf4j
public class WmsStorageLocationsController extends JeecgController<WmsStorageLocations, IWmsStorageLocationsService> {
	@Autowired
	private IWmsStorageLocationsService wmsStorageLocationsService;
	 @Autowired
	 private IWmsWarehousesService wmsWarehousesService;
	//注入库区service
	 @Autowired
	 private IWmsStorageZonesService wmsStorageZonesService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsStorageLocations
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "库位表-分页列表查询")
	@Operation(summary="库位表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsStorageLocations>> queryPageList(WmsStorageLocations wmsStorageLocations,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsStorageLocations> queryWrapper = QueryGenerator.initQueryWrapper(wmsStorageLocations, req.getParameterMap());
		Page<WmsStorageLocations> page = new Page<WmsStorageLocations>(pageNo, pageSize);
		IPage<WmsStorageLocations> pageList = wmsStorageLocationsService.page(page, queryWrapper);
		//如果结果集为空则直接返回
		if(pageList.getTotal()==0){
			return Result.OK(pageList);
		}
		// 查询仓库id
		List<String> warehousIds = pageList.getRecords().stream().map(item -> item.getWarehouseId()).collect(Collectors.toList());

		//获取mapper
		BaseMapper<WmsWarehouses> baseMapper = wmsWarehousesService.getBaseMapper();
		// 查询仓库信息
		List<WmsWarehouses> wmsWarehouses = baseMapper.selectBatchIds(warehousIds);
		// 设置仓库信息
		pageList.getRecords().stream().forEach(item -> {
			item.setWarehouse(wmsWarehouses.stream().filter(warehouse -> warehouse.getId().equals(item.getWarehouseId())).findFirst().orElse(null));
		});

		//查询库区id
		List<String> zoneIds = pageList.getRecords().stream().map(item -> item.getZoneId()).collect(Collectors.toList());
		//获取mapper
		BaseMapper<WmsStorageZones> baseMapper1 = wmsStorageZonesService.getBaseMapper();
		//查询库区信息
		List<WmsStorageZones> wmsStorageZones = baseMapper1.selectBatchIds(zoneIds);
		//设置库区信息
		pageList.getRecords().stream().forEach(item -> {
			item.setStorageZones(wmsStorageZones.stream().filter(zone -> zone.getId().equals(item.getZoneId())).findFirst().orElse(null));
		});

		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsStorageLocations
	 * @return
	 */
	@AutoLog(value = "库位表-添加")
	@Operation(summary="库位表-添加")
	@RequiresPermissions("warehouse:wms_storage_locations:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsStorageLocations wmsStorageLocations) {
		//库区id
		String zoneId = wmsStorageLocations.getZoneId();
		//查询库区信息
		WmsStorageZones storageZones = wmsStorageZonesService.getById(zoneId);
		if(storageZones!=null){
			//设置所属仓库
			wmsStorageLocations.setWarehouseId(storageZones.getWarehouseId());
		}
		wmsStorageLocationsService.save(wmsStorageLocations);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsStorageLocations
	 * @return
	 */
	@AutoLog(value = "库位表-编辑")
	@Operation(summary="库位表-编辑")
	@RequiresPermissions("warehouse:wms_storage_locations:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsStorageLocations wmsStorageLocations) {
		//库区id
		String zoneId = wmsStorageLocations.getZoneId();
		//查询库区信息
		WmsStorageZones storageZones = wmsStorageZonesService.getById(zoneId);
		if(storageZones!=null){
			//设置所属仓库
			wmsStorageLocations.setWarehouseId(storageZones.getWarehouseId());
		}
		wmsStorageLocationsService.updateById(wmsStorageLocations);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "库位表-通过id删除")
	@Operation(summary="库位表-通过id删除")
	@RequiresPermissions("warehouse:wms_storage_locations:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsStorageLocationsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "库位表-批量删除")
	@Operation(summary="库位表-批量删除")
	@RequiresPermissions("warehouse:wms_storage_locations:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsStorageLocationsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "库位表-通过id查询")
	@Operation(summary="库位表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsStorageLocations> queryById(@RequestParam(name="id",required=true) String id) {
		WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.getById(id);
		if(wmsStorageLocations==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsStorageLocations);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsStorageLocations
    */
    @RequiresPermissions("warehouse:wms_storage_locations:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsStorageLocations wmsStorageLocations) {
        return super.exportXls(request, wmsStorageLocations, WmsStorageLocations.class, "库位表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("warehouse:wms_storage_locations:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsStorageLocations.class);
    }

}
