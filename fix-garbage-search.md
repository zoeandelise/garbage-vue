# 垃圾分类查询搜索问题修复指南

## 问题描述

在垃圾分类查询模块中，点击搜索按钮时出现以下错误：

```
Required request parameter 'keyword' for method parameter type String is not present
```

这表明后端接口需要一个名为 `keyword` 的参数，但前端没有提供此参数。

## 问题原因

经分析，问题出在以下几方面：

1. 后端控制器 `GarbageGuideController` 的 `search` 方法需要一个名为 `keyword` 的必填参数
2. 前端 `search.vue` 在调用 `searchGuide` 函数时，传递的是包含 `garbageName` 字段的对象，而不是 `keyword`

## 解决方案

修改 `ruoyi-ui/src/views/garbage/guide/search.vue` 文件中的 `handleQuery` 方法，将查询参数中的 `garbageName` 映射为 `keyword`：

```javascript
/** 搜索按钮操作 */
handleQuery() {
  if (!this.queryParams.garbageName && !this.queryParams.garbageType) {
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
    // 将garbageName映射为keyword参数
    const searchParams = {
      keyword: this.queryParams.garbageName
    };
    
    searchGuide(searchParams).then(response => {
      if (response.code === 200) {
        const data = response.data || {};
        this.guideList = data.content || [];
        this.total = data.totalElements || 0;
        this.showDefaultGuides = this.guideList.length === 0;
      } else {
        this.guideList = [];
        this.total = 0;
        this.$message.error(response.msg || '搜索失败');
      }
      this.loading = false;
    }).catch(() => {
      this.loading = false;
    });
  } else {
    // 否则使用常规列表接口
    this.getList();
  }
}
```

## 验证步骤

1. 修改代码后重新构建前端项目
2. 进入垃圾分类查询页面
3. 在搜索框中输入垃圾名称（如"塑料瓶"）
4. 点击搜索按钮
5. 确认能够正常显示搜索结果，没有出现错误提示

## 其他建议

1. 确保MongoDB中已经初始化了垃圾分类数据
2. 如果搜索结果仍然为空，检查数据库中是否存在与搜索关键词匹配的数据
3. 可以使用浏览器开发者工具检查网络请求，确认接口调用正常 