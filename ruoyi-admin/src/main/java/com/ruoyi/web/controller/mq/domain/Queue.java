package com.ruoyi.web.controller.mq.domain;

import java.util.Date;

/**
 * 队列实体类
 * 
 * @author ruoyi
 */
public class Queue {
    /** 队列名称 */
    private String queueName;
    
    /** 消息数量 */
    private int messageCount;
    
    /** 消费者数量 */
    private int consumerCount;
    
    /** 状态（active/inactive） */
    private String status;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 最后活跃时间 */
    private Date lastActiveTime;
    
    /** 已处理消息数 */
    private int processedCount;
    
    /** 待处理消息数 */
    private int pendingCount;
    
    /** 错误消息数 */
    private int errorCount;
    
    public Queue() {
    }
    
    public Queue(String queueName, int messageCount, int consumerCount, String status, Date createTime,
            Date lastActiveTime, int processedCount, int pendingCount, int errorCount) {
        this.queueName = queueName;
        this.messageCount = messageCount;
        this.consumerCount = consumerCount;
        this.status = status;
        this.createTime = createTime;
        this.lastActiveTime = lastActiveTime;
        this.processedCount = processedCount;
        this.pendingCount = pendingCount;
        this.errorCount = errorCount;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getConsumerCount() {
        return consumerCount;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
} 