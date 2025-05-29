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
import com.ruoyi.garbage.domain.GarbageItem;
import com.ruoyi.garbage.domain.GarbageItemQuery;
import com.ruoyi.garbage.service.IGarbageItemService;

/**
 * 垃圾物品管理 控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/item")
public class GarbageItemController extends BaseController {
    
    @Autowired
    private IGarbageItemService garbageItemService;
    
    /**
     * 获取垃圾物品列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:item:list')")
    @GetMapping("/list")
    public TableDataInfo list(GarbageItemQuery garbageItemQuery) {
        startPage();
        List<GarbageItem> list = garbageItemService.selectGarbageItemList(garbageItemQuery);
        return getDataTable(list);
    }
    
    /**
     * 获取垃圾物品详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:item:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(garbageItemService.selectGarbageItemById(id));
    }
    
    /**
     * 新增垃圾物品
     */
    @PreAuthorize("@ss.hasPermi('garbage:item:add')")
    @Log(title = "垃圾物品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GarbageItem garbageItem) {
        if ("1".equals(garbageItemService.checkItemNameUnique(garbageItem))) {
            return AjaxResult.error("新增垃圾物品'" + garbageItem.getItemName() + "'失败，物品名称已存在");
        }
        return toAjax(garbageItemService.insertGarbageItem(garbageItem));
    }
    
    /**
     * 修改垃圾物品
     */
    @PreAuthorize("@ss.hasPermi('garbage:item:edit')")
    @Log(title = "垃圾物品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GarbageItem garbageItem) {
        if ("1".equals(garbageItemService.checkItemNameUnique(garbageItem))) {
            return AjaxResult.error("修改垃圾物品'" + garbageItem.getItemName() + "'失败，物品名称已存在");
        }
        return toAjax(garbageItemService.updateGarbageItem(garbageItem));
    }
    
    /**
     * 删除垃圾物品
     */
    @PreAuthorize("@ss.hasPermi('garbage:item:remove')")
    @Log(title = "垃圾物品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(garbageItemService.deleteGarbageItemByIds(ids));
    }
    
    /**
     * 获取分类下的物品列表
     */
    @GetMapping("/category/{categoryId}")
    public AjaxResult getItemsByCategory(@PathVariable("categoryId") String categoryId) {
        List<GarbageItem> list = garbageItemService.selectItemsByCategoryId(categoryId);
        return AjaxResult.success(list);
    }
    
    /**
     * 搜索垃圾物品
     */
    @GetMapping("/search/{keyword}")
    public AjaxResult searchItems(@PathVariable("keyword") String keyword) {
        List<GarbageItem> list = garbageItemService.searchGarbageItems(keyword);
        return AjaxResult.success(list);
    }
    
    /**
     * 校验物品名称唯一性
     */
    @GetMapping("/checkItemNameUnique")
    public AjaxResult checkItemNameUnique(GarbageItem garbageItem) {
        String result = garbageItemService.checkItemNameUnique(garbageItem);
        return AjaxResult.success(result);
    }
} 