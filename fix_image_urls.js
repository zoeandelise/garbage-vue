// MongoDB脚本 - 修复垃圾投递记录中的图片URL
// 使用方法: mongo localhost:27017/garbage_classification fix_image_urls.js

print("开始修复图片URL...");

// 首先修复格式不正确的URL
print("步骤1: 修复格式不正确的URL路径...");
var records = db.garbage_record.find({
  photoUrl: { $exists: true, $ne: null },
  $and: [
    { photoUrl: { $ne: "" } },
    { photoUrl: { $not: /^http:\/\// } },
    { photoUrl: { $not: /^https:\/\// } },
    { photoUrl: { $not: /^\/profile\// } },
    { photoUrl: { $not: /^data:image\// } }
  ]
});

var count = 0;
records.forEach(function(record) {
  var oldUrl = record.photoUrl;
  var newUrl = "/profile/" + oldUrl;
  
  print("修复记录ID: " + record._id + ", 旧URL: " + oldUrl + ", 新URL: " + newUrl);
  
  db.garbage_record.updateOne(
    { _id: record._id },
    { $set: { photoUrl: newUrl } }
  );
  
  count++;
});

print("格式不正确的URL修复完成，共修复" + count + "条记录");

// 步骤2: 检查空的photoUrl但有photo_data或photoData字段的记录
print("\n步骤2: 修复有图片数据但URL为空的记录...");

// 处理MongoDB字段名可能为photo_url和photo_data(下划线风格)的情况
var base64Count = 0;

// 查找photoUrl为空但photoData不为空的记录(驼峰命名)
var emptyUrlRecords = db.garbage_record.find({
  $or: [
    { photoUrl: "" },
    { photoUrl: { $exists: false } },
    { photoUrl: null }
  ],
  $and: [
    { 
      $or: [
        { photoData: { $exists: true, $ne: null, $ne: "" } },
        { photo_data: { $exists: true, $ne: null, $ne: "" } }
      ]
    }
  ]
});

emptyUrlRecords.forEach(function(record) {
  var photoData = record.photoData || record.photo_data;
  if (!photoData) return;
  
  // 如果photoData不包含data:image前缀，添加前缀
  var newUrl = photoData;
  if (!photoData.startsWith("data:image/")) {
    newUrl = "data:image/jpeg;base64," + photoData;
  }
  
  print("修复Base64记录ID: " + record._id + ", 添加图片数据到URL字段");
  
  db.garbage_record.updateOne(
    { _id: record._id },
    { $set: { photoUrl: newUrl } }
  );
  
  base64Count++;
});

print("Base64图片数据修复完成，共修复" + base64Count + "条记录");

// 步骤3: 检查是否有使用下划线风格的字段名(photo_url而不是photoUrl)
print("\n步骤3: 修复字段名称不一致的问题...");

var fieldCount = 0;
var fieldRecords = db.garbage_record.find({
  $or: [
    { photo_url: { $exists: true } },
    { photo_data: { $exists: true } }
  ]
});

fieldRecords.forEach(function(record) {
  var updates = {};
  var changed = false;
  
  // 如果存在photo_url字段，将其值复制到photoUrl字段
  if (record.photo_url !== undefined) {
    updates.photoUrl = record.photo_url;
    changed = true;
  }
  
  // 如果存在photo_data字段，将其值复制到photoData字段
  if (record.photo_data !== undefined) {
    updates.photoData = record.photo_data;
    changed = true;
  }
  
  if (changed) {
    print("修复字段名记录ID: " + record._id + ", 统一字段名为驼峰命名法");
    
    db.garbage_record.updateOne(
      { _id: record._id },
      { $set: updates }
    );
    
    fieldCount++;
  }
});

print("字段名称修复完成，共修复" + fieldCount + "条记录");

// 总结
print("\n=======================");
print("修复总结:");
print("格式不正确的URL: " + count + "条");
print("Base64图片数据: " + base64Count + "条");
print("字段名称不一致: " + fieldCount + "条");
print("修复总数: " + (count + base64Count + fieldCount) + "条");
print("=======================");

// 检查可能存在但找不到实际文件的图片记录
print("\n开始检查图片文件存在性...");

var photoRecords = db.garbage_record.find({
  photoUrl: { $exists: true, $ne: null, $ne: "" },
  photoUrl: /^\/profile\//
});

photoRecords.forEach(function(record) {
  var filePath = record.photoUrl.substring(9); // 去掉"/profile/"
  print("记录ID: " + record._id + ", 图片路径: " + filePath);
});

print("\n请手动确认以上图片文件在服务器上是否存在。"); 