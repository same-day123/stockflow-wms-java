package org.jeecg.modules.iot.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.manage.dto.CtrlDeviceReq;
import org.jeecg.modules.iot.manage.dto.DeviceHistoryData;
import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.vo.DeviceFieldModel;
import org.jeecg.modules.iot.manage.vo.DeviceServiceModel;
import org.jeecg.modules.iot.manage.vo.IotDeviceDetail;

/**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2025-04-24
 * @Version: V1.0
 */
public interface IIotDeviceService extends IService<IotDevice> {
    /**
     * 根据ID获取设备详情页信息
     *
     * @return
     */
    IotDeviceDetail getDetailById(String id);

    /**
     * 查询设备属性数据
     *
     * @return
     */
    IPage<DeviceFieldModel> queryDeviceFieldsPage(String id, String name, Integer pageNo, Integer pageSize);

    /**
     * 查询设备服务分页
     *
     * @param id
     * @param name
     * @return
     */
    IPage<DeviceServiceModel> queryDeviceServicesPage(String id, String name, Integer pageNo, Integer pageSize);

    /**
     * 设备控制
     *
     * @param ctrlDeviceReq
     * @return
     */
    String ctrlDevice(CtrlDeviceReq ctrlDeviceReq);

    /**
     * 查询设备历史数据
     *
     * @return
     */
    IPage<DeviceHistoryData> queryHistoryData(String id, String modelCode, Integer pageNo, Integer pageSize);

}
