package com.ruoyi.garbage.domain;

/**
 * 垃圾物品查询条件
 * 
 * @author ruoyi
 */
public class GarbageItemQuery {
    
    /** 物品名称 */
    private String itemName;
    
    /** 所属分类ID */
    private String categoryId;
    
    /** 关键词 */
    private String keywords;
    
    /** 状态（0正常 1停用） */
    private Integer status;
    
    /** 是否常见 */
    private Boolean isCommon;
    
    /** 开始日期 */
    private String beginTime;
    
    /** 结束日期 */
    private String endTime;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsCommon() {
        return isCommon;
    }

    public void setIsCommon(Boolean isCommon) {
        this.isCommon = isCommon;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
} 