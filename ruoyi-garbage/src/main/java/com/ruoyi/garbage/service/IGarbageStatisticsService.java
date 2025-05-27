package com.ruoyi.garbage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 垃圾分类统计服务接口
 * 
 * @author ruoyi
 */
public interface IGarbageStatisticsService {
    
    /**
     * 获取垃圾分类总体统计数据
     * 
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 总体统计数据
     */
    Map<String, Object> getOverviewStatistics(Date beginTime, Date endTime);
    
    /**
     * 获取垃圾类型分布统计
     * 
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 垃圾类型分布统计
     */
    List<Map<String, Object>> getTypeDistribution(Date beginTime, Date endTime);
    
    /**
     * 获取每日投递趋势数据
     * 
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 每日投递趋势数据
     */
    List<Map<String, Object>> getDailyTrend(Date beginTime, Date endTime);
    
    /**
     * 获取区域投递分布数据
     * 
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 区域投递分布数据
     */
    List<Map<String, Object>> getAreaDistribution(Date beginTime, Date endTime);
    
    /**
     * 获取用户积分排行榜
     * 
     * @param limit 限制数量
     * @return 用户积分排行榜
     */
    List<Map<String, Object>> getUserPointsRanking(int limit);
    
    /**
     * 获取垃圾分类指南查询热度排行
     * 
     * @param limit 限制数量
     * @return 垃圾分类指南查询热度排行
     */
    List<Map<String, Object>> getGuideSearchRanking(int limit);
} 