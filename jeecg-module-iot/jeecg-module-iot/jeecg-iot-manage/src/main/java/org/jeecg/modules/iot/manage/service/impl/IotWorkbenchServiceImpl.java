package org.jeecg.modules.iot.manage.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.P;
import org.jeecg.modules.iot.base.mqtt.constant.AlertLevelEnum;
import org.jeecg.modules.iot.base.mqtt.constant.EnableEnum;
import org.jeecg.modules.iot.base.mqtt.constant.ProcessStatusEnum;
import org.jeecg.modules.iot.base.mqtt.constant.RedisConst;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.iot.manage.constant.DeviceStatusEnum;
import org.jeecg.modules.iot.manage.dto.dashboard.*;
import org.jeecg.modules.iot.manage.entity.IotAlertLog;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.entity.IotProduct;
import org.jeecg.modules.iot.manage.mapper.IotAlertLogMapper;
import org.jeecg.modules.iot.manage.mapper.IotDeviceMapper;
import org.jeecg.modules.iot.manage.mapper.IotProductMapper;
import org.jeecg.modules.iot.manage.service.IIotWorkbenchService;
import org.jeecg.modules.iot.manage.service.IWarehouseService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author muht
 * @create 2025/5/13 15:26
 */
@Slf4j
@Service
public class IotWorkbenchServiceImpl implements IIotWorkbenchService {

    @Autowired
    private IotProductMapper productMapper;

    @Autowired
    private IotDeviceMapper deviceMapper;

    @Autowired
    private IotAlertLogMapper alertLogMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private JeecgBaseConfig jeecgBaseConfig;

    @Autowired
    private IWarehouseService warehouseService;

    @Override
    public WorkbenchDashboard getDashboard(String warehouseCodes) {
        WorkbenchDashboard dashboard = new WorkbenchDashboard();
        // 初始化上下文
        DashboardContext context = this.initContext(warehouseCodes);
        // 产品分布
        List<ProductRatio> productRatios = this.getProductRatios(context.getWarehouseCodeList(), false);
        dashboard.setProductRatios(productRatios);
        // 当前待处理报警信息
        List<IotAlertLog> unsolvedAlertLogs = new ArrayList<>();
        // 近7日报警信息
        List<IotAlertLog> latest7DaysAlertLogs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(context.getAlertLogs())) {
            List<IotAlertLog> alertLogs = context.getAlertLogs();
            unsolvedAlertLogs = alertLogs.stream()
                    .filter(alert -> ProcessStatusEnum.UNSOLVED.getValue().equals(alert.getProcessStatus()))
                    .collect(Collectors.toList());
            latest7DaysAlertLogs = alertLogs.stream()
                    .filter(alert -> (LocalDateTimeUtil.of(alert.getAlertTime()).isAfter(LocalDateTime.now().minusDays(7))))
                    .collect(Collectors.toList());
        }

        // 当前待处理报警设备分组
        Map<String, List<IotAlertLog>> deviceAlertLogsMap = unsolvedAlertLogs.stream()
                .collect(Collectors.groupingBy(alert -> alert.getDeviceCode()));

        //完成对应的业务使得页面显示数据
        // 统计数据
        CounterInfo counterInfo = new CounterInfo();
        //设备数量
        counterInfo.setDeviceCount(context.getDeviceMap().size());
        //产品数量
        counterInfo.setProductCount(context.getProductMap().size());
        //当前报警设备数量
        counterInfo.setAlertDeviceCount(deviceAlertLogsMap.size());
        //当前报警信息数量
        counterInfo.setCurrentAlertCount(unsolvedAlertLogs.size());
        //近7天报警信息数量
        counterInfo.setLast7DaysAlertCount(latest7DaysAlertLogs.size());
        dashboard.setCounterInfo(counterInfo);

        // 报警排名
        List<AlertRank> alertRanks = this.getAlertRanks(context);
        dashboard.setAlertRanks(alertRanks);

        // 设备分布
        DeviceDistribution deviceDistribution = this.getDeviceDistribution(context);
        dashboard.setDeviceDistribution(deviceDistribution);

