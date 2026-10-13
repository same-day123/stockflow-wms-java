package org.jeecg.modules.iot.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.base.mqtt.exception.BusiException;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.TenantConstant;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotProduct;
import org.jeecg.modules.iot.manage.mapper.IotProductMapper;
import org.jeecg.modules.iot.manage.service.IIotDeviceService;
import org.jeecg.modules.iot.manage.service.IIotProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 设备产品表
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
@Slf4j
@Service
public class IotProductServiceImpl extends ServiceImpl<IotProductMapper, IotProduct> implements IIotProductService {

    @Autowired
    private IIotDeviceService deviceService;

    @Override
    public List<IotProduct> list(Wrapper<IotProduct> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public boolean save(IotProduct entity) {
        this.checkUnique(entity);
        entity.setTenantId(Integer.valueOf(TenantContext.getTenant()));
        return super.save(entity);
    }

    @Override
    public boolean updateById(IotProduct entity) {
        this.checkUnique(entity);
        return super.updateById(entity);
    }

    @Override
    public <E extends IPage<IotProduct>> E page(E page, Wrapper<IotProduct> queryWrapper) {
        // 查询产品实体，产品列表不区分仓库，只区分租户
        E productPage = super.page(page, queryWrapper);

        // 查询设备分组信息
        Map<String, List<IotDevice>> deviceGroup = this.allDeviceGroupByProduct();

        // 赋值设备数量
        productPage.getRecords().forEach(product -> {
            List<IotDevice> deviceNum = Optional.ofNullable(deviceGroup.get(product.getProductCode())).orElse(List.of());
            product.setNum(deviceNum.size());
        });

        return productPage;
    }

    @Override
    public boolean removeById(Serializable id) {
        IotProduct product = super.getById(id);
        List<IotDevice> devices = deviceService.list(new LambdaQueryWrapper<IotDevice>()
                .eq(IotDevice::getProductCode, product.getProductCode()));
        if (!CollectionUtils.isEmpty(devices)) {
            log.info("关联设备列表：{}", devices.stream().map(IotDevice::getDeviceName).toList());
            throw new BusiException("该产品下还存在" + devices.size() + "个设备！");
        }
        return super.removeById(id);
    }

    /**
     * 按产品分组设备，以统计设备数量
     *
     * @return
     */
    private Map<String, List<IotDevice>> allDeviceGroupByProduct() {
        List<IotDevice> allDevices = deviceService.list();

        if (CollectionUtils.isEmpty(allDevices)) {
            return Map.of();
        }

        // 分组聚合
        Map<String, List<IotDevice>> deviceGroupMap = allDevices.stream()
                .filter(e -> Objects.nonNull(e.getProductCode()))
                .collect(Collectors.groupingBy(e -> e.getProductCode()));

        return deviceGroupMap;
    }

    /**
     * 校验唯一
     *
     * @param product
     */
    private void checkUnique(IotProduct product) {
        LambdaQueryWrapper<IotProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotProduct::getProductCode, product.getProductCode())
                .or(q -> q.eq(IotProduct::getProductName, product.getProductName()));

        List<IotProduct> dbProducts = super.list(queryWrapper);

        if (!CollectionUtils.isEmpty(dbProducts)) {
            List<IotProduct> products = dbProducts.stream()
                    .filter(p -> !p.getId().equals(product.getId()))
                    .toList();

            for (IotProduct iotProduct : products) {
                String keyword = "产品编码不能重复";
                if (iotProduct.getProductName().equals(product.getProductName())) {
                    keyword = "产品名称不能重复";
                }
                throw new BusiException(keyword);
            }
        }
    }
}
