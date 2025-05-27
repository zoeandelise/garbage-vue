package com.ruoyi.garbage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.service.IPointsService;

/**
 * 积分控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/points")
public class PointsController extends BaseController {

    @Autowired
    private IPointsService pointsService;

    /**
     * 获取积分记录列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                          @RequestParam(value = "userId", required = false) Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<PointsRecord> page;
        if (userId != null) {
            page = pointsService.getPointsRecordsByUserId(userId, pageable);
        } else {
            // 这里需要自定义查询所有记录的分页方法，暂时返回空结果
            return AjaxResult.error("请指定用户ID");
        }
        
        return AjaxResult.success(page);
    }

    /**
     * 获取积分记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(pointsService.getPointsRecordById(id));
    }

    /**
     * 获取当前用户的积分记录
     */
    @Anonymous
    @GetMapping("/my")
    public AjaxResult myPoints(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Long userId = SecurityUtils.getUserId();
        Page<PointsRecord> page = pointsService.getPointsRecordsByUserId(userId, pageable);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取当前用户的总积分
     */
    @Anonymous
    @GetMapping("/my/total")
    public AjaxResult myTotalPoints() {
        Long userId = SecurityUtils.getUserId();
        int totalPoints = pointsService.getTotalPoints(userId);
        return AjaxResult.success(totalPoints);
    }
    
    /**
     * 消费积分
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:consume')")
    @PostMapping("/consume")
    public AjaxResult consumePoints(@RequestParam("userId") Long userId,
                                   @RequestParam("points") int points,
                                   @RequestParam("remark") String remark) {
        // 获取用户名
        String userName = SecurityUtils.getUsername();
        
        PointsRecord record = pointsService.consumePoints(userId, userName, points, remark);
        if (record == null) {
            return AjaxResult.error("积分不足或消费失败");
        }
        return AjaxResult.success(record);
    }
    
    /**
     * 当前用户消费积分
     */
    @PostMapping("/my/consume")
    public AjaxResult myConsumePoints(@RequestParam("points") int points,
                                     @RequestParam("remark") String remark) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        
        PointsRecord record = pointsService.consumePoints(userId, userName, points, remark);
        if (record == null) {
            return AjaxResult.error("积分不足或消费失败");
        }
        return AjaxResult.success(record);
    }
} 