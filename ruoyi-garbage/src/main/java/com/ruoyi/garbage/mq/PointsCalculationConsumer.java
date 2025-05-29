package com.ruoyi.garbage.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.ruoyi.garbage.config.RabbitMQConfig;
import com.ruoyi.garbage.domain.PointsRecord;
import com.ruoyi.garbage.service.IPointsService;

/**
 * 积分计算消息消费者
 * 
 * @author ruoyi
 */
@Component
public class PointsCalculationConsumer {

    private static final Logger log = LoggerFactory.getLogger(PointsCalculationConsumer.class);
    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;

    @Autowired
    private IPointsService pointsService;

    /**
     * 处理积分计算消息
     * 使用String类型接收消息内容，避免类型转换问题
     * 
     * @param recordId 垃圾投递记录ID
     * @param message RabbitMQ消息对象
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitMQConfig.POINTS_CALCULATION_QUEUE)
    public void handlePointsCalculation(String recordId, Message message, Channel channel) throws Exception {
        // 获取消息的投递标签
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        log.info("开始处理积分计算消息，记录ID: \"{}\"，投递标签: {}", recordId, deliveryTag);
        
        try {
            // 1. 检查记录ID是否有效
            if (recordId == null || recordId.trim().isEmpty()) {
                log.error("积分计算消息无效，记录ID为空");
                // 无效消息，直接确认并丢弃
                channel.basicAck(deliveryTag, false);
                return;
            }
            
            // 2. 计算积分 - 不使用异步方法，改为同步调用
            PointsRecord pointsRecord = pointsService.calculatePoints(recordId);
            
            // 3. 处理结果
            if (pointsRecord != null) {
                log.info("积分计算成功，用户ID: {}, 积分: {}", pointsRecord.getUserId(), pointsRecord.getPoints());
                // 确认消息已处理
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("积分计算失败，记录ID: \"{}\"", recordId);
                
                // 拒绝消息，不重新入队（交给Spring重试机制处理）
                channel.basicReject(deliveryTag, false);
            }
        } catch (Exception e) {
            log.error("处理积分计算消息异常: {}", e.getMessage(), e);
            try {
                // 记录具体异常信息
                log.error("异常详情: ", e);
                
                // 拒绝消息，不重新入队（交给Spring重试机制处理）
                channel.basicReject(deliveryTag, false);
            } catch (Exception ex) {
                log.error("确认消息异常，可能导致消息重复消费: {}", ex.getMessage());
                // 抛出异常，让Spring AMQP处理
                throw new AmqpRejectAndDontRequeueException("无法处理消息，且无法确认: " + ex.getMessage(), ex);
            }
        }
    }
} 