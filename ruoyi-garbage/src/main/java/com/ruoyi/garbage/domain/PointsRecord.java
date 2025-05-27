package com.ruoyi.garbage.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 积分记录实体
 * 
 * @author ruoyi
 */
@Document(collection = "points_record")
public class PointsRecord {

    /** 记录ID */
    @Id
    private String id;

    /** 用户ID */
    @Field("user_id")
    private Long userId;

    /** 用户名 */
    @Field("user_name")
    private String userName;

    /** 积分变动 */
    @Field("points_change")
    private Integer pointsChange;

    /** 积分类型（1：获取，2：消费） */
    @Field("points_type")
    private Integer pointsType;

    /** 关联记录ID */
    @Field("reference_id")
    private String referenceId;

    /** 备注 */
    @Field("remark")
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field("create_time")
    private Date createTime;

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

    public Integer getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(Integer pointsChange) {
        this.pointsChange = pointsChange;
    }

    public Integer getPointsType() {
        return pointsType;
    }

    public void setPointsType(Integer pointsType) {
        this.pointsType = pointsType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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
} 