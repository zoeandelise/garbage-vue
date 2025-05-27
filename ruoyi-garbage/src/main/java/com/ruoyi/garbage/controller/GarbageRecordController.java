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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.service.IGarbageRecordService;

/**
 * 垃圾投递记录控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/record")
public class GarbageRecordController extends BaseController {

    @Autowired
    private IGarbageRecordService garbageRecordService;

    /**
     * 获取垃圾投递记录列表
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                          @RequestParam(value = "userId", required = false) Long userId,
                          @RequestParam(value = "garbageType", required = false) String garbageType) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<GarbageRecord> page;
        if (userId != null) {
            page = garbageRecordService.getRecordsByUserId(userId, pageable);
        } else if (garbageType != null && !garbageType.isEmpty()) {
            // 由于MongoDB不支持直接按类型分页，这里先获取所有符合条件的数据，再手动分页
            List<GarbageRecord> records = garbageRecordService.getRecordsByGarbageType(garbageType);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), records.size());
            page = new org.springframework.data.domain.PageImpl<>(
                    records.subList(start, end), pageable, records.size());
        } else {
            page = garbageRecordService.getAllRecords(pageable);
        }
        
        return AjaxResult.success(page);
    }

    /**
     * 获取垃圾投递记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(garbageRecordService.getRecordById(id));
    }

    /**
     * 新增垃圾投递记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GarbageRecord record) {
        // 如果没有设置用户ID和用户名，则使用当前登录用户的信息
        if (record.getUserId() == null) {
            record.setUserId(SecurityUtils.getUserId());
        }
        if (record.getUserName() == null || record.getUserName().isEmpty()) {
            record.setUserName(SecurityUtils.getUsername());
        }
        return AjaxResult.success(garbageRecordService.saveRecord(record));
    }

    /**
     * 修改垃圾投递记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GarbageRecord record) {
        return AjaxResult.success(garbageRecordService.updateRecord(record));
    }

    /**
     * 删除垃圾投递记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        for (String id : ids) {
            garbageRecordService.deleteRecord(id);
        }
        return AjaxResult.success();
    }
    
    /**
     * 获取当前用户的垃圾投递记录
     */
    @Anonymous
    @GetMapping("/my")
    public AjaxResult myRecords(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Long userId = SecurityUtils.getUserId();
        Page<GarbageRecord> page = garbageRecordService.getRecordsByUserId(userId, pageable);
        return AjaxResult.success(page);
    }
}