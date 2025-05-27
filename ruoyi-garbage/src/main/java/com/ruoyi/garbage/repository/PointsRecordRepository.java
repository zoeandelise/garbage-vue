package com.ruoyi.garbage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.PointsRecord;

/**
 * 积分记录仓库接口
 * 
 * @author ruoyi
 */
@Repository
public interface PointsRecordRepository extends MongoRepository<PointsRecord, String> {
    
    /**
     * 根据用户ID查询积分记录
     * 
     * @param userId 用户ID
     * @return 积分记录列表
     */
    List<PointsRecord> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询积分记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 积分记录分页结果
     */
    Page<PointsRecord> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据积分类型查询积分记录
     * 
     * @param pointsType 积分类型
     * @return 积分记录列表
     */
    List<PointsRecord> findByPointsType(Integer pointsType);
    
    /**
     * 根据关联记录ID查询积分记录
     * 
     * @param referenceId 关联记录ID
     * @return 积分记录
     */
    PointsRecord findByReferenceId(String referenceId);
}