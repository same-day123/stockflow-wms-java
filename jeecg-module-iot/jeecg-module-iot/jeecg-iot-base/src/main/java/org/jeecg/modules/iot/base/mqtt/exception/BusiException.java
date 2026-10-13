package org.jeecg.modules.iot.base.mqtt.exception;

/**
 * 业务异常
 *
 * @author muht
 * @create 2025/5/9 09:53
 */
public class BusiException extends RuntimeException {
    public BusiException(String msg) {
        super(msg);
    }
}
