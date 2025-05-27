package com.ruoyi.garbage.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 垃圾分类指南实体
 * 
 * @author ruoyi
 */
@Document(collection = "garbage_guide")
public class GarbageGuide {

    /** 指南ID */
    @Id
    private String id;

    /** 垃圾名称 */
    @Indexed
    @Field("garbage_name")
    private String garbageName;

    /** 垃圾类型 */
    @Field("garbage_type")
    private String garbageType;

    /** 投放建议 */
    @Field("disposal_tips")
    private String disposalTips;

    /** 备注说明 */
    @Field("remark")
    private String remark;

    /** 图片URL */
    @Field("image_url")
    private String imageUrl;

    /** 创建者 */
    @Field("create_by")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field("create_time")
    private Date createTime;

    /** 更新者 */
    @Field("update_by")
    private String updateBy;

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

    public String getGarbageName() {
        return garbageName;
    }

    public void setGarbageName(String garbageName) {
        this.garbageName = garbageName;
    }

    public String getGarbageType() {
        return garbageType;
    }

    public void setGarbageType(String garbageType) {
        this.garbageType = garbageType;
    }

    public String getDisposalTips() {
        return disposalTips;
    }

    public void setDisposalTips(String disposalTips) {
        this.disposalTips = disposalTips;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
} 