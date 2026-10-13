package org.jeecg.modules.wms.wave.controller;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.wms.wave.entity.WmsWaveStrategy;
import org.jeecg.modules.wms.wave.service.IWaveStrategyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 波次策略表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Tag(name="波次策略表")
@RestController
@RequestMapping("/wave/waveStrategy")
@Slf4j
public class WaveStrategyController extends JeecgController<WmsWaveStrategy, IWaveStrategyService> {
	@Autowired
	private IWaveStrategyService waveStrategyService;

	/**
	 * 分页列表查询
	 *
	 * @param waveStrategy
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "波次策略表-分页列表查询")
	@Operation(summary="波次策略表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WmsWaveStrategy>> queryPageList(WmsWaveStrategy waveStrategy,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest req) {
        QueryWrapper<WmsWaveStrategy> queryWrapper = QueryGenerator.initQueryWrapper(waveStrategy, req.getParameterMap());
		Page<WmsWaveStrategy> page = new Page<WmsWaveStrategy>(pageNo, pageSize);
		IPage<WmsWaveStrategy> pageList = waveStrategyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param waveStrategy
	 * @return
	 */
	@AutoLog(value = "波次策略表-添加")
	@Operation(summary="波次策略表-添加")
	@RequiresPermissions("wave:wave_strategy:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WmsWaveStrategy waveStrategy) {
		waveStrategyService.save(waveStrategy);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param waveStrategy
	 * @return
	 */
	@AutoLog(value = "波次策略表-编辑")
	@Operation(summary="波次策略表-编辑")
	@RequiresPermissions("wave:wave_strategy:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WmsWaveStrategy waveStrategy) {
		waveStrategyService.updateById(waveStrategy);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "波次策略表-通过id删除")
	@Operation(summary="波次策略表-通过id删除")
	@RequiresPermissions("wave:wave_strategy:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		waveStrategyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "波次策略表-批量删除")
	@Operation(summary="波次策略表-批量删除")
	@RequiresPermissions("wave:wave_strategy:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.waveStrategyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "波次策略表-通过id查询")
	@Operation(summary="波次策略表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WmsWaveStrategy> queryById(@RequestParam(name="id",required=true) String id) {
		WmsWaveStrategy waveStrategy = waveStrategyService.getById(id);
		if(waveStrategy==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(waveStrategy);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param waveStrategy
    */
    @RequiresPermissions("wave:wave_strategy:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WmsWaveStrategy waveStrategy) {
        return super.exportXls(request, waveStrategy, WmsWaveStrategy.class, "波次策略表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wave:wave_strategy:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WmsWaveStrategy.class);
    }

}
