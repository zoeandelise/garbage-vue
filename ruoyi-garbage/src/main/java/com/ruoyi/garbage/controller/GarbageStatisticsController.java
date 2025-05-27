package com.ruoyi.garbage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.garbage.service.IGarbageGuideService;
import com.ruoyi.garbage.service.IGarbageRecordService;
import com.ruoyi.garbage.service.IPointsService;
import com.ruoyi.garbage.service.IGarbageStatisticsService;

/**
 * 垃圾分类统计控制器（公共访问）
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/statistics")
public class GarbageStatisticsController extends BaseController {

    @Autowired
    private IGarbageRecordService garbageRecordService;
    
    @Autowired
    private IPointsService pointsService;
    
    @Autowired
    private IGarbageGuideService garbageGuideService;

    @Autowired
    private IGarbageStatisticsService garbageStatisticsService;

    /**
     * 获取垃圾分类总体统计数据
     */
    @Anonymous
    @GetMapping("/overview")
    public AjaxResult getOverview(@RequestParam(required = false) String beginTime,
                                 @RequestParam(required = false) String endTime) {
        Date beginDate = DateUtils.parseDate(beginTime);
        Date endDate = DateUtils.parseDate(endTime);
        Map<String, Object> data = garbageStatisticsService.getOverviewStatistics(beginDate, endDate);
        return AjaxResult.success(data);
    }

    /**
     * 获取垃圾类型分布统计
     */
    @Anonymous
    @GetMapping("/type-distribution")
    public AjaxResult getTypeDistribution(@RequestParam(required = false) String beginTime,
                                         @RequestParam(required = false) String endTime) {
        Date beginDate = DateUtils.parseDate(beginTime);
        Date endDate = DateUtils.parseDate(endTime);
        List<Map<String, Object>> data = garbageStatisticsService.getTypeDistribution(beginDate, endDate);
        return AjaxResult.success(data);
    }

    /**
     * 获取每日投递趋势数据
     */
    @Anonymous
    @GetMapping("/daily-trend")
    public AjaxResult getDailyTrend(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime) {
        Date beginDate = DateUtils.parseDate(beginTime);
        Date endDate = DateUtils.parseDate(endTime);
        List<Map<String, Object>> data = garbageStatisticsService.getDailyTrend(beginDate, endDate);
        return AjaxResult.success(data);
    }

    /**
     * 获取区域投递分布数据
     */
    @Anonymous
    @GetMapping("/area-distribution")
    public AjaxResult getAreaDistribution(@RequestParam(required = false) String beginTime,
                                         @RequestParam(required = false) String endTime) {
        Date beginDate = DateUtils.parseDate(beginTime);
        Date endDate = DateUtils.parseDate(endTime);
        List<Map<String, Object>> data = garbageStatisticsService.getAreaDistribution(beginDate, endDate);
        return AjaxResult.success(data);
    }
} 