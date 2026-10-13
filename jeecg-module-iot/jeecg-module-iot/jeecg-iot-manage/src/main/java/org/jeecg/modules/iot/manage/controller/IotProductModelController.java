package org.jeecg.modules.iot.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.modules.iot.manage.entity.IotProductModel;
import org.jeecg.modules.iot.manage.service.IIotProductModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

 /**
 * @Description: 产品物模型
 * @Author: jeecg-boot
 * @Date:   2025-04-24
 * @Version: V1.0
 */
@Tag(name="物模型")
@RestController
@RequestMapping("/iot/manage/model")
@Slf4j
public class IotProductModelController extends JeecgController<IotProductModel, IIotProductModelService> {
	@Autowired
	private IIotProductModelService iotProductModelService;

	/**
	 * 分页列表查询
	 *
	 * @param wmsIotProductModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "产品物模型-分页列表查询")
	@Operation(summary="产品物模型-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IotProductModel>> queryPageList(IotProductModel wmsIotProductModel,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
        QueryWrapper<IotProductModel> queryWrapper = QueryGenerator.initQueryWrapper(wmsIotProductModel, req.getParameterMap());
		Page<IotProductModel> page = new Page<>(pageNo, pageSize);
		IPage<IotProductModel> pageList = iotProductModelService.page(page, queryWrapper);
		return Result.OK(pageList);

	}

	/**
	 *   添加
	 *
	 * @param wmsIotProductModel
	 * @return
	 */
	@AutoLog(value = "产品物模型-添加")
	@Operation(summary="产品物模型-添加")
//	@RequiresPermissions("manage:wms_iot_product_model:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IotProductModel wmsIotProductModel) {
		iotProductModelService.save(wmsIotProductModel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param wmsIotProductModel
	 * @return
	 */
	@AutoLog(value = "产品物模型-编辑")
	@Operation(summary="产品物模型-编辑")
//	@RequiresPermissions("manage:wms_iot_product_model:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IotProductModel wmsIotProductModel) {
		iotProductModelService.updateById(wmsIotProductModel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "产品物模型-通过id删除")
	@Operation(summary="产品物模型-通过id删除")
//	@RequiresPermissions("manage:wms_iot_product_model:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		iotProductModelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "产品物模型-批量删除")
	@Operation(summary="产品物模型-批量删除")
	@RequiresPermissions("manage:wms_iot_product_model:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.iotProductModelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "产品物模型-通过id查询")
	@Operation(summary="产品物模型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IotProductModel> queryById(@RequestParam(name="id",required=true) String id) {
		IotProductModel wmsIotProductModel = iotProductModelService.getById(id);
		if(wmsIotProductModel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(wmsIotProductModel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wmsIotProductModel
    */
//    @RequiresPermissions("manage:wms_iot_product_model:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IotProductModel wmsIotProductModel) {
        return super.exportXls(request, wmsIotProductModel, IotProductModel.class, "产品物模型");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
//    @RequiresPermissions("manage:wms_iot_product_model:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IotProductModel.class);
    }

}
