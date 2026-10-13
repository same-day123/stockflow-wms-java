package org.jeecg.modules.iot.manage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.iot.manage.mapper.IotWarehousesMapper;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.service.ISysDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author muht
 * @create 2025/6/13 16:32
 */
@Slf4j
@Service
public class WarehouseServiceImpl implements IWarehouseService {

    @Autowired
    private ISysDepartService departService;

    @Autowired
    private IotWarehousesMapper iotWarehousesMapper;

    @Override
    public List<String> getWarehouseCodes(String specifiedWarehouseCodes) {
        // 若指定了仓库编码(页面查询时可能会指定)
        if (StringUtils.hasText(specifiedWarehouseCodes)) {
            return Arrays.stream(specifiedWarehouseCodes.split(",")).collect(Collectors.toList());
        }

        // 查询与用户绑定的部门/仓库
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepart> warehouses = departService.queryUserDeparts(user.getId());

        if (CollectionUtils.isEmpty(warehouses)) {
            log.info("用户没有所属仓库！");
            // 设置一个不存在的仓库编码，防止因为返回空集导致一些判断条件未生效而查出所有仓库数据
            return List.of("null");
        }

        // 若用户未绑定仓库，则默认仓库范围是当前租户下全部仓库
//        if (CollectionUtils.isEmpty(warehouses)) {
//            // 当前租户下所有的仓库
//            List<WmsWarehouses> tenantWarehouses = iotWarehousesMapper.selectList(new LambdaQueryWrapper<WmsWarehouses>()
//                    .eq(WmsWarehouses::getTenantId, TenantContext.getTenant()));
//
//            // 若未创建任何仓库，则查询一个不存在的仓库编码
//            if (CollectionUtils.isEmpty(tenantWarehouses)) {
//                return List.of("null");
//            }
//            return tenantWarehouses.stream().map(WmsWarehouses::getWarehouseCode).collect(Collectors.toList());
//        }

        return warehouses.stream().map(d -> d.getOrgCode()).toList();
    }
}
