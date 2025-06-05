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
     * @param name 垃圾名称
     * @return 垃圾分类指南列表
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByNameLike(String name);
    
    /**
     * 根据垃圾名称模糊查询（分页）
     * 
     * @param name 垃圾名称
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<GarbageGuide> findByNameLike(String name, Pageable pageable);
    
    /**
     * 根据垃圾类型查询
     * 
     * @param category 垃圾类型
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> findByCategory(String category);
    
    /**
     * 根据垃圾类型查询（分页）
     * 
     * @param category 垃圾类型
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据垃圾名称精确查询
     * 
     * @param name 垃圾名称
     * @return 垃圾分类指南
     */
    GarbageGuide findByName(String name);
    
    /**
     * 根据详细描述模糊查询
     * 
     * @param description 详细描述
     * @return 垃圾分类指南列表
     */
    @Query("{'detailed_description': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByDetailedDescriptionLike(String description);
    
    /**
     * 根据备注模糊查询
     * 
     * @param remark 备注
     * @return 垃圾分类指南列表
     */
    @Query("{'remark': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByRemarkLike(String remark);
    
    /**
     * 根据包含物品模糊查询
     * 
     * @param item 物品名称
     * @return 垃圾分类指南列表
     */
    @Query("{'included_items': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByIncludedItemsLike(String item);
    
    /**
     * 兼容旧版本的方法
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByGarbageNameLike(String garbageName);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<GarbageGuide> findByGarbageNameLike(String garbageName, Pageable pageable);
    
    List<GarbageGuide> findByGarbageType(String garbageType);
    
    Page<GarbageGuide> findByGarbageType(String garbageType, Pageable pageable);
    
    GarbageGuide findByGarbageName(String garbageName);
    
    @Query("{'detailed_description': {$regex: ?0, $options: 'i'}}")
    List<GarbageGuide> findByDescriptionLike(String description);
} 