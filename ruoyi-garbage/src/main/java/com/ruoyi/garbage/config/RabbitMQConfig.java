package com.ruoyi.garbage.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.util.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RabbitMQ配置类
 * 
 * @author ruoyi
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // 积分计算队列
    public static final String POINTS_CALCULATION_QUEUE = "points.calculation.queue";
    // 积分计算交换机
    public static final String POINTS_CALCULATION_EXCHANGE = "points.calculation.exchange";
    // 积分计算路由键
    public static final String POINTS_CALCULATION_ROUTING_KEY = "points.calculation.key";
    
    // 默认消费者数量
    @Value("${spring.rabbitmq.listener.simple.concurrency:1}")
    private int concurrentConsumers;
    
    // 最大消费者数量
    @Value("${spring.rabbitmq.listener.simple.max-concurrency:3}")
    private int maxConcurrentConsumers;
    
    // 预取数量
    @Value("${spring.rabbitmq.listener.simple.prefetch:1}")
    private int prefetchCount;

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
     * 消息转换器 - 使用简单字符串转换器，避免类型转换问题
     */
    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

    /**
     * 使用字符串转换器的RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        
        // 消息发送失败回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.error("消息发送失败, 应答码: {}, 原因: {}, 交换机: {}, 路由键: {}, 消息: {}", 
                replyCode, replyText, exchange, routingKey, message);
        });
        
        // 消息确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送到交换机失败, 原因: {}, 关联数据: {}", cause, correlationData);
            }
        });
        
        return rabbitTemplate;
    }
    
    /**
     * 配置RabbitMQ监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        
        // 设置手动确认模式
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        
        // 设置并发消费者数量
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        
        // 设置预取数量为1，每次只获取一条消息
        factory.setPrefetchCount(prefetchCount);
        
        // 设置错误处理器
        factory.setErrorHandler(errorHandler());
        
        // 设置默认不重新入队被拒绝的消息
        factory.setDefaultRequeueRejected(false);
        
        return factory;
    }
    
    /**
     * 消息恢复策略 - 拒绝并不重新入队
     */
    @Bean
    public MessageRecoverer messageRecoverer() {
        return new RejectAndDontRequeueRecoverer();
    }
    
    /**
     * 自定义错误处理器
     */
    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(customExceptionStrategy());
    }
    
    /**
     * 自定义异常策略
     */
    @Bean
    public FatalExceptionStrategy customExceptionStrategy() {
        return new ConditionalRejectingErrorHandler.DefaultExceptionStrategy() {
            @Override
            public boolean isFatal(Throwable t) {
                // 记录异常信息
                log.error("RabbitMQ处理消息时发生异常: {}", t.getMessage(), t);
                
                // 调用父类方法判断是否为致命异常
                return super.isFatal(t);
            }
        };
    }
} 