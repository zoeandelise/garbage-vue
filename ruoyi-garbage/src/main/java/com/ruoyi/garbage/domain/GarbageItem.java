package com.ruoyi.garbage.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 垃圾物品实体类
 * 
 * @author ruoyi
 */
@Document(collection = "garbage_item")
public class GarbageItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 物品ID */
    @Id
    private String id;

    /** 物品名称 */
    @Field("item_name")
    private String itemName;

    /** 物品图片 */
    @Field("item_image")
    private String itemImage;

    /** 物品描述 */
    @Field("description")
    private String description;

    /** 所属分类ID */
    @Field("category_id")
    private String categoryId;
    
    /** 所属分类名称 */
    @Field("category_name")
    private String categoryName;

    /** 处理方法 */
    @Field("disposal_method")
    private String disposalMethod;

    /** 注意事项 */
    @Field("precautions")
    private String precautions;

    /** 关键词 */
    @Field("keywords")
    private String keywords;

    /** 是否常见 */
    @Field("is_common")
    private Boolean isCommon;

    /** 创建时间 */
    @Field("create_time")
    private Date createTime;

    /** 更新时间 */
    @Field("update_time")
    private Date updateTime;

    /** 是否启用 */
    @Field("status")
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDisposalMethod() {
        return disposalMethod;
    }

    public void setDisposalMethod(String disposalMethod) {
        this.disposalMethod = disposalMethod;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Boolean getIsCommon() {
        return isCommon;
    }

    public void setIsCommon(Boolean isCommon) {
        this.isCommon = isCommon;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
} 