package org.jeecg.modules.wms.goods.controller;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.goods.entity.*;
import org.jeecg.modules.wms.goods.excel.ImportGoodsListener;
import org.jeecg.modules.wms.goods.excel.WmsProductsImport;
import org.jeecg.modules.wms.goods.service.*;

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
 * @Description: 商品信息表
 * @Author: jeecg-boot
 * @Date:   2025-04-14
 * @Version: V1.0
 */
@Tag(name="商品信息表")
@RestController
@RequestMapping("/goods/wmsProducts")
@Slf4j
public class WmsProductsController extends JeecgController<WmsProducts, IWmsProductsService> {
	@Autowired
	private IWmsProductsService wmsProductsService;

	//商品品牌service
	 @Autowired
	 private IWmsProductBrandService wmsProductBrandService;

	//注入货主service
	 @Autowired
	 private IWmsCargoOwnersService wmsCargoOwnersService;

	 //注入商品分类service
	 @Autowired
	 private IWmsProductCategoriesService wmsProductCategoriesService;

	 //注入商品图片service
	 @Autowired
	 private IWmsProductImagesService wmsProductImagesService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsProducts
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "商品信息表-分页列表查询")
	@Operation(summary="商品信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsProducts>> queryPageList(WmsProducts wmsProducts,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<WmsProducts> queryWrapper = QueryGenerator.initQueryWrapper(wmsProducts, req.getParameterMap());
		Page<WmsProducts> page = new Page<WmsProducts>(pageNo, pageSize);
		IPage<WmsProducts> pageList = wmsProductsService.page(page, queryWrapper);
		if(pageList.getTotal()<=0){
			return Result.OK(pageList);
		}
		/**
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
		 */
		//按上边注释的代码实现根据货主id关联查询货主名称
		List<String> ownerIds = pageList.getRecords().stream().map(item -> item.getOwnerId()).collect(Collectors.toList());

		List<WmsCargoOwners> wmsCargoOwners = wmsCargoOwnersService.listByIds(ownerIds);
		pageList.getRecords().stream().forEach(item -> {
			item.setOwnerName(wmsCargoOwners.stream().filter(owner -> owner.getId().equals(item.getOwnerId())).findFirst().map(WmsCargoOwners::getOwnerName).orElse(null));
		});
		//按上边注释的代码实现根据分类id关联查询分类名称
		List<String> categoryIds = pageList.getRecords().stream().map(item -> item.getCategoryId()).collect(Collectors.toList());
		List<WmsProductCategories> wmsProductCategories = wmsProductCategoriesService.listByIds(categoryIds);
		pageList.getRecords().stream().forEach(item -> {
			item.setCategoryName(wmsProductCategories.stream().filter(category -> category.getId().equals(item.getCategoryId())).findFirst().map(WmsProductCategories::getCategoryName).orElse(null));
		});
		//现根据品牌id关联查询品牌名称
		List<String> brandIds = pageList.getRecords().stream().map(item -> item.getProductBrand()).collect(Collectors.toList());
		List<WmsProductBrand> wmsProductBrands = wmsProductBrandService.listByIds(brandIds);
		pageList.getRecords().stream().forEach(item -> {
			item.setProductBrandName(wmsProductBrands.stream().filter(brand -> brand.getId().equals(item.getProductBrand())).findFirst().map(WmsProductBrand::getName).orElse(null));
		});

		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param wmsProducts
	 * @return
	 */
	@AutoLog(value = "商品信息表-添加")
	@Operation(summary="商品信息表-添加")
	@RequiresPermissions("goods:wms_products:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsProducts wmsProducts) {
		wmsProductsService.add(wmsProducts);
		//图片
		String productImgs = wmsProducts.getProductImgs();

		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsProducts
	 * @return
	 */
	@AutoLog(value = "商品信息表-编辑")
	@Operation(summary="商品信息表-编辑")
	@RequiresPermissions("goods:wms_products:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsProducts wmsProducts) {
		wmsProductsService.edit(wmsProducts);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商品信息表-通过id删除")
	@Operation(summary="商品信息表-通过id删除")
	@RequiresPermissions("goods:wms_products:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsProductsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商品信息表-批量删除")
	@Operation(summary="商品信息表-批量删除")
	@RequiresPermissions("goods:wms_products:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsProductsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "商品信息表-通过id查询")
	@Operation(summary="商品信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsProducts> queryById(@RequestParam(name="id",required=true) String id) {
		WmsProducts wmsProducts = wmsProductsService.getById(id);
		//根据商品id查询商品图片，并组成字符串，中间以逗号分隔
		List<WmsProductImages> wmsProductImages = wmsProductImagesService.list(new LambdaQueryWrapper<WmsProductImages>().eq(WmsProductImages::getProductId, id));
		String productImgs = wmsProductImages.stream().map(item -> item.getOriginal()).collect(Collectors.joining(","));
		wmsProducts.setProductImgs(productImgs);
		if(wmsProducts==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsProducts);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsProducts
    */
    @RequiresPermissions("goods:wms_products:exportXls")
    @RequestMapping(value = "/exportXls")
    public void exportXls(HttpServletRequest request, HttpServletResponse response,WmsProducts wmsProducts) throws IOException {
		//查询商品列表
		Result<IPage<WmsProducts>> iPageResult = queryPageList(wmsProducts, 1, 1000, request);
		List<WmsProducts> list = iPageResult.getResult().getRecords();
		// 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
		String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), WmsProducts.class).sheet("模板").doWrite(list);
    }
//    public ModelAndView exportXls(HttpServletRequest request, WmsProducts wmsProducts) {
//        return super.exportXls(request, wmsProducts, WmsProducts.class, "商品信息表");
//    }

    /**
      * 通过excel导入数据
    *
    * @param file
    * @return
    */
    @RequiresPermissions("goods:wms_products:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(MultipartFile  file) throws IOException {
		EasyExcel.read(file.getInputStream(), WmsProductsImport.class, new ImportGoodsListener(wmsProductsService)).sheet().doRead();
		return Result.OK("文件导入成功！");
    }
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, WmsProducts.class);
//    }

}
