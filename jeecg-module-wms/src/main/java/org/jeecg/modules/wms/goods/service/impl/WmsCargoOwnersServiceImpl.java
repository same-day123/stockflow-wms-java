package org.jeecg.modules.wms.goods.service.impl;

import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.mapper.WmsCargoOwnersMapper;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
@Service
public class WmsCargoOwnersServiceImpl extends ServiceImpl<WmsCargoOwnersMapper, WmsCargoOwners> implements IWmsCargoOwnersService {
    @Autowired
    private RedisUtil redisUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WmsCargoOwners wmsCargoOwners) {
        //生成货主编码
        wmsCargoOwners.setOwnerCode(generateOwnerCode());
        save(wmsCargoOwners);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WmsCargoOwners wmsCargoOwners) {
        updateById(wmsCargoOwners);
    }

    /**
     * 生成货主编码
     * @return
     */
    @Override
    public String generateOwnerCode() {
        //编码规则：C+5位序号，序号使用redis自增序号实现
        String code = "C";
        try {
            code += String.format("%05d", redisUtil.incr("WMS_CARGO_OWNERS_CODE", 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
}
