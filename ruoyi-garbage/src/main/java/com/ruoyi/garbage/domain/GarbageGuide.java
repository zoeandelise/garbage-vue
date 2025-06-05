package com.ruoyi.garbage.domain;

import java.util.Date;
import java.util.List;

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
    @Field("name")
    private String name;

    /** 垃圾类型 */
    @Field("category")
    private String category;

    /** 投放建议 */
    @Field("disposal_method")
    private String disposal_method;

    /** 详细描述 */
    @Field("detailed_description")
    private String detailed_description;

    /** 小贴士 */
    @Field("tips")
    private List<String> tips;

    /** 包含物品 */
    @Field("included_items")
    private List<String> included_items;

    /** 备注说明 */
    @Field("remark")
    private String remark;

    /** 图片URL */
    @Field("image_url")
    private String image_url;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDisposal_method() {
        return disposal_method;
    }

    public void setDisposal_method(String disposal_method) {
        this.disposal_method = disposal_method;
    }

    public String getDetailed_description() {
        return detailed_description;
    }

    public void setDetailed_description(String detailed_description) {
        this.detailed_description = detailed_description;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public List<String> getIncluded_items() {
        return included_items;
    }

    public void setIncluded_items(List<String> included_items) {
        this.included_items = included_items;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    // 兼容旧版本的方法
    public String getGarbageName() {
        return name;
    }

    public void setGarbageName(String garbageName) {
        this.name = garbageName;
    }

    public String getGarbageType() {
        return category;
    }

    public void setGarbageType(String garbageType) {
        this.category = garbageType;
    }

    public String getDisposalTips() {
        return disposal_method;
    }

    public void setDisposalTips(String disposalTips) {
        this.disposal_method = disposalTips;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }
} 