        // 报警日志
        DeviceAlertInfo deviceAlertInfo = this.getDeviceAlertInfo(context);
        dashboard.setDeviceAlertInfo(deviceAlertInfo);

        return dashboard;
    }

    @Override
    public ModelAndView exportXlsProductRatio(HttpServletRequest request) {
        String title = "产品分布";
        List<String> warehouseCodes = warehouseService.getWarehouseCodes(null);
        List<ProductRatio> allProductRatios = this.getProductRatios(warehouseCodes, true);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        log.info("sysUser={}", JSON.toJSONString(sysUser));

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, ProductRatio.class);
        ExportParams exportParams = new ExportParams(title + "报表", "", title);
        exportParams.setImageBasePath(jeecgBaseConfig.getPath().getUpload());
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, allProductRatios);

        return mv;
    }

    /**
     * 初始化上下文对象
     *
     * @return
     */
    private DashboardContext initContext(String warehouseCodes) {
        // 查询仓库编码范围
        List<String> warehouseCodeList = warehouseService.getWarehouseCodes(warehouseCodes);
        log.info("用户所属的仓库编码列表：{}", warehouseCodeList);

        // 查询仓库范围下的设备列表
        List<IotDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<IotDevice>()
                .in(IotDevice::getWarehouseCode, warehouseCodeList));
        // 提取出设备编码
        List<String> deviceCodes = devices.stream().map(IotDevice::getDeviceCode).collect(Collectors.toList());

        // 查询产品列表，基于设备
        List<IotProduct> products = productMapper.selectList(new LambdaQueryWrapper<IotProduct>())
                .stream()
                .filter(p -> devices.stream()
                        .map(IotDevice::getProductCode)
                        .collect(Collectors.toSet())
                        .contains(p.getProductCode()))
                .collect(Collectors.toList());

        Map<Object, Object> deviceStatusTable = redisUtil.hmget(RedisConst.DEVICE_STATUS_TABLE_KEY);
        // 设备状态校对
        devices.stream().forEach(d -> {
            if (EnableEnum.NO.getValue().equals(d.getIsEnable())) {
                d.setDeviceStatus(DeviceStatusEnum.DISABLE.getStatus());
            } else {
                Integer status = (Integer) deviceStatusTable.get(d.getDeviceCode());
                if (Objects.nonNull(status)) {
                    d.setDeviceStatus(status);
                }
            }
        });

        DashboardContext dashboardContext = new DashboardContext(products, devices);
        dashboardContext.setWarehouseCodeList(warehouseCodeList);
