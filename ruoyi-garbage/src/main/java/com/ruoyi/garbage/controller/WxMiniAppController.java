package com.ruoyi.garbage.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.service.IGarbageGuideService;
import com.ruoyi.garbage.service.IGarbageRecordService;
import com.ruoyi.garbage.service.IPointsService;

/**
 * 微信小程序接口控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx")
public class WxMiniAppController {

    @Autowired
    private IGarbageRecordService garbageRecordService;
    
    @Autowired
    private IPointsService pointsService;
    
    @Autowired
    private IGarbageGuideService garbageGuideService;
    
    /**
     * 提交垃圾投递记录
     */
    @PostMapping("/record/submit")
    public AjaxResult submitRecord(@RequestBody GarbageRecord record) {
        // 设置ID
        record.setId(UUID.randomUUID().toString());
        
        // 设置创建时间和更新时间
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        
        // 设置积分未计算标志
        record.setPointsCalculated(false);
        
        // 保存记录
        GarbageRecord savedRecord = garbageRecordService.saveRecord(record);
        
        return AjaxResult.success(savedRecord);
    }
    
    /**
     * 获取用户垃圾投递记录
     */
    @GetMapping("/record/list/{userId}")
    public AjaxResult getUserRecords(@PathVariable("userId") Long userId,
                                    @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<GarbageRecord> page = garbageRecordService.getRecordsByUserId(userId, pageable);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取用户积分记录
     */
    @GetMapping("/points/list/{userId}")
    public AjaxResult getUserPoints(@PathVariable("userId") Long userId,
                                   @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<PointsRecord> page = pointsService.getPointsRecordsByUserId(userId, pageable);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取用户总积分
     */
    @GetMapping("/points/total/{userId}")
    public AjaxResult getUserTotalPoints(@PathVariable("userId") Long userId) {
        int totalPoints = pointsService.getTotalPoints(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("totalPoints", totalPoints);
        
        return AjaxResult.success(data);
    }
    
    /**
     * 搜索垃圾分类指南
     */
    @GetMapping("/guide/search")
    public AjaxResult searchGuide(@RequestParam("keyword") String keyword,
                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "garbageName"));
        Page<GarbageGuide> page = garbageGuideService.searchGuidesByGarbageName(keyword, pageable);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取垃圾分类指南详情
     */
    @GetMapping("/guide/{id}")
    public AjaxResult getGuideDetail(@PathVariable("id") String id) {
        GarbageGuide guide = garbageGuideService.getGuideById(id);
        if (guide != null) {
            return AjaxResult.success(guide);
        } else {
            return AjaxResult.error("未找到相关垃圾分类信息");
        }
    }
    
    /**
     * 根据垃圾名称获取分类指南
     */
    @GetMapping("/guide/name/{garbageName}")
    public AjaxResult getGuideByName(@PathVariable("garbageName") String garbageName) {
        GarbageGuide guide = garbageGuideService.getGuideByGarbageName(garbageName);
        if (guide != null) {
            return AjaxResult.success(guide);
        } else {
            return AjaxResult.error("未找到相关垃圾分类信息");
        }
    }
} 