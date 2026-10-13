package org.jeecg.modules.iot.manage.convert;

import org.jeecg.modules.iot.manage.entity.IotDevice;
import org.jeecg.modules.iot.manage.vo.IotDeviceDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author muht
 * @create 2025/5/5 13:39
 */
@Mapper
public interface DeviceConverter {

    DeviceConverter INSTANCE = Mappers.getMapper(DeviceConverter.class);

    IotDeviceDetail convertToDetail(IotDevice device);

}
