package org.jeecg.modules.iot.manage.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.service.ISysDepartService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author muht
 * @create 2025/6/10 14:08
 */
@Slf4j
public class CommonUtil {

    /**
     * 获取本下级部门 orgCode
     * @return
     */
    public static List<String> getOrgCodes() {
        ISysBaseAPI baseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
        ISysDepartService deptService = SpringContextUtils.getBean(ISysDepartService.class);

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String currDeptId = baseApi.getDepartIdsByOrgCode(user.getOrgCode());

        if (currDeptId == null) {
            log.warn("部门编码查询异常，当前用户无部门！");
            // 返回不存在的org_code
            return List.of("default");
        }
        List<String> deptIds = deptService.getSubDepIdsByDepId(currDeptId);
        List<SysDepart> depts = deptService.getBaseMapper().selectList(
                new LambdaQueryWrapper<SysDepart>().in(SysDepart::getId, deptIds));
        List<String> orgCodes = depts.stream()
                .map(SysDepart::getOrgCode)
                .collect(Collectors.toList());
        return orgCodes;
    }
}