//        dashboardContext.setOrgCodes(warehouseCodeList);
        List<IotAlertLog> alertLogs = new ArrayList<>();
        // 报警信息查询，当前未解决的报警信息或近15天的报警信息，后续做分类使用
        //如果deviceCodes不空
        if (!CollectionUtils.isEmpty(deviceCodes)) {
             alertLogs = alertLogMapper.selectList(new LambdaQueryWrapper<IotAlertLog>()
                    .in(IotAlertLog::getDeviceCode, deviceCodes)
                    .and(QueryWrapper -> QueryWrapper
                            .eq(IotAlertLog::getProcessStatus, ProcessStatusEnum.UNSOLVED.getValue())
                            .or(qw -> qw.gt(IotAlertLog::getAlertTime, LocalDateTime.now().minusDays(15)))));
        }

        dashboardContext.setAlertLogs(alertLogs);

        return dashboardContext;
    }

    /**
     * 查询产品分布
     *
     * @return
     */
    private List<ProductRatio> getProductRatios(List<String> warehouseCodes, boolean getAllData) {
        List<ProductRatio> allProductRatioList = productMapper.selectProductRatio(warehouseCodes);
        if (CollectionUtils.isEmpty(allProductRatioList)) {
            return List.of();
        }

        // 获取数量前10的设备
        List<ProductRatio> top10 = allProductRatioList.stream()
                .sorted(Comparator.comparing(ProductRatio::getDeviceCount, Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());

        // 获取全量，直接返回
        if (getAllData) {
            return allProductRatioList.stream()
                    .sorted(Comparator.comparing(ProductRatio::getDeviceCount, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }

        if (allProductRatioList.size() > 10) {
            Set<String> top10ProductCodes = top10.stream().map(ProductRatio::getProductCode).collect(Collectors.toSet());
            int totalCount = allProductRatioList.stream()
                    .mapToInt(ProductRatio::getDeviceCount)
                    .sum();
            // 所有不在前10集合里的设备数量累加
            int otherDeviceCount = allProductRatioList.stream()
                    .filter(p -> !top10ProductCodes.contains(p.getProductCode()))
                    .mapToInt(ProductRatio::getDeviceCount)
                    .sum();

            Double otherRatio = Double.valueOf(otherDeviceCount) / totalCount;
            String format = String.format("%.2f", otherRatio * 100);
            ProductRatio otherRatioDto = new ProductRatio("其他", "other", otherDeviceCount, format);
            top10.add(otherRatioDto);
        }

        return top10;
    }

    /**
     * 获取报警排名
     *
     * @param context
     * @return
     */
    private List<AlertRank> getAlertRanks(DashboardContext context) {
        Map<String, List<IotAlertLog>> deviceAlertLogsMap = context.getDeviceAlertLogMap();
        if (CollectionUtils.isEmpty(deviceAlertLogsMap)) {
            return List.of();
        }

        // 近期天数，上一期天数
        final int days = 7, prevDays = 14;
        // 根据设备编码，统计7天内的报警记录，然后计算环比
        Map<String, Long> past7DaysAlertCountMap = deviceAlertLogsMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
                        .filter(alert -> LocalDateTimeUtil.of(alert.getAlertTime()).isAfter(LocalDateTime.now().minusDays(days)))
                        .count()));

        // 根据报警统计构造 rank 对象，并倒序取前 5
        List<AlertRank> topFive = past7DaysAlertCountMap.entrySet()
                .stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    // 构造 rank
                    AlertRank alertRank = new AlertRank();
                    alertRank.setDeviceCode(e.getKey());
                    alertRank.setDeviceName(context.getDeviceMap().get(e.getKey()).getDeviceName());
                    alertRank.setAlertCount(e.getValue());
                    return alertRank;
                }).collect(Collectors.toList());

        // 设置名次与环比
        for (int i = 0; i < topFive.size(); i++) {
            AlertRank rank = topFive.get(i);
            List<IotAlertLog> alertLogs = deviceAlertLogsMap.get(rank.getDeviceCode());
            // 计算上一期的报警数量
            long prevAlertCount = alertLogs.stream()
                    .filter(alert -> {
                        LocalDateTime alertTime = LocalDateTimeUtil.of(alert.getAlertTime());
                        return alertTime.isAfter(LocalDateTime.now().minusDays(prevDays))
                                && alertTime.isBefore(LocalDateTime.now().minusDays(days));
                    }).count();
            Long past7DaysAlertCount = past7DaysAlertCountMap.get(rank.getDeviceCode());
            rank.setRankNo(i + 1);
            rank.setPrevDiff(past7DaysAlertCount - prevAlertCount);
        }

        return topFive;
    }

    /**
     * 获取设备分布情况
     *
     * @param context
     * @return
     */
    private DeviceDistribution getDeviceDistribution(DashboardContext context) {
        // 产品数量、设备数量、报警数量
        DeviceDistribution deviceDistribution = new DeviceDistribution();
        // 找到1个包含仓库名称的设备
        String warehouseName = context.getDeviceMap().values().stream()
                .filter(device -> Objects.nonNull(device.getWarehouseName()))
                .findAny()
                .orElse(new IotDevice())
                .getWarehouseName();
        deviceDistribution.setWarehouseName(Optional.ofNullable(warehouseName).orElse("wms仓库"));
        deviceDistribution.setProductCount(context.getProductMap().size());
        deviceDistribution.setDeviceCount(context.getDeviceMap().size());
        deviceDistribution.setAlertCount(context.getAlertLogs().size());
        deviceDistribution.setProductDevicesList(new ArrayList<>());

        // 分组设备
        Map<String, IotDevice> deviceMap = context.getDeviceMap();
        Map<String, List<IotDevice>> productDevicesMap = deviceMap.values().stream()
                .collect(Collectors.groupingBy(IotDevice::getProductCode));

        productDevicesMap.entrySet().stream().forEach(e -> {
            String productCode = e.getKey();
            List<IotDevice> devices = e.getValue();

            DeviceDistribution.ProductDevices productDevices = new DeviceDistribution.ProductDevices();
            productDevices.setProductName(context.getProductMap().get(productCode).getProductName());
            productDevices.setDevices(new ArrayList<>());
            // 遍历设备列表
            devices.forEach(d -> {
                DeviceDistribution.DeviceInfo deviceInfo = new DeviceDistribution.DeviceInfo();
                deviceInfo.setDeviceName(d.getDeviceName());
                deviceInfo.setStatusName(DeviceStatusEnum.getByStatus(d.getDeviceStatus()).getStatusName());
                deviceInfo.setAlertStatus("正常");
                // 判断设备报警状态
                if (context.getDeviceAlertLogMap().containsKey(d.getDeviceCode())) {
                    boolean present = context.getDeviceAlertLogMap().get(d.getDeviceCode()).stream().filter(alert ->
                            ProcessStatusEnum.UNSOLVED.getValue().equals(alert.getProcessStatus())
                    ).findAny().isPresent();
                    deviceInfo.setAlertStatus(present ? "报警中" : "正常");
                }
                productDevices.getDevices().add(deviceInfo);
            });
            deviceDistribution.getProductDevicesList().add(productDevices);
        });

        return deviceDistribution;
    }

    /**
     * 获取设备报警信息
     *
     * @param context
     * @return
     */
    private DeviceAlertInfo getDeviceAlertInfo(DashboardContext context) {
        DeviceAlertInfo deviceAlertInfo = new DeviceAlertInfo();
        // 筛选出未解决的报警日志
        List<IotAlertLog> unsolvedAlerts = context.getAlertLogs().stream()
                .filter(alert -> ProcessStatusEnum.UNSOLVED.getValue().equals(alert.getProcessStatus()))
                .collect(Collectors.toList());

        Double total = Double.valueOf(unsolvedAlerts.size());

        Map<String, List<IotAlertLog>> levelMap = unsolvedAlerts.stream()
                .collect(Collectors.groupingBy(alert -> alert.getAlertLevel()));

        List<IotAlertLog> emerAlerts = Optional.ofNullable(levelMap.get(AlertLevelEnum.CRIT.getLevel())).orElse(List.of());
        List<IotAlertLog> warnAlerts = Optional.ofNullable(levelMap.get(AlertLevelEnum.WARN.getLevel())).orElse(List.of());
        List<IotAlertLog> generalAlerts = Optional.ofNullable(levelMap.get(AlertLevelEnum.GENERAL.getLevel())).orElse(List.of());

        deviceAlertInfo.setEmerCount(emerAlerts.size());
        deviceAlertInfo.setEmerRatio(total.equals(0.0) ? 0.0 : emerAlerts.size() / total * 100);
        deviceAlertInfo.setWarnCount(warnAlerts.size());
        deviceAlertInfo.setWarnRatio(total.equals(0.0) ? 0.0 : warnAlerts.size() / total * 100);
        deviceAlertInfo.setGeneralCount(generalAlerts.size());
        deviceAlertInfo.setGeneralRatio(total.equals(0.0) ? 0.0 : generalAlerts.size() / total * 100);
        deviceAlertInfo.setAlertLogs(new ArrayList<>());

        unsolvedAlerts.stream()
                .sorted(Comparator.comparing((IotAlertLog a) -> a.getAlertTime().getTime()).reversed())
                .forEach(alert -> {
                    DeviceAlertInfo.AlertLogInfo alertInfo = new DeviceAlertInfo.AlertLogInfo();
                    alertInfo.setId(alert.getId());
                    alertInfo.setRuleName(alert.getRuleName());
                    alertInfo.setAlertLevel(alert.getAlertLevel());
                    alertInfo.setDeviceName(alert.getDeviceName());
                    alertInfo.setAlertTime(DateUtil.format(alert.getAlertTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    deviceAlertInfo.getAlertLogs().add(alertInfo);
                });

        return deviceAlertInfo;
    }
}
