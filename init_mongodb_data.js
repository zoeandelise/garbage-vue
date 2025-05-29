// MongoDB脚本 - 初始化积分记录数据

// 连接到MongoDB
// 使用方法: mongo localhost:27017/ruoyi init_mongodb_data.js

// 清空现有数据
db.points_record.drop();
print("已清空积分记录集合");

// 获取所有用户ID
var userIds = [];
var userNames = [];

// 从MySQL中查询用户列表（需要先手动获取）
// 这里先模拟一些用户数据
userIds = [1, 2, 3, 4, 5]; // 替换为实际的用户ID
userNames = ["admin", "test", "user1", "user2", "user3"]; // 替换为实际的用户名

// 垃圾类型
var garbageTypes = ["可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"];

// 积分来源描述
var sourceDescMap = {
    1: "垃圾投递积分奖励",
    2: "管理员调整",
    3: "积分兑换"
};

// 生成过去90天的随机日期
function randomDate(start, end) {
    return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
}

var endDate = new Date();
var startDate = new Date();
startDate.setDate(startDate.getDate() - 90);

// 为每个用户生成积分记录
var records = [];
for (var i = 0; i < userIds.length; i++) {
    var userId = userIds[i];
    var userName = userNames[i];
    
    // 每个用户生成20-30条记录
    var recordCount = Math.floor(Math.random() * 11) + 20;
    
    for (var j = 0; j < recordCount; j++) {
        // 随机积分类型：1-增加，2-减少
        var type = Math.random() <= 0.8 ? 1 : 2; // 80%概率增加积分
        
        // 随机积分来源：1-垃圾投递，2-管理员调整，3-积分兑换
        var source;
        if (type == 1) {
            // 增加积分：80%来自垃圾投递，20%来自管理员调整
            source = Math.random() <= 0.8 ? 1 : 2;
        } else {
            // 减少积分：80%来自积分兑换，20%来自管理员调整
            source = Math.random() <= 0.8 ? 3 : 2;
        }
        
        // 随机积分数量
        var points;
        if (source == 1) {
            // 垃圾投递：5-50积分
            points = Math.floor(Math.random() * 46) + 5;
        } else if (source == 2) {
            // 管理员调整：10-100积分
            points = Math.floor(Math.random() * 91) + 10;
        } else {
            // 积分兑换：20-200积分
            points = Math.floor(Math.random() * 181) + 20;
        }
        
        // 随机创建时间
        var createTime = randomDate(startDate, endDate);
        
        // 设置备注
        var remark;
        if (source == 1) {
            // 垃圾投递
            var garbageType = garbageTypes[Math.floor(Math.random() * garbageTypes.length)];
            var weight = (Math.random() * 9.5 + 0.5).toFixed(1); // 0.5-10.0kg，保留一位小数
            remark = "投递" + garbageType + " " + weight + "kg";
        } else {
            // 其他来源
            remark = sourceDescMap[source];
        }
        
        // 创建记录
        var record = {
            _id: UUID().toString().replace(/-/g, ""),
            user_id: userId,
            user_name: userName,
            points: points,
            type: type,
            source: source,
            remark: remark,
            create_time: createTime,
            create_by: "admin"
        };
        
        records.push(record);
    }
}

// 批量插入记录
db.points_record.insertMany(records);
print("成功插入 " + records.length + " 条积分记录");

// 查询统计
var totalRecords = db.points_record.count();
var addedPoints = db.points_record.find({type: 1}).count();
var reducedPoints = db.points_record.find({type: 2}).count();

print("总记录数: " + totalRecords);
print("增加积分记录数: " + addedPoints);
print("减少积分记录数: " + reducedPoints); 