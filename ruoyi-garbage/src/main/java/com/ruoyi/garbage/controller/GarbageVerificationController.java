package com.ruoyi.garbage.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 垃圾图片验证控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/verification")
public class GarbageVerificationController extends BaseController
{
    /**
     * 验证垃圾投递图片
     */
    @PostMapping("/image")
    public AjaxResult verifyImage(@RequestBody Map<String, Object> params)
    {
        String garbageType = (String) params.get("garbageType");
        String photoData = (String) params.get("photoData");
        
        if (StringUtils.isEmpty(garbageType) || StringUtils.isEmpty(photoData))
        {
            return AjaxResult.error("垃圾类型和图片数据不能为空");
        }
        
        // 这里可以接入实际的图片识别服务
        // 由于暂无实际图像识别服务，这里模拟处理，随机返回验证结果
        boolean isVerified = Math.random() > 0.2; // 80%概率通过验证
        
        Map<String, Object> result = new HashMap<>();
        result.put("verified", isVerified);
        
        if (isVerified)
        {
            result.put("message", "照片验证通过");
        }
        else
        {
            result.put("message", "照片验证不通过，请确保拍摄的是" + garbageType);
        }
        
        // 生成验证ID用于后续查询
        String verificationId = UUID.randomUUID().toString();
        result.put("verificationId", verificationId);
        
        return AjaxResult.success(result);
    }
    
    /**
     * AI识别垃圾类型
     */
    @PostMapping("/identify")
    public AjaxResult identifyGarbageType(@RequestBody Map<String, Object> params)
    {
        String photoData = (String) params.get("photoData");
        String expectedType = (String) params.get("garbageType");
        
        if (StringUtils.isEmpty(photoData))
        {
            return AjaxResult.error("图片数据不能为空");
        }
        
        // 这里可以接入实际的AI图片识别服务
        // 由于暂无实际识别服务，这里模拟处理
        String[] garbageTypes = {"可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"};
        
        // 如果提供了期望类型，有70%概率返回该类型，增加用户体验
        String identifiedType;
        if (StringUtils.isNotEmpty(expectedType) && Math.random() > 0.3)
        {
            identifiedType = expectedType;
        }
        else
        {
            // 随机返回一种垃圾类型
            int index = (int) (Math.random() * garbageTypes.length);
            identifiedType = garbageTypes[index];
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("identifiedType", identifiedType);
        result.put("confidence", Math.random() * 50 + 50); // 50-100的置信度
        
        return AjaxResult.success(result);
    }
    
    /**
     * 比对垃圾类型与图片
     */
    @PostMapping("/compare")
    public AjaxResult compareGarbageTypeWithImage(@RequestBody Map<String, Object> params)
    {
        String garbageType = (String) params.get("garbageType");
        String photoData = (String) params.get("photoData");
        
        if (StringUtils.isEmpty(garbageType) || StringUtils.isEmpty(photoData))
        {
            return AjaxResult.error("垃圾类型和图片数据不能为空");
        }
        
        // 模拟比对过程
        boolean isMatch = Math.random() > 0.3; // 70%概率匹配成功
        
        Map<String, Object> result = new HashMap<>();
        result.put("match", isMatch);
        result.put("message", isMatch ? "垃圾类型与图片匹配" : "垃圾类型与图片不匹配");
        
        return AjaxResult.success(result);
    }
    
    /**
     * 获取验证结果
     */
    @GetMapping("/result/{verificationId}")
    public AjaxResult getVerificationResult(@PathVariable("verificationId") String verificationId)
    {
        if (StringUtils.isEmpty(verificationId))
        {
            return AjaxResult.error("验证ID不能为空");
        }
        
        // 这里应该从数据库或缓存中获取验证结果
        // 由于是示例，这里返回模拟数据
        Map<String, Object> result = new HashMap<>();
        result.put("verificationId", verificationId);
        result.put("verified", true);
        result.put("message", "照片验证通过");
        
        return AjaxResult.success(result);
    }
} 