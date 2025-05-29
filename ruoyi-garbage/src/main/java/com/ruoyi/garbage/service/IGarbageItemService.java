package com.ruoyi.garbage.service;

import java.util.List;

import com.ruoyi.garbage.domain.GarbageItem;
import com.ruoyi.garbage.domain.GarbageItemQuery;

/**
 * 垃圾物品服务接口
 * 
 * @author ruoyi
 */
public interface IGarbageItemService {
    
    /**
     * 查询垃圾物品列表
     * 
     * @param query 查询条件
     * @return 垃圾物品列表
     */
    List<GarbageItem> selectGarbageItemList(GarbageItemQuery query);
    
    /**
     * 查询垃圾物品详情
     * 
     * @param id 物品ID
     * @return 垃圾物品
     */
    GarbageItem selectGarbageItemById(String id);
    
    /**
     * 新增垃圾物品
     * 
     * @param garbageItem 垃圾物品
     * @return 结果
     */
    int insertGarbageItem(GarbageItem garbageItem);
    
    /**
     * 修改垃圾物品
     * 
     * @param garbageItem 垃圾物品
     * @return 结果
     */
    int updateGarbageItem(GarbageItem garbageItem);
    
    /**
     * 删除垃圾物品
     * 
     * @param id 物品ID
     * @return 结果
     */
    int deleteGarbageItemById(String id);
    
    /**
     * 批量删除垃圾物品
     * 
     * @param ids 物品ID数组
     * @return 结果
     */
    int deleteGarbageItemByIds(String[] ids);
    
    /**
     * 根据分类ID查询物品列表
     * 
     * @param categoryId 分类ID
     * @return 物品列表
     */
    List<GarbageItem> selectItemsByCategoryId(String categoryId);
    
    /**
     * 根据关键词搜索垃圾物品
     * 
     * @param keyword 关键词
     * @return 垃圾物品列表
     */
    List<GarbageItem> searchGarbageItems(String keyword);
    
    /**
     * 检查物品名称是否唯一
     * 
     * @param garbageItem 垃圾物品
     * @return 结果
     */
    String checkItemNameUnique(GarbageItem garbageItem);
} 