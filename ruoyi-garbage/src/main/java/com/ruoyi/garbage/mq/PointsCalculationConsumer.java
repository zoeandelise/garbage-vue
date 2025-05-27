package com.ruoyi.garbage.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private IPointsService pointsService;

    /**
     * 处理积分计算消息
     * 
     * @param recordId 垃圾投递记录ID
     * @param channel RabbitMQ通道
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = RabbitMQConfig.POINTS_CALCULATION_QUEUE)
    public void handlePointsCalculation(String recordId, Channel channel, long deliveryTag) {
        try {
            log.info("开始处理积分计算消息，记录ID: {}", recordId);
            
            // 计算积分
            PointsRecord pointsRecord = pointsService.calculatePoints(recordId);
            
            if (pointsRecord != null) {
                log.info("积分计算成功，用户ID: {}, 积分: {}", pointsRecord.getUserId(), pointsRecord.getPointsChange());
                // 确认消息已处理
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("积分计算失败，记录ID: {}", recordId);
                // 拒绝消息，并重新入队
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (Exception e) {
            log.error("处理积分计算消息异常", e);
            try {
                // 拒绝消息，并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("拒绝消息异常", ex);
            }
        }
    }
} 