package com.ruoyi.garbage.util;

import com.ruoyi.garbage.domain.GarbageGuide;
import com.ruoyi.garbage.repository.GarbageGuideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 垃圾分类示例数据加载器
 * 负责加载垃圾分类示例数据，包括常见垃圾、分类示例和测验题目
 */
@Component
public class GarbageExampleDataLoader {

    private static final Logger log = LoggerFactory.getLogger(GarbageExampleDataLoader.class);

    @Autowired
    private GarbageGuideRepository garbageGuideRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private GarbageImageGenerator garbageImageGenerator;

    /**
     * 加载垃圾分类示例数据
     */
    public void loadExampleData() {
        log.info("开始加载垃圾分类示例数据...");
        
        // 加载常见垃圾数据
        loadCommonGarbageData();
        
        // 加载分类示例数据
        loadCategoryExamplesData();
        
        // 加载测验题目数据
        loadQuizItemsData();
        
        log.info("垃圾分类示例数据加载完成");
    }

    /**
     * 加载常见垃圾数据
     */
    private void loadCommonGarbageData() {
        log.info("加载常见垃圾数据...");
        
        // 可回收物处理方法
        String recyclableDisposal = "请投放到可回收物收集容器，保持物品清洁干燥";
        
        // 可回收物列表
        List<String> recyclableItems = Arrays.asList(
            "纸张", "报纸", "书籍", "纸板箱", "塑料瓶", "饮料瓶", "矿泉水瓶", "洗发水瓶", 
            "玻璃瓶", "玻璃杯", "玻璃罐", "金属罐", "易拉罐", "铁罐", "铁钉", "铁片", 
            "旧衣物", "旧布料", "纺织物"
        );
        
        // 保存可回收物数据
        for (String item : recyclableItems) {
            // 检查是否已存在
            if (!isItemExists(item)) {
                GarbageGuide guide = new GarbageGuide();
                guide.setName(item);
                guide.setCategory("recyclable");
                guide.setDisposal_method(recyclableDisposal);
                guide.setDetailed_description("可回收物是指废纸张、废塑料、废玻璃、废金属和废织物等适宜回收和可循环利用的垃圾。");
                guide.setTips(Arrays.asList("投放前请清理干净食物残渣，保持物品干燥，拆除易拉罐拉环等。"));
                
                // 生成图片
                String imagePath = garbageImageGenerator.generateImageForItem(item, "recyclable");
                guide.setImage_url(imagePath);
                
                // 添加创建时间
                guide.setCreateBy("system");
                guide.setCreateTime(new Date());
                
                garbageGuideRepository.save(guide);
                log.info("已添加可回收物: {}", item);
            }
        }
        
        // 有害垃圾处理方法
        String hazardousDisposal = "请投放到有害垃圾收集点，单独投放，避免混合";
        
        // 有害垃圾列表
        List<String> hazardousItems = Arrays.asList(
            "电池", "充电宝", "废电路板", "废电子产品", "药品", "药片", "过期药物",
            "荧光灯", "荧光灯管", "节能灯", "水银温度计", "油漆", "油漆桶", "染发剂", 
            "杀虫剂", "消毒剂"
        );
        
        // 保存有害垃圾数据
        for (String item : hazardousItems) {
            // 检查是否已存在
            if (!isItemExists(item)) {
                GarbageGuide guide = new GarbageGuide();
                guide.setName(item);
                guide.setCategory("hazardous");
                guide.setDisposal_method(hazardousDisposal);
                guide.setDetailed_description("有害垃圾是指对人体健康或自然环境造成直接或潜在危害的垃圾，包含有毒有害物质。");
                guide.setTips(Arrays.asList("请轻拿轻放，避免有害物质泄漏或挥发，特别注意电池等物品的安全处理。"));
                
                // 生成图片
                String imagePath = garbageImageGenerator.generateImageForItem(item, "hazardous");
                guide.setImage_url(imagePath);
                
                // 添加创建时间
                guide.setCreateBy("system");
                guide.setCreateTime(new Date());
                
                garbageGuideRepository.save(guide);
                log.info("已添加有害垃圾: {}", item);
            }
        }
        
        // 厨余垃圾处理方法
        String kitchenDisposal = "请投放到厨余垃圾收集容器，沥干水分后投放";
        
        // 厨余垃圾列表
        List<String> kitchenItems = Arrays.asList(
            "果皮", "果核", "蔬菜", "剩饭剩菜", "茶叶渣", "咖啡渣", "蛋壳", 
            "骨头", "鱼刺", "花卉", "落叶", "宠物饲料", "中药渣"
        );
        
        // 保存厨余垃圾数据
        for (String item : kitchenItems) {
            // 检查是否已存在
            if (!isItemExists(item)) {
                GarbageGuide guide = new GarbageGuide();
                guide.setName(item);
                guide.setCategory("kitchen");
                guide.setDisposal_method(kitchenDisposal);
                guide.setDetailed_description("厨余垃圾是指易腐烂的、含有机质的生活垃圾，主要来源于日常生活中的食物加工、饮食剩余和过期食品等。");
                guide.setTips(Arrays.asList("投放前请沥干水分，去除包装物，大的骨头应破碎后投放。"));
                
                // 生成图片
                String imagePath = garbageImageGenerator.generateImageForItem(item, "kitchen");
                guide.setImage_url(imagePath);
                
                // 添加创建时间
                guide.setCreateBy("system");
                guide.setCreateTime(new Date());
                
                garbageGuideRepository.save(guide);
                log.info("已添加厨余垃圾: {}", item);
            }
        }
        
        // 其他垃圾处理方法
        String otherDisposal = "请投放到其他垃圾收集容器";
        
        // 其他垃圾列表
        List<String> otherItems = Arrays.asList(
            "纸巾", "尿不湿", "烟头", "一次性筷子", "牙签", "卫生纸", "卫生巾", 
            "尿片", "猫砂", "陶瓷碎片", "胶带", "创可贴", "口香糖"
        );
        
        // 保存其他垃圾数据
        for (String item : otherItems) {
            // 检查是否已存在
            if (!isItemExists(item)) {
                GarbageGuide guide = new GarbageGuide();
                guide.setName(item);
                guide.setCategory("other");
                guide.setDisposal_method(otherDisposal);
                guide.setDetailed_description("其他垃圾是指除可回收物、有害垃圾和厨余垃圾之外的其他生活垃圾，具有低价值、难回收的特点。");
                guide.setTips(Arrays.asList("投放前请尽量沥干水分，减少体积，避免包含可回收材料。"));
                
                // 生成图片
                String imagePath = garbageImageGenerator.generateImageForItem(item, "other");
                guide.setImage_url(imagePath);
                
                // 添加创建时间
                guide.setCreateBy("system");
                guide.setCreateTime(new Date());
                
                garbageGuideRepository.save(guide);
                log.info("已添加其他垃圾: {}", item);
            }
        }
    }

