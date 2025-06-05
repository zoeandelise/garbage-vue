// MongoDB脚本 - 检查垃圾分类指南数据

// 连接到MongoDB
// 使用方法: mongo localhost:27017/garbage_classification check_garbage_guide_data.js

// 显示集合信息
print("=== 数据库信息 ===");
db.stats();
print("\n");

// 检查垃圾分类指南集合是否存在
print("=== 垃圾分类指南集合 ===");
const collections = db.getCollectionNames();
if (collections.includes("garbage_guide")) {
    print("垃圾分类指南集合存在");

    // 统计记录数
    const totalCount = db.garbage_guide.count();
    print("总记录数: " + totalCount);

    // 按垃圾类型统计
    print("\n按垃圾类型统计:");
    const types = db.garbage_guide.distinct("garbage_type");
    types.forEach(type => {
        const count = db.garbage_guide.count({ garbage_type: type });
        print(`- ${type}: ${count}条`);
    });

    // 显示几条示例数据
    print("\n示例数据:");
    db.garbage_guide.find().limit(3).forEach(doc => {
        print(JSON.stringify(doc, null, 2));
    });

    // 检查字段完整性
    print("\n字段完整性检查:");
    const missingGarbageName = db.garbage_guide.count({ garbage_name: { $exists: false } });
    const missingGarbageType = db.garbage_guide.count({ garbage_type: { $exists: false } });
    const missingDisposalTips = db.garbage_guide.count({ disposal_tips: { $exists: false } });
    
    print(`- 缺少garbage_name字段: ${missingGarbageName}条`);
    print(`- 缺少garbage_type字段: ${missingGarbageType}条`);
    print(`- 缺少disposal_tips字段: ${missingDisposalTips}条`);
} else {
    print("垃圾分类指南集合不存在！请先初始化数据。");
}

// 提供建议
print("\n=== 建议 ===");
if (!collections.includes("garbage_guide")) {
    print("请运行初始化脚本: init_garbage_guide_data.js");
} else if (db.garbage_guide.count() === 0) {
    print("垃圾分类指南集合存在但没有数据，请运行初始化脚本: init_garbage_guide_data.js");
} else {
    print("数据检查完成，如需重新初始化数据，请运行: init_garbage_guide_data.js");
} 