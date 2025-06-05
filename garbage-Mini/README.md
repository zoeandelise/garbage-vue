# TDesign 通用页面模板

基于 TDesign 打造的通用页面模板，包含通用的登陆注册、个人中心、设置中心、信息流等等功能。

## 模版功能预览

### 首页

<div style="display: flex">
  <img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/home-1.png">
  <img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/home-2.png">
</div>

### 信息发布

<img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/publish-1.png">

### 搜索页

<img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/search-1.png">

### 个人中心
<div style="display: flex">
  <img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/user-1.png">
  <img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/user-2.png">
  <img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/user-3.png">
</div>


### 设置中心

<img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/setting-1.png">

### 消息中心

<img width="375" alt="image" src="https://tdesign.gtimg.com/miniprogram/template/message-1.png">


## 开发预览
### 目录结构（TODO: 生成目录结构树）


### 在开发者工具中预览

```bash
# 安装项目依赖
npm install

```

打开[微信开发者工具](https://mp.weixin.qq.com/debug/wxadoc/dev/devtools/download.html)，导入整个项目，构建 npm 包，就可以预览示例了。

### 基础库版本

最低基础库版本`^2.6.5`


## 贡献成员

<a href="https://github.com/TDesignOteam/tdesign-miniprogram-starter/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=TDesignOteam/tdesign-miniprogram-starter" />
</a>

## 反馈

有任何问题，建议通过 [Github issues](https://github.com/TDesignOteam/tdesign-miniprogram-starter/issues) 反馈。

## 开源协议

TDesign 遵循 [MIT 协议](https://github.com/TDesignOteam/tdesign-miniprogram-starter/blob/main/LICENSE)。

# 城市垃圾分类小程序

## 项目概述

城市垃圾分类小程序是一款帮助用户正确分类垃圾、参与垃圾分类活动并获取相关知识的微信小程序。通过与RuoYi后台系统集成，实现了垃圾分类的智能识别、投递记录、积分奖励等功能。

## 功能特点

- **垃圾分类查询**：支持关键词搜索垃圾分类方式
- **拍照识别**：通过拍照上传实现垃圾智能分类识别
- **垃圾投递记录**：记录用户的垃圾投递行为，统计重量和积分
- **积分奖励系统**：用户参与垃圾分类可获得积分奖励
- **分类知识库**：提供垃圾分类知识、环保小贴士等内容
- **积分排行榜**：展示用户参与垃圾分类的积分排名

## 目录结构

```
garbage-Mini/
├── api/                # API接口请求
├── components/         # 自定义组件
├── custom-tab-bar/     # 自定义底部导航栏
├── mock/               # 模拟数据
├── pages/              # 页面文件
│   ├── home/           # 首页
│   ├── guide/          # 分类指南
│   ├── record/         # 投递记录
│   ├── my/             # 个人中心
│   ├── points/         # 积分相关
│   ├── search/         # 搜索页面
│   └── ...
├── static/             # 静态资源
├── utils/              # 工具函数
├── app.js              # 小程序入口文件
├── app.json            # 小程序全局配置
└── config.js           # 全局配置
```

## 安装和运行

1. 克隆项目到本地
2. 使用微信开发者工具打开项目
3. 在`config.js`中配置后端API地址
4. 编译并运行项目

## 开发配置

在`config.js`文件中进行开发配置：

```javascript
module.exports = {
  // 是否使用模拟数据
  isMock: false,
  // 开发环境API地址
  baseUrl: 'http://localhost:8080',
  // 生产环境可在此配置
  // baseUrl: process.env.NODE_ENV === 'production' ? 'https://api.example.com' : 'http://localhost:8080',
  // API版本
  apiVersion: 'v1',
  // 请求超时时间
  timeout: 10000,
  // 请求头
  headers: {
    'Content-Type': 'application/json'
  }
};
```

## 最近优化更新

### 1. 修复启动问题
- 修复了缺少`getLocalUrl`函数导致的启动错误
- 修复了import路径使用tilde导致的问题

### 2. API请求增强
- 改进请求函数，添加重试机制和指数退避策略
- 为API请求添加更完善的错误处理
- 支持与RuoYi后端API的兼容性

### 3. 首页性能和用户体验提升
- 优化数据加载流程，添加加载状态和错误提示
- 实现数据加载并行化，提高页面加载速度
- 添加下拉刷新功能，方便用户获取最新数据

### 4. 垃圾分类知识功能扩展
- 新增垃圾分类知识列表页面
- 新增垃圾分类知识详情页面
- 支持富文本内容和图片展示

### 5. 其他改进
- 统一页面样式和交互体验
- 优化垃圾分类展示，添加颜色标识
- 完善环保小贴士功能
- 增强页面容错性，防止数据异常导致的显示问题

## 故障排除

### 错误: TypeError: (0 , _util.getLocalUrl) is not a function

**问题描述**: 小程序启动时出现`TypeError: (0 , _util.getLocalUrl) is not a function`错误。

**解决方案**: 
1. 在`utils/util.js`中添加缺失的`getLocalUrl`函数:
   ```javascript
   const getLocalUrl = (path) => {
     return path;
   }
   
   // 确保在模块导出中包含该函数
   module.exports = {
     formatTime,
     ...,
     getLocalUrl
   }
   ```

2. 修改`mock/my/getPersonalInfo.js`中的导入路径:
   ```javascript
   // 不推荐: import { getLocalUrl } from '~/utils/util'
   // 推荐: 使用相对路径
   import { getLocalUrl } from '../../utils/util'
   ```

**注意**: 使用波浪线`~`作为导入路径前缀可能会导致问题，建议使用相对路径进行导入。

## 联系方式

项目维护人: [维护者姓名]
联系邮箱: [维护者邮箱]
