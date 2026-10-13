package org.jeecg.modules.iot.simulate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.jeecg"})
public class IotSimulateApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotSimulateApplication.class, args);
    }

}
