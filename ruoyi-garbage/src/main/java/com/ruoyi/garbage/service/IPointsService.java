package com.ruoyi.garbage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ruoyi.garbage.domain.PointsRecord;

/**
 * 积分服务接口
 * 
 * @author ruoyi
 */
public interface IPointsService {
    
    /**
     * 计算垃圾投递积分
     * 
     * @param recordId 垃圾投递记录ID
     * @return 积分记录
     */
    PointsRecord calculatePoints(String recordId);
    
    /**
     * 保存积分记录
     * 
     * @param pointsRecord 积分记录
     * @return 保存的积分记录
     */
    PointsRecord savePointsRecord(PointsRecord pointsRecord);
    
    /**
     * 根据ID查询积分记录
     * 
     * @param id 记录ID
     * @return 积分记录
     */
    PointsRecord getPointsRecordById(String id);
    
    /**
     * 根据用户ID查询积分记录
     * 
     * @param userId 用户ID
     * @return 积分记录列表
     */
    List<PointsRecord> getPointsRecordsByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询积分记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 积分记录分页结果
     */
    Page<PointsRecord> getPointsRecordsByUserId(Long userId, Pageable pageable);
    
    /**
     * 查询用户总积分
     * 
     * @param userId 用户ID
     * @return 用户总积分
     */
    int getTotalPoints(Long userId);
    
    /**
     * 消费积分
     * 
     * @param userId 用户ID
     * @param userName 用户名
     * @param points 消费积分数量
     * @param remark 备注
     * @return 积分记录
     */
    PointsRecord consumePoints(Long userId, String userName, int points, String remark);
} 