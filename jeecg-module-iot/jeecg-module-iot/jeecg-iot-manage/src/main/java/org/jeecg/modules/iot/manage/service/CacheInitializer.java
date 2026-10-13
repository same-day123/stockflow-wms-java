package org.jeecg.modules.iot.manage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author muht
 * @create 2025/5/23 17:35
 */
@Slf4j
@Component
public class CacheInitializer implements ApplicationRunner {

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(ApplicationArguments args) {
        cacheService.initDeviceStatusTable();
    }
}
