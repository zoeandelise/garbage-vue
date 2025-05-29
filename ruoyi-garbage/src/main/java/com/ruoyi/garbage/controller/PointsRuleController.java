package com.ruoyi.garbage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.garbage.domain.PointsRule;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 积分规则控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/garbage/points/rule")
public class PointsRuleController extends BaseController {
    
    // 积分规则单例
    private PointsRule pointsRule = new PointsRule();
    
    public PointsRuleController() {
        // 初始化默认规则
        pointsRule.setRecyclablePoints(10);
        pointsRule.setHazardousPoints(15);
        pointsRule.setKitchenPoints(5);
        pointsRule.setOtherPoints(3);
        pointsRule.setDailyDeliveryLimit(10);
        pointsRule.setDailyPointsLimit(200);
        pointsRule.setContinuousDays(7);
        pointsRule.setContinuousBonus(50);
        pointsRule.setMonthlyTopThreeBonus(100);
        pointsRule.setFirstUseBonus(20);
        pointsRule.setPointsExchangeRate(10);
        pointsRule.setMinExchangePoints(100);
        pointsRule.setRuleDescription("1. 可回收物每公斤可获得10积分\n2. 有害垃圾每公斤可获得15积分\n3. 厨余垃圾每公斤可获得5积分\n4. 其他垃圾每公斤可获得3积分\n5. 每天最多投递10次，最多获得200积分\n6. 连续7天投递可额外获得50积分\n7. 月度排名前三可获得100积分奖励\n8. 首次使用可获得20积分");
    }
    
    /**
     * 获取积分规则
     */
    @GetMapping
    public AjaxResult getPointsRule() {
        return AjaxResult.success(pointsRule);
    }
    
    /**
     * 更新积分规则
     */
    @PreAuthorize("@ss.hasPermi('points:rule:edit')")
    @PutMapping
    public AjaxResult updatePointsRule(@RequestBody PointsRule rule) {
        // 更新规则
        if (rule.getRecyclablePoints() != null) {
            pointsRule.setRecyclablePoints(rule.getRecyclablePoints());
        }
        if (rule.getHazardousPoints() != null) {
            pointsRule.setHazardousPoints(rule.getHazardousPoints());
        }
        if (rule.getKitchenPoints() != null) {
            pointsRule.setKitchenPoints(rule.getKitchenPoints());
        }
        if (rule.getOtherPoints() != null) {
            pointsRule.setOtherPoints(rule.getOtherPoints());
        }
        if (rule.getDailyDeliveryLimit() != null) {
            pointsRule.setDailyDeliveryLimit(rule.getDailyDeliveryLimit());
        }
        if (rule.getDailyPointsLimit() != null) {
            pointsRule.setDailyPointsLimit(rule.getDailyPointsLimit());
        }
        if (rule.getContinuousDays() != null) {
            pointsRule.setContinuousDays(rule.getContinuousDays());
        }
        if (rule.getContinuousBonus() != null) {
            pointsRule.setContinuousBonus(rule.getContinuousBonus());
        }
        if (rule.getMonthlyTopThreeBonus() != null) {
            pointsRule.setMonthlyTopThreeBonus(rule.getMonthlyTopThreeBonus());
        }
        if (rule.getFirstUseBonus() != null) {
            pointsRule.setFirstUseBonus(rule.getFirstUseBonus());
        }
        if (rule.getPointsExchangeRate() != null) {
            pointsRule.setPointsExchangeRate(rule.getPointsExchangeRate());
        }
        if (rule.getMinExchangePoints() != null) {
            pointsRule.setMinExchangePoints(rule.getMinExchangePoints());
        }
        if (rule.getRuleDescription() != null) {
            pointsRule.setRuleDescription(rule.getRuleDescription());
        }
        
        return AjaxResult.success();
    }
    
    /**
     * 获取积分规则配置（兼容另一个API路径）
     */
    @GetMapping("/config")
    public AjaxResult getPointsRuleConfig() {
        return getPointsRule();
    }
    
    /**
     * 更新积分规则配置（兼容另一个API路径）
     */
    @PreAuthorize("@ss.hasPermi('points:rule:edit')")
    @PutMapping("/config")
    public AjaxResult updatePointsRuleConfig(@RequestBody Map<String, Object> ruleData) {
        // 将Map转换为PointsRule对象
        PointsRule rule = new PointsRule();
        if (ruleData.containsKey("recyclablePoints")) {
            rule.setRecyclablePoints((Integer) ruleData.get("recyclablePoints"));
        }
        if (ruleData.containsKey("hazardousPoints")) {
            rule.setHazardousPoints((Integer) ruleData.get("hazardousPoints"));
        }
        if (ruleData.containsKey("kitchenPoints")) {
            rule.setKitchenPoints((Integer) ruleData.get("kitchenPoints"));
        }
        if (ruleData.containsKey("otherPoints")) {
            rule.setOtherPoints((Integer) ruleData.get("otherPoints"));
        }
        if (ruleData.containsKey("dailyDeliveryLimit")) {
            rule.setDailyDeliveryLimit((Integer) ruleData.get("dailyDeliveryLimit"));
        }
        if (ruleData.containsKey("dailyPointsLimit")) {
            rule.setDailyPointsLimit((Integer) ruleData.get("dailyPointsLimit"));
        }
        if (ruleData.containsKey("continuousDays")) {
            rule.setContinuousDays((Integer) ruleData.get("continuousDays"));
        }
        if (ruleData.containsKey("continuousBonus")) {
            rule.setContinuousBonus((Integer) ruleData.get("continuousBonus"));
        }
        if (ruleData.containsKey("monthlyTopThreeBonus")) {
            rule.setMonthlyTopThreeBonus((Integer) ruleData.get("monthlyTopThreeBonus"));
        }
        if (ruleData.containsKey("firstUseBonus")) {
            rule.setFirstUseBonus((Integer) ruleData.get("firstUseBonus"));
        }
        if (ruleData.containsKey("pointsExchangeRate")) {
            rule.setPointsExchangeRate((Integer) ruleData.get("pointsExchangeRate"));
        }
        if (ruleData.containsKey("minExchangePoints")) {
            rule.setMinExchangePoints((Integer) ruleData.get("minExchangePoints"));
        }
        if (ruleData.containsKey("ruleDescription")) {
            rule.setRuleDescription((String) ruleData.get("ruleDescription"));
        }
        
        return updatePointsRule(rule);
    }
} 