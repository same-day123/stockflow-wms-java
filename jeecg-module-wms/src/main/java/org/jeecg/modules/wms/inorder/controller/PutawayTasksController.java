package org.jeecg.modules.wms.inorder.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
* @Description: 上架任务
* @Author: jeecg-boot
* @Date:   2025-04-23
* @Version: V1.0
*/
@Tag(name="任务表")
@RestController
@RequestMapping("/inorder/putawayTasks")
@Slf4j
public class PutawayTasksController {

    @Autowired
   private IWmsTasksService wmsTasksService;

//   @Autowired
//   private IPutawayTasksService putawayTasksService;

   @Autowired
   private IWmsTasksRecordsService wmsTasksRecordsService;

   @Operation(summary="待上架任务查询")
   @GetMapping(value = "/list")
   public Result<IPage<WmsTasks>> list(WmsTasks wmsTasks,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       //todo 设置任务类型为收货任务
       IPage<WmsTasks> wmsTasksIPage = wmsTasksService.list(wmsTasks, pageNo, pageSize);
       return Result.OK(wmsTasksIPage);
   }
   @Operation(summary="上架记录查询")
   @GetMapping(value = "/records")
   public Result<IPage<WmsTasksRecords>> records(WmsTasksRecords wmsTasksRecords,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       //todo 设置任务类型为收货任务
       IPage<WmsTasksRecords> records = wmsTasksRecordsService.pageList(wmsTasksRecords, pageNo, pageSize);
       return Result.OK(records);
   }

   /**
    *   上架
    *
    * @param wmsTasksRecords
    * @return
    */
   @AutoLog(value = "上架")
   @Operation(summary="上架")
   @RequiresPermissions("inorder:putaway_task:addRecords")
   @PostMapping(value = "/addRecords")
   public Result<String> add(@RequestBody WmsTasksRecords wmsTasksRecords) {
       String taskId = wmsTasksRecords.getId();
       wmsTasksRecords.setTaskId(taskId);
       wmsTasksRecords.setId(null);
       wmsTasksService.putaway(wmsTasksRecords);
       return Result.OK("添加成功！");
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
