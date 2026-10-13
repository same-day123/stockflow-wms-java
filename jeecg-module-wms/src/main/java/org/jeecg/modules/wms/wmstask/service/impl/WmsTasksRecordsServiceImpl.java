package org.jeecg.modules.wms.wmstask.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksRecordsMapper;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 任务执行记录表
 * @Author: jeecg-boot
 * @Date:   2025-08-30
 * @Version: V1.0
 */
@Service
public class WmsTasksRecordsServiceImpl extends ServiceImpl<WmsTasksRecordsMapper, WmsTasksRecords> implements IWmsTasksRecordsService {

    @Override
    public IPage<WmsTasksRecords> pageList(WmsTasksRecords wmsTasksRecords, Integer pageNo, Integer pageSize) {
        wmsTasksRecords = ObjectUtils.defaultIfNull(wmsTasksRecords, new WmsTasksRecords());
        //参考上边的代码分页查询任务记录列表
        Page<WmsTasksRecords> page = PageHelper.startPage(pageNo, pageSize);
        List<WmsTasksRecords> list = baseMapper.queryList(wmsTasksRecords);
        PageDTO<WmsTasksRecords> wmsTasksRecordsPageDTO = new PageDTO<>();
        wmsTasksRecordsPageDTO.setRecords(list);
        wmsTasksRecordsPageDTO.setTotal(page.getTotal());
        wmsTasksRecordsPageDTO.setSize(page.getPageSize());
        wmsTasksRecordsPageDTO.setCurrent(page.getPageNum());
        wmsTasksRecordsPageDTO.setPages(page.getPages());

        return wmsTasksRecordsPageDTO;
    }
}
