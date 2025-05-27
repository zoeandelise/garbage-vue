package com.ruoyi.garbage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ruoyi.garbage.domain.GarbageRecord;

/**
 * 垃圾投递记录服务接口
 * 
 * @author ruoyi
 */
public interface IGarbageRecordService {
    
    /**
     * 保存垃圾投递记录
     * 
     * @param record 垃圾投递记录
     * @return 保存的垃圾投递记录
     */
    GarbageRecord saveRecord(GarbageRecord record);
    
    /**
     * 根据ID查询垃圾投递记录
     * 
     * @param id 记录ID
     * @return 垃圾投递记录
     */
    GarbageRecord getRecordById(String id);
    
    /**
     * 根据用户ID查询垃圾投递记录
     * 
     * @param userId 用户ID
     * @return 垃圾投递记录列表
     */
    List<GarbageRecord> getRecordsByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询垃圾投递记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 垃圾投递记录分页结果
     */
    Page<GarbageRecord> getRecordsByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据垃圾类型查询垃圾投递记录
     * 
     * @param garbageType 垃圾类型
     * @return 垃圾投递记录列表
     */
    List<GarbageRecord> getRecordsByGarbageType(String garbageType);
    
    /**
     * 查询所有垃圾投递记录
     * 
     * @return 垃圾投递记录列表
     */
    List<GarbageRecord> getAllRecords();
    
    /**
     * 分页查询所有垃圾投递记录
     * 
     * @param pageable 分页参数
     * @return 垃圾投递记录分页结果
     */
    Page<GarbageRecord> getAllRecords(Pageable pageable);
    
    /**
     * 更新垃圾投递记录
     * 
     * @param record 垃圾投递记录
     * @return 更新后的垃圾投递记录
     */
    GarbageRecord updateRecord(GarbageRecord record);
    
    /**
     * 删除垃圾投递记录
     * 
     * @param id 记录ID
     */
    void deleteRecord(String id);
}