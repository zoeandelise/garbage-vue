package com.ruoyi.garbage.service;

import java.util.List;

import com.ruoyi.garbage.domain.GarbageCategory;
import com.ruoyi.garbage.domain.GarbageCategoryQuery;

/**
 * 垃圾分类服务接口
 * 
 * @author ruoyi
 */
public interface IGarbageCategoryService {
    
    /**
     * 查询垃圾分类列表
     * 
     * @param query 查询条件
     * @return 垃圾分类列表
     */
    List<GarbageCategory> selectGarbageCategoryList(GarbageCategoryQuery query);
    
    /**
     * 查询垃圾分类详情
     * 
     * @param id 分类ID
     * @return 垃圾分类
     */
    GarbageCategory selectGarbageCategoryById(String id);
    
    /**
     * 新增垃圾分类
     * 
     * @param garbageCategory 垃圾分类
     * @return 结果
     */
    int insertGarbageCategory(GarbageCategory garbageCategory);
    
    /**
     * 修改垃圾分类
     * 
     * @param garbageCategory 垃圾分类
     * @return 结果
     */
    int updateGarbageCategory(GarbageCategory garbageCategory);
    
    /**
     * 删除垃圾分类
     * 
     * @param id 分类ID
     * @return 结果
     */
    int deleteGarbageCategoryById(String id);
    
    /**
     * 批量删除垃圾分类
     * 
     * @param ids 分类ID数组
     * @return 结果
     */
    int deleteGarbageCategoryByIds(String[] ids);
    
    /**
     * 获取所有启用的垃圾分类
     * 
     * @return 垃圾分类列表
     */
    List<GarbageCategory> selectAllEnabledCategories();
    
    /**
     * 检查分类名称是否唯一
     * 
     * @param garbageCategory 垃圾分类
     * @return 结果
     */
    String checkCategoryNameUnique(GarbageCategory garbageCategory);
    
    /**
     * 检查分类编码是否唯一
     * 
     * @param garbageCategory 垃圾分类
     * @return 结果
     */
    String checkCategoryCodeUnique(GarbageCategory garbageCategory);
} 