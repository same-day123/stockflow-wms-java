package org.jeecg.modules.iot.manage.service;

import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.iot.manage.dto.dashboard.WorkbenchDashboard;
import org.springframework.web.servlet.ModelAndView;

/**
 * 工作台服务
 *
 * @author muht
 */
public interface IIotWorkbenchService {

    /**
     * 查询仪表盘信息
     *
     * @return
     */
    WorkbenchDashboard getDashboard(String warehouseCodes);

    /**
     * 获取产品分布
     * @return
     */
    ModelAndView exportXlsProductRatio(HttpServletRequest request);
}
