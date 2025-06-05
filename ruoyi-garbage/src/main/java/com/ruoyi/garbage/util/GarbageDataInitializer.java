package com.ruoyi.garbage.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.ruoyi.garbage.constant.GarbageConstants;
import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.repository.GarbageGuideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 垃圾分类数据初始化工具
 * 
 * @author ruoyi
 */
@Component
public class GarbageDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(GarbageDataInitializer.class);

    @Autowired
    private GarbageGuideRepository garbageGuideRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private GarbageImageGenerator imageGenerator;
    
    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }
    
    /**
     * 初始化数据（可手动触发）
     */
    public void run() throws Exception {
        initializeData();
    }
    
    /**
     * 初始化数据的具体实现
     */
    private void initializeData() {
        // 检查是否已有数据
        long count = garbageGuideRepository.count();
        if (count > 0) {
            log.info("垃圾分类数据已存在，共 {} 条记录，跳过初始化", count);
            return;
        }
        
        log.info("开始初始化垃圾分类数据...");
        
        // 确保图片已生成
        imageGenerator.generateDefaultImages();
        
        // 初始化垃圾分类数据
        List<GarbageGuide> guides = initGarbageData();
        
        // 保存到MongoDB
        garbageGuideRepository.saveAll(guides);
        
        log.info("垃圾分类数据初始化完成，共添加 {} 条记录", guides.size());
    }
    
    /**
     * 初始化垃圾分类数据
     */
    private List<GarbageGuide> initGarbageData() {
        List<GarbageGuide> guides = new ArrayList<>();
        
        // 创建垃圾分类数据
        addRecyclableItems(guides);
        addHarmfulItems(guides);
        addKitchenItems(guides);
        addOtherItems(guides);
        
        return guides;
    }
    
    /**
     * 添加可回收物
     */
    private void addRecyclableItems(List<GarbageGuide> guides) {
        String type = "recyclable";
        String typeName = "可回收物";
        String disposalTips = "可回收物应清洁干燥，分类投放。纸类应保持平整；金属、塑料、玻璃应清洁后投放；纺织物应保持清洁。";
        
        // 纸类
        addGuide(guides, "纸张", type, typeName, disposalTips, "纸张包括报纸、杂志、图书、各种包装纸等，但是要注意纸巾和厕所纸由于水溶性太强不可回收。", "/profile/garbage/paper.png");
        addGuide(guides, "报纸", type, typeName, disposalTips, "报纸应保持平整，不要揉皱，可以叠放整齐后投放到可回收物收集容器。", "/profile/garbage/paper.png");
        addGuide(guides, "杂志", type, typeName, disposalTips, "杂志应保持平整，可以叠放整齐后投放到可回收物收集容器。", "/profile/garbage/paper.png");
        addGuide(guides, "书籍", type, typeName, disposalTips, "书籍应保持平整，可以叠放整齐后投放到可回收物收集容器。", "/profile/garbage/paper.png");
        addGuide(guides, "纸板箱", type, typeName, disposalTips, "纸板箱应折叠压扁后投放，以节省空间。", "/profile/garbage/paper.png");
        
        // 塑料
        addGuide(guides, "塑料", type, typeName, disposalTips, "塑料包括各种塑料袋、塑料瓶、塑料盒等，投放前应清洁并压扁。", "/profile/garbage/plastic.png");
        addGuide(guides, "塑料瓶", type, typeName, disposalTips, "塑料瓶应清空内容物，压扁后投放到可回收物收集容器。", "/profile/garbage/plastic.png");
        addGuide(guides, "塑料袋", type, typeName, disposalTips, "塑料袋应清洁后投放到可回收物收集容器。", "/profile/garbage/plastic.png");
        addGuide(guides, "塑料玩具", type, typeName, disposalTips, "塑料玩具应尽量完整投放到可回收物收集容器。", "/profile/garbage/plastic.png");
        
        // 金属
        addGuide(guides, "金属", type, typeName, disposalTips, "金属包括易拉罐、罐头盒、金属厨具、金属玩具等，投放前应清洁并保持干燥。", "/profile/garbage/metal.png");
        addGuide(guides, "易拉罐", type, typeName, disposalTips, "易拉罐应清空内容物，压扁后投放到可回收物收集容器。", "/profile/garbage/metal.png");
        addGuide(guides, "罐头盒", type, typeName, disposalTips, "罐头盒应清空内容物，清洗后投放到可回收物收集容器。", "/profile/garbage/metal.png");
        addGuide(guides, "金属厨具", type, typeName, disposalTips, "废弃的金属厨具应清洁后投放到可回收物收集容器。", "/profile/garbage/metal.png");
        
        // 玻璃
        addGuide(guides, "玻璃", type, typeName, disposalTips, "玻璃包括各种玻璃瓶、玻璃杯、玻璃制品等，投放时要注意安全，防止破碎。", "/profile/garbage/glass.png");
        addGuide(guides, "玻璃瓶", type, typeName, disposalTips, "玻璃瓶应清空内容物，清洗后投放到可回收物收集容器。", "/profile/garbage/glass.png");
        addGuide(guides, "玻璃杯", type, typeName, disposalTips, "玻璃杯应完整投放到可回收物收集容器，如已破碎应小心包裹后投放。", "/profile/garbage/glass.png");
        
        // 纺织物
        addGuide(guides, "纺织物", type, typeName, disposalTips, "纺织物包括废弃衣物、床单、毛巾等，投放前应保持清洁干燥。", "/profile/garbage/textile.png");
        addGuide(guides, "废弃衣物", type, typeName, disposalTips, "废弃衣物应保持清洁干燥，可以叠放整齐后投放到可回收物收集容器。", "/profile/garbage/textile.png");
        addGuide(guides, "床单", type, typeName, disposalTips, "废弃床单应保持清洁干燥，可以叠放整齐后投放到可回收物收集容器。", "/profile/garbage/textile.png");
    }
    
    /**
     * 添加有害垃圾
     */
    private void addHarmfulItems(List<GarbageGuide> guides) {
        String type = "hazardous";
        String typeName = "有害垃圾";
        String disposalTips = "有害垃圾应专门收集，特殊处理。废电池、废灯管、废药品、废油漆等应分类投放到有害垃圾收集点。";
        
        // 电池
        addGuide(guides, "电池", type, typeName, disposalTips, "废电池含有重金属等有害物质，应投放到有害垃圾收集容器。", "/profile/garbage/battery.png");
        addGuide(guides, "充电电池", type, typeName, disposalTips, "废充电电池含有重金属等有害物质，应投放到有害垃圾收集容器。", "/profile/garbage/battery.png");
        addGuide(guides, "纽扣电池", type, typeName, disposalTips, "纽扣电池含有重金属等有害物质，应投放到有害垃圾收集容器。", "/profile/garbage/battery.png");
        
        // 灯管
        addGuide(guides, "荧光灯", type, typeName, disposalTips, "荧光灯含有汞等有害物质，应完整投放到有害垃圾收集容器。", "/profile/garbage/fluorescent.png");
        addGuide(guides, "节能灯", type, typeName, disposalTips, "节能灯含有汞等有害物质，应完整投放到有害垃圾收集容器。", "/profile/garbage/fluorescent.png");
        
        // 药品
        addGuide(guides, "药品", type, typeName, disposalTips, "废弃药品应连同包装一起投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/medicine.png");
        addGuide(guides, "过期药品", type, typeName, disposalTips, "过期药品应连同包装一起投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/medicine.png");
        addGuide(guides, "药物胶囊", type, typeName, disposalTips, "废弃药物胶囊应投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/medicine.png");
        
        // 油漆
        addGuide(guides, "油漆", type, typeName, disposalTips, "废油漆及其容器应投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/paint.png");
        addGuide(guides, "油漆桶", type, typeName, disposalTips, "废油漆桶应投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/paint.png");
        
        // 其他有害垃圾
        addGuide(guides, "杀虫剂", type, typeName, disposalTips, "废杀虫剂及其容器应投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/hazardous.png");
        addGuide(guides, "温度计", type, typeName, disposalTips, "含汞温度计应完整投放到有害垃圾收集容器，不要随意丢弃。", "/profile/garbage/hazardous.png");
        addGuide(guides, "化妆品", type, typeName, disposalTips, "废弃化妆品应连同容器一起投放到有害垃圾收集容器。", "/profile/garbage/hazardous.png");
    }
    
    /**
     * 添加厨余垃圾
     */
    private void addKitchenItems(List<GarbageGuide> guides) {
        String type = "kitchen";
        String typeName = "厨余垃圾";
        String disposalTips = "厨余垃圾应沥干水分后投放，包装物应去除后分类投放。果皮、菜叶、剩饭剩菜等易腐烂的食物垃圾应及时处理。";
        
        // 食物残渣
        addGuide(guides, "剩饭剩菜", type, typeName, disposalTips, "剩饭剩菜应沥干水分后投放到厨余垃圾收集容器。", "/profile/garbage/leftover.png");
        addGuide(guides, "面包", type, typeName, disposalTips, "废弃面包应投放到厨余垃圾收集容器。", "/profile/garbage/leftover.png");
        addGuide(guides, "蛋糕", type, typeName, disposalTips, "废弃蛋糕应投放到厨余垃圾收集容器。", "/profile/garbage/leftover.png");
        
        // 果蔬垃圾
        addGuide(guides, "果皮", type, typeName, disposalTips, "果皮应投放到厨余垃圾收集容器。", "/profile/garbage/fruit_peel.png");
        addGuide(guides, "菜根菜叶", type, typeName, disposalTips, "菜根菜叶应投放到厨余垃圾收集容器。", "/profile/garbage/vegetable.png");
        addGuide(guides, "瓜皮", type, typeName, disposalTips, "瓜皮应投放到厨余垃圾收集容器。", "/profile/garbage/fruit_peel.png");
        
        // 肉食垃圾
        addGuide(guides, "骨头", type, typeName, disposalTips, "肉骨头应投放到厨余垃圾收集容器。", "/profile/garbage/bone.png");
        addGuide(guides, "鱼刺", type, typeName, disposalTips, "鱼刺应投放到厨余垃圾收集容器。", "/profile/garbage/bone.png");
        addGuide(guides, "畜禽内脏", type, typeName, disposalTips, "畜禽内脏应沥干水分后投放到厨余垃圾收集容器。", "/profile/garbage/offal.png");
        
        // 其他厨余垃圾
        addGuide(guides, "蛋壳", type, typeName, disposalTips, "蛋壳应投放到厨余垃圾收集容器。", "/profile/garbage/eggshell.png");
        addGuide(guides, "茶叶渣", type, typeName, disposalTips, "茶叶渣应沥干水分后投放到厨余垃圾收集容器。", "/profile/garbage/kitchen.png");
        addGuide(guides, "中药渣", type, typeName, disposalTips, "中药渣应沥干水分后投放到厨余垃圾收集容器。", "/profile/garbage/kitchen.png");
    }
    
    /**
     * 添加其他垃圾
     */
    private void addOtherItems(List<GarbageGuide> guides) {
        String type = "other";
        String typeName = "其他垃圾";
        String disposalTips = "其他垃圾应投放到其他垃圾收集容器。尽量沥干水分，减少垃圾体积。";
        
        // 卫生垃圾
        addGuide(guides, "纸巾", type, typeName, disposalTips, "使用过的纸巾由于卫生原因不可回收，应投放到其他垃圾收集容器。", "/profile/garbage/tissue.png");
        addGuide(guides, "卫生纸", type, typeName, disposalTips, "使用过的卫生纸由于卫生原因不可回收，应投放到其他垃圾收集容器。", "/profile/garbage/toilet_paper.png");
        addGuide(guides, "尿不湿", type, typeName, disposalTips, "使用过的尿不湿应包裹好后投放到其他垃圾收集容器。", "/profile/garbage/diaper.png");
        
        // 烟头
        addGuide(guides, "烟头", type, typeName, disposalTips, "烟头应熄灭后投放到其他垃圾收集容器。", "/profile/garbage/cigarette.png");
        
        // 陶瓷
        addGuide(guides, "陶瓷", type, typeName, disposalTips, "废弃陶瓷制品应包裹好尖锐部分后投放到其他垃圾收集容器。", "/profile/garbage/ceramic.png");
        addGuide(guides, "陶瓷碗", type, typeName, disposalTips, "废弃陶瓷碗应包裹好尖锐部分后投放到其他垃圾收集容器。", "/profile/garbage/ceramic.png");
        addGuide(guides, "陶瓷杯", type, typeName, disposalTips, "废弃陶瓷杯应包裹好尖锐部分后投放到其他垃圾收集容器。", "/profile/garbage/ceramic.png");
        
        // 其他
        addGuide(guides, "灰土", type, typeName, disposalTips, "灰土应装袋后投放到其他垃圾收集容器。", "/profile/garbage/other.png");
        addGuide(guides, "橡皮泥", type, typeName, disposalTips, "废弃橡皮泥应投放到其他垃圾收集容器。", "/profile/garbage/other.png");
        addGuide(guides, "一次性餐具", type, typeName, disposalTips, "废弃一次性餐具应投放到其他垃圾收集容器。", "/profile/garbage/other.png");
    }
    
    /**
     * 添加垃圾分类指南
     */
    private void addGuide(List<GarbageGuide> guides, String name, String type, String typeName, String disposalTips, String description, String imageUrl) {
        GarbageGuide guide = new GarbageGuide();
        guide.setName(name);
        guide.setCategory(type);
        guide.setDisposal_method(disposalTips);
        guide.setDetailed_description(description);
        guide.setRemark("类型：" + typeName + ", 名称：" + name);
        guide.setImage_url(imageUrl);
        
        // 添加小贴士
        List<String> tips = new ArrayList<>();
        tips.add("正确分类可以减少环境污染");
        tips.add("请确保垃圾投放到正确的收集容器");
        guide.setTips(tips);
        
        // 添加包含物品
        List<String> items = new ArrayList<>();
        items.add(name);
        guide.setIncluded_items(items);
        
        guide.setCreateBy("admin");
        guide.setCreateTime(new Date());
        guide.setUpdateBy("admin");
        guide.setUpdateTime(new Date());
        
        guides.add(guide);
    }
} 