package com.ruoyi.garbage.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.PointsRecord;

/**
 * 积分记录数据访问层
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
     * 根据用户ID和时间范围查询积分记录
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 积分记录列表
     */
    List<PointsRecord> findByUserIdAndCreateTimeBetween(Long userId, Date startTime, Date endTime);
    
    /**
     * 根据用户ID和积分类型查询积分记录
     * 
     * @param userId 用户ID
     * @param type 积分类型
     * @return 积分记录列表
     */
    List<PointsRecord> findByUserIdAndType(Long userId, Integer type);
    
    /**
     * 根据用户ID和积分来源查询积分记录
     * 
     * @param userId 用户ID
     * @param source 积分来源
     * @return 积分记录列表
     */
    List<PointsRecord> findByUserIdAndSource(Long userId, Integer source);
    
    /**
     * 根据用户名称模糊查询积分记录
     * 
     * @param userName 用户名称
     * @return 积分记录列表
     */
    List<PointsRecord> findByUserNameLike(String userName);
    
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
     * @param type 积分类型
     * @return 积分记录列表
     */
    List<PointsRecord> findByType(Integer type);
    
    /**
     * 根据业务ID查询积分记录
     * 
     * @param businessId 业务ID
     * @return 积分记录
     */
    PointsRecord findByBusinessId(String businessId);
}