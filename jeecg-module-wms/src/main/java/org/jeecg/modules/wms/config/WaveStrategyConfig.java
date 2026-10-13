package org.jeecg.modules.wms.config;

import org.jeecg.modules.wms.outorder.service.IWmsOutOrdersItemsService;
import org.jeecg.modules.wms.wave.service.IWaveStrategyService;
import org.jeecg.modules.wms.wave.service.IWmsWaveMasterService;
import org.jeecg.modules.wms.wave.strategy.SIFQWaveStrategy;
import org.jeecg.modules.wms.wave.strategy.SISQWaveStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WaveStrategyConfig {



    @Bean
    @Qualifier("sifq2Strategy")
    public SIFQWaveStrategy sifq2Strategy(IWmsWaveMasterService wmsWaveMasterService, IWaveStrategyService waveStrategyService, IWmsOutOrdersItemsService wmsOutOrdersItemsService) {
        SIFQWaveStrategy strategy = new SIFQWaveStrategy(wmsWaveMasterService,waveStrategyService,wmsOutOrdersItemsService);
        strategy.setQuantity(2);
        return strategy;
    }

    @Bean
    @Qualifier("sifq3Strategy")
    public SIFQWaveStrategy sifq3Strategy(IWmsWaveMasterService wmsWaveMasterService, IWaveStrategyService waveStrategyService, IWmsOutOrdersItemsService wmsOutOrdersItemsService) {
        SIFQWaveStrategy strategy = new SIFQWaveStrategy(wmsWaveMasterService,waveStrategyService,wmsOutOrdersItemsService);
        strategy.setQuantity(3);
        return strategy;
    }

    // 其他策略bean...
}
