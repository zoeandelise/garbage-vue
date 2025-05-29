package com.ruoyi.garbage.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分记录实体类
 * 
 * @author ruoyi
 */
@Document(collection = "points_record")
public class PointsRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @Id
    private String id;

    /** 用户ID */
    @Indexed
    @Field("user_id")
    private Long userId;

    /** 用户名称 */
    @Field("user_name")
    private String userName;

    /** 积分变动数量 */
    @Field("points")
    private Integer points;

    /** 积分变动类型（1:增加 2:减少） */
    @Field("type")
    private Integer type;

    /** 积分来源（1:垃圾投递 2:管理员调整 3:积分兑换） */
    @Field("source")
    private Integer source;

    /** 相关业务ID（如垃圾投递记录ID） */
    @Field("business_id")
    private String businessId;

    /** 备注 */
    @Field("remark")
    private String remark;

    /** 创建时间 */
    @Field("create_time")
    private Date createTime;

    /** 创建者 */
    @Field("create_by")
    private String createBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return "PointsRecord{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", points=" + points +
                ", type=" + type +
                ", source=" + source +
                ", businessId='" + businessId + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                '}';
    }
} 