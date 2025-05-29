package com.ruoyi.web.controller.mq.domain;

/**
 * 消息队列状态实体类
 * 
 * @author ruoyi
 */
public class MqStatus {
    /** 状态（running/stopped） */
    private String status;
    
    /** 队列总数 */
    private int queueCount;
    
    /** 消息总数 */
    private int messageCount;
    
    /** 错误消息数 */
    private int errorCount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
} 