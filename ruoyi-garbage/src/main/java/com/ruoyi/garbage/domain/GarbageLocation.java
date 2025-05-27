package com.ruoyi.garbage.domain;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 垃圾投递地理位置
 * 
 * @author ruoyi
 */
public class GarbageLocation {

    /** 经度 */
    @Field("longitude")
    private Double longitude;

    /** 纬度 */
    @Field("latitude")
    private Double latitude;

    /** 地址描述 */
    @Field("address")
    private String address;

    /** 城市 */
    @Field("city")
    private String city;

    /** 区县 */
    @Field("district")
    private String district;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
} 