package com.ruoyi.garbage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.garbage.constant.GarbageConstants;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.service.IGarbageGuideService;
import com.ruoyi.garbage.service.IGarbageRecordService;
import com.ruoyi.garbage.service.IPointsService;

/**
 * 垃圾分类统计分析控制器（管理员专用）
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/admin/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private IGarbageRecordService garbageRecordService;
    
    @Autowired
    private IPointsService pointsService;
    
    @Autowired
    private IGarbageGuideService garbageGuideService;

    /**
     * 获取垃圾分类总体统计数据
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/overview")
    public AjaxResult getOverview(
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 这里应该根据日期范围查询数据库，获取实际统计数据
        // 目前使用模拟数据进行展示
        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", 1256);
        data.put("totalWeight", 3254.5);
        data.put("totalPoints", 6280);
        data.put("userCount", 328);
        
        return AjaxResult.success(data);
    }
    
    /**
     * 获取垃圾类型分布统计
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/type-distribution")
    public AjaxResult getTypeDistribution(
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 这里应该根据日期范围查询数据库，获取实际统计数据
        // 目前使用模拟数据进行展示
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> recyclable = new HashMap<>();
        recyclable.put("name", GarbageConstants.GARBAGE_TYPE_RECYCLABLE);
        recyclable.put("value", 45);
        data.add(recyclable);
        
        Map<String, Object> harmful = new HashMap<>();
        harmful.put("name", GarbageConstants.GARBAGE_TYPE_HARMFUL);
        harmful.put("value", 10);
        data.add(harmful);
        
        Map<String, Object> kitchen = new HashMap<>();
        kitchen.put("name", GarbageConstants.GARBAGE_TYPE_KITCHEN);
        kitchen.put("value", 30);
        data.add(kitchen);
        
        Map<String, Object> other = new HashMap<>();
        other.put("name", GarbageConstants.GARBAGE_TYPE_OTHER);
        other.put("value", 15);
        data.add(other);
        
        return AjaxResult.success(data);
    }
    
    /**
     * 获取每日投递趋势数据
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/daily-trend")
    public AjaxResult getDailyTrend(
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 这里应该根据日期范围查询数据库，获取实际统计数据
        // 目前使用模拟数据进行展示
        List<Map<String, Object>> data = new ArrayList<>();
        
        // 生成最近7天的数据
        Date now = new Date();
        for (int i = 6; i >= 0; i--) {
            Date date = DateUtils.addDays(now, -i);
            String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", date);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", 35 + (int)(Math.random() * 30)); // 随机生成35-65之间的数据
            data.add(item);
        }
        
        return AjaxResult.success(data);
    }
    
    /**
     * 获取区域投递分布数据
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/area-distribution")
    public AjaxResult getAreaDistribution(
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 这里应该根据日期范围查询数据库，获取实际统计数据
        // 目前使用模拟数据进行展示
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] areas = {"东城区", "西城区", "朝阳区", "海淀区", "丰台区", "石景山区"};
        int[] values = {120, 95, 180, 210, 85, 65};
        
        for (int i = 0; i < areas.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", areas[i]);
            item.put("value", values[i]);
            data.add(item);
        }
        
        return AjaxResult.success(data);
    }
    
    /**
     * 获取用户积分排行榜
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/user-points-ranking")
    public AjaxResult getUserPointsRanking(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        // 这里应该查询数据库，获取实际用户积分排行
        // 目前使用模拟数据进行展示
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (int i = 1; i <= limit; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i);
            item.put("userId", 100 + i);
            item.put("userName", "用户" + i);
            item.put("points", 1000 - (i * 50) + (int)(Math.random() * 30));
            data.add(item);
        }
        
        return AjaxResult.success(data);
    }
    
    /**
     * 获取垃圾分类指南查询热度排行
     */
    @PreAuthorize("@ss.hasPermi('garbage:statistics:view')")
    @GetMapping("/guide-search-ranking")
    public AjaxResult getGuideSearchRanking(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        // 这里应该查询数据库，获取实际查询热度排行
        // 目前使用模拟数据进行展示
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] items = {"塑料瓶", "废电池", "果皮", "纸巾", "易拉罐", "玻璃瓶", "废纸", "口罩", "外卖盒", "药品包装"};
        
        for (int i = 0; i < Math.min(items.length, limit); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("name", items[i]);
            item.put("count", 500 - (i * 40) + (int)(Math.random() * 20));
            data.add(item);
        }
        
        return AjaxResult.success(data);
    }
} 