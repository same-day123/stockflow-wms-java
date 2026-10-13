package org.jeecg.modules.iot.alert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.jeecg"})
//@MapperScan(basePackages = {"org.jeecg.modules.iot.alert.mapper"})
@MapperScan(value={"org.jeecg.**.mapper*"})
@EnableScheduling
public class IotAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotAlertApplication.class, args);
    }

}
