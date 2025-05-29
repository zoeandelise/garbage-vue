package com.ruoyi.garbage.repository.impl;

import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.domain.PointsRecordQuery;
import com.ruoyi.garbage.repository.PointsRecordRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 积分记录自定义数据访问实现类
 * 
 * @author ruoyi
 */
@Repository
public class PointsRecordRepositoryImpl implements PointsRecordRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据查询条件分页查询积分记录
     * 
     * @param query 查询条件
     * @return 积分记录列表
     */
    @Override
    public List<PointsRecord> findByCondition(PointsRecordQuery query) {
        Query mongoQuery = createQuery(query);
        
        // 设置分页和排序
        PageRequest pageRequest = PageRequest.of(query.getPageNum() - 1, query.getPageSize(), 
                Sort.by(Sort.Direction.DESC, "create_time"));
        mongoQuery.with(pageRequest);
        
        return mongoTemplate.find(mongoQuery, PointsRecord.class);
    }

    /**
     * 根据查询条件统计积分记录数量
     * 
     * @param query 查询条件
     * @return 记录数量
     */
    @Override
    public long countByCondition(PointsRecordQuery query) {
        Query mongoQuery = createQuery(query);
        return mongoTemplate.count(mongoQuery, PointsRecord.class);
    }

    /**
     * 统计用户总积分
     * 
     * @param userId 用户ID
     * @return 总积分
     */
    @Override
    public int sumPointsByUserId(Long userId) {
        // 使用聚合查询计算总积分
        Aggregation aggregation = Aggregation.newAggregation(
            // 匹配指定用户ID
            Aggregation.match(Criteria.where("user_id").is(userId)),
            // 根据积分类型进行条件计算
            Aggregation.group()
                .sum(ConditionalOperators.when(Criteria.where("type").is(1))
                    .then("$points")
                    .otherwise(ArithmeticOperators.Multiply.valueOf("$points").multiplyBy(-1)))
                .as("totalPoints")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "points_record", Map.class);
        
        if (results.getMappedResults().isEmpty()) {
            return 0;
        }
        
        Map<String, Object> result = results.getMappedResults().get(0);
        Object totalPoints = result.get("totalPoints");
        
        return totalPoints == null ? 0 : ((Number) totalPoints).intValue();
    }

    /**
     * 创建MongoDB查询对象
     * 
     * @param query 查询条件
     * @return MongoDB查询对象
     */
    private Query createQuery(PointsRecordQuery query) {
        Criteria criteria = new Criteria();
        
        // 添加查询条件
        if (query.getUserId() != null) {
            criteria.and("user_id").is(query.getUserId());
        }
        
        if (StringUtils.hasText(query.getUserName())) {
            criteria.and("user_name").regex(query.getUserName(), "i");
        }
        
        if (query.getType() != null) {
            criteria.and("type").is(query.getType());
        }
        
        if (query.getSource() != null) {
            criteria.and("source").is(query.getSource());
        }
        
        if (query.getBeginTime() != null && query.getEndTime() != null) {
            criteria.and("create_time").gte(query.getBeginTime()).lte(query.getEndTime());
        } else if (query.getBeginTime() != null) {
            criteria.and("create_time").gte(query.getBeginTime());
        } else if (query.getEndTime() != null) {
            criteria.and("create_time").lte(query.getEndTime());
        }
        
        return new Query(criteria);
    }
} 