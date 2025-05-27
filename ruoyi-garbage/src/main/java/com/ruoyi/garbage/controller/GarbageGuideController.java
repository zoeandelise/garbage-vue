package com.ruoyi.garbage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.service.IGarbageGuideService;

/**
 * 垃圾分类指南控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/guide")
public class GarbageGuideController extends BaseController {

    @Autowired
    private IGarbageGuideService garbageGuideService;

    /**
     * 获取垃圾分类指南列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                          @RequestParam(value = "garbageType", required = false) String garbageType) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "garbageName"));
        
        Page<GarbageGuide> page;
        if (garbageType != null && !garbageType.isEmpty()) {
            page = garbageGuideService.getGuidesByType(garbageType, pageable);
        } else {
            page = garbageGuideService.getAllGuides(pageable);
        }
        
        return AjaxResult.success(page);
    }

    /**
     * 获取垃圾分类指南详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(garbageGuideService.getGuideById(id));
    }

    /**
     * 新增垃圾分类指南
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GarbageGuide guide) {
        return AjaxResult.success(garbageGuideService.saveGuide(guide));
    }

    /**
     * 修改垃圾分类指南
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GarbageGuide guide) {
        return AjaxResult.success(garbageGuideService.updateGuide(guide));
    }

    /**
     * 删除垃圾分类指南
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        for (String id : ids) {
            garbageGuideService.deleteGuide(id);
        }
        return AjaxResult.success();
    }
    
    /**
     * 搜索垃圾分类指南
     */
    @Anonymous
    @GetMapping("/search")
    public AjaxResult search(@RequestParam("keyword") String keyword) {
        List<GarbageGuide> guides = garbageGuideService.searchGuides(keyword);
        return AjaxResult.success(guides);
    }
    
    /**
     * 获取所有垃圾类型
     */
    @Anonymous
    @GetMapping("/types")
    public AjaxResult getAllTypes() {
        List<String> types = garbageGuideService.getAllGarbageTypes();
        return AjaxResult.success(types);
    }
} 