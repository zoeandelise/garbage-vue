package com.ruoyi.garbage.service;

import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;

import java.util.List;

/**
 * 积分记录服务接口
 * 
 * @author ruoyi
 */
public interface IPointsRecordService {
    
    /**
     * 根据查询条件分页查询积分记录
     * 
     * @param query 查询条件
     * @return 积分记录列表
     */
    List<PointsRecord> selectPointsRecordList(PointsRecordQuery query);
    
    /**
     * 根据查询条件统计积分记录数量
     * 
     * @param query 查询条件
     * @return 记录数量
     */
    long countPointsRecord(PointsRecordQuery query);
    
    /**
     * 根据ID查询积分记录
     * 
     * @param id 记录ID
     * @return 积分记录
     */
    PointsRecord selectPointsRecordById(String id);
    
    /**
     * 新增积分记录
     * 
     * @param pointsRecord 积分记录
     * @return 结果
     */
    int insertPointsRecord(PointsRecord pointsRecord);
    
    /**
     * 修改积分记录
     * 
     * @param pointsRecord 积分记录
     * @return 结果
     */
    int updatePointsRecord(PointsRecord pointsRecord);
    
    /**
     * 批量删除积分记录
     * 
     * @param ids 需要删除的记录ID数组
     * @return 结果
     */
    int deletePointsRecordByIds(String[] ids);
    
    /**
     * 删除积分记录
     * 
     * @param id 记录ID
     * @return 结果
     */
    int deletePointsRecordById(String id);
    
    /**
     * 统计用户总积分
     * 
     * @param userId 用户ID
     * @return 总积分
     */
    int getUserTotalPoints(Long userId);
} 