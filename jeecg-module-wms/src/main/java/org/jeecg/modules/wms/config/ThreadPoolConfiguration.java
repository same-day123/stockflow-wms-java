package org.jeecg.modules.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 波次处理任务线程池
 *
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean("waybillThreadPool")
    public ThreadPoolExecutor waybillThreadPool() {
        int corePoolSize = 10;          // 核心线程数
        int maximumPoolSize = 30;      // 最大线程数
        long keepAliveTime = 30L;      // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        int queueCapacity = 2000;       // 队列容量

        // 创建任务队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);

        // 创建拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

        // 创建线程池
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                Executors.defaultThreadFactory(),
                handler
        );
    }
}