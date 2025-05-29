package com.ruoyi.garbage.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 数据初始化命令行工具
 * 通过启动参数触发：--init.points.data=true
 * 
 * @author ruoyi
 */
@Component
@ConditionalOnProperty(name = "init.points.data", havingValue = "true")
public class DataInitRunner implements CommandLineRunner {
    
    @Autowired
    private ManualDataInitializer dataInitializer;
    
    @Override
    public void run(String... args) {
        System.out.println("=== 开始初始化积分数据 ===");
        dataInitializer.manualInit();
        System.out.println("=== 积分数据初始化完成 ===");
    }
} 