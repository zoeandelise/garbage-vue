package com.ruoyi.garbage.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.garbage.domain.GarbageCategory;
import com.ruoyi.garbage.domain.GarbageItem;
import com.ruoyi.garbage.domain.GarbageItemQuery;
import com.ruoyi.garbage.repository.GarbageCategoryRepository;
import com.ruoyi.garbage.repository.GarbageItemRepository;
import com.ruoyi.garbage.service.IGarbageItemService;

/**
 * 垃圾物品服务实现
 * 
 * @author ruoyi
 */
@Service
public class GarbageItemServiceImpl implements IGarbageItemService {
    
    @Autowired
    private GarbageItemRepository garbageItemRepository;
    
    @Autowired
    private GarbageCategoryRepository garbageCategoryRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<GarbageItem> selectGarbageItemList(GarbageItemQuery query) {
        Query mongoQuery = new Query();
        
        if (query != null) {
            if (StringUtils.hasText(query.getItemName())) {
                mongoQuery.addCriteria(Criteria.where("item_name").regex(query.getItemName(), "i"));
            }
            if (StringUtils.hasText(query.getCategoryId())) {
                mongoQuery.addCriteria(Criteria.where("category_id").is(query.getCategoryId()));
            }
            if (StringUtils.hasText(query.getKeywords())) {
                mongoQuery.addCriteria(Criteria.where("keywords").regex(query.getKeywords(), "i"));
            }
            if (query.getStatus() != null) {
                mongoQuery.addCriteria(Criteria.where("status").is(query.getStatus()));
            }
            if (query.getIsCommon() != null) {
                mongoQuery.addCriteria(Criteria.where("is_common").is(query.getIsCommon()));
            }
            if (StringUtils.hasText(query.getBeginTime()) && StringUtils.hasText(query.getEndTime())) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()).lte(query.getEndTime()));
            }
        }
        
        return mongoTemplate.find(mongoQuery, GarbageItem.class);
    }

    @Override
    public GarbageItem selectGarbageItemById(String id) {
        return garbageItemRepository.findById(id).orElse(null);
    }

    @Override
    public int insertGarbageItem(GarbageItem garbageItem) {
        Date now = new Date();
        garbageItem.setCreateTime(now);
        garbageItem.setUpdateTime(now);
        if (garbageItem.getStatus() == null) {
            garbageItem.setStatus(0); // 默认启用
        }
        if (garbageItem.getIsCommon() == null) {
            garbageItem.setIsCommon(false); // 默认非常见
        }
        
        // 设置分类名称
        if (StringUtils.hasText(garbageItem.getCategoryId())) {
            GarbageCategory category = garbageCategoryRepository.findById(garbageItem.getCategoryId()).orElse(null);
            if (category != null) {
                garbageItem.setCategoryName(category.getCategoryName());
            }
        }
        
        GarbageItem savedItem = garbageItemRepository.save(garbageItem);
        return savedItem != null ? 1 : 0;
    }

    @Override
    public int updateGarbageItem(GarbageItem garbageItem) {
        GarbageItem existingItem = garbageItemRepository.findById(garbageItem.getId()).orElse(null);
        if (existingItem == null) {
            return 0;
        }
        
        garbageItem.setUpdateTime(new Date());
        garbageItem.setCreateTime(existingItem.getCreateTime());
        
        // 设置分类名称
        if (StringUtils.hasText(garbageItem.getCategoryId())) {
            GarbageCategory category = garbageCategoryRepository.findById(garbageItem.getCategoryId()).orElse(null);
            if (category != null) {
                garbageItem.setCategoryName(category.getCategoryName());
            }
        }
        
        GarbageItem updatedItem = garbageItemRepository.save(garbageItem);
        return updatedItem != null ? 1 : 0;
    }

    @Override
    public int deleteGarbageItemById(String id) {
        garbageItemRepository.deleteById(id);
        return 1;
    }

    @Override
    public int deleteGarbageItemByIds(String[] ids) {
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                garbageItemRepository.deleteById(id);
            }
            return ids.length;
        }
        return 0;
    }

    @Override
    public List<GarbageItem> selectItemsByCategoryId(String categoryId) {
        return garbageItemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<GarbageItem> searchGarbageItems(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(
            Criteria.where("item_name").regex(keyword, "i"),
            Criteria.where("keywords").regex(keyword, "i"),
            Criteria.where("description").regex(keyword, "i")
        );
        query.addCriteria(criteria);
        query.addCriteria(Criteria.where("status").is(0)); // 只查询启用的物品
        
        return mongoTemplate.find(query, GarbageItem.class);
    }

    @Override
    public String checkItemNameUnique(GarbageItem garbageItem) {
        String itemId = garbageItem.getId() == null ? "" : garbageItem.getId();
        GarbageItem existingItem = garbageItemRepository.findByItemName(garbageItem.getItemName());
        
        if (existingItem != null && !existingItem.getId().equals(itemId)) {
            return "1"; // 不唯一
        }
        
        return "0"; // 唯一
    }
} 