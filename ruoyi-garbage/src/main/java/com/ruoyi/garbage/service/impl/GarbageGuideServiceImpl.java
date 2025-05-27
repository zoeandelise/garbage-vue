package com.ruoyi.garbage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public GarbageGuide saveGuide(GarbageGuide guide) {
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
        
        return garbageGuideRepository.save(guide);
    }

    @Override
    public GarbageGuide getGuideById(String id) {
        Optional<GarbageGuide> guide = garbageGuideRepository.findById(id);
        return guide.orElse(null);
    }

    @Override
    public GarbageGuide getGuideByGarbageName(String garbageName) {
        return garbageGuideRepository.findByGarbageName(garbageName);
    }

    @Override
    public List<GarbageGuide> searchGuidesByGarbageName(String garbageName) {
        return garbageGuideRepository.findByGarbageNameLike(garbageName);
    }

    @Override
    public Page<GarbageGuide> searchGuidesByGarbageName(String garbageName, Pageable pageable) {
        return garbageGuideRepository.findByGarbageNameLike(garbageName, pageable);
    }

    @Override
    public List<GarbageGuide> getGuidesByGarbageType(String garbageType) {
        return garbageGuideRepository.findByGarbageType(garbageType);
    }
    
    @Override
    public Page<GarbageGuide> getGuidesByType(String garbageType, Pageable pageable) {
        return garbageGuideRepository.findByGarbageType(garbageType, pageable);
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
        GarbageGuide exactMatch = garbageGuideRepository.findByGarbageName(keyword.trim());
        if (exactMatch != null) {
            List<GarbageGuide> result = new ArrayList<>();
            result.add(exactMatch);
            return result;
        }
        
        // 再按名称模糊匹配
        List<GarbageGuide> nameMatches = garbageGuideRepository.findByGarbageNameLike(keyword.trim());
        if (!nameMatches.isEmpty()) {
            return nameMatches;
        }
        
        // 最后按描述模糊匹配
        return garbageGuideRepository.findByDescriptionLike(keyword.trim());
    }
    
    @Override
    public List<String> getAllGarbageTypes() {
        // 返回所有垃圾类型
        Set<String> types = new HashSet<>();
        types.add(GarbageConstants.GARBAGE_TYPE_RECYCLABLE);
        types.add(GarbageConstants.GARBAGE_TYPE_HARMFUL);
        types.add(GarbageConstants.GARBAGE_TYPE_KITCHEN);
        types.add(GarbageConstants.GARBAGE_TYPE_OTHER);
        
        return new ArrayList<>(types);
    }
} 