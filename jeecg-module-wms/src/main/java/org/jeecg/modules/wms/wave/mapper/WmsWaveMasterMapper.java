package org.jeecg.modules.wms.wave.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 波次主表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
public interface WmsWaveMasterMapper extends BaseMapper<WmsWaveMaster> {
    /**
     * 查询列表
     */
    List<WmsWaveMaster> queryList(WmsWaveMaster wmsWaveMaster);
}
