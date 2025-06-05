# 垃圾分类查询搜索问题修复指南

## 问题描述

当点击垃圾分类查询模块的搜索按钮时，出现以下错误：

```
Required request parameter 'keyword' for method parameter type String is not present
```

## 原因分析

通过检查代码，发现问题在于参数名称不匹配：

1. 后端控制器 `GarbageGuideController` 的 `search` 方法要求一个名为 `keyword` 的必填参数
2. 前端在调用 API 时使用的是 `garbageName` 参数，而没有提供 `keyword` 参数

## 解决方案

### 步骤1：修改前端搜索函数

打开 `ruoyi-ui/src/views/garbage/guide/search.vue` 文件，找到 `handleQuery` 方法，将其修改为：

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
        const data = response.data || [];
        this.guideList = data;
        this.total = data.length || 0;
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

### 步骤2：确保MongoDB数据库中有数据

确保已经初始化了垃圾分类指南数据：

1. 运行 `init_garbage_guide_data.bat`（Windows）或 `./init_garbage_guide_data.sh`（Linux/Mac）
2. 检查数据是否成功导入到 MongoDB 中

### 步骤3：重新构建和启动前端项目

```bash
# 进入前端项目目录
cd ruoyi-ui

# 安装依赖（如果未安装）
npm install

# 启动开发服务器
npm run dev
```

### 步骤4：验证修复结果

1. 打开浏览器访问系统
2. 登录系统
3. 进入垃圾分类查询页面
4. 在搜索框中输入垃圾名称（如"塑料瓶"）
5. 点击搜索按钮
6. 观察是否能正常显示搜索结果

## 备选方案

如果无法直接修改前端代码，也可以考虑修改后端控制器以兼容前端参数：

打开 `ruoyi-garbage/src/main/java/com/ruoyi/garbage/controller/GarbageGuideController.java` 文件，将搜索方法修改为：

```java
/**
 * 搜索垃圾分类指南
 */
@Anonymous
@GetMapping("/search")
public AjaxResult search(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "garbageName", required = false) String garbageName) {
    // 如果keyword为空但garbageName不为空，使用garbageName作为关键词
    String searchKeyword = keyword;
    if ((searchKeyword == null || searchKeyword.isEmpty()) && garbageName != null && !garbageName.isEmpty()) {
        searchKeyword = garbageName;
    }
    
    List<GarbageGuide> guides = garbageGuideService.searchGuides(searchKeyword);
    return AjaxResult.success(guides);
}
```

## 注意事项

1. 如果修改了前端代码，需要重新构建前端项目才能生效
2. 确保MongoDB服务已启动并正常运行
3. 如果搜索结果为空，可能是因为数据库中没有匹配的数据，尝试使用其他关键词搜索 