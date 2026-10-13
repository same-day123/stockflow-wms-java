package org.jeecg.modules.wms.wave.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.wave.service.IPickingTasksService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
* @Description: 拣货任务
* @Author: jeecg-boot
* @Date:   2025-04-23
* @Version: V1.0
*/
@Tag(name="任务表")
@RestController
@RequestMapping("/wave/pickingTasks")
@Slf4j
public class PickingTasksController {

   @Autowired
   private IWmsTasksService wmsTasksService;

   @Resource(name = "pickingTasksServiceImpl")
   private IPickingTasksService pickingTasksService;

    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;

   @Operation(summary="待拣货任务查询")
   @GetMapping(value = "/list")
   public Result<IPage<WmsTasks>> pickinglist(WmsTasks wmsTasks,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       //设置任务类型为收货任务
       IPage<WmsTasks> wmsTasksIPage = wmsTasksService.list(wmsTasks, pageNo, pageSize);
       return Result.OK(wmsTasksIPage);
   }
   @Operation(summary="拣货记录查询")
   @GetMapping(value = "/records")
   public Result<IPage<WmsTasksRecords>> records(WmsTasksRecords wmsTasksRecords,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       //设置任务类型为收货任务
       IPage<WmsTasksRecords> records = wmsTasksRecordsService.pageList(wmsTasksRecords, pageNo, pageSize);
       return Result.OK(records);
   }

    /**
     * 创建拣货任务
     */
    @AutoLog(value = "波次主表-创建拣货任务")
    @Operation(summary="波次主表-创建拣货任务")
    @PostMapping(value = "/createPickTask")
    public Result<String> createPickTask(@RequestParam(name="ids",required=true) String ids) {
        List<String> waveIds = Arrays.asList(ids.split(","));
        for (String waveId : waveIds) {
            pickingTasksService.createPickTask(waveId);
        }
        return Result.OK("创建成功！");
    }

    /**
     * 完成拣货
     * 当存在缺货时需要手动执行完成拣货操作
     */
    @AutoLog(value = "波次主表-完成拣货")
    @Operation(summary="波次主表-完成拣货")
    @PostMapping(value = "/completePickTask")
    public Result<String> completePickTask(@RequestParam(name="ids",required=true) String ids) {
        List<String> waveIds = Arrays.asList(ids.split(","));
        for (String waveId : waveIds) {
            pickingTasksService.completePickTask(waveId);
        }
        return Result.OK("完成拣货成功！");
    }


    /**
    * 拣货
    *
    * @param wmsTasksRecords
    * @return
    */
   @AutoLog(value = "拣货")
   @Operation(summary="拣货")
   @RequiresPermissions("wave:pick_task:addRecords")
   @PostMapping(value = "/addRecords")
   public Result<String> addRecords(@RequestBody WmsTasksRecords wmsTasksRecords) {
       String taskId = wmsTasksRecords.getId();
       wmsTasksRecords.setTaskId(taskId);
       wmsTasksRecords.setId(null);
       pickingTasksService.pick(wmsTasksRecords);
       return Result.OK("添加成功！");
   }


    /**
     * 分拣
     * @param wmsOutOrdersItemsList 出库单明细
     */
    @AutoLog(value = "分拣")
    @Operation(summary="分拣")
    @PostMapping(value = "/completeSorting")
    public Result<String> completeSorting(@RequestBody List<WmsOutOrdersItems> wmsOutOrdersItemsList) {
        pickingTasksService.completeSorting(wmsOutOrdersItemsList);
        return Result.OK("分拣完成！");
    }

   /**
    *  编辑
    *
    * @param wmsTasks
    * @return
    */
   @AutoLog(value = "任务表-编辑")
   @Operation(summary="任务表-编辑")
   @RequiresPermissions("wmstask:wms_tasks:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody WmsTasks wmsTasks) {
       wmsTasksService.updateById(wmsTasks);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "任务表-通过id删除")
   @Operation(summary="任务表-通过id删除")
   @RequiresPermissions("wmstask:wms_tasks:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       wmsTasksService.removeById(id);
       return Result.OK("删除成功!");
   }

    /**
     * 查看拣货路径
     */
    @AutoLog(value = "任务表-查看拣货路径")
    @Operation(summary="任务表-查看拣货路径")
    @GetMapping(value = "/viewPickPath")
    public Result<HashMap<String, String>> viewPickPath(@RequestParam(name="waveId",required=true) String waveId) {
        String svg = pickingTasksService.viewPickPath(waveId);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("svg", svg);
        return Result.OK(stringStringHashMap);
    }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "任务表-批量删除")
   @Operation(summary="任务表-批量删除")
   @RequiresPermissions("wmstask:wms_tasks:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.wmsTasksService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "任务表-通过id查询")
   @Operation(summary="任务表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<WmsTasks> queryById(@RequestParam(name="id",required=true) String id) {
       WmsTasks wmsTasks = wmsTasksService.getById(id);
       if(wmsTasks==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(wmsTasks);
   }


}
