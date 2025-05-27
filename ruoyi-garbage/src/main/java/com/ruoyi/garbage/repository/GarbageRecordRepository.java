package com.ruoyi.garbage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.GarbageRecord;

/**
 * 垃圾投递记录仓库接口
 * 
 * @author ruoyi
 */
@Repository
public interface GarbageRecordRepository extends MongoRepository<GarbageRecord, String> {
    
    /**
     * 根据用户ID查询垃圾投递记录
     * 
     * @param userId 用户ID
     * @return 垃圾投递记录列表
     */
    List<GarbageRecord> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询垃圾投递记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 垃圾投递记录分页结果
     */
    Page<GarbageRecord> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据垃圾类型查询垃圾投递记录
     * 
     * @param garbageType 垃圾类型
     * @return 垃圾投递记录列表
     */
    List<GarbageRecord> findByGarbageType(String garbageType);
    
    /**
     * 查询未计算积分的垃圾投递记录
     * 
     * @return 未计算积分的垃圾投递记录列表
     */
    List<GarbageRecord> findByPointsCalculatedIsFalseOrPointsCalculatedIsNull();
} 