    /**
     * 加载分类示例数据
     */
    private void loadCategoryExamplesData() {
        log.info("加载分类示例数据...");
        
        // 可回收物示例
        if (!isItemExists("可回收物示例")) {
            GarbageGuide recyclableExample = new GarbageGuide();
            recyclableExample.setName("可回收物示例");
            recyclableExample.setCategory("example");
            recyclableExample.setDisposal_method("请投放到可回收物收集容器");
            recyclableExample.setDetailed_description("可回收物包括：废纸类（报纸、杂志、书籍、纸板箱等）、塑料类（饮料瓶、食品包装等）、玻璃类（玻璃瓶、玻璃杯等）、金属类（易拉罐、罐头盒等）、织物类（旧衣服、旧床单等）。");
            recyclableExample.setTips(Arrays.asList("投放前请将物品清洁干燥，去除不可回收部分。", "纸质包装应折叠压扁，塑料瓶应拧紧瓶盖并压扁，避免占用过多空间。"));
            recyclableExample.setImage_url(garbageImageGenerator.generateImage("可回收物", "蓝色", "recyclable"));
            recyclableExample.setCreateBy("system");
            recyclableExample.setCreateTime(new Date());
            garbageGuideRepository.save(recyclableExample);
            log.info("已添加可回收物示例");
        }
        
        // 有害垃圾示例
        if (!isItemExists("有害垃圾示例")) {
            GarbageGuide hazardousExample = new GarbageGuide();
            hazardousExample.setName("有害垃圾示例");
            hazardousExample.setCategory("example");
            hazardousExample.setDisposal_method("请投放到有害垃圾收集点");
            hazardousExample.setDetailed_description("有害垃圾包括：废电池（干电池、纽扣电池等）、废灯管（荧光灯管、节能灯等）、废药品（过期药品、药片、药水等）、废油漆（油漆桶、稀释剂等）、废杀虫剂（杀虫喷雾等）、废水银产品（水银温度计、水银血压计等）。");
            hazardousExample.setTips(Arrays.asList("有害垃圾应密闭包装，防止泄漏。", "电池应完整投放，不要拆解。", "药品应去除外包装再投放。", "灯管应小心轻放，避免破碎。"));
            hazardousExample.setImage_url(garbageImageGenerator.generateImage("有害垃圾", "红色", "hazardous"));
            hazardousExample.setCreateBy("system");
            hazardousExample.setCreateTime(new Date());
            garbageGuideRepository.save(hazardousExample);
            log.info("已添加有害垃圾示例");
        }
        
        // 厨余垃圾示例
        if (!isItemExists("厨余垃圾示例")) {
            GarbageGuide kitchenExample = new GarbageGuide();
            kitchenExample.setName("厨余垃圾示例");
            kitchenExample.setCategory("example");
            kitchenExample.setDisposal_method("请投放到厨余垃圾收集容器");
            kitchenExample.setDetailed_description("厨余垃圾包括：食物残渣（剩菜剩饭、过期食品等）、果皮果核（苹果核、香蕉皮等）、蔬菜废料（菜帮菜叶、变质蔬菜等）、肉骨废料（鱼骨、肉骨头等）、茶渣咖啡渣、宠物饲料、花卉落叶等。");
            kitchenExample.setTips(Arrays.asList("投放前应去除包装物（如塑料袋、食品盒等），尽量沥干水分减少垃圾重量。", "大块骨头应打碎再投放。"));
            kitchenExample.setImage_url(garbageImageGenerator.generateImage("厨余垃圾", "绿色", "kitchen"));
            kitchenExample.setCreateBy("system");
            kitchenExample.setCreateTime(new Date());
            garbageGuideRepository.save(kitchenExample);
            log.info("已添加厨余垃圾示例");
        }
        
        // 其他垃圾示例
        if (!isItemExists("其他垃圾示例")) {
            GarbageGuide otherExample = new GarbageGuide();
            otherExample.setName("其他垃圾示例");
            otherExample.setCategory("example");
            otherExample.setDisposal_method("请投放到其他垃圾收集容器");
            otherExample.setDetailed_description("其他垃圾包括：卫生纸（纸巾、卫生纸等）、卫生用品（卫生巾、尿不湿等）、烟头、污染严重的垃圾（油污纸张等）、一次性餐具、陶瓷碎片、镜子碎片、灰土、橡皮泥等。");
            otherExample.setTips(Arrays.asList("投放前应确保垃圾不含有可回收物、有害垃圾或厨余垃圾。", "尽量压缩体积减少空间占用。"));
            otherExample.setImage_url(garbageImageGenerator.generateImage("其他垃圾", "灰色", "other"));
            otherExample.setCreateBy("system");
            otherExample.setCreateTime(new Date());
            garbageGuideRepository.save(otherExample);
            log.info("已添加其他垃圾示例");
        }
    }

