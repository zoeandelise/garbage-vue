# 垃圾分类查询模块问题修复指南

## 问题概述

垃圾分类查询模块存在以下问题：
1. 切换到垃圾分类查询模块时没有自动搜索
2. 点击搜索按钮时没有网络请求
3. 带有垃圾类型的搜索可以出结果
4. 搜索垃圾名称时有网络请求，但没有正常显示结果

## 解决方案

### 步骤1：检查MongoDB数据

首先，检查MongoDB中是否有垃圾分类数据：

1. 运行 `check_garbage_guide_data.bat` 脚本
2. 检查输出结果，确认 `garbage_guide` 集合是否存在且有数据
3. 如果没有数据，运行 `init_garbage_guide_data.bat` 初始化数据

### 步骤2：修复前端API调用

修改 `ruoyi-ui/src/api/garbage/guide.js` 文件中的 `searchGuide` 函数：

```javascript
// 搜索垃圾分类指南
export function searchGuide(query) {
  // 确保查询参数中包含keyword参数
  const params = { ...query };
  if (params.garbageName && !params.keyword) {
    params.keyword = params.garbageName;
    delete params.garbageName;
  }
  
  // 确保keyword有值
  if (!params.keyword) {
    console.warn('搜索垃圾分类指南时没有提供keyword参数');
  }
  
  return request({
    url: '/garbage/guide/search',
    method: 'get',
    params: params
  });
}
```

### 步骤3：修改搜索组件处理响应数据的方式

修改 `ruoyi-ui/src/views/garbage/guide/search.vue` 文件中的 `handleQuery` 方法：

```javascript
/** 搜索按钮操作 */
handleQuery() {
  console.log("搜索参数:", this.queryParams);
  
  if (!this.queryParams.garbageName && !this.queryParams.garbageType) {
    console.log("未输入搜索条件，显示默认指南");
    // 如果没有输入搜索条件，则显示默认指南
    this.guideList = [];
    this.total = 0;
    this.showDefaultGuides = true;
    return;
  }

  this.loading = true;
  this.queryParams.pageNum = 1;
  
  // 如果有输入垃圾名称，则使用搜索接口
  if (this.queryParams.garbageName) {
    console.log("按垃圾名称搜索:", this.queryParams.garbageName);
    // 将garbageName映射为keyword参数
    const searchParams = {
      keyword: this.queryParams.garbageName
    };
    
    console.log("发送搜索请求参数:", searchParams);
    searchGuide(searchParams).then(response => {
      console.log("搜索结果响应:", response);
      if (response.code === 200) {
        // 修改数据处理方式，兼容返回数组的情况
        const responseData = response.data;
        console.log("搜索结果数据:", responseData);
        
        if (Array.isArray(responseData)) {
          // 如果是数组，直接使用
          this.guideList = responseData;
          this.total = responseData.length || 0;
        } else if (responseData && responseData.content) {
          // 如果是分页对象，取content
          this.guideList = responseData.content;
          this.total = responseData.totalElements || 0;
        } else {
          // 其他情况，尝试转换为数组
          this.guideList = responseData ? [responseData] : [];
          this.total = this.guideList.length;
        }
        
        console.log("处理后的指南列表:", this.guideList);
        this.showDefaultGuides = this.guideList.length === 0;
        
        if (this.guideList.length === 0) {
          this.$message.info('未找到匹配的垃圾分类信息');
        }
      } else {
        this.guideList = [];
        this.total = 0;
        this.$message.error(response.msg || '搜索失败');
      }
      this.loading = false;
    }).catch((error) => {
      console.error("搜索请求失败:", error);
      this.loading = false;
      this.$message.error('搜索请求失败，请检查网络连接');
    });
  } else if (this.queryParams.garbageType) {
    console.log("按垃圾类型搜索:", this.queryParams.garbageType);
    // 按垃圾类型搜索
    this.getList();
  } else {
    // 否则使用常规列表接口
    this.getList();
  }
}
```

### 步骤4：添加初始加载功能

在 `created` 方法中添加加载所有垃圾分类数据的功能：

```javascript
created() {
  // 可以根据URL参数预先设置搜索条件
  const { keyword, type } = this.$route.query;
  if (keyword) {
    this.queryParams.garbageName = keyword;
    this.handleQuery();
  } else if (type) {
    this.queryParams.garbageType = type;
    this.handleQuery();
  } else {
    // 初始化时加载所有垃圾分类数据
    this.loadAllGuides();
  }
},
```

并添加 `loadAllGuides` 方法：

```javascript
/** 加载所有垃圾分类数据 */
loadAllGuides() {
  this.loading = true;
  // 调用后端接口获取所有垃圾分类数据
  listGarbageGuides({
    pageNum: 1,
    pageSize: 100 // 加载更多数据
  }).then(response => {
    console.log("加载所有垃圾分类数据响应:", response);
    if (response.code === 200) {
      const data = response.data || {};
      if (Array.isArray(data)) {
        this.guideList = data;
        this.total = data.length;
      } else if (data.content) {
        this.guideList = data.content || [];
        this.total = data.totalElements || 0;
      } else {
        this.guideList = [];
        this.total = 0;
      }
      // 如果有数据则不显示默认指南
      this.showDefaultGuides = this.guideList.length === 0;
    }
    this.loading = false;
  }).catch(error => {
    console.error("加载所有垃圾分类数据失败:", error);
    this.loading = false;
  });
}
```

### 步骤5：使用浏览器开发者工具进行调试

1. 打开浏览器开发者工具（F12）
2. 切换到"Network"选项卡
3. 监视API请求和响应
4. 查看"Console"选项卡中的日志信息
5. 如果发现错误，根据错误信息进一步调整代码

## 验证修复效果

完成上述修改后，重新启动应用并验证：

1. 进入垃圾分类查询页面，确认是否自动加载垃圾分类数据
2. 输入垃圾名称（如"塑料瓶"）进行搜索，观察是否有网络请求
3. 查看搜索结果是否正确显示
4. 选择垃圾类型进行搜索，确认是否能正常显示结果

## 其他建议

1. 确保MongoDB服务正常运行
2. 检查日志文件以排查后端问题
3. 如果仍然存在问题，可以考虑修改后端控制器，使其接受更灵活的参数
4. 使用类似Postman的工具直接测试后端API，确认响应格式是否符合预期 