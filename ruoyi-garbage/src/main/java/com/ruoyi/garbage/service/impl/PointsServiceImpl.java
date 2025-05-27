package com.ruoyi.garbage.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruoyi.garbage.constant.GarbageConstants;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.repository.GarbageRecordRepository;
import com.ruoyi.garbage.repository.PointsRecordRepository;
import com.ruoyi.garbage.service.IPointsService;

/**
 * 积分服务实现类
 * 
 * @author ruoyi
 */
@Service
public class PointsServiceImpl implements IPointsService {

    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Autowired
    private GarbageRecordRepository garbageRecordRepository;

    @Override
    @Async
    public PointsRecord calculatePoints(String recordId) {
        // 查询垃圾投递记录
        Optional<GarbageRecord> optionalRecord = garbageRecordRepository.findById(recordId);
        if (!optionalRecord.isPresent()) {
            return null;
        }
        
        GarbageRecord record = optionalRecord.get();
        
        // 检查是否已计算积分
        if (Boolean.TRUE.equals(record.getPointsCalculated())) {
            // 查询已有积分记录
            return pointsRecordRepository.findByReferenceId(recordId);
        }
        
        // 根据垃圾类型计算积分
        int points = calculatePointsByGarbageType(record.getGarbageType(), record.getWeight());
        
        // 创建积分记录
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setId(UUID.randomUUID().toString());
        pointsRecord.setUserId(record.getUserId());
        pointsRecord.setUserName(record.getUserName());
        pointsRecord.setPointsChange(points);
        pointsRecord.setPointsType(1); // 1表示获取积分
        pointsRecord.setReferenceId(recordId);
        pointsRecord.setRemark("垃圾投递积分奖励");
        pointsRecord.setCreateTime(new Date());
        
        // 保存积分记录
        PointsRecord savedRecord = pointsRecordRepository.save(pointsRecord);
        
        // 更新垃圾投递记录的积分信息
        record.setPoints(points);
        record.setPointsCalculated(true);
        garbageRecordRepository.save(record);
        
        return savedRecord;
    }
    
    /**
     * 根据垃圾类型和重量计算积分
     * 
     * @param garbageType 垃圾类型
     * @param weight 重量(kg)
     * @return 积分
     */
    private int calculatePointsByGarbageType(String garbageType, Double weight) {
        int basePoints = 0;
        
        // 根据垃圾类型获取基础积分
        switch (garbageType) {
            case GarbageConstants.GARBAGE_TYPE_RECYCLABLE:
                basePoints = GarbageConstants.POINTS_RECYCLABLE;
                break;
            case GarbageConstants.GARBAGE_TYPE_HARMFUL:
                basePoints = GarbageConstants.POINTS_HARMFUL;
                break;
            case GarbageConstants.GARBAGE_TYPE_KITCHEN:
                basePoints = GarbageConstants.POINTS_KITCHEN;
                break;
            case GarbageConstants.GARBAGE_TYPE_OTHER:
                basePoints = GarbageConstants.POINTS_OTHER;
                break;
            default:
                basePoints = 0;
        }
        
        // 考虑重量因素，每千克额外加分（向下取整）
        int weightBonus = 0;
        if (weight != null && weight > 0) {
            weightBonus = (int) (weight * 2);
        }
        
        return basePoints + weightBonus;
    }

    @Override
    public PointsRecord savePointsRecord(PointsRecord pointsRecord) {
        // 设置创建时间
        if (pointsRecord.getCreateTime() == null) {
            pointsRecord.setCreateTime(new Date());
        }
        
        return pointsRecordRepository.save(pointsRecord);
    }

    @Override
    public PointsRecord getPointsRecordById(String id) {
        Optional<PointsRecord> record = pointsRecordRepository.findById(id);
        return record.orElse(null);
    }

    @Override
    public List<PointsRecord> getPointsRecordsByUserId(Long userId) {
        return pointsRecordRepository.findByUserId(userId);
    }

    @Override
    public Page<PointsRecord> getPointsRecordsByUserId(Long userId, Pageable pageable) {
        return pointsRecordRepository.findByUserId(userId, pageable);
    }

    @Override
    public int getTotalPoints(Long userId) {
        List<PointsRecord> records = pointsRecordRepository.findByUserId(userId);
        
        // 计算总积分
        int total = 0;
        for (PointsRecord record : records) {
            // 积分类型：1-获取，2-消费
            if (record.getPointsType() == 1) {
                total += record.getPointsChange();
            } else if (record.getPointsType() == 2) {
                total -= record.getPointsChange();
            }
        }
        
        return total;
    }

    @Override
    public PointsRecord consumePoints(Long userId, String userName, int points, String remark) {
        // 检查用户积分是否足够
        int totalPoints = getTotalPoints(userId);
        if (totalPoints < points) {
            return null;
        }
        
        // 创建积分消费记录
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setId(UUID.randomUUID().toString());
        pointsRecord.setUserId(userId);
        pointsRecord.setUserName(userName);
        pointsRecord.setPointsChange(points);
        pointsRecord.setPointsType(2); // 2表示消费积分
        pointsRecord.setRemark(remark);
        pointsRecord.setCreateTime(new Date());
        
        return pointsRecordRepository.save(pointsRecord);
    }
} 