    /**
     * 加载测验题目数据
     */
    private void loadQuizItemsData() {
        log.info("加载测验题目数据...");
        
        // 测验题目1
        if (!isItemExists("测验题目：塑料饮料瓶")) {
            GarbageGuide quiz1 = new GarbageGuide();
            quiz1.setName("测验题目：塑料饮料瓶");
            quiz1.setCategory("quiz");
            quiz1.setDisposal_method("请投放到可回收物收集容器");
            quiz1.setDetailed_description("塑料饮料瓶属于可回收物，是一种常见的可回收塑料制品。");
            quiz1.setTips(Arrays.asList("投放前请将瓶内液体倒空，瓶盖拧紧，标签可以不用撕下，尽量压扁减少体积。"));
            quiz1.setImage_url(garbageImageGenerator.generateImageForItem("塑料饮料瓶", "recyclable"));
            quiz1.setCreateBy("system");
            quiz1.setCreateTime(new Date());
            garbageGuideRepository.save(quiz1);
            log.info("已添加测验题目1");
        }
        
        // 测验题目2
        if (!isItemExists("测验题目：废旧电池")) {
            GarbageGuide quiz2 = new GarbageGuide();
            quiz2.setName("测验题目：废旧电池");
            quiz2.setCategory("quiz");
            quiz2.setDisposal_method("请投放到有害垃圾收集点");
            quiz2.setDetailed_description("废旧电池属于有害垃圾，含有重金属等有害物质，需要特殊处理。");
            quiz2.setTips(Arrays.asList("请不要将电池与其他垃圾混合投放，应送至专门的有害垃圾收集点。", "不要拆解电池，防止有害物质泄漏。"));
            quiz2.setImage_url(garbageImageGenerator.generateImageForItem("废旧电池", "hazardous"));
            quiz2.setCreateBy("system");
            quiz2.setCreateTime(new Date());
            garbageGuideRepository.save(quiz2);
            log.info("已添加测验题目2");
        }
        
        // 测验题目3
        if (!isItemExists("测验题目：剩菜剩饭")) {
            GarbageGuide quiz3 = new GarbageGuide();
            quiz3.setName("测验题目：剩菜剩饭");
            quiz3.setCategory("quiz");
            quiz3.setDisposal_method("请投放到厨余垃圾收集容器");
            quiz3.setDetailed_description("剩菜剩饭属于厨余垃圾，是一种易腐烂的有机垃圾。");
            quiz3.setTips(Arrays.asList("投放前请去除包装物（如塑料盒、保鲜膜等），尽量沥干水分后投放，减少异味产生。"));
            quiz3.setImage_url(garbageImageGenerator.generateImageForItem("剩菜剩饭", "kitchen"));
            quiz3.setCreateBy("system");
            quiz3.setCreateTime(new Date());
            garbageGuideRepository.save(quiz3);
            log.info("已添加测验题目3");
        }
        
        // 测验题目4
        if (!isItemExists("测验题目：废纸巾")) {
            GarbageGuide quiz4 = new GarbageGuide();
            quiz4.setName("测验题目：废纸巾");
            quiz4.setCategory("quiz");
            quiz4.setDisposal_method("请投放到其他垃圾收集容器");
            quiz4.setDetailed_description("废纸巾属于其他垃圾，虽然是纸制品，但已被污染且纤维过短，不适合回收再利用。");
            quiz4.setTips(Arrays.asList("使用过的纸巾受到污染，不能与可回收的纸张一起回收，应作为其他垃圾处理。"));
            quiz4.setImage_url(garbageImageGenerator.generateImageForItem("废纸巾", "other"));
            quiz4.setCreateBy("system");
            quiz4.setCreateTime(new Date());
            garbageGuideRepository.save(quiz4);
            log.info("已添加测验题目4");
        }
        
        // 测验题目5
        if (!isItemExists("测验题目：玻璃瓶")) {
            GarbageGuide quiz5 = new GarbageGuide();
            quiz5.setName("测验题目：玻璃瓶");
            quiz5.setCategory("quiz");
            quiz5.setDisposal_method("请投放到可回收物收集容器");
            quiz5.setDetailed_description("玻璃瓶属于可回收物，可以被回收利用制造新的玻璃制品。");
            quiz5.setTips(Arrays.asList("投放前请将瓶内液体倒空并清洗干净，小心轻放避免破碎。", "如已破碎，请用纸包好再投放，防止割伤收集人员。"));
            quiz5.setImage_url(garbageImageGenerator.generateImageForItem("玻璃瓶", "recyclable"));
            quiz5.setCreateBy("system");
            quiz5.setCreateTime(new Date());
            garbageGuideRepository.save(quiz5);
            log.info("已添加测验题目5");
        }
    }

    /**
     * 检查物品是否已存在
     * 
     * @param itemName 物品名称
     * @return 是否存在
     */
    private boolean isItemExists(String itemName) {
        Query query = new Query(Criteria.where("name").is(itemName));
        return mongoTemplate.exists(query, GarbageGuide.class);
    }
} 