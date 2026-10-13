package org.jeecg.modules.wms.goods.controller;

import java.util.Arrays;
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
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.wms.goods.entity.WmsProductCategories;
import org.jeecg.modules.wms.goods.service.IWmsProductCategoriesService;

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
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

 /**
 * @Description: 商品类别
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
@Tag(name="商品类别")
@RestController
@RequestMapping("/goods/wmsProductCategories")
@Slf4j
public class WmsProductCategoriesController extends JeecgController<WmsProductCategories, IWmsProductCategoriesService>{
	@Autowired
	private IWmsProductCategoriesService wmsProductCategoriesService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsProductCategories
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "商品类别-分页列表查询")
	@Operation(summary="商品类别-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<WmsProductCategories>> queryPageList(WmsProductCategories wmsProductCategories,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<WmsProductCategories> queryWrapper =  QueryGenerator.initQueryWrapper(wmsProductCategories, req.getParameterMap());
            List<WmsProductCategories> list = wmsProductCategoriesService.queryTreeListNoPage(queryWrapper);
            IPage<WmsProductCategories> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = wmsProductCategories.getParentId();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            wmsProductCategories.setParentId(null);
            QueryWrapper<WmsProductCategories> queryWrapper = QueryGenerator.initQueryWrapper(wmsProductCategories, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("parent_id", parentId);
            Page<WmsProductCategories> page = new Page<WmsProductCategories>(pageNo, pageSize);
            IPage<WmsProductCategories> pageList = wmsProductCategoriesService.page(page, queryWrapper);
            return Result.OK(pageList);
        }
	}

	 /**
	  * 【vue3专用】加载节点的子数据
	  *
	  * @param pid
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = wmsProductCategoriesService.queryListByPid(pid);
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】加载一级节点/如果是同步 则所有数据
	  *
	  * @param async
	  * @param pcode
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = wmsProductCategoriesService.queryListByCode(pcode);
			 if (!async) {
				 loadAllChildren(ls);
			 }
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】递归求子节点 同步加载用到
	  *
	  * @param ls
	  */
	 private void loadAllChildren(List<SelectTreeModel> ls) {
		 for (SelectTreeModel tsm : ls) {
			 List<SelectTreeModel> temp = wmsProductCategoriesService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
      * 获取子数据
      * @param wmsProductCategories
      * @param req
      * @return
      */
	//@AutoLog(value = "商品类别-获取子数据")
	@Operation(summary="商品类别-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<WmsProductCategories>> queryPageList(WmsProductCategories wmsProductCategories,HttpServletRequest req) {
		QueryWrapper<WmsProductCategories> queryWrapper = QueryGenerator.initQueryWrapper(wmsProductCategories, req.getParameterMap());
		List<WmsProductCategories> list = wmsProductCategoriesService.list(queryWrapper);
		IPage<WmsProductCategories> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	//@AutoLog(value = "商品类别-批量获取子数据")
    @Operation(summary="商品类别-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<WmsProductCategories> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("parent_id", parentIdList);
            List<WmsProductCategories> list = wmsProductCategoriesService.list(queryWrapper);
            IPage<WmsProductCategories> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }
	
	/**
	 *   添加
	 *
	 * @param wmsProductCategories
	 * @return
	 */
	@AutoLog(value = "商品类别-添加")
	@Operation(summary="商品类别-添加")
    @RequiresPermissions("goods:wms_product_categories:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsProductCategories wmsProductCategories) {
		wmsProductCategoriesService.addWmsProductCategories(wmsProductCategories);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wmsProductCategories
	 * @return
	 */
	@AutoLog(value = "商品类别-编辑")
	@Operation(summary="商品类别-编辑")
    @RequiresPermissions("goods:wms_product_categories:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsProductCategories wmsProductCategories) {
		wmsProductCategoriesService.updateWmsProductCategories(wmsProductCategories);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商品类别-通过id删除")
	@Operation(summary="商品类别-通过id删除")
    @RequiresPermissions("goods:wms_product_categories:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		wmsProductCategoriesService.deleteWmsProductCategories(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商品类别-批量删除")
	@Operation(summary="商品类别-批量删除")
    @RequiresPermissions("goods:wms_product_categories:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wmsProductCategoriesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "商品类别-通过id查询")
	@Operation(summary="商品类别-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsProductCategories> queryById(@RequestParam(name="id",required=true) String id) {
		WmsProductCategories wmsProductCategories = wmsProductCategoriesService.getById(id);
		if(wmsProductCategories==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsProductCategories);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsProductCategories
    */
    @RequiresPermissions("goods:wms_product_categories:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsProductCategories wmsProductCategories) {
		return super.exportXls(request, wmsProductCategories, WmsProductCategories.class, "商品类别");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("goods:wms_product_categories:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, WmsProductCategories.class);
    }

}
