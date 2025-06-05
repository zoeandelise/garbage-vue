/**
 * MongoDB脚本：将Base64图片数据转换为文件并更新数据库记录
 * 
 * 使用方法：
 * 1. 确保MongoDB服务已启动
 * 2. 在命令行中执行：mongo localhost:27017/ruoyi_vue migrate_base64_to_files.js
 * 3. 脚本会自动处理所有包含photoData的记录
 * 
 * 注意：
 * - 请在执行前备份数据库
 * - 确保服务器上的/profile/garbage-photos/目录存在且有写入权限
 * - 脚本会在控制台输出处理结果
 */

// 连接到数据库
// var conn = new Mongo("localhost:27017");
// var db = conn.getDB("ruoyi_vue");

// 设置文件存储路径（需要根据实际部署情况修改）
var baseFilePath = "/profile/garbage-photos/";
var actualFilePath = "D:/ruoyi/uploadPath/profile/garbage-photos/";

// 创建文件存储目录（如果不存在）
try {
    var fileDir = new java.io.File(actualFilePath);
    if (!fileDir.exists()) {
        fileDir.mkdirs();
        print("创建目录: " + actualFilePath);
    }
} catch (e) {
    print("无法创建目录，请手动创建: " + actualFilePath);
    print("错误: " + e);
}

// 查找所有包含photoData但没有photoUrl的记录
var cursor = db.garbage_record.find({
    $and: [
        { photo_data: { $exists: true, $ne: null, $ne: "" } },
        { $or: [
            { photo_url: { $exists: false } },
            { photo_url: null },
            { photo_url: "" }
        ]}
    ]
});

var totalRecords = cursor.count();
print("找到 " + totalRecords + " 条需要迁移的记录");

var successCount = 0;
var errorCount = 0;

// 处理每条记录
cursor.forEach(function(record) {
    try {
        var photoData = record.photo_data;
        
        // 跳过不是Base64的数据
        if (!photoData.startsWith("data:image/")) {
            print("记录 " + record._id + " 的photoData不是有效的Base64数据，跳过");
            errorCount++;
            return;
        }
        
        // 提取Base64数据部分
        var base64Data = photoData.split(",")[1];
        if (!base64Data) {
            print("记录 " + record._id + " 的Base64数据格式无效，跳过");
            errorCount++;
            return;
        }
        
        // 确定图片格式
        var imageFormat = "jpg";
        if (photoData.includes("image/png")) {
            imageFormat = "png";
        }
        
        // 生成唯一文件名
        var fileName = "migrated_" + new Date().getTime() + "_" + record._id + "." + imageFormat;
        var filePath = actualFilePath + fileName;
        var dbFilePath = baseFilePath + fileName;
        
        // 将Base64数据写入文件
        var decoded = java.util.Base64.getDecoder().decode(base64Data);
        var fos = new java.io.FileOutputStream(filePath);
        fos.write(decoded);
        fos.close();
        
        // 更新数据库记录
        db.garbage_record.updateOne(
            { _id: record._id },
            { $set: { photo_url: dbFilePath } }
        );
        
        print("成功迁移记录 " + record._id + " 的图片到 " + dbFilePath);
        successCount++;
    } catch (e) {
        print("处理记录 " + record._id + " 时出错: " + e);
        errorCount++;
    }
});

print("\n迁移完成:");
print("总记录数: " + totalRecords);
print("成功迁移: " + successCount);
print("失败记录: " + errorCount);

if (errorCount > 0) {
    print("\n有一些记录迁移失败，请检查日志并手动处理。");
}

/**
 * 注意：此脚本执行后，原有的photoData字段仍会保留。
 * 如果需要清理photoData字段以减小数据库体积，可以在确认迁移成功后执行以下脚本：
 * 
 * db.garbage_record.updateMany(
 *     { photo_url: { $exists: true, $ne: null, $ne: "" } },
 *     { $unset: { photo_data: "" } }
 * );
 * 
 * 警告：执行上述脚本前务必确保所有图片已成功迁移，并且已备份数据库！
 */ 