package com.ruoyi.garbage.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.GarbageCategory;

/**
 * 垃圾分类Repository接口
 * 
 * @author ruoyi
 */
@Repository
public interface GarbageCategoryRepository extends MongoRepository<GarbageCategory, String> {
    
    /**
     * 根据分类名称查询
     * 
     * @param categoryName 分类名称
     * @return 垃圾分类
     */
    GarbageCategory findByCategoryName(String categoryName);
    
    /**
     * 根据分类编码查询
     * 
     * @param categoryCode 分类编码
     * @return 垃圾分类
     */
    GarbageCategory findByCategoryCode(String categoryCode);
    
    /**
     * 根据状态查询
     * 
     * @param status 状态
     * @return 垃圾分类列表
     */
    List<GarbageCategory> findByStatus(Integer status);
    
    /**
     * 根据状态和分类名称模糊查询
     * 
     * @param status 状态
     * @param categoryName 分类名称
     * @return 垃圾分类列表
     */
    List<GarbageCategory> findByStatusAndCategoryNameLike(Integer status, String categoryName);
} 