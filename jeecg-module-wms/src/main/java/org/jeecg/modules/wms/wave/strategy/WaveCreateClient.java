package org.jeecg.modules.wms.wave.strategy;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 创建责任链客户端
 */
public class WaveCreateClient {

    /**
     * 定义一个链头策略对象
     */
    private IWaveStrategy waveStrategy;

    /**
     * 在构造方法中将所有的策略对象组成一个链
     * @param strategies 所有策略对象
     * @param selectedStrategies 页面传入
     */
    public WaveCreateClient(List<IWaveStrategy> strategies,List<String> selectedStrategies) {
        //从strategies这个里面找到所有的策略类并且按优先级升序排序
        List<IWaveStrategy> collect = strategies.stream()
                .filter(strategy -> selectedStrategies.contains(strategy.getStrategyType()))
                .sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
                .collect(Collectors.toList());
        //创建一个链头
        this.waveStrategy = collect.get(0);
        //将selectedStrategies组成一个链
        for (int i = 1; i < collect.size(); i++) {
            IWaveStrategy currentStrategy = collect.get(i-1);//当前策略
            currentStrategy.setNextStrategy(collect.get(i));//设置下一个策略
        }

    }
    /**
     * 处理订单波次分配
     */
    public void process(List<WmsOutOrders> orders, Map<String, List<WmsOutOrdersAllocation>> allocationsMap) {
        if(waveStrategy == null){
            throw new JeecgBootException("没有配置波次策略");
        }
        waveStrategy.process(orders, allocationsMap);
    }

}
