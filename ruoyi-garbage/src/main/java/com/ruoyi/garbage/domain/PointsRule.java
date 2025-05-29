package com.ruoyi.garbage.domain;

import java.io.Serializable;

/**
 * 积分规则实体类
 * 
 * @author ruoyi
 */
public class PointsRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 可回收物积分/kg */
    private Integer recyclablePoints;

    /** 有害垃圾积分/kg */
    private Integer hazardousPoints;

    /** 厨余垃圾积分/kg */
    private Integer kitchenPoints;

    /** 其他垃圾积分/kg */
    private Integer otherPoints;

    /** 每日最大投递次数 */
    private Integer dailyDeliveryLimit;

    /** 每日最大积分上限 */
    private Integer dailyPointsLimit;

    /** 连续投递奖励天数 */
    private Integer continuousDays;

    /** 连续投递奖励积分 */
    private Integer continuousBonus;

    /** 月度排名前三奖励 */
    private Integer monthlyTopThreeBonus;

    /** 首次使用奖励积分 */
    private Integer firstUseBonus;

    /** 积分兑换比例(积分/元) */
    private Integer pointsExchangeRate;

    /** 最低兑换积分 */
    private Integer minExchangePoints;

    /** 积分规则说明 */
    private String ruleDescription;

    public Integer getRecyclablePoints() {
        return recyclablePoints;
    }

    public void setRecyclablePoints(Integer recyclablePoints) {
        this.recyclablePoints = recyclablePoints;
    }

    public Integer getHazardousPoints() {
        return hazardousPoints;
    }

    public void setHazardousPoints(Integer hazardousPoints) {
        this.hazardousPoints = hazardousPoints;
    }

    public Integer getKitchenPoints() {
        return kitchenPoints;
    }

    public void setKitchenPoints(Integer kitchenPoints) {
        this.kitchenPoints = kitchenPoints;
    }

    public Integer getOtherPoints() {
        return otherPoints;
    }

    public void setOtherPoints(Integer otherPoints) {
        this.otherPoints = otherPoints;
    }

    public Integer getDailyDeliveryLimit() {
        return dailyDeliveryLimit;
    }

    public void setDailyDeliveryLimit(Integer dailyDeliveryLimit) {
        this.dailyDeliveryLimit = dailyDeliveryLimit;
    }

    public Integer getDailyPointsLimit() {
        return dailyPointsLimit;
    }

    public void setDailyPointsLimit(Integer dailyPointsLimit) {
        this.dailyPointsLimit = dailyPointsLimit;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public Integer getContinuousBonus() {
        return continuousBonus;
    }

    public void setContinuousBonus(Integer continuousBonus) {
        this.continuousBonus = continuousBonus;
    }

    public Integer getMonthlyTopThreeBonus() {
        return monthlyTopThreeBonus;
    }

    public void setMonthlyTopThreeBonus(Integer monthlyTopThreeBonus) {
        this.monthlyTopThreeBonus = monthlyTopThreeBonus;
    }

    public Integer getFirstUseBonus() {
        return firstUseBonus;
    }

    public void setFirstUseBonus(Integer firstUseBonus) {
        this.firstUseBonus = firstUseBonus;
    }

    public Integer getPointsExchangeRate() {
        return pointsExchangeRate;
    }

    public void setPointsExchangeRate(Integer pointsExchangeRate) {
        this.pointsExchangeRate = pointsExchangeRate;
    }

    public Integer getMinExchangePoints() {
        return minExchangePoints;
    }

    public void setMinExchangePoints(Integer minExchangePoints) {
        this.minExchangePoints = minExchangePoints;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
} 