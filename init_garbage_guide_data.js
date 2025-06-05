// MongoDB脚本 - 初始化垃圾分类指南数据

// 连接到MongoDB
// 使用方法: mongo localhost:27017/garbage_classification init_garbage_guide_data.js

print("开始初始化垃圾分类指南数据...");

// 删除现有集合
print("删除现有garbage_guide集合...");
db.garbage_guide.drop();

// 垃圾分类类型
var garbageTypes = {
    "可回收物": "适宜回收和资源利用的垃圾",
    "有害垃圾": "对人体健康或自然环境造成直接或潜在危害的垃圾",
    "厨余垃圾": "易腐烂的、含有机质的生活垃圾",
    "其他垃圾": "除可回收物、有害垃圾、厨余垃圾以外的其他生活垃圾"
};

// 创建垃圾分类指南数据
var guides = [
    // 可回收物
    {
        garbage_name: "纸箱",
        garbage_type: "可回收物",
        disposal_tips: "清除胶带，压扁后投放",
        remark: "纸箱是常见的可回收物，回收后可再生产成纸制品",
        image_url: "https://example.com/images/cardboard.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "塑料瓶",
        garbage_type: "可回收物",
        disposal_tips: "清洗干净，压扁后投放",
        remark: "塑料瓶可回收再利用，减少白色污染",
        image_url: "https://example.com/images/plastic_bottle.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "玻璃瓶",
        garbage_type: "可回收物",
        disposal_tips: "清洗干净，轻放避免破碎",
        remark: "玻璃可以100%回收再利用，且质量不会下降",
        image_url: "https://example.com/images/glass_bottle.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "易拉罐",
        garbage_type: "可回收物",
        disposal_tips: "清洗干净，压扁后投放",
        remark: "铝制易拉罐可以无限次回收再利用",
        image_url: "https://example.com/images/can.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "废纸",
        garbage_type: "可回收物",
        disposal_tips: "保持干燥，避免污染",
        remark: "纸张可以回收再生产成再生纸",
        image_url: "https://example.com/images/waste_paper.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    
    // 有害垃圾
    {
        garbage_name: "废电池",
        garbage_type: "有害垃圾",
        disposal_tips: "投放到专门的有害垃圾收集容器",
        remark: "电池含有重金属，需要特殊处理",
        image_url: "https://example.com/images/battery.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "废灯管",
        garbage_type: "有害垃圾",
        disposal_tips: "轻放，避免破碎",
        remark: "荧光灯管含有汞，需要特殊处理",
        image_url: "https://example.com/images/light_tube.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "过期药品",
        garbage_type: "有害垃圾",
        disposal_tips: "去除外包装后投放",
        remark: "药品可能含有有害物质，需要特殊处理",
        image_url: "https://example.com/images/medicine.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "温度计",
        garbage_type: "有害垃圾",
        disposal_tips: "轻放，避免破碎",
        remark: "含汞温度计破碎后会释放有害物质",
        image_url: "https://example.com/images/thermometer.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "油漆桶",
        garbage_type: "有害垃圾",
        disposal_tips: "确保密封，避免残留物泄露",
        remark: "油漆含有有害化学物质",
        image_url: "https://example.com/images/paint.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    
    // 厨余垃圾
    {
        garbage_name: "剩饭剩菜",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分后投放",
        remark: "厨余垃圾可制成堆肥",
        image_url: "https://example.com/images/leftover.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "果皮",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分后投放",
        remark: "果皮可降解，是良好的堆肥材料",
        image_url: "https://example.com/images/fruit_peels.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "茶叶渣",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分后投放",
        remark: "茶叶渣可用于堆肥或养花",
        image_url: "https://example.com/images/tea_leaves.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "蛋壳",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分后投放",
        remark: "蛋壳含有钙质，是良好的堆肥材料",
        image_url: "https://example.com/images/eggshells.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "鱼骨",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分后投放",
        remark: "鱼骨可降解，但分解速度较慢",
        image_url: "https://example.com/images/fish_bones.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },

    // 其他垃圾
    {
        garbage_name: "烟蒂",
        garbage_type: "其他垃圾",
        disposal_tips: "确保已熄灭后投放",
        remark: "烟蒂含有有害物质，不易降解",
        image_url: "https://example.com/images/cigarette_butts.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "纸巾",
        garbage_type: "其他垃圾",
        disposal_tips: "用过的纸巾不可回收",
        remark: "使用后的纸巾已被污染，不宜回收",
        image_url: "https://example.com/images/tissue.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "尿不湿",
        garbage_type: "其他垃圾",
        disposal_tips: "包裹好后投放",
        remark: "尿不湿含有高分子材料，不易降解",
        image_url: "https://example.com/images/diaper.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "陶瓷碎片",
        garbage_type: "其他垃圾",
        disposal_tips: "包裹好锋利边缘后投放",
        remark: "陶瓷不属于玻璃，不可回收",
        image_url: "https://example.com/images/ceramic.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "毛发",
        garbage_type: "其他垃圾",
        disposal_tips: "包裹好后投放",
        remark: "毛发不易降解，不宜作为厨余垃圾处理",
        image_url: "https://example.com/images/hair.jpg",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    }
];

// 为四种垃圾类型添加类型指南
var typeGuides = [
    {
        garbage_name: "可回收物",
        garbage_type: "可回收物",
        disposal_tips: "轻投轻放；清洁干燥，避免污染；废纸尽量平整；立体包装物请清空内容物并清洁后压扁投放；有尖锐边角的，应包裹后投放",
        remark: "可回收物是指适宜回收和资源利用的生活废弃物",
        image_url: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "有害垃圾",
        garbage_type: "有害垃圾",
        disposal_tips: "投放时请注意轻放；易破损的请连同包装或包裹后轻放；如果是液体，请密封后投放",
        remark: "有害垃圾是指对人体健康或者自然环境造成直接或潜在危害的生活废弃物",
        image_url: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "厨余垃圾",
        garbage_type: "厨余垃圾",
        disposal_tips: "沥干水分；盛放在容器中密封投放；有包装物的厨余垃圾应将包装物去除后分类投放",
        remark: "厨余垃圾是指日常生活中产生的食物残渣、菜叶等易腐烂的生物质废弃物",
        image_url: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    },
    {
        garbage_name: "其他垃圾",
        garbage_type: "其他垃圾",
        disposal_tips: "投放前尽量沥干水分；难以辨别类别的生活垃圾投入其他垃圾容器内",
        remark: "其他垃圾是指除可回收物、有害垃圾、厨余垃圾以外的其他生活废弃物",
        image_url: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png",
        create_by: "admin",
        create_time: new Date(),
        update_by: "admin",
        update_time: new Date()
    }
];

// 合并所有数据
guides = guides.concat(typeGuides);

// 插入数据
print("正在插入垃圾分类指南数据...");
db.garbage_guide.insertMany(guides);

print("垃圾分类指南数据初始化完成!");
print("总共插入记录: " + guides.length + "条");
print("其中可回收物: " + guides.filter(g => g.garbage_type === "可回收物").length + "条");
print("其中有害垃圾: " + guides.filter(g => g.garbage_type === "有害垃圾").length + "条");
print("其中厨余垃圾: " + guides.filter(g => g.garbage_type === "厨余垃圾").length + "条");
print("其中其他垃圾: " + guides.filter(g => g.garbage_type === "其他垃圾").length + "条"); 