import json
from datetime import datetime
from bson.objectid import ObjectId

# 垃圾分类数据
garbage_guides = [
    {
        "name": "可回收物",
        "description": "可回收物是指适宜回收循环使用和资源利用的废物。",
        "image_url": "/profile/garbage/recyclable.png",
        "category": "recyclable",
        "items": [
            "废纸", "塑料", "玻璃", "金属", "织物等"
        ],
        "disposal_method": "投放到可回收物收集容器",
        "tips": "尽量保持清洁干燥，避免污染"
    },
    {
        "name": "有害垃圾",
        "description": "有害垃圾是指对人体健康或自然环境造成直接或潜在危害的废弃物。",
        "image_url": "/profile/garbage/hazardous.png",
        "category": "hazardous",
        "items": [
            "废电池", "废荧光灯管", "废药品", "废油漆", "废杀虫剂"
        ],
        "disposal_method": "投放到有害垃圾收集容器",
        "tips": "轻放轻投，避免二次污染"
    },
    {
        "name": "厨余垃圾",
        "description": "厨余垃圾是指日常生活中产生的食物残余、食品加工废料等易腐烂有机物质。",
        "image_url": "/profile/garbage/kitchen.png",
        "category": "kitchen",
        "items": [
            "剩菜剩饭", "骨头", "菜根菜叶", "果皮", "蛋壳", "畜禽内脏"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免渗漏"
    },
    {
        "name": "其他垃圾",
        "description": "其他垃圾是指除可回收物、有害垃圾、厨余垃圾以外的其他生活废弃物。",
        "image_url": "/profile/garbage/other.png",
        "category": "other",
        "items": [
            "纸巾", "尿不湿", "烟头", "陶瓷", "卫生纸"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "尽量减少产生量，做好包装密封"
    },
    {
        "name": "废纸",
        "description": "废纸是可回收物中的一种，包括报纸、杂志、书籍等纸制品。",
        "image_url": "/profile/garbage/paper.png",
        "category": "recyclable",
        "items": [
            "报纸", "杂志", "书籍", "纸板箱", "包装纸"
        ],
        "disposal_method": "投放到可回收物收集容器",
        "tips": "保持干燥，避免沾染油污"
    },
    {
        "name": "塑料",
        "description": "塑料是可回收物中的一种，包括塑料瓶、塑料袋等。",
        "image_url": "/profile/garbage/plastic.png",
        "category": "recyclable",
        "items": [
            "塑料瓶", "塑料袋", "塑料盒", "塑料玩具", "塑料餐具"
        ],
        "disposal_method": "投放到可回收物收集容器",
        "tips": "清洗干净，压扁节省空间"
    },
    {
        "name": "玻璃",
        "description": "玻璃是可回收物中的一种，包括玻璃瓶、玻璃杯等。",
        "image_url": "/profile/garbage/glass.png",
        "category": "recyclable",
        "items": [
            "玻璃瓶", "玻璃杯", "玻璃罐", "玻璃窗", "镜子"
        ],
        "disposal_method": "投放到可回收物收集容器",
        "tips": "小心轻放，避免破碎造成伤害"
    },
    {
        "name": "金属",
        "description": "金属是可回收物中的一种，包括易拉罐、金属罐头等。",
        "image_url": "/profile/garbage/metal.png",
        "category": "recyclable",
        "items": [
            "易拉罐", "金属罐头", "铁钉", "铝箔", "金属餐具"
        ],
        "disposal_method": "投放到可回收物收集容器",
        "tips": "清洗干净，压扁节省空间"
    },
    {
        "name": "织物",
        "description": "织物是可回收物中的一种，包括旧衣服、旧床单等。",
        "image_url": "/profile/garbage/textile.png",
        "category": "recyclable",
        "items": [
            "旧衣服", "旧床单", "旧毛巾", "旧窗帘", "旧鞋子"
        ],
        "disposal_method": "投放到可回收物收集容器或专门的旧衣回收箱",
        "tips": "保持干燥，避免发霉"
    },
    {
        "name": "废电池",
        "description": "废电池是有害垃圾中的一种，包括干电池、纽扣电池等。",
        "image_url": "/profile/garbage/battery.png",
        "category": "hazardous",
        "items": [
            "干电池", "纽扣电池", "充电电池", "手机电池", "汽车电池"
        ],
        "disposal_method": "投放到有害垃圾收集容器",
        "tips": "单独投放，避免与其他垃圾混合"
    },
    {
        "name": "废荧光灯管",
        "description": "废荧光灯管是有害垃圾中的一种，含有汞等有害物质。",
        "image_url": "/profile/garbage/fluorescent.png",
        "category": "hazardous",
        "items": [
            "荧光灯管", "节能灯", "LED灯", "灯泡", "灯带"
        ],
        "disposal_method": "投放到有害垃圾收集容器",
        "tips": "小心轻放，避免破碎造成汞污染"
    },
    {
        "name": "废药品",
        "description": "废药品是有害垃圾中的一种，包括过期药品、药物包装等。",
        "image_url": "/profile/garbage/medicine.png",
        "category": "hazardous",
        "items": [
            "过期药品", "药物包装", "药瓶", "药片", "药膏"
        ],
        "disposal_method": "投放到有害垃圾收集容器",
        "tips": "不要随意丢弃，避免环境污染"
    },
    {
        "name": "废油漆",
        "description": "废油漆是有害垃圾中的一种，含有有害化学物质。",
        "image_url": "/profile/garbage/paint.png",
        "category": "hazardous",
        "items": [
            "油漆", "涂料", "染料", "稀释剂", "油漆桶"
        ],
        "disposal_method": "投放到有害垃圾收集容器",
        "tips": "密封包装，避免泄漏"
    },
    {
        "name": "剩菜剩饭",
        "description": "剩菜剩饭是厨余垃圾中的一种，是食物残余。",
        "image_url": "/profile/garbage/leftover.png",
        "category": "kitchen",
        "items": [
            "剩饭", "剩菜", "剩汤", "剩粥", "过期食品"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免渗漏"
    },
    {
        "name": "骨头",
        "description": "骨头是厨余垃圾中的一种，是食物残余。",
        "image_url": "/profile/garbage/bone.png",
        "category": "kitchen",
        "items": [
            "鱼骨", "鸡骨", "猪骨", "牛骨", "羊骨"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免异味"
    },
    {
        "name": "菜根菜叶",
        "description": "菜根菜叶是厨余垃圾中的一种，是食物加工废料。",
        "image_url": "/profile/garbage/vegetable.png",
        "category": "kitchen",
        "items": [
            "菜根", "菜叶", "菜梗", "菜皮", "菜籽"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免渗漏"
    },
    {
        "name": "果皮",
        "description": "果皮是厨余垃圾中的一种，是食物加工废料。",
        "image_url": "/profile/garbage/fruit_peel.png",
        "category": "kitchen",
        "items": [
            "苹果皮", "香蕉皮", "橙子皮", "西瓜皮", "梨皮"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免渗漏"
    },
    {
        "name": "蛋壳",
        "description": "蛋壳是厨余垃圾中的一种，是食物加工废料。",
        "image_url": "/profile/garbage/eggshell.png",
        "category": "kitchen",
        "items": [
            "鸡蛋壳", "鸭蛋壳", "鹅蛋壳", "鹌鹑蛋壳", "皮蛋壳"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免异味"
    },
    {
        "name": "畜禽内脏",
        "description": "畜禽内脏是厨余垃圾中的一种，是食物加工废料。",
        "image_url": "/profile/garbage/offal.png",
        "category": "kitchen",
        "items": [
            "鸡内脏", "鸭内脏", "猪内脏", "牛内脏", "羊内脏"
        ],
        "disposal_method": "投放到厨余垃圾收集容器",
        "tips": "沥干水分，避免异味"
    },
    {
        "name": "纸巾",
        "description": "纸巾是其他垃圾中的一种，不可回收。",
        "image_url": "/profile/garbage/tissue.png",
        "category": "other",
        "items": [
            "餐巾纸", "面巾纸", "卫生纸", "湿纸巾", "厨房纸巾"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "避免与可回收物混合"
    },
    {
        "name": "尿不湿",
        "description": "尿不湿是其他垃圾中的一种，不可回收。",
        "image_url": "/profile/garbage/diaper.png",
        "category": "other",
        "items": [
            "婴儿尿不湿", "成人尿不湿", "卫生巾", "卫生护垫", "医用纸尿裤"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "包裹密封，避免异味"
    },
    {
        "name": "烟头",
        "description": "烟头是其他垃圾中的一种，不可回收。",
        "image_url": "/profile/garbage/cigarette.png",
        "category": "other",
        "items": [
            "香烟烟头", "雪茄烟头", "电子烟弹", "烟灰", "烟盒"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "确保完全熄灭，避免火灾"
    },
    {
        "name": "陶瓷",
        "description": "陶瓷是其他垃圾中的一种，不可回收。",
        "image_url": "/profile/garbage/ceramic.png",
        "category": "other",
        "items": [
            "陶瓷碗", "陶瓷盘", "陶瓷杯", "陶瓷花盆", "陶瓷装饰品"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "包裹好碎片，避免割伤"
    },
    {
        "name": "卫生纸",
        "description": "卫生纸是其他垃圾中的一种，不可回收。",
        "image_url": "/profile/garbage/toilet_paper.png",
        "category": "other",
        "items": [
            "卫生纸", "厕纸", "纸巾筒", "纸巾盒", "纸巾芯"
        ],
        "disposal_method": "投放到其他垃圾收集容器",
        "tips": "避免与可回收物混合"
    }
]

# 为每个指南添加MongoDB所需的字段
for guide in garbage_guides:
    # 添加MongoDB ObjectId
    guide["_id"] = {"$oid": str(ObjectId())}
    
    # 添加创建和更新时间
    now = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%fZ")
    guide["create_time"] = now
    guide["update_time"] = now
    
    # 添加创建者和更新者
    guide["create_by"] = "admin"
    guide["update_by"] = "admin"
    
    # 添加删除标志
    guide["del_flag"] = "0"
    
    # 添加备注
    guide["remark"] = f"{guide['name']}分类指南"

# 将数据保存为JSON文件
with open('garbage_classification_guides.json', 'w', encoding='utf-8') as f:
    json.dump(garbage_guides, f, ensure_ascii=False, indent=2)

print(
    f"已生成{len(garbage_guides)}条垃圾分类指南数据，"
    f"并保存到garbage_classification_guides.json文件中"
)

# 生成MongoDB导入格式
with open('garbage_guide_mongodb.json', 'w', encoding='utf-8') as f:
    for guide in garbage_guides:
        f.write(json.dumps(guide, ensure_ascii=False) + '\n')

print("已生成MongoDB导入格式的数据，保存到garbage_guide_mongodb.json文件中") 