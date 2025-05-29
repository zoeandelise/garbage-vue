package com.ruoyi.garbage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;
import com.ruoyi.garbage.service.IPointsRecordService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 积分排行榜控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/points/ranking")
public class PointsRankingController extends BaseController {
    
    @Autowired
    private IPointsRecordService pointsRecordService;
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 获取积分排行榜
     */
    @PreAuthorize("@ss.hasPermi('points:ranking:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        startPage();
        
        // 获取所有用户
        SysUser queryUser = new SysUser();
        List<SysUser> allUsers = userService.selectUserList(queryUser);
        
        // 计算每个用户的总积分
        List<Map<String, Object>> rankingList = allUsers.stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("userName", user.getUserName());
                map.put("nickName", user.getNickName());
                
                // 获取用户积分
                int points = pointsRecordService.getUserTotalPoints(user.getUserId());
                map.put("totalPoints", points);
                
                return map;
            })
            .sorted((a, b) -> Integer.compare((Integer) b.get("totalPoints"), (Integer) a.get("totalPoints")))
            .collect(Collectors.toList());
        
        return getDataTable(rankingList);
    }
    
    /**
     * 获取积分排行榜前N名
     */
    @GetMapping("/top/{n}")
    public AjaxResult getTopRanking(@PathVariable("n") int n) {
        // 限制查询数量
        n = Math.max(1, Math.min(n, 100));
        
        // 获取所有用户
        SysUser queryUser = new SysUser();
        List<SysUser> allUsers = userService.selectUserList(queryUser);
        
        // 计算每个用户的总积分
        List<Map<String, Object>> rankingList = allUsers.stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("userName", user.getUserName());
                map.put("nickName", user.getNickName());
                
                // 获取用户积分
                int points = pointsRecordService.getUserTotalPoints(user.getUserId());
                map.put("totalPoints", points);
                
                return map;
            })
            .sorted((a, b) -> Integer.compare((Integer) b.get("totalPoints"), (Integer) a.get("totalPoints")))
            .limit(n)
            .collect(Collectors.toList());
        
        // 添加排名信息
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).put("rank", i + 1);
        }
        
        return AjaxResult.success(rankingList);
    }
    
    /**
     * 获取用户积分排名
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getUserRanking(@PathVariable("userId") Long userId) {
        // 获取所有用户
        SysUser queryUser = new SysUser();
        List<SysUser> allUsers = userService.selectUserList(queryUser);
        
        // 计算每个用户的总积分
        List<Map<String, Object>> rankingList = allUsers.stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("userName", user.getUserName());
                map.put("nickName", user.getNickName());
                
                // 获取用户积分
                int points = pointsRecordService.getUserTotalPoints(user.getUserId());
                map.put("totalPoints", points);
                
                return map;
            })
            .sorted((a, b) -> Integer.compare((Integer) b.get("totalPoints"), (Integer) a.get("totalPoints")))
            .collect(Collectors.toList());
        
        // 添加排名信息
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).put("rank", i + 1);
        }
        
        // 查找指定用户的排名
        Optional<Map<String, Object>> userRanking = rankingList.stream()
            .filter(map -> userId.equals(map.get("userId")))
            .findFirst();
        
        if (userRanking.isPresent()) {
            return AjaxResult.success(userRanking.get());
        } else {
            return AjaxResult.error("未找到用户排名信息");
        }
    }
    
    /**
     * 获取积分统计数据
     */
    @PreAuthorize("@ss.hasPermi('points:ranking:stats')")
    @GetMapping("/stats")
    public AjaxResult getPointsStats() {
        // 获取所有积分记录
        List<PointsRecord> allRecords = pointsRecordService.selectPointsRecordList(null);
        
        // 统计总积分
        int totalPointsAdded = 0;
        int totalPointsReduced = 0;
        
        // 按来源统计
        Map<Integer, Integer> pointsBySource = new HashMap<>();
        
        // 按月统计
        Map<String, Integer> pointsByMonth = new HashMap<>();
        
        // 按类型统计
        Map<Integer, Integer> pointsByType = new HashMap<>();
        
        Calendar calendar = Calendar.getInstance();
        
        for (PointsRecord record : allRecords) {
            // 按类型统计
            if (record.getType() == 1) {
                // 增加积分
                totalPointsAdded += record.getPoints();
            } else if (record.getType() == 2) {
                // 减少积分
                totalPointsReduced += record.getPoints();
            }
            
            // 按来源统计
            Integer sourcePoints = pointsBySource.getOrDefault(record.getSource(), 0);
            if (record.getType() == 1) {
                sourcePoints += record.getPoints();
            } else {
                sourcePoints -= record.getPoints();
            }
            pointsBySource.put(record.getSource(), sourcePoints);
            
            // 按月统计
            if (record.getCreateTime() != null) {
                calendar.setTime(record.getCreateTime());
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                String monthKey = String.format("%d-%02d", year, month);
                
                Integer monthPoints = pointsByMonth.getOrDefault(monthKey, 0);
                if (record.getType() == 1) {
                    monthPoints += record.getPoints();
                } else {
                    monthPoints -= record.getPoints();
                }
                pointsByMonth.put(monthKey, monthPoints);
            }
            
            // 按类型统计
            Integer typePoints = pointsByType.getOrDefault(record.getType(), 0);
            typePoints += record.getPoints();
            pointsByType.put(record.getType(), typePoints);
        }
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalPointsAdded", totalPointsAdded);
        result.put("totalPointsReduced", totalPointsReduced);
        result.put("netPoints", totalPointsAdded - totalPointsReduced);
        result.put("pointsBySource", pointsBySource);
        result.put("pointsByMonth", pointsByMonth);
        result.put("pointsByType", pointsByType);
        
        return AjaxResult.success(result);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/detail/{userId}")
    public AjaxResult getUserDetail(@PathVariable("userId") Long userId) {
        // 获取用户基本信息
        SysUser user = userService.selectUserById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        
        // 查询用户积分记录
        PointsRecordQuery query = new PointsRecordQuery();
        query.setUserId(userId);
        List<PointsRecord> records = pointsRecordService.selectPointsRecordList(query);
        
        // 计算各项积分
        int totalPoints = pointsRecordService.getUserTotalPoints(userId);
        
        // 计算本月积分
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date monthStart = calendar.getTime();
        
        int monthPoints = 0;
        for (PointsRecord record : records) {
            if (record.getCreateTime() != null && record.getCreateTime().after(monthStart)) {
                if (record.getType() == 1) {
                    monthPoints += record.getPoints();
                } else if (record.getType() == 2) {
                    monthPoints -= record.getPoints();
                }
            }
        }
        
        // 计算本周积分
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date weekStart = calendar.getTime();
        
        int weekPoints = 0;
        for (PointsRecord record : records) {
            if (record.getCreateTime() != null && record.getCreateTime().after(weekStart)) {
                if (record.getType() == 1) {
                    weekPoints += record.getPoints();
                } else if (record.getType() == 2) {
                    weekPoints -= record.getPoints();
                }
            }
        }
        
        // 获取最近的10条记录
        List<PointsRecord> recentRecords = records.stream()
            .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
            .limit(10)
            .collect(Collectors.toList());
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("userName", user.getUserName());
        result.put("nickName", user.getNickName());
        result.put("avatar", user.getAvatar());
        result.put("totalPoints", totalPoints);
        result.put("monthPoints", monthPoints);
        result.put("weekPoints", weekPoints);
        result.put("recentRecords", recentRecords);
        result.put("createTime", user.getCreateTime());
        
        return AjaxResult.success(result);
    }
} 