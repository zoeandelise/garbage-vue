package com.ruoyi.garbage.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.service.IGarbageGuideService;
import com.ruoyi.garbage.util.GarbageDataInitializer;
import com.ruoyi.garbage.util.GarbageImageGenerator;
import com.ruoyi.garbage.util.GarbageExampleDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 垃圾分类查询 控制器
 */
@Anonymous
@RestController
@RequestMapping("/garbage/search")
public class GarbageSearchController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(GarbageSearchController.class);

    @Autowired
    private IGarbageGuideService garbageGuideService;
    
    @Autowired
    private GarbageDataInitializer garbageDataInitializer;
    
    @Autowired
    private GarbageImageGenerator garbageImageGenerator;
    
    @Autowired
    private GarbageExampleDataLoader garbageExampleDataLoader;

    /**
     * 根据关键词搜索垃圾分类指南
     */
    @GetMapping("/keyword")
    public AjaxResult searchByKeyword(@RequestParam String keyword) {
        try {
            // 解码关键词
            String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8.name());
            System.out.println("搜索关键词: " + decodedKeyword);
            
            // 安全处理：移除或替换可能导致MongoDB查询问题的特殊字符
            // 这是一个额外的安全措施，服务层已经有了正则表达式转义
            decodedKeyword = decodedKeyword.trim();
            
            List<GarbageGuide> guides = garbageGuideService.searchGuides(decodedKeyword);
            List<Map<String, Object>> result = convertToFrontendFormat(guides);
            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("搜索垃圾分类指南出错: {}", e.getMessage());
            return AjaxResult.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 根据垃圾名称精确查询垃圾分类指南
     */
    @GetMapping("/name")
    public AjaxResult getByName(@RequestParam String name) {
        try {
            // 解码名称
            String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            System.out.println("查询垃圾名称: " + decodedName);
            
            // 安全处理
            decodedName = decodedName.trim();
            
            GarbageGuide guide = garbageGuideService.getGuideByGarbageName(decodedName);
            if (guide != null) {
                Map<String, Object> result = convertToFrontendFormat(guide);
                return AjaxResult.success(result);
            }
            return AjaxResult.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询垃圾分类指南出错: {}", e.getMessage());
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有垃圾类型
     */
    @GetMapping("/types")
    public AjaxResult getAllTypes() {
        try {
            List<String> types = garbageGuideService.getAllGarbageTypes();
            return AjaxResult.success(types);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取垃圾类型出错: {}", e.getMessage());
            return AjaxResult.error("获取垃圾类型失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化垃圾分类图片
     */
    @GetMapping("/init-images")
    public AjaxResult initImages() {
        try {
            // 手动触发图片生成
            garbageImageGenerator.generateDefaultImages();
            return AjaxResult.success("垃圾分类图片初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化垃圾分类图片出错: {}", e.getMessage());
            return AjaxResult.error("初始化垃圾分类图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化垃圾分类数据
     */
    @GetMapping("/init-data")
    public AjaxResult initData() {
        try {
            // 手动触发数据初始化
            garbageDataInitializer.run();
            return AjaxResult.success("垃圾分类数据初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化垃圾分类数据出错: {}", e.getMessage());
            return AjaxResult.error("初始化垃圾分类数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化垃圾分类示例数据
     */
    @GetMapping("/init-examples")
    @Anonymous
    public AjaxResult initExamples() {
        try {
            garbageExampleDataLoader.loadExampleData();
            return AjaxResult.success("垃圾分类示例数据初始化成功");
        } catch (Exception e) {
            log.error("垃圾分类示例数据初始化失败", e);
            return AjaxResult.error("垃圾分类示例数据初始化失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据类别列出垃圾分类指南
     */
    @GetMapping("/list")
    @Anonymous
    public AjaxResult listByCategory(@RequestParam(required = false) String category) {
        try {
            List<GarbageGuide> guides;
            if (category != null && !category.isEmpty()) {
                guides = garbageGuideService.getGuidesByCategory(category);
            } else {
                guides = garbageGuideService.getAllGuides();
            }
            return AjaxResult.success(guides);
        } catch (Exception e) {
            log.error("列出垃圾分类指南出错: {}", e.getMessage());
            return AjaxResult.error("列出垃圾分类指南失败: " + e.getMessage());
        }
    }
    
    /**
     * 将后端实体转换为前端期望的格式
     */
    private List<Map<String, Object>> convertToFrontendFormat(List<GarbageGuide> guides) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (guides != null) {
            for (GarbageGuide guide : guides) {
                result.add(convertToFrontendFormat(guide));
            }
        }
        return result;
    }
    
    /**
     * 将单个后端实体转换为前端期望的格式
     */
    private Map<String, Object> convertToFrontendFormat(GarbageGuide guide) {
        Map<String, Object> result = new HashMap<>();
        if (guide != null) {
            result.put("_id", guide.getId());
            result.put("name", guide.getName());
            result.put("category", guide.getCategory());
            result.put("disposal_method", guide.getDisposal_method());
            result.put("detailed_description", guide.getDetailed_description());
            result.put("tips", guide.getTips());
            result.put("included_items", guide.getIncluded_items());
            result.put("image_url", guide.getImage_url());
            result.put("remark", guide.getRemark());
            result.put("create_by", guide.getCreateBy());
            result.put("create_time", guide.getCreateTime());
            result.put("update_by", guide.getUpdateBy());
            result.put("update_time", guide.getUpdateTime());
        }
        return result;
    }
} 