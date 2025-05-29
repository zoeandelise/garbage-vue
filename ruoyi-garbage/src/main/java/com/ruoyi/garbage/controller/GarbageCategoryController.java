package com.ruoyi.garbage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.garbage.domain.GarbageCategory;
import com.ruoyi.garbage.domain.GarbageCategoryQuery;
import com.ruoyi.garbage.service.IGarbageCategoryService;

/**
 * 垃圾分类管理 控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/category")
public class GarbageCategoryController extends BaseController {
    
    @Autowired
    private IGarbageCategoryService garbageCategoryService;
    
    /**
     * 获取垃圾分类列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(GarbageCategoryQuery garbageCategoryQuery) {
        startPage();
        List<GarbageCategory> list = garbageCategoryService.selectGarbageCategoryList(garbageCategoryQuery);
        return getDataTable(list);
    }
    
    /**
     * 获取所有启用的垃圾分类
     */
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<GarbageCategory> list = garbageCategoryService.selectAllEnabledCategories();
        return AjaxResult.success(list);
    }
    
    /**
     * 获取垃圾分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:category:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(garbageCategoryService.selectGarbageCategoryById(id));
    }
    
    /**
     * 新增垃圾分类
     */
    @PreAuthorize("@ss.hasPermi('garbage:category:add')")
    @Log(title = "垃圾分类管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GarbageCategory garbageCategory) {
        // 校验分类名称和编码唯一性
        if ("1".equals(garbageCategoryService.checkCategoryNameUnique(garbageCategory))) {
            return AjaxResult.error("新增垃圾分类'" + garbageCategory.getCategoryName() + "'失败，分类名称已存在");
        }
        if ("1".equals(garbageCategoryService.checkCategoryCodeUnique(garbageCategory))) {
            return AjaxResult.error("新增垃圾分类'" + garbageCategory.getCategoryName() + "'失败，分类编码已存在");
        }
        
        return toAjax(garbageCategoryService.insertGarbageCategory(garbageCategory));
    }
    
    /**
     * 修改垃圾分类
     */
    @PreAuthorize("@ss.hasPermi('garbage:category:edit')")
    @Log(title = "垃圾分类管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GarbageCategory garbageCategory) {
        // 校验分类名称和编码唯一性
        if ("1".equals(garbageCategoryService.checkCategoryNameUnique(garbageCategory))) {
            return AjaxResult.error("修改垃圾分类'" + garbageCategory.getCategoryName() + "'失败，分类名称已存在");
        }
        if ("1".equals(garbageCategoryService.checkCategoryCodeUnique(garbageCategory))) {
            return AjaxResult.error("修改垃圾分类'" + garbageCategory.getCategoryName() + "'失败，分类编码已存在");
        }
        
        return toAjax(garbageCategoryService.updateGarbageCategory(garbageCategory));
    }
    
    /**
     * 删除垃圾分类
     */
    @PreAuthorize("@ss.hasPermi('garbage:category:remove')")
    @Log(title = "垃圾分类管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(garbageCategoryService.deleteGarbageCategoryByIds(ids));
    }
    
    /**
     * 校验分类名称唯一性
     */
    @GetMapping("/checkCategoryNameUnique")
    public AjaxResult checkCategoryNameUnique(GarbageCategory garbageCategory) {
        String result = garbageCategoryService.checkCategoryNameUnique(garbageCategory);
        return AjaxResult.success(result);
    }
    
    /**
     * 校验分类编码唯一性
     */
    @GetMapping("/checkCategoryCodeUnique")
    public AjaxResult checkCategoryCodeUnique(GarbageCategory garbageCategory) {
        String result = garbageCategoryService.checkCategoryCodeUnique(garbageCategory);
        return AjaxResult.success(result);
    }
} 