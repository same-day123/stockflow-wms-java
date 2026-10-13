package org.jeecg.modules.wms.wave.strategy;

import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;

import java.util.List;
import java.util.Map;

/**
 * 抽象策略类
 */
public abstract class IWaveStrategy {



    /**
     * 下一个策略类
     */
    protected IWaveStrategy nextStrategy;

    /**
     * 设置下一个策略类
     */
    public void setNextStrategy(IWaveStrategy nextStrategy) {
        this.nextStrategy = nextStrategy;
    }

    /**
     * 匹配订单，创建波次
     */
    abstract void process(List<WmsOutOrders> orders, Map<String,List<WmsOutOrdersAllocation>> allocationMap);

    /**
     * 获取策略类型
     */
    abstract String getStrategyType();

    /**
     * 获取策略优先级(数值越小优先级越高)
     */
    abstract int getPriority();

    /**
     * 下一个策略
     */
    public IWaveStrategy  next(){
        return nextStrategy;
    }

    /**
     * 执行下一个策略
     */
    protected void processNext(List<WmsOutOrders> orders, Map<String,List<WmsOutOrdersAllocation>> allocationMap) {
        // 判断下一个策略类和orders是否为空，不为空则执行下一个策略
        if (nextStrategy != null && orders.size() > 0 && orders != null) {
            nextStrategy.process(orders,allocationMap);
        }
    }
}
