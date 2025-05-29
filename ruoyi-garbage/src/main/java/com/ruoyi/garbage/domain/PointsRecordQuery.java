package com.ruoyi.garbage.domain;

import java.util.Date;

/**
 * 积分记录查询对象
 * 
 * @author ruoyi
 */
public class PointsRecordQuery {
    /** 用户ID */
    private Long userId;

    /** 用户名称 */
    private String userName;

    /** 积分变动类型（1:增加 2:减少） */
    private Integer type;

    /** 积分来源（1:垃圾投递 2:管理员调整 3:积分兑换） */
    private Integer source;

    /** 开始时间 */
    private Date beginTime;

    /** 结束时间 */
    private Date endTime;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页记录数 */
    private Integer pageSize = 10;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
} 