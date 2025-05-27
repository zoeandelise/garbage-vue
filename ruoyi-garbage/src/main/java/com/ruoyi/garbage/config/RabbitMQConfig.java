package com.ruoyi.garbage.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 
 * @author ruoyi
 */
@Configuration
public class RabbitMQConfig {

    // 积分计算队列
    public static final String POINTS_CALCULATION_QUEUE = "points.calculation.queue";
    // 积分计算交换机
    public static final String POINTS_CALCULATION_EXCHANGE = "points.calculation.exchange";
    // 积分计算路由键
    public static final String POINTS_CALCULATION_ROUTING_KEY = "points.calculation.key";

    /**
     * 积分计算队列
     */
    @Bean
    public Queue pointsCalculationQueue() {
        return new Queue(POINTS_CALCULATION_QUEUE, true);
    }

    /**
     * 积分计算交换机
     */
    @Bean
    public DirectExchange pointsCalculationExchange() {
        return new DirectExchange(POINTS_CALCULATION_EXCHANGE);
    }

    /**
     * 将积分计算队列绑定到交换机
     */
    @Bean
    public Binding bindingPointsCalculation() {
        return BindingBuilder.bind(pointsCalculationQueue()).to(pointsCalculationExchange())
                .with(POINTS_CALCULATION_ROUTING_KEY);
    }

    /**
     * 使用JSON序列化消息
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
} 