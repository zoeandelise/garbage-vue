# 垃圾分类查询数据初始化指南

## 概述

本文档提供了初始化垃圾分类查询数据库和解决搜索功能问题的步骤。系统使用MongoDB存储垃圾分类指南数据。

## 先决条件

1. 已安装MongoDB（推荐版本4.0+）
2. MongoDB服务已启动
3. MongoDB默认运行在localhost:27017（如果不是，请修改初始化脚本中的连接信息）

## 初始化数据步骤

### Windows系统

1. 打开命令提示符或PowerShell窗口
2. 导航到项目根目录
3. 执行以下命令：
   ```
   init_garbage_guide_data.bat
   ```

### Linux/Mac系统

1. 打开终端
2. 导航到项目根目录
3. 执行以下命令：
   ```
   chmod +x init_garbage_guide_data.sh
   ./init_garbage_guide_data.sh
   ```

## 数据初始化说明

初始化脚本将：
1. 清空垃圾分类指南集合（如果存在）
2. 插入20条垃圾分类指南数据，包括：
   - 5条可回收物（纸箱、塑料瓶、玻璃瓶、易拉罐、旧衣服）
   - 5条有害垃圾（废电池、过期药品、荧光灯管、废油漆桶、温度计）
   - 5条厨余垃圾（剩菜剩饭、果皮、茶叶渣、蛋壳、鱼骨）
   - 5条其他垃圾（烟蒂、纸巾、一次性餐具、破碎陶瓷、灰尘）

## 修复前端搜索问题

本次更新还修复了前端搜索无网络请求的问题。具体修改内容：

1. 修改了 `ruoyi-ui/src/api/garbage/guide.js` 文件，移除了模拟数据部分，改为发送真实的网络请求。
2. 确保MongoDB中有垃圾分类指南数据供查询。

## 验证步骤

1. 启动应用系统
2. 登录系统
3. 进入"垃圾分类管理"->"垃圾分类指南"页面
4. 在搜索框中输入垃圾名称（如"塑料瓶"）进行搜索
5. 观察是否有网络请求发送并正确显示搜索结果

## 故障排除

如果遇到问题，请检查：

1. MongoDB服务是否正常运行
2. 数据库连接配置是否正确
3. 应用日志中是否有相关错误信息
4. 前端网络请求是否发送成功（通过浏览器开发者工具查看）

## 后续改进计划

1. 添加更多垃圾分类数据
2. 优化搜索算法，提高搜索精确度
3. 添加图片识别功能，通过拍照自动识别垃圾类型 