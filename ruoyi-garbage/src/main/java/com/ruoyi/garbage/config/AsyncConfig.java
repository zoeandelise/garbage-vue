package com.ruoyi.garbage.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步配置类
 * 
 * @author ruoyi
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 定义异步任务执行器
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列大小
        executor.setQueueCapacity(500);
        // 线程名前缀
        executor.setThreadNamePrefix("garbage-async-");
        // 初始化
        executor.initialize();
        return executor;
    }
} 