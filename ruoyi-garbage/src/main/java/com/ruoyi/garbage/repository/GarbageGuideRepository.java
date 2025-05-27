package com.ruoyi.garbage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.GarbageGuide;

/**
 * 垃圾分类指南仓库接口
 * 
 * @author ruoyi
 */
@Repository
public interface GarbageGuideRepository extends MongoRepository<GarbageGuide, String> {
    
    /**
     * 根据垃圾名称模糊查询
     * 
     * @param garbageName 垃圾名称
     * @return 垃圾分类指南列表
     */
    @Query("{'garbageName': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByGarbageNameLike(String garbageName);
    
    /**
     * 根据垃圾名称模糊查询（分页）
     * 
     * @param garbageName 垃圾名称
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    @Query("{'garbageName': {$regex: ?0, $options: 'i'}}")
    Page<GarbageGuide> findByGarbageNameLike(String garbageName, Pageable pageable);
    
    /**
     * 根据垃圾类型查询
     * 
     * @param garbageType 垃圾类型
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> findByGarbageType(String garbageType);
    
    /**
     * 根据垃圾类型查询（分页）
     * 
     * @param garbageType 垃圾类型
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> findByGarbageType(String garbageType, Pageable pageable);
    
    /**
     * 根据垃圾名称精确查询
     * 
     * @param garbageName 垃圾名称
     * @return 垃圾分类指南
     */
    GarbageGuide findByGarbageName(String garbageName);
    
    /**
     * 根据描述模糊查询
     * 
     * @param description 描述
     * @return 垃圾分类指南列表
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByDescriptionLike(String description);
} 