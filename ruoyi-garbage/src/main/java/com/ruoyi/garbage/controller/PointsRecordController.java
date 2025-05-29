package com.ruoyi.garbage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;
import com.ruoyi.garbage.service.IPointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分记录控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/points/record")
public class PointsRecordController extends BaseController {
    
    @Autowired
    private IPointsRecordService pointsRecordService;
    
    /**
     * 查询积分记录列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsRecordQuery query) {
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(query);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(pointsRecordService.countPointsRecord(query));
        return rspData;
    }
    
    /**
     * 获取积分记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(pointsRecordService.selectPointsRecordById(id));
    }
    
    /**
     * 新增积分记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:add')")
    @Log(title = "积分记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PointsRecord pointsRecord) {
        pointsRecord.setCreateBy(SecurityUtils.getUsername());
        return toAjax(pointsRecordService.insertPointsRecord(pointsRecord));
    }
    
    /**
     * 修改积分记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:edit')")
    @Log(title = "积分记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PointsRecord pointsRecord) {
        return toAjax(pointsRecordService.updatePointsRecord(pointsRecord));
    }
    
    /**
     * 删除积分记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:points:remove')")
    @Log(title = "积分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(pointsRecordService.deletePointsRecordByIds(ids));
    }
    
    /**
     * 获取用户总积分
     */
    @GetMapping("/total/{userId}")
    public AjaxResult getUserTotalPoints(@PathVariable("userId") Long userId) {
        int totalPoints = pointsRecordService.getUserTotalPoints(userId);
        return success(totalPoints);
    }
} 