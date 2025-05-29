package com.ruoyi.garbage.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruoyi.garbage.constant.GarbageConstants;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.repository.GarbageRecordRepository;
import com.ruoyi.garbage.repository.PointsRecordRepository;
import com.ruoyi.garbage.service.IPointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 积分服务实现类
 * 
 * @author ruoyi
 */
@Service
public class PointsServiceImpl implements IPointsService {

    private static final Logger log = LoggerFactory.getLogger(PointsServiceImpl.class);

    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Autowired
    private GarbageRecordRepository garbageRecordRepository;

    @Override
    public PointsRecord calculatePoints(String recordId) {
        try {
            // 查询垃圾投递记录
            Optional<GarbageRecord> optionalRecord = garbageRecordRepository.findById(recordId);
            if (!optionalRecord.isPresent()) {
                log.error("计算积分失败：找不到垃圾投递记录，ID: {}", recordId);
                return null;
            }
            
            GarbageRecord record = optionalRecord.get();
            
            // 检查是否已计算积分
            if (Boolean.TRUE.equals(record.getPointsCalculated())) {
                // 查询已有积分记录
                PointsRecord existingRecord = pointsRecordRepository.findByBusinessId(recordId);
                if (existingRecord != null) {
                    log.info("积分已计算，返回已有积分记录，用户ID: {}, 积分: {}", existingRecord.getUserId(), existingRecord.getPoints());
                    return existingRecord;
                }
                // 已标记为计算但找不到积分记录，继续计算
                log.warn("记录已标记为计算积分，但找不到积分记录，将重新计算, ID: {}", recordId);
            }
            
            // 根据垃圾类型计算积分
            int points = calculatePointsByGarbageType(record.getGarbageType(), record.getWeight());
            
            // 创建积分记录
            PointsRecord pointsRecord = new PointsRecord();
            pointsRecord.setId(UUID.randomUUID().toString());
            pointsRecord.setUserId(record.getUserId());
            pointsRecord.setUserName(record.getUserName());
            pointsRecord.setPoints(points);
            pointsRecord.setType(1); // 1表示增加积分
            pointsRecord.setSource(1); // 1表示垃圾投递
            pointsRecord.setBusinessId(recordId);
            pointsRecord.setRemark("垃圾投递积分奖励");
            pointsRecord.setCreateTime(new Date());
            
            // 保存积分记录
            PointsRecord savedRecord = pointsRecordRepository.save(pointsRecord);
            log.info("成功保存积分记录，用户ID: {}, 积分: {}", savedRecord.getUserId(), savedRecord.getPoints());
            
            // 更新垃圾投递记录的积分信息
            record.setPoints(points);
            record.setPointsCalculated(true);
            garbageRecordRepository.save(record);
            log.info("成功更新垃圾投递记录积分状态，ID: {}", recordId);
            
            return savedRecord;
        } catch (Exception e) {
            // 记录详细错误信息
            log.error("积分计算过程中发生异常：{}，记录ID: {}", e.getMessage(), recordId, e);
            return null;
        }
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
            // 积分类型：1-增加，2-减少
            if (record.getType() == 1) {
                total += record.getPoints();
            } else if (record.getType() == 2) {
                total -= record.getPoints();
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
        pointsRecord.setPoints(points);
        pointsRecord.setType(2); // 2表示减少积分
        pointsRecord.setSource(3); // 3表示积分兑换
        pointsRecord.setRemark(remark);
        pointsRecord.setCreateTime(new Date());
        
        return pointsRecordRepository.save(pointsRecord);
    }
} 