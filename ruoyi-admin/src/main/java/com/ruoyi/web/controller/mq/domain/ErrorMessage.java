package com.ruoyi.web.controller.mq.domain;

import java.util.Date;
import java.util.Map;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 错误消息实体类
 * 
 * @author ruoyi
 */
public class ErrorMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @Excel(name = "消息ID")
    private String messageId;
    
    /** 队列名称 */
    @Excel(name = "队列名称")
    private String queueName;
    
    /** 状态（success/error/processing） */
    @Excel(name = "状态", readConverterExp = "success=成功,error=失败,processing=处理中")
    private String status;
    
    /** 错误类型 */
    @Excel(name = "错误类型")
    private String errorType;
    
    /** 错误消息 */
    @Excel(name = "错误消息")
    private String errorMessage;
    
    /** 重试次数 */
    @Excel(name = "重试次数")
    private int retryCount;
    
    /** 处理时间 */
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;
    
    /** 消息内容 */
    private String messageContent;
    
    /** 错误堆栈 */
    private String errorStack;
    
    public ErrorMessage() {
    }
    
    public ErrorMessage(String messageId, String queueName, String status, String errorType, String errorMessage,
            int retryCount, Date processTime, String messageContent, String errorStack) {
        this.messageId = messageId;
        this.queueName = queueName;
        this.status = status;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.processTime = processTime;
        this.messageContent = messageContent;
        this.errorStack = errorStack;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(String errorStack) {
        this.errorStack = errorStack;
    }
} 