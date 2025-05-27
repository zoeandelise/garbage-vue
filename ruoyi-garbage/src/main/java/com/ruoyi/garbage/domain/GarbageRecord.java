package com.ruoyi.garbage.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 垃圾投递记录实体
 * 
 * @author ruoyi
 */
@Document(collection = "garbage_record")
public class GarbageRecord {

    /** 记录ID */
    @Id
    private String id;

    /** 用户ID */
    @Field("user_id")
    private Long userId;

    /** 用户名 */
    @Field("user_name")
    private String userName;

    /** 垃圾类型 */
    @Field("garbage_type")
    private String garbageType;

    /** 垃圾重量(kg) */
    @Field("weight")
    private Double weight;

    /** 投递地点 */
    @Field("location")
    private GarbageLocation location;

    /** 照片URL */
    @Field("photo_url")
    private String photoUrl;

    /** 照片数据（Base64编码） */
    @Field("photo_data")
    private String photoData;

    /** 备注 */
    @Field("remark")
    private String remark;

    /** 积分 */
    @Field("points")
    private Integer points;

    /** 积分是否已计算 */
    @Field("points_calculated")
    private Boolean pointsCalculated;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field("create_time")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field("update_time")
    private Date updateTime;

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

    public String getGarbageType() {
        return garbageType;
    }

    public void setGarbageType(String garbageType) {
        this.garbageType = garbageType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public GarbageLocation getLocation() {
        return location;
    }

    public void setLocation(GarbageLocation location) {
        this.location = location;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getPointsCalculated() {
        return pointsCalculated;
    }

    public void setPointsCalculated(Boolean pointsCalculated) {
        this.pointsCalculated = pointsCalculated;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
} 