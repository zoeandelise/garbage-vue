package com.ruoyi.garbage.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ruoyi.garbage.domain.GarbageItem;

/**
 * 垃圾物品Repository接口
 * 
 * @author ruoyi
 */
@Repository
public interface GarbageItemRepository extends MongoRepository<GarbageItem, String> {
    
    /**
     * 根据物品名称查询
     * 
     * @param itemName 物品名称
     * @return 垃圾物品
     */
    GarbageItem findByItemName(String itemName);
    
    /**
     * 根据分类ID查询
     * 
     * @param categoryId 分类ID
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByCategoryId(String categoryId);
    
    /**
     * 根据状态查询
     * 
     * @param status 状态
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByStatus(Integer status);
    
    /**
     * 根据是否常见查询
     * 
     * @param isCommon 是否常见
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByIsCommon(Boolean isCommon);
    
    /**
     * 根据关键词模糊查询
     * 
     * @param keywords 关键词
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByKeywordsLike(String keywords);
    
    /**
     * 根据物品名称模糊查询
     * 
     * @param itemName 物品名称
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByItemNameLike(String itemName);
    
    /**
     * 根据分类ID和状态查询
     * 
     * @param categoryId 分类ID
     * @param status 状态
     * @return 垃圾物品列表
     */
    List<GarbageItem> findByCategoryIdAndStatus(String categoryId, Integer status);
} 