package com.ruoyi.garbage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB配置类
 * 
 * @author ruoyi
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.ruoyi.garbage.repository")
public class MongoDBConfig {
    // MongoDB配置已在application.yml中完成
} 