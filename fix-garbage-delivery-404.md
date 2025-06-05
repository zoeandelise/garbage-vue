# 垃圾投递记录模块 404 错误修复指南

## 问题描述

在《垃圾投递记录》模块中，点击填写基本信息后，出现404错误：

```
请求网址: http://localhost:81/dev-api/garbage/guide/name/%E5%8E%A8%E4%BD%99%E5%9E%83%E5%9C%BE
请求方法: GET
状态代码: 404 Not Found
```

该请求试图获取垃圾分类指南中关于"厨余垃圾"的信息，但后端没有实现该接口，导致404错误。

## 解决方案

### 1. 在后端添加缺失的接口

在 `GarbageGuideController.java` 中添加 `/name/{garbageName}` 接口：

```java
/**
 * 根据垃圾名称查询分类指南
 */
@Anonymous
@GetMapping("/name/{garbageName}")
public AjaxResult getByName(@PathVariable("garbageName") String garbageName) {
    GarbageGuide guide = garbageGuideService.getGuideByGarbageName(garbageName);
    if (guide == null) {
        // 如果找不到精确匹配，尝试模糊查询
        List<GarbageGuide> guides = garbageGuideService.searchGuidesByGarbageName(garbageName);
        if (guides != null && !guides.isEmpty()) {
            // 返回第一个结果
            return AjaxResult.success(guides.get(0));
        }
        // 如果仍然找不到，返回找不到的信息
        return AjaxResult.error("未找到匹配的垃圾分类信息");
    }
    return AjaxResult.success(guide);
}
```

### 2. 优化 Service 层实现

修改 `GarbageGuideServiceImpl.java` 中的 `getGuideByGarbageName` 方法：

```java
@Override
public GarbageGuide getGuideByGarbageName(String garbageName) {
    // 先检查是否是垃圾类型
    if (garbageName != null && (
            GarbageConstants.GARBAGE_TYPE_RECYCLABLE.equals(garbageName) ||
            GarbageConstants.GARBAGE_TYPE_HARMFUL.equals(garbageName) ||
            GarbageConstants.GARBAGE_TYPE_KITCHEN.equals(garbageName) ||
            GarbageConstants.GARBAGE_TYPE_OTHER.equals(garbageName))) {
        
        // 如果是垃圾类型，创建一个对应的指南对象
        GarbageGuide guide = new GarbageGuide();
        guide.setGarbageName(garbageName);
        guide.setGarbageType(garbageName);
        
        // 根据不同类型设置投放建议
        if (GarbageConstants.GARBAGE_TYPE_RECYCLABLE.equals(garbageName)) {
            guide.setDisposalTips("轻投轻放；清洁干燥，避免污染；废纸尽量平整；立体包装物请清空内容物并清洁后压扁投放；有尖锐边角的，应包裹后投放");
        } else if (GarbageConstants.GARBAGE_TYPE_HARMFUL.equals(garbageName)) {
            guide.setDisposalTips("投放时请注意轻放；易破损的请连同包装或包裹后轻放；如果是液体，请密封后投放");
        } else if (GarbageConstants.GARBAGE_TYPE_KITCHEN.equals(garbageName)) {
            guide.setDisposalTips("沥干水分；盛放在容器中密封投放；有包装物的厨余垃圾应将包装物去除后分类投放");
        } else if (GarbageConstants.GARBAGE_TYPE_OTHER.equals(garbageName)) {
            guide.setDisposalTips("投放前尽量沥干水分；难以辨别类别的生活垃圾投入其他垃圾容器内");
        }
        
        return guide;
    }
    
    // 如果不是垃圾类型，则按正常流程查询
    return garbageGuideRepository.findByGarbageName(garbageName);
}
```

### 3. 在前端增加错误处理

修改 `ruoyi-ui/src/views/garbage/record/submit.vue` 中的 `getGarbageGuide` 方法：

```javascript
// 获取垃圾分类指南
getGarbageGuide() {
  console.log("正在获取垃圾分类指南:", this.form.garbageType);
  
  getGuideByName(this.form.garbageType)
    .then(response => {
      console.log("垃圾分类指南响应:", response);
      if (response.code === 200) {
        this.garbageGuide = response.data;
        console.log("成功获取垃圾分类指南:", this.garbageGuide);
      } else {
        console.warn("获取垃圾分类指南失败，使用默认数据:", response);
        this.useDefaultGuideData();
      }
    })
    .catch(error => {
      console.error("获取垃圾分类指南出错:", error);
      this.useDefaultGuideData();
    });
},

// 使用默认的垃圾分类指南数据
useDefaultGuideData() {
  // 创建一个基础的垃圾分类指南数据
  const defaultGuides = {
    "可回收物": {
      disposalTips: "轻投轻放；清洁干燥，避免污染；废纸尽量平整；立体包装物请清空内容物并清洁后压扁投放；有尖锐边角的，应包裹后投放"
    },
    "有害垃圾": {
      disposalTips: "投放时请注意轻放；易破损的请连同包装或包裹后轻放；如果是液体，请密封后投放"
    },
    "厨余垃圾": {
      disposalTips: "沥干水分；盛放在容器中密封投放；有包装物的厨余垃圾应将包装物去除后分类投放"
    },
    "其他垃圾": {
      disposalTips: "投放前尽量沥干水分；难以辨别类别的生活垃圾投入其他垃圾容器内"
    }
  };
  
  // 根据当前选择的垃圾类型设置指南
  if (this.form.garbageType && defaultGuides[this.form.garbageType]) {
    this.garbageGuide = defaultGuides[this.form.garbageType];
    console.log("使用默认垃圾分类指南:", this.garbageGuide);
  } else {
    this.garbageGuide = { disposalTips: "请按照垃圾分类要求进行投放" };
  }
}
```

### 4. 初始化垃圾分类指南数据

确保MongoDB中有垃圾分类指南数据：

1. 运行 `init_garbage_guide_data.bat` 脚本初始化数据
2. 脚本将向MongoDB中插入基本的垃圾分类指南数据，包括垃圾类型指南

## 验证修复

完成上述修改后：

1. 重启应用服务器
2. 打开开发者工具（F12）监控网络请求
3. 进入《垃圾投递记录》模块，选择垃圾类型（如"厨余垃圾"）
4. 观察网络请求，确认 `/garbage/guide/name/厨余垃圾` 接口返回200状态码而非404
5. 确认页面正常显示垃圾分类指南信息，没有错误提示

## 其他建议

1. 对于其他可能的404错误，可使用相同的方法检查API是否实现
2. 考虑添加全局错误处理，为前端提供更友好的错误提示
3. 在开发阶段启用详细日志，方便跟踪和排查问题 