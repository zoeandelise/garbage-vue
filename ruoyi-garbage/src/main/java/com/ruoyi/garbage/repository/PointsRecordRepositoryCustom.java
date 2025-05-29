package com.ruoyi.garbage.repository;

import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;

import java.util.List;

/**
 * 积分记录自定义数据访问接口
 * 
 * @author ruoyi
 */
public interface PointsRecordRepositoryCustom {
    
    /**
     * 根据查询条件分页查询积分记录
     * 
     * @param query 查询条件
     * @return 积分记录列表
     */
    List<PointsRecord> findByCondition(PointsRecordQuery query);
    
    /**
     * 根据查询条件统计积分记录数量
     * 
     * @param query 查询条件
     * @return 记录数量
     */
    long countByCondition(PointsRecordQuery query);
    
    /**
     * 统计用户总积分
     * 
     * @param userId 用户ID
     * @return 总积分
     */
    int sumPointsByUserId(Long userId);
} 