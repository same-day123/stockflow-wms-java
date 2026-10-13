package org.jeecg.modules.wms.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.waybill.sf.SfExpressUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 顺丰对接参数
 * @author: jeecg-boot
 * wms:
 *   express_api:
 *     client_code: Y2VL6F82
 *     check_word: jy1HZyiDinIuipRALDjdKksUtstLveBy
 *     call_url: https://sfapi-sbox.sf-express.com/std/service
 *     monthly_card: 7551234567
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name="client_code",prefix = "wms.express_api")
public class SfExpressConfig {
    @Value(value = "${wms.express_api.client_code}")
    private String client_code;
    @Value(value = "${wms.express_api.check_word}")
    private String check_word;
    @Value(value = "${wms.express_api.call_url}")
    private String call_url;
    @Value(value = "${wms.express_api.monthly_card}")
    private String monthly_card;

    @PostConstruct
    public void initExpress(){
        SfExpressUtil.setCall_url(call_url);
        SfExpressUtil.setCheck_word(check_word);
        SfExpressUtil.setClient_code(client_code);
        SfExpressUtil.setMonthly_card(monthly_card);
    }

}
