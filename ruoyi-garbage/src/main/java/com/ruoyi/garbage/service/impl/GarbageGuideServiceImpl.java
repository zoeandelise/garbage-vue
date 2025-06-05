package com.ruoyi.garbage.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.constant.GarbageConstants;
import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.repository.GarbageGuideRepository;
import com.ruoyi.garbage.service.IGarbageGuideService;

/**
 * 垃圾分类指南服务实现类
 * 
 * @author ruoyi
 */
@Service
public class GarbageGuideServiceImpl implements IGarbageGuideService {

    @Autowired
    private GarbageGuideRepository garbageGuideRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public GarbageGuide saveGuide(GarbageGuide guide) {
        // 生成ID（如果没有提供）
        if (guide.getId() == null || guide.getId().isEmpty()) {
            guide.setId(generateId());
        }
        
        // 设置创建者和创建时间
        Date now = new Date();
        guide.setCreateTime(now);
        guide.setUpdateTime(now);
        
        try {
            // 尝试获取当前登录用户
            String username = SecurityUtils.getUsername();
            guide.setCreateBy(username);
            guide.setUpdateBy(username);
        } catch (Exception e) {
            // 如果无法获取当前用户（例如在测试环境中），使用默认值
            guide.setCreateBy("system");
            guide.setUpdateBy("system");
        }
        
        // 保存前打印日志
        System.out.println("保存垃圾分类指南: " + guide.getName() + ", ID: " + guide.getId());
        
        try {
            GarbageGuide savedGuide = garbageGuideRepository.save(guide);
            System.out.println("保存成功，返回ID: " + savedGuide.getId());
            return savedGuide;
        } catch (Exception e) {
            System.err.println("保存垃圾分类指南失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // 生成唯一ID
    private String generateId() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public GarbageGuide getGuideById(String id) {
        System.out.println("根据ID查询垃圾指南: " + id);
        
        try {
            Optional<GarbageGuide> guide = garbageGuideRepository.findById(id);
            if (guide.isPresent()) {
                GarbageGuide result = guide.get();
                System.out.println("查询成功: " + result.getName());
                return result;
            } else {
                System.err.println("未找到ID为 " + id + " 的垃圾指南");
                return null;
            }
        } catch (Exception e) {
            System.err.println("根据ID查询垃圾指南出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GarbageGuide getGuideByName(String name) {
        // 先检查是否是垃圾类型
        if (name != null && (
                "recyclable".equals(name) ||
                "hazardous".equals(name) ||
                "kitchen".equals(name) ||
                "other".equals(name))) {
            
            // 如果是垃圾类型，创建一个对应的指南对象
            GarbageGuide guide = new GarbageGuide();
            guide.setName(name);
            guide.setCategory(name);
            
            // 根据不同类型设置投放建议
            if ("recyclable".equals(name)) {
                guide.setDisposal_method("轻投轻放；清洁干燥，避免污染；废纸尽量平整；立体包装物请清空内容物并清洁后压扁投放；有尖锐边角的，应包裹后投放");
            } else if ("hazardous".equals(name)) {
                guide.setDisposal_method("投放时请注意轻放；易破损的请连同包装或包裹后轻放；如果是液体，请密封后投放");
            } else if ("kitchen".equals(name)) {
                guide.setDisposal_method("沥干水分；盛放在容器中密封投放；有包装物的厨余垃圾应将包装物去除后分类投放");
            } else if ("other".equals(name)) {
                guide.setDisposal_method("投放前尽量沥干水分；难以辨别类别的生活垃圾投入其他垃圾容器内");
            }
            
            return guide;
        }
        
        // 如果不是垃圾类型，则按正常流程查询
        return garbageGuideRepository.findByName(name);
    }

    @Override
    public List<GarbageGuide> searchGuidesByName(String name) {
        // MongoDB正则表达式，不需要添加.*前缀和后缀
        String searchTerm = name;
        System.out.println("模糊查询垃圾名称: " + searchTerm);
        try {
            List<GarbageGuide> result = garbageGuideRepository.findByNameLike(searchTerm);
            System.out.println("模糊查询结果数量: " + result.size());
            return result;
        } catch (Exception e) {
            System.err.println("模糊查询出错: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Page<GarbageGuide> searchGuidesByName(String name, Pageable pageable) {
        // MongoDB正则表达式，不需要添加.*前缀和后缀
        String searchTerm = name;
        System.out.println("分页模糊查询垃圾名称: " + searchTerm + ", 页码: " + pageable.getPageNumber() + ", 每页数量: " + pageable.getPageSize());
        try {
            Page<GarbageGuide> result = garbageGuideRepository.findByNameLike(searchTerm, pageable);
            System.out.println("分页模糊查询结果数量: " + result.getContent().size() + ", 总数: " + result.getTotalElements());
            return result;
        } catch (Exception e) {
            System.err.println("分页模糊查询出错: " + e.getMessage());
            e.printStackTrace();
            // 返回空页
            return Page.empty(pageable);
        }
    }

    @Override
    public List<GarbageGuide> getGuidesByCategory(String category) {
        return garbageGuideRepository.findByCategory(category);
    }
    
    @Override
    public Page<GarbageGuide> getGuidesByCategory(String category, Pageable pageable) {
        return garbageGuideRepository.findByCategory(category, pageable);
    }

    @Override
    public List<GarbageGuide> getAllGuides() {
        return garbageGuideRepository.findAll();
    }

    @Override
    public Page<GarbageGuide> getAllGuides(Pageable pageable) {
        return garbageGuideRepository.findAll(pageable);
    }

    @Override
    public GarbageGuide updateGuide(GarbageGuide guide) {
        // 检查指南是否存在
        Optional<GarbageGuide> existingGuide = garbageGuideRepository.findById(guide.getId());
        if (!existingGuide.isPresent()) {
            return null;
        }
        
        // 设置更新时间和更新者
        guide.setUpdateTime(new Date());
        
        try {
            // 尝试获取当前登录用户
            String username = SecurityUtils.getUsername();
            guide.setUpdateBy(username);
        } catch (Exception e) {
            // 如果无法获取当前用户，使用默认值
            guide.setUpdateBy("system");
        }
        
        // 保留创建信息
        GarbageGuide original = existingGuide.get();
        guide.setCreateBy(original.getCreateBy());
        guide.setCreateTime(original.getCreateTime());
        
        return garbageGuideRepository.save(guide);
    }

    @Override
    public void deleteGuide(String id) {
        garbageGuideRepository.deleteById(id);
    }
    
    @Override
    public List<GarbageGuide> searchGuides(String keyword) {
        // 综合搜索，包括名称、描述等字段
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 先按名称精确匹配
        GarbageGuide exactMatch = garbageGuideRepository.findByName(keyword.trim());
        if (exactMatch != null) {
            List<GarbageGuide> result = new ArrayList<>();
            result.add(exactMatch);
            return result;
        }
        
        try {
            // 构建安全的正则表达式模式
            String regexPattern = escapeRegexSpecialChars(keyword.trim());
            System.out.println("正则表达式模式: " + regexPattern);
            
            // 按名称模糊匹配
            List<GarbageGuide> nameMatches = garbageGuideRepository.findByNameLike(regexPattern);
            
            // 如果没有找到结果，尝试按详细描述和备注搜索
            if (nameMatches.isEmpty()) {
                List<GarbageGuide> descMatches = garbageGuideRepository.findByDetailedDescriptionLike(regexPattern);
                if (!descMatches.isEmpty()) {
                    return descMatches;
                }
                
                List<GarbageGuide> remarkMatches = garbageGuideRepository.findByRemarkLike(regexPattern);
                if (!remarkMatches.isEmpty()) {
                    return remarkMatches;
                }
                
                // 最后尝试按包含物品搜索
                List<GarbageGuide> itemMatches = garbageGuideRepository.findByIncludedItemsLike(regexPattern);
                return itemMatches;
            }
            
            return nameMatches;
        } catch (Exception e) {
            System.err.println("模糊查询出错: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 转义正则表达式中的特殊字符
     */
    private String escapeRegexSpecialChars(String input) {
        return input.replaceAll("([\\\\+*?\\[\\](){}\\|^\\-\\.])", "\\\\$1");
    }
    
    @Override
    public List<String> getAllGarbageTypes() {
        // 返回所有垃圾类型
        return Arrays.asList("recyclable", "hazardous", "kitchen", "other");
    }
    
    // 兼容旧版本的方法
    @Override
    public GarbageGuide getGuideByGarbageName(String garbageName) {
        return getGuideByName(garbageName);
    }
    
    @Override
    public List<GarbageGuide> searchGuidesByGarbageName(String garbageName) {
        return searchGuidesByName(garbageName);
    }
    
    @Override
    public Page<GarbageGuide> searchGuidesByGarbageName(String garbageName, Pageable pageable) {
        return searchGuidesByName(garbageName, pageable);
    }
    
    @Override
    public List<GarbageGuide> getGuidesByGarbageType(String garbageType) {
        return getGuidesByCategory(garbageType);
    }
    
    @Override
    public Page<GarbageGuide> getGuidesByType(String garbageType, Pageable pageable) {
        return getGuidesByCategory(garbageType, pageable);
    }
} 