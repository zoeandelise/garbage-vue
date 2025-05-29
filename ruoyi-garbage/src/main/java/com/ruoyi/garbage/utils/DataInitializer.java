package com.ruoyi.garbage.utils;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.repository.PointsRecordRepository;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数据初始化工具类
 * 用于生成测试数据
 * 
 * @author ruoyi
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据
        long count = pointsRecordRepository.count();
        if (count > 0) {
            log.info("已存在{}条积分记录，跳过初始化", count);
            return;
        }
        
        // 获取所有用户
        SysUser queryUser = new SysUser();
        List<SysUser> users = userService.selectUserList(queryUser);
        
        if (users.isEmpty()) {
            log.warn("未找到用户数据，无法初始化积分记录");
            return;
        }
        
        log.info("开始初始化积分记录数据...");
        
        // 生成积分记录
        List<PointsRecord> records = new ArrayList<>();
        
        // 垃圾类型
        String[] garbageTypes = {"可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"};
        
        // 积分来源描述
        Map<Integer, String> sourceDescMap = new HashMap<>();
        sourceDescMap.put(1, "垃圾投递积分奖励");
        sourceDescMap.put(2, "管理员调整");
        sourceDescMap.put(3, "积分兑换");
        
        // 生成过去90天的数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -90);
        Date startDate = calendar.getTime();
        Date endDate = new Date();
        
        // 为每个用户生成积分记录
        for (SysUser user : users) {
            // 每个用户生成10-30条记录
            int recordCount = ThreadLocalRandom.current().nextInt(10, 31);
            
            for (int i = 0; i < recordCount; i++) {
                PointsRecord record = new PointsRecord();
                record.setId(UUID.randomUUID().toString());
                record.setUserId(user.getUserId());
                record.setUserName(user.getUserName());
                
                // 随机积分类型：1-增加，2-减少
                int type = ThreadLocalRandom.current().nextInt(1, 10) <= 8 ? 1 : 2; // 80%概率增加积分
                record.setType(type);
                
                // 随机积分来源：1-垃圾投递，2-管理员调整，3-积分兑换
                int source;
                if (type == 1) {
                    // 增加积分：80%来自垃圾投递，20%来自管理员调整
                    source = ThreadLocalRandom.current().nextInt(1, 11) <= 8 ? 1 : 2;
                } else {
                    // 减少积分：80%来自积分兑换，20%来自管理员调整
                    source = ThreadLocalRandom.current().nextInt(1, 11) <= 8 ? 3 : 2;
                }
                record.setSource(source);
                
                // 随机积分数量
                int points;
                if (source == 1) {
                    // 垃圾投递：5-50积分
                    points = ThreadLocalRandom.current().nextInt(5, 51);
                } else if (source == 2) {
                    // 管理员调整：10-100积分
                    points = ThreadLocalRandom.current().nextInt(10, 101);
                } else {
                    // 积分兑换：20-200积分
                    points = ThreadLocalRandom.current().nextInt(20, 201);
                }
                record.setPoints(points);
                
                // 随机创建时间
                Date randomDate = randomDateBetween(startDate, endDate);
                record.setCreateTime(randomDate);
                
                // 设置备注
                String remark;
                if (source == 1) {
                    // 垃圾投递
                    String garbageType = garbageTypes[ThreadLocalRandom.current().nextInt(garbageTypes.length)];
                    double weight = ThreadLocalRandom.current().nextDouble(0.5, 10.0);
                    weight = Math.round(weight * 10) / 10.0; // 保留一位小数
                    remark = String.format("投递%s %.1fkg", garbageType, weight);
                } else {
                    // 其他来源
                    remark = sourceDescMap.get(source);
                }
                record.setRemark(remark);
                
                // 设置创建者
                record.setCreateBy("admin");
                
                // 添加到列表
                records.add(record);
            }
        }
        
        // 保存所有记录
        pointsRecordRepository.saveAll(records);
        
        log.info("成功初始化{}条积分记录数据", records.size());
    }
    
    /**
     * 生成两个日期之间的随机日期
     */
    private Date randomDateBetween(Date startDate, Date endDate) {
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);
        return new Date(randomMillis);
    }
} 