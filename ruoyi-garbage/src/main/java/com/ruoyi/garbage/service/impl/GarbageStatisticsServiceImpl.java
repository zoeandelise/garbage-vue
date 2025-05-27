package com.ruoyi.garbage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.repository.GarbageRecordRepository;
import com.ruoyi.garbage.repository.PointsRecordRepository;
import com.ruoyi.garbage.service.IGarbageStatisticsService;

/**
 * 垃圾分类统计服务实现类
 * 
 * @author ruoyi
 */
@Service
public class GarbageStatisticsServiceImpl implements IGarbageStatisticsService {

    @Autowired
    private GarbageRecordRepository garbageRecordRepository;
    
    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> getOverviewStatistics(Date beginTime, Date endTime) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", 1256);
        result.put("totalWeight", 3254.5);
        result.put("totalPoints", 6280);
        result.put("userCount", 328);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTypeDistribution(Date beginTime, Date endTime) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "可回收物");
        item1.put("value", 45);
        result.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "有害垃圾");
        item2.put("value", 10);
        result.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "厨余垃圾");
        item3.put("value", 30);
        result.add(item3);
        
        Map<String, Object> item4 = new HashMap<>();
        item4.put("name", "其他垃圾");
        item4.put("value", 15);
        result.add(item4);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDailyTrend(Date beginTime, Date endTime) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取当前日期，并生成过去7天的数据
        Date now = new Date();
        for (int i = 6; i >= 0; i--) {
            Date date = DateUtils.addDays(now, -i);
            String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", date);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", 35 + (int)(Math.random() * 30)); // 随机生成35-65之间的数据
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAreaDistribution(Date beginTime, Date endTime) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] areas = {"东城区", "西城区", "朝阳区", "海淀区", "丰台区", "石景山区"};
        int[] values = {120, 95, 180, 210, 85, 65};
        
        for (int i = 0; i < areas.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", areas[i]);
            item.put("value", values[i]);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserPointsRanking(int limit) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (int i = 1; i <= limit; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i);
            item.put("userId", 100 + i);
            item.put("userName", "用户" + (100 + i));
            item.put("points", 1000 - (i * 50) + (int)(Math.random() * 30));
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getGuideSearchRanking(int limit) {
        // 实际项目中应该从数据库中查询真实数据
        // 这里暂时使用模拟数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] items = {"塑料瓶", "废电池", "果皮", "纸巾", "易拉罐", "灯泡", "剩饭", "药品", "纸箱", "玻璃瓶"};
        
        for (int i = 0; i < Math.min(items.length, limit); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("garbageName", items[i]);
            item.put("searchCount", 500 - (i * 40) + (int)(Math.random() * 20));
            result.add(item);
        }
        
        return result;
    }
} 