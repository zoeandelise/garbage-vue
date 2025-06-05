package com.ruoyi.garbage.controller;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;
import java.io.File;

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
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.FileTypeUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.config.RuoYiConfig;
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
    
    /**
     * 上传垃圾投递照片
     */
    @PostMapping("/upload")
    public AjaxResult uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return AjaxResult.error("未提供照片数据");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png"))) {
                return AjaxResult.error("只能上传JPG/PNG格式的图片");
            }
            
            // 检查文件大小
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return AjaxResult.error("图片大小不能超过10MB");
            }
            
            // 生成文件名
            String extension = "jpg";
            if (contentType.startsWith("image/png")) {
                extension = "png";
            }
            String fileName = UUID.randomUUID().toString() + "." + extension;
            
            // 获取上传路径
            String uploadPath = RuoYiConfig.getProfile() + "/garbage-photos/";
            
            // 保存文件
            File dest = new File(uploadPath + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            
            // 返回完整的访问URL
            String url = "/profile/garbage-photos/" + fileName;
            String fullUrl = url; // 如果有域名可以拼接：serverConfig.getUrl() + url
            
            // 构造返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("fullUrl", fullUrl);
            result.put("fileName", fileName);
            
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("照片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核垃圾投递记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:audit')")
    @PostMapping("/audit")
    public AjaxResult auditRecord(@RequestBody Map<String, Object> params) {
        String recordId = (String) params.get("recordId");
        Boolean approved = (Boolean) params.get("approved");
        String remarks = (String) params.get("remarks");
        
        if (StringUtils.isEmpty(recordId) || approved == null) {
            return AjaxResult.error("记录ID和审核结果不能为空");
        }
        
        GarbageRecord record = garbageRecordService.getRecordById(recordId);
        if (record == null) {
            return AjaxResult.error("未找到指定的垃圾投递记录");
        }
        
        // 更新审核状态
        record.setVerified(approved);
        record.setAuditRemarks(remarks);
        record.setAuditorId(SecurityUtils.getUserId());
        record.setAuditorName(SecurityUtils.getUsername());
        
        garbageRecordService.updateRecord(record);
        
        return AjaxResult.success();
    }
    
    /**
     * 批量审核垃圾投递记录
     */
    @PreAuthorize("@ss.hasPermi('garbage:record:audit')")
    @PostMapping("/batchAudit")
    public AjaxResult batchAudit(@RequestBody Map<String, Object> params) {
        List<String> recordIds = (List<String>) params.get("recordIds");
        Boolean approved = (Boolean) params.get("approved");
        String remarks = (String) params.get("remarks");
        
        if (recordIds == null || recordIds.isEmpty() || approved == null) {
            return AjaxResult.error("记录ID列表和审核结果不能为空");
        }
        
        for (String recordId : recordIds) {
            GarbageRecord record = garbageRecordService.getRecordById(recordId);
            if (record != null) {
                // 更新审核状态
                record.setVerified(approved);
                record.setAuditRemarks(remarks);
                record.setAuditorId(SecurityUtils.getUserId());
                record.setAuditorName(SecurityUtils.getUsername());
                
                garbageRecordService.updateRecord(record);
            }
        }
        
        return AjaxResult.success();
    }
}