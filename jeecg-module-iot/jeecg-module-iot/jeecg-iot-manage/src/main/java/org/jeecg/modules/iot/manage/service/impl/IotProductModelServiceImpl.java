package org.jeecg.modules.iot.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.iot.base.mqtt.constant.CRUD;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;
import org.jeecg.common.config.TenantContext;
import org.jeecg.modules.iot.manage.constant.ModelTypeEnum;
import org.jeecg.modules.iot.manage.entity.IotAlertRule;
import org.jeecg.modules.iot.manage.entity.IotProductModel;
import org.jeecg.modules.iot.manage.mapper.IotAlertRuleMapper;
import org.jeecg.modules.iot.manage.mapper.IotProductModelMapper;
import org.jeecg.modules.iot.manage.mqtt.processor.ReportDataProcessor;
import org.jeecg.modules.iot.manage.service.CacheService;
import org.jeecg.modules.iot.manage.service.IIotProductModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static org.jeecg.modules.iot.base.mqtt.constant.RedisConst.PRODUCT_MODEL_MAPPING_CACHE_KEY;

/**
 * @Description: 产品物模型
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
@Service
public class IotProductModelServiceImpl extends ServiceImpl<IotProductModelMapper, IotProductModel> implements IIotProductModelService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IotAlertRuleMapper alertRuleMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 添加物模型
     */
    @Override
    public boolean save(IotProductModel entity) {
        if (entity.getProductCode() == null) {
            throw new BusiException("缺少产品编码");
        }
        // 校验唯一
        this.checkUnique(entity);

        // 设置租户ID
        entity.setTenantId(Integer.valueOf(TenantContext.getTenant()));
        boolean save = super.save(entity);

        // 同步物模型映射缓存：先失效，再整份重建，保证缓存完整（含该产品已有模型）
        // key   : wms:iot:product_models:{productCode}
        // field : 功能编码 code
        // value : 功能类型 type（field=属性, service=服务）
        String cacheKey = String.format(RedisConst.PRODUCT_MODEL_MAPPING_CACHE_KEY, entity.getProductCode());
        redisUtil.del(cacheKey);
        cacheService.getModelCodesByProductCode(entity.getProductCode());

        return save;
    }

    /**
     * 修改物模型
     */
    @Override
    public boolean updateById(IotProductModel entity) {
        if (entity.getProductCode() == null) {
            throw new BusiException("缺少产品编码");
        }
        this.checkUnique(entity);
        boolean updated = super.updateById(entity);
        //先拿到对应的设备编码的缓存key
        String cacheKey = String.format(RedisConst.PRODUCT_MODEL_MAPPING_CACHE_KEY,entity.getProductCode());
        //修改对应的缓存
        redisUtil.del(cacheKey);
        //调用cacheService里面的方法重新生成物模型缓存
        cacheService.getModelCodesByProductCode(entity.getProductCode());
        return updated;
    }

    /**
     * 删除物模型
     */
    @Override
    public boolean removeById(Serializable id) {
        IotProductModel model = super.getById(id);
        // 绑定报警规则的物模型不允许删除
        boolean exists = alertRuleMapper.exists(new LambdaQueryWrapper<IotAlertRule>().eq(IotAlertRule::getModelCode, model.getCode()));
        if (exists) {
            throw new BusiException("该功能已绑定报警规则！");
        }
        boolean removed = super.removeById(id);
        //首先检索出对应的key
        String cacheKey = String.format(RedisConst.PRODUCT_MODEL_MAPPING_CACHE_KEY, model.getProductCode());
        //删除缓存
        redisUtil.del(cacheKey);
        return removed;
    }

    /**
     * 校验唯一
     * 同产品下
     *
     * @return
     */
    private void checkUnique(IotProductModel model) {
        LambdaQueryWrapper<IotProductModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotProductModel::getModelName, model.getModelName())
                .or(query -> query.eq(IotProductModel::getCode, model.getCode()));
        List<IotProductModel> dbModels = super.list(queryWrapper);
        if (!CollectionUtils.isEmpty(dbModels)) {
            List<IotProductModel> models = dbModels.stream()
                    .filter(m -> !m.getId().equals(model.getId()))
                    .toList();
            // 存在可能重复的名称或编码
            if (!CollectionUtils.isEmpty(models)) {
                for (IotProductModel dbModel : models) {
                    if (dbModel.getCode().equals(model.getCode())) {
                        throw new BusiException("功能编码不能重复！");
                    }
                    // 同产品下名称不能重复
                    if (dbModel.getProductCode().equals(model.getProductCode())
                            && dbModel.getModelName().equals(model.getModelName())) {
                        throw new BusiException("同产品下功能名称不能重复！");
                    }
                }
            }
        }
    }
}
