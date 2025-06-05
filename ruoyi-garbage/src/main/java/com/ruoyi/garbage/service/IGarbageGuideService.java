package com.ruoyi.garbage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ruoyi.garbage.domain.GarbageGuide;

/**
 * 垃圾分类指南服务接口
 * 
 * @author ruoyi
 */
public interface IGarbageGuideService {
    
    /**
     * 保存垃圾分类指南
     * 
     * @param guide 垃圾分类指南
     * @return 保存的垃圾分类指南
     */
    GarbageGuide saveGuide(GarbageGuide guide);
    
    /**
     * 根据ID查询垃圾分类指南
     * 
     * @param id 指南ID
     * @return 垃圾分类指南
     */
    GarbageGuide getGuideById(String id);
    
    /**
     * 根据垃圾名称精确查询
     * 
     * @param name 垃圾名称
     * @return 垃圾分类指南
     */
    GarbageGuide getGuideByName(String name);
    
    /**
     * 根据垃圾名称模糊查询
     * 
     * @param name 垃圾名称
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> searchGuidesByName(String name);
    
    /**
     * 根据垃圾名称模糊查询（分页）
     * 
     * @param name 垃圾名称
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> searchGuidesByName(String name, Pageable pageable);
    
    /**
     * 根据垃圾类型查询
     * 
     * @param category 垃圾类型
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> getGuidesByCategory(String category);
    
    /**
     * 根据垃圾类型查询（分页）
     * 
     * @param category 垃圾类型
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> getGuidesByCategory(String category, Pageable pageable);
    
    /**
     * 查询所有垃圾分类指南
     * 
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> getAllGuides();
    
    /**
     * 分页查询所有垃圾分类指南
     * 
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> getAllGuides(Pageable pageable);
    
    /**
     * 更新垃圾分类指南
     * 
     * @param guide 垃圾分类指南
     * @return 更新后的垃圾分类指南
     */
    GarbageGuide updateGuide(GarbageGuide guide);
    
    /**
     * 删除垃圾分类指南
     * 
     * @param id 指南ID
     */
    void deleteGuide(String id);
    
    /**
     * 搜索垃圾分类指南（综合搜索）
     * 
     * @param keyword 关键字
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> searchGuides(String keyword);
    
    /**
     * 获取所有垃圾类型
     * 
     * @return 垃圾类型列表
     */
    List<String> getAllGarbageTypes();
    
    /**
     * 根据垃圾名称精确查询（兼容旧版本）
     * 
     * @param garbageName 垃圾名称
     * @return 垃圾分类指南
     */
    GarbageGuide getGuideByGarbageName(String garbageName);
    
    /**
     * 根据垃圾名称模糊查询（兼容旧版本）
     * 
     * @param garbageName 垃圾名称
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> searchGuidesByGarbageName(String garbageName);
    
    /**
     * 根据垃圾名称模糊查询（分页）（兼容旧版本）
     * 
     * @param garbageName 垃圾名称
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> searchGuidesByGarbageName(String garbageName, Pageable pageable);
    
    /**
     * 根据垃圾类型查询（兼容旧版本）
     * 
     * @param garbageType 垃圾类型
     * @return 垃圾分类指南列表
     */
    List<GarbageGuide> getGuidesByGarbageType(String garbageType);
    
    /**
     * 根据垃圾类型查询（分页）（兼容旧版本）
     * 
     * @param garbageType 垃圾类型
     * @param pageable 分页参数
     * @return 垃圾分类指南分页结果
     */
    Page<GarbageGuide> getGuidesByType(String garbageType, Pageable pageable);
} 