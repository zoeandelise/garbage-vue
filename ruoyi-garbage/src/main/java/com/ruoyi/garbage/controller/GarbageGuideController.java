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
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
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
    public TableDataInfo list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                          @RequestParam(value = "garbageType", required = false) String garbageType,
                          @RequestParam(value = "garbageName", required = false) String garbageName) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<GarbageGuide> page;
        
        // 记录查询条件
        System.out.println("查询垃圾指南列表 - 页码: " + pageNum + ", 每页数量: " + pageSize 
                + ", 垃圾类型: " + garbageType + ", 垃圾名称: " + garbageName);
        
        // 根据条件查询
        if (garbageName != null && !garbageName.isEmpty()) {
            // 按名称模糊查询
            page = garbageGuideService.searchGuidesByGarbageName(garbageName, pageable);
        } else if (garbageType != null && !garbageType.isEmpty()) {
            // 按类型查询
            page = garbageGuideService.getGuidesByType(garbageType, pageable);
        } else {
            // 查询所有
            page = garbageGuideService.getAllGuides(pageable);
        }
        
        // 转换为前端需要的格式
        List<GarbageGuide> list = page.getContent();
        long total = page.getTotalElements();
        
        System.out.println("查询结果 - 总数: " + total + ", 当前页数据量: " + list.size());
        // 打印更详细的数据内容，帮助排查问题
        if (list.size() > 0) {
            System.out.println("第一条数据: " + list.get(0).getGarbageName() + ", 类型: " + list.get(0).getGarbageType());
        }
        
        // 直接返回TableDataInfo
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        
        return rspData;
    }

    /**
     * 获取垃圾分类指南详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:guide:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        System.out.println("获取垃圾分类指南详情，ID: " + id);
        try {
            GarbageGuide guide = garbageGuideService.getGuideById(id);
            if (guide != null) {
                System.out.println("成功获取垃圾分类指南: " + guide.getGarbageName());
                return AjaxResult.success(guide);
            } else {
                System.err.println("未找到ID为 " + id + " 的垃圾分类指南");
                return AjaxResult.error("未找到指定的垃圾分类指南");
            }
        } catch (Exception e) {
            System.err.println("获取垃圾分类指南详情出错: " + e.getMessage());
            e.printStackTrace();
            return AjaxResult.error("获取垃圾分类指南详情失败：" + e.getMessage());
        }
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
    
    /**
     * 根据垃圾名称查询分类指南
     */
    @Anonymous
    @GetMapping("/name/{garbageName}")
    public AjaxResult getByName(@PathVariable("garbageName") String garbageName) {
        GarbageGuide guide = garbageGuideService.getGuideByGarbageName(garbageName);
        if (guide == null) {
            // 如果找不到精确匹配，尝试模糊查询
            List<GarbageGuide> guides = garbageGuideService.searchGuidesByGarbageName(garbageName);
            if (guides != null && !guides.isEmpty()) {
                // 返回第一个结果
                return AjaxResult.success(guides.get(0));
            }
            // 如果仍然找不到，返回找不到的信息
            return AjaxResult.error("未找到匹配的垃圾分类信息");
        }
        return AjaxResult.success(guide);
    }
} 