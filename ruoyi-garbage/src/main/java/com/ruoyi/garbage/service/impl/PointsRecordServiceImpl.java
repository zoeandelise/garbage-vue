package com.ruoyi.garbage.service.impl;

import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;
import com.ruoyi.garbage.repository.PointsRecordRepository;
import com.ruoyi.garbage.service.IPointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 积分记录服务实现类
 * 
 * @author ruoyi
 */
@Service
public class PointsRecordServiceImpl implements IPointsRecordService {
    
    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PointsRecord> selectPointsRecordList(PointsRecordQuery query) {
        // 构建查询条件
        Query mongoQuery = new Query();
        
        if (query != null) {
            // 用户ID条件
            if (query.getUserId() != null) {
                mongoQuery.addCriteria(Criteria.where("user_id").is(query.getUserId()));
            }
            
            // 用户名条件
            if (StringUtils.hasText(query.getUserName())) {
                mongoQuery.addCriteria(Criteria.where("user_name").regex(query.getUserName(), "i"));
            }
            
            // 积分类型条件
            if (query.getType() != null) {
                mongoQuery.addCriteria(Criteria.where("type").is(query.getType()));
            }
            
            // 积分来源条件
            if (query.getSource() != null) {
                mongoQuery.addCriteria(Criteria.where("source").is(query.getSource()));
            }
            
            // 时间范围条件
            if (query.getBeginTime() != null && query.getEndTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()).lte(query.getEndTime()));
            } else if (query.getBeginTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()));
            } else if (query.getEndTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").lte(query.getEndTime()));
            }
        }
        
        // 按创建时间降序排序
        mongoQuery.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "create_time"));
        
        return mongoTemplate.find(mongoQuery, PointsRecord.class);
    }

    @Override
    public long countPointsRecord(PointsRecordQuery query) {
        // 构建查询条件
        Query mongoQuery = new Query();
        
        if (query != null) {
            // 用户ID条件
            if (query.getUserId() != null) {
                mongoQuery.addCriteria(Criteria.where("user_id").is(query.getUserId()));
            }
            
            // 用户名条件
            if (StringUtils.hasText(query.getUserName())) {
                mongoQuery.addCriteria(Criteria.where("user_name").regex(query.getUserName(), "i"));
            }
            
            // 积分类型条件
            if (query.getType() != null) {
                mongoQuery.addCriteria(Criteria.where("type").is(query.getType()));
            }
            
            // 积分来源条件
            if (query.getSource() != null) {
                mongoQuery.addCriteria(Criteria.where("source").is(query.getSource()));
            }
            
            // 时间范围条件
            if (query.getBeginTime() != null && query.getEndTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()).lte(query.getEndTime()));
            } else if (query.getBeginTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").gte(query.getBeginTime()));
            } else if (query.getEndTime() != null) {
                mongoQuery.addCriteria(Criteria.where("create_time").lte(query.getEndTime()));
            }
        }
        
        return mongoTemplate.count(mongoQuery, PointsRecord.class);
    }

    @Override
    public PointsRecord selectPointsRecordById(String id) {
        Optional<PointsRecord> record = pointsRecordRepository.findById(id);
        return record.orElse(null);
    }

    @Override
    public int insertPointsRecord(PointsRecord pointsRecord) {
        PointsRecord saved = pointsRecordRepository.save(pointsRecord);
        return saved != null ? 1 : 0;
    }

    @Override
    public int updatePointsRecord(PointsRecord pointsRecord) {
        PointsRecord saved = pointsRecordRepository.save(pointsRecord);
        return saved != null ? 1 : 0;
    }

    @Override
    public int deletePointsRecordByIds(String[] ids) {
        int count = 0;
        for (String id : ids) {
            pointsRecordRepository.deleteById(id);
            count++;
        }
        return count;
    }

    @Override
    public int deletePointsRecordById(String id) {
        pointsRecordRepository.deleteById(id);
        return 1;
    }

    @Override
    public int getUserTotalPoints(Long userId) {
        // 查询用户所有积分记录
        List<PointsRecord> records = pointsRecordRepository.findByUserId(userId);
        
        // 计算总积分
        int total = 0;
        for (PointsRecord record : records) {
            // 积分类型：1-增加，2-减少
            if (record.getType() == 1) {
                total += record.getPoints();
            } else if (record.getType() == 2) {
                total -= record.getPoints();
            }
        }
        
        return total;
    }
} 