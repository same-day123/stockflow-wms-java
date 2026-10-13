package org.jeecg.modules.wms.goods.service;

import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
public interface IWmsCargoOwnersService extends IService<WmsCargoOwners> {

    /**
     * 货主添加
     */
    public void add(WmsCargoOwners wmsCargoOwners);

    /**
     * 货主编辑
     */
    public void edit(WmsCargoOwners wmsCargoOwners);

    /**
     * 生成货主编码
     */
    public String generateOwnerCode();
}
