package com.ruoyi.garbage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.garbage.domain.GarbageCategory;
import com.ruoyi.garbage.domain.GarbageCategoryQuery;
import com.ruoyi.garbage.repository.GarbageCategoryRepository;
import com.ruoyi.garbage.service.IGarbageCategoryService;

/**
 * 垃圾分类服务实现
 * 
 * @author ruoyi
 */
@Service
public class GarbageCategoryServiceImpl implements IGarbageCategoryService {
    
    @Autowired
    private GarbageCategoryRepository garbageCategoryRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<GarbageCategory> selectGarbageCategoryList(GarbageCategoryQuery query) {
        Query mongoQuery = new Query();
        
        if (query != null) {
            if (StringUtils.hasText(query.getCategoryName())) {
                mongoQuery.addCriteria(Criteria.where("category_name").regex(query.getCategoryName(), "i"));
            }
            if (StringUtils.hasText(query.getCategoryCode())) {
                mongoQuery.addCriteria(Criteria.where("category_code").regex(query.getCategoryCode(), "i"));
            }
            if (query.getStatus() != null) {
                mongoQuery.addCriteria(Criteria.where("status").is(query.getStatus()));
            }
            if (StringUtils.hasText(query.getBeginTime()) && StringUtils.hasText(query.getEndTime())) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()).lte(query.getEndTime()));
            }
        }
        
        return mongoTemplate.find(mongoQuery, GarbageCategory.class);
    }

    @Override
    public GarbageCategory selectGarbageCategoryById(String id) {
        return garbageCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public int insertGarbageCategory(GarbageCategory garbageCategory) {
        Date now = new Date();
        garbageCategory.setCreateTime(now);
        garbageCategory.setUpdateTime(now);
        if (garbageCategory.getStatus() == null) {
            garbageCategory.setStatus(0); // 默认启用
        }
        if (garbageCategory.getSort() == null) {
            garbageCategory.setSort(1); // 默认排序
        }
        
        GarbageCategory savedCategory = garbageCategoryRepository.save(garbageCategory);
        return savedCategory != null ? 1 : 0;
    }

    @Override
    public int updateGarbageCategory(GarbageCategory garbageCategory) {
        GarbageCategory existingCategory = garbageCategoryRepository.findById(garbageCategory.getId()).orElse(null);
        if (existingCategory == null) {
            return 0;
        }
        
        garbageCategory.setUpdateTime(new Date());
        garbageCategory.setCreateTime(existingCategory.getCreateTime());
        
        GarbageCategory updatedCategory = garbageCategoryRepository.save(garbageCategory);
        return updatedCategory != null ? 1 : 0;
    }

    @Override
    public int deleteGarbageCategoryById(String id) {
        garbageCategoryRepository.deleteById(id);
        return 1;
    }

    @Override
    public int deleteGarbageCategoryByIds(String[] ids) {
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                garbageCategoryRepository.deleteById(id);
            }
            return ids.length;
        }
        return 0;
    }

    @Override
    public List<GarbageCategory> selectAllEnabledCategories() {
        return garbageCategoryRepository.findByStatus(0);
    }

    @Override
    public String checkCategoryNameUnique(GarbageCategory garbageCategory) {
        String categoryId = garbageCategory.getId() == null ? "" : garbageCategory.getId();
        GarbageCategory existingCategory = garbageCategoryRepository.findByCategoryName(garbageCategory.getCategoryName());
        
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            return "1"; // 不唯一
        }
        
        return "0"; // 唯一
    }

    @Override
    public String checkCategoryCodeUnique(GarbageCategory garbageCategory) {
        String categoryId = garbageCategory.getId() == null ? "" : garbageCategory.getId();
        GarbageCategory existingCategory = garbageCategoryRepository.findByCategoryCode(garbageCategory.getCategoryCode());
        
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            return "1"; // 不唯一
        }
        
        return "0"; // 唯一
    }
} 