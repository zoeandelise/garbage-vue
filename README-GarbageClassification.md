# 垃圾分类管理系统

## 项目介绍

垃圾分类管理系统是一个基于若依框架开发的垃圾分类管理平台，旨在帮助用户更好地进行垃圾分类，提高环保意识，减少环境污染。系统包括垃圾投递记录管理、积分管理、垃圾分类指南和数据统计分析等功能。

## 系统功能

### 1. 垃圾投递记录管理

- 记录用户的垃圾投递情况，包括投递时间、地点、垃圾类型、重量等信息
- 支持按用户、垃圾类型等条件查询投递记录
- 用户可以查看自己的投递记录

### 2. 积分管理

- 根据用户投递的垃圾类型和重量计算积分
- 支持积分兑换功能
- 用户可以查看自己的积分记录和总积分

### 3. 垃圾分类指南

- 提供垃圾分类知识，帮助用户正确分类垃圾
- 支持按名称搜索垃圾，查询其分类方式
- 提供各类垃圾的处理建议和环保小知识

### 4. 数据统计分析

- 统计垃圾投递总量、各类垃圾占比等数据
- 展示每日投递趋势和区域分布情况
- 提供用户积分排行榜和垃圾分类指南查询热度排行

## 技术架构

- 前端：Vue.js + Element UI
- 后端：Spring Boot + Spring Security + MyBatis
- 数据库：MySQL + MongoDB（存储垃圾分类指南和投递记录）
- 缓存：Redis

## API接口说明

### 垃圾投递记录接口

| 接口 | 方法 | 说明 | 权限 |
| --- | --- | --- | --- |
| /garbage/record/list | GET | 获取垃圾投递记录列表 | 需要权限 |
| /garbage/record/{id} | GET | 获取垃圾投递记录详情 | 需要权限 |
| /garbage/record | POST | 新增垃圾投递记录 | 需要权限 |
| /garbage/record | PUT | 修改垃圾投递记录 | 需要权限 |
| /garbage/record/{ids} | DELETE | 删除垃圾投递记录 | 需要权限 |
| /garbage/record/my | GET | 获取当前用户的垃圾投递记录 | 匿名访问 |

### 积分管理接口

| 接口 | 方法 | 说明 | 权限 |
| --- | --- | --- | --- |
| /garbage/points/list | GET | 获取积分记录列表 | 需要权限 |
| /garbage/points/{id} | GET | 获取积分记录详情 | 需要权限 |
| /garbage/points/consume | POST | 消费积分 | 需要权限 |
| /garbage/points/my | GET | 获取当前用户的积分记录 | 匿名访问 |
| /garbage/points/my/total | GET | 获取当前用户的总积分 | 匿名访问 |
| /garbage/points/my/consume | POST | 当前用户消费积分 | 需要登录 |

### 垃圾分类指南接口

| 接口 | 方法 | 说明 | 权限 |
| --- | --- | --- | --- |
| /garbage/guide/list | GET | 获取垃圾分类指南列表 | 需要权限 |
| /garbage/guide/{id} | GET | 获取垃圾分类指南详情 | 需要权限 |
| /garbage/guide | POST | 新增垃圾分类指南 | 需要权限 |
| /garbage/guide | PUT | 修改垃圾分类指南 | 需要权限 |
| /garbage/guide/{ids} | DELETE | 删除垃圾分类指南 | 需要权限 |
| /garbage/guide/search | GET | 搜索垃圾分类指南 | 匿名访问 |
| /garbage/guide/types | GET | 获取所有垃圾类型 | 匿名访问 |

### 数据统计分析接口

| 接口 | 方法 | 说明 | 权限 |
| --- | --- | --- | --- |
| /garbage/statistics/overview | GET | 获取垃圾分类总体统计数据 | 匿名访问 |
| /garbage/statistics/type-distribution | GET | 获取垃圾类型分布统计 | 匿名访问 |
| /garbage/statistics/daily-trend | GET | 获取每日投递趋势数据 | 匿名访问 |
| /garbage/statistics/area-distribution | GET | 获取区域投递分布数据 | 匿名访问 |
| /garbage/statistics/user-points-ranking | GET | 获取用户积分排行榜 | 需要权限 |
| /garbage/statistics/guide-search-ranking | GET | 获取垃圾分类指南查询热度排行 | 需要权限 |

## 使用说明

### 系统登录

1. 访问系统登录页面
2. 输入用户名和密码
3. 点击登录按钮进入系统

### 垃圾投递记录管理

1. 点击左侧菜单"垃圾分类管理"->"垃圾投递记录"
2. 可以查看、新增、修改、删除垃圾投递记录
3. 普通用户可以通过"垃圾分类个人中心"->"我的投递记录"查看自己的投递记录

### 积分管理

1. 点击左侧菜单"垃圾分类管理"->"积分管理"
2. 可以查看用户积分记录、消费积分
3. 普通用户可以通过"垃圾分类个人中心"->"我的积分"查看自己的积分记录和总积分

### 垃圾分类指南

1. 点击左侧菜单"垃圾分类管理"->"垃圾分类指南"
2. 可以查看、新增、修改、删除垃圾分类指南
3. 普通用户可以通过"垃圾分类个人中心"->"垃圾分类查询"搜索垃圾分类指南

### 数据统计分析

1. 点击左侧菜单"垃圾分类管理"->"数据统计分析"
2. 可以查看垃圾分类总体统计数据、垃圾类型分布、每日投递趋势、区域投递分布等统计图表

## 部署说明

1. 安装JDK 1.8+、Maven 3.6+、MySQL 5.7+、MongoDB 4.0+、Redis 5.0+
2. 创建数据库并导入SQL脚本
3. 修改配置文件中的数据库连接信息
4. 执行Maven打包命令：`mvn clean package -Dmaven.test.skip=true`
5. 运行生成的JAR包：`java -jar ruoyi-admin.jar`
6. 访问系统：`http://localhost:80`

## 注意事项

1. 系统默认管理员账号：admin，密码：admin123
2. 首次登录后请及时修改默认密码
3. 定期备份数据库，确保数据安全
4. 系统支持水平扩展，可根据需求进行集群部署

## 系统改进计划

1. 添加微信小程序端，方便用户随时随地查询垃圾分类信息
2. 增加垃圾图像识别功能，通过拍照自动识别垃圾类型
3. 完善积分兑换商城，提供更多兑换选项
4. 增加社区互动功能，促进用户交流和环保知识分享
5. 优化数据统计分析功能，提供更多维度的数据分析 