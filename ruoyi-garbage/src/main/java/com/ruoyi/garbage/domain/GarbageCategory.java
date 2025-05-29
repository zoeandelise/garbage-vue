package com.ruoyi.garbage.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 垃圾分类实体类
 * 
 * @author ruoyi
 */
@Document(collection = "garbage_category")
public class GarbageCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @Id
    private String id;

    /** 分类名称 */
    @Field("category_name")
    private String categoryName;

    /** 分类编码 */
    @Field("category_code")
    private String categoryCode;

    /** 分类图标 */
    @Field("category_icon")
    private String categoryIcon;

    /** 分类描述 */
    @Field("description")
    private String description;

    /** 处理方法 */
    @Field("disposal_method")
    private String disposalMethod;

    /** 注意事项 */
    @Field("precautions")
    private String precautions;

    /** 排序 */
    @Field("sort")
    private Integer sort;

    /** 创建时间 */
    @Field("create_time")
    private Date createTime;

    /** 更新时间 */
    @Field("update_time")
    private Date updateTime;

    /** 是否启用 */
    @Field("status")
    private Integer status;

    /** 典型物品 */
    @Field("typical_items")
    private List<String> typicalItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

    public List<String> getTypicalItems() {
        return typicalItems;
    }

    public void setTypicalItems(List<String> typicalItems) {
        this.typicalItems = typicalItems;
    }
} 