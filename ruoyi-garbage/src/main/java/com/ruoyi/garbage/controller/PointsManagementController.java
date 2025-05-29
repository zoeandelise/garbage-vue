package com.ruoyi.garbage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;
import com.ruoyi.garbage.service.IPointsRecordService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 积分管理控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/points")
public class PointsManagementController extends BaseController {
    
    @Autowired
    private IPointsRecordService pointsRecordService;
    
    @Autowired
    private ISysUserService userService;
    
    /**
     * 查询积分记录列表
     */
    @PreAuthorize("@ss.hasPermi('points:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsRecordQuery query) {
        startPage();
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(query);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> resultList = list.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("recordId", record.getId());
            map.put("userId", record.getUserId());
            map.put("userName", record.getUserName());
            map.put("changeValue", record.getPoints());
            map.put("afterPoints", 0); // 需要计算
            map.put("pointsType", record.getSource().toString());
            map.put("description", record.getRemark());
            map.put("createTime", record.getCreateTime());
            return map;
        }).collect(Collectors.toList());
        
        return getDataTable(resultList);
    }
    
    /**
     * 查询积分记录详细
     */
    @PreAuthorize("@ss.hasPermi('points:query')")
    @GetMapping("/detail/{recordId}")
    public AjaxResult getDetail(@PathVariable("recordId") String recordId) {
        PointsRecord record = pointsRecordService.selectPointsRecordById(recordId);
        if (record == null) {
            return AjaxResult.error("记录不存在");
        }
        
        // 转换为前端需要的格式
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("userId", record.getUserId());
        result.put("userName", record.getUserName());
        result.put("changeValue", record.getPoints());
        result.put("afterPoints", 0); // 需要计算
        result.put("pointsType", record.getSource().toString());
        result.put("description", record.getRemark());
        result.put("createTime", record.getCreateTime());
        result.put("relatedId", record.getBusinessId());
        
        return AjaxResult.success(result);
    }
    
    /**
     * 导出积分记录
     */
    @PreAuthorize("@ss.hasPermi('points:export')")
    @Log(title = "积分记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, PointsRecordQuery query) {
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(query);
        
        // 直接导出原始数据
        ExcelUtil<PointsRecord> util = new ExcelUtil<>(PointsRecord.class);
        util.exportExcel(response, list, "积分记录数据");
    }
    
    /**
     * 查询用户列表（用于积分管理）
     */
    @PreAuthorize("@ss.hasPermi('points:users')")
    @GetMapping("/users")
    public TableDataInfo listUsers(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        
        // 转换为前端需要的格式，添加积分信息
        List<Map<String, Object>> resultList = list.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", u.getUserId());
            map.put("userName", u.getUserName());
            map.put("nickName", u.getNickName());
            
            // 获取用户积分
            int points = pointsRecordService.getUserTotalPoints(u.getUserId());
            map.put("points", points);
            
            return map;
        }).collect(Collectors.toList());
        
        return getDataTable(resultList);
    }
    
    /**
     * 获取用户积分信息
     */
    @PreAuthorize("@ss.hasPermi('points:query')")
    @GetMapping("/user/{userId}")
    public AjaxResult getUserPoints(@PathVariable("userId") Long userId) {
        SysUser user = userService.selectUserById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        
        // 获取用户积分
        int points = pointsRecordService.getUserTotalPoints(userId);
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("userName", user.getUserName());
        result.put("points", points);
        
        return AjaxResult.success(result);
    }
    
    /**
     * 调整用户积分
     */
    @PreAuthorize("@ss.hasPermi('points:edit')")
    @Log(title = "积分调整", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    public AjaxResult adjustPoints(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String userName = params.get("userName").toString();
        String adjustType = params.get("adjustType").toString();
        Integer adjustValue = Integer.valueOf(params.get("adjustValue").toString());
        String reason = params.get("reason").toString();
        
        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setUserName(userName);
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(new Date());
        record.setRemark(reason);
        
        // 根据调整类型设置积分
        if ("add".equals(adjustType)) {
            record.setPoints(adjustValue);
            record.setType(1); // 增加积分
            record.setSource(2); // 管理员调整
        } else if ("subtract".equals(adjustType)) {
            record.setPoints(adjustValue);
            record.setType(2); // 减少积分
            record.setSource(2); // 管理员调整
        } else if ("set".equals(adjustType)) {
            // 获取当前积分
            int currentPoints = pointsRecordService.getUserTotalPoints(userId);
            int diff = adjustValue - currentPoints;
            
            if (diff > 0) {
                record.setPoints(diff);
                record.setType(1); // 增加积分
            } else {
                record.setPoints(-diff);
                record.setType(2); // 减少积分
            }
            record.setSource(2); // 管理员调整
        } else {
            return AjaxResult.error("不支持的调整类型");
        }
        
        // 保存积分记录
        pointsRecordService.insertPointsRecord(record);
        
        return AjaxResult.success();
    }
    
    /**
     * 获取积分来源名称
     */
    private String getSourceName(Integer source) {
        switch (source) {
            case 1: return "垃圾投递";
            case 2: return "管理员调整";
            case 3: return "积分兑换";
            default: return "未知来源";
        }
    }
} 