package com.ruoyi.garbage.domain;

/**
 * 垃圾分类查询条件
 * 
 * @author ruoyi
 */
public class GarbageCategoryQuery {
    
    /** 分类名称 */
    private String categoryName;
    
    /** 分类编码 */
    private String categoryCode;
    
    /** 状态（0正常 1停用） */
    private Integer status;
    
    /** 开始日期 */
    private String beginTime;
    
    /** 结束日期 */
    private String endTime;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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