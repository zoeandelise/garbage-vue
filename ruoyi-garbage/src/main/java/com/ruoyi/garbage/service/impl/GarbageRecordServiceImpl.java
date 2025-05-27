package com.ruoyi.garbage.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.garbage.config.RabbitMQConfig;
import com.ruoyi.garbage.domain.GarbageRecord;
import com.ruoyi.garbage.repository.GarbageRecordRepository;
import com.ruoyi.garbage.service.IGarbageRecordService;

/**
 * 垃圾投递记录服务实现类
 * 
 * @author ruoyi
 */
@Service
public class GarbageRecordServiceImpl implements IGarbageRecordService {

    @Autowired
    private GarbageRecordRepository garbageRecordRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public GarbageRecord saveRecord(GarbageRecord record) {
        // 设置创建时间和更新时间
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        
        // 设置积分未计算标志
        record.setPointsCalculated(false);
        
        // 保存记录
        GarbageRecord savedRecord = garbageRecordRepository.save(record);
        
        // 发送消息到RabbitMQ，异步计算积分
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.POINTS_CALCULATION_EXCHANGE,
                RabbitMQConfig.POINTS_CALCULATION_ROUTING_KEY,
                savedRecord.getId());
        
        return savedRecord;
    }

    @Override
    public GarbageRecord getRecordById(String id) {
        Optional<GarbageRecord> record = garbageRecordRepository.findById(id);
        return record.orElse(null);
    }

    @Override
    public List<GarbageRecord> getRecordsByUserId(Long userId) {
        return garbageRecordRepository.findByUserId(userId);
    }

    @Override
    public Page<GarbageRecord> getRecordsByUserId(Long userId, Pageable pageable) {
        return garbageRecordRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<GarbageRecord> getRecordsByGarbageType(String garbageType) {
        return garbageRecordRepository.findByGarbageType(garbageType);
    }

    @Override
    public List<GarbageRecord> getAllRecords() {
        return garbageRecordRepository.findAll();
    }

    @Override
    public Page<GarbageRecord> getAllRecords(Pageable pageable) {
        return garbageRecordRepository.findAll(pageable);
    }

    @Override
    public GarbageRecord updateRecord(GarbageRecord record) {
        // 检查记录是否存在
        Optional<GarbageRecord> existingRecord = garbageRecordRepository.findById(record.getId());
        if (!existingRecord.isPresent()) {
            return null;
        }
        
        // 设置更新时间
        record.setUpdateTime(new Date());
        
        // 保存记录
        return garbageRecordRepository.save(record);
    }

    @Override
    public void deleteRecord(String id) {
        garbageRecordRepository.deleteById(id);
    }
} 