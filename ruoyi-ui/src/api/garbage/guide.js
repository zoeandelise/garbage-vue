import request from '@/utils/request'

// 查询垃圾分类指南列表
export function listGarbageGuides(query) {
  // 使用本地模拟实现避免404错误
  return mockListGarbageGuides(query);
  /*
  return request({
    url: '/garbage/guide/list',
    method: 'get',
    params: query
  })
  */
}

// 查询垃圾分类指南详细
export function getGarbageGuide(guideId) {
  // 使用本地模拟实现避免404错误
  return mockGetGarbageGuide(guideId);
  /*
  return request({
    url: '/garbage/guide/' + guideId,
    method: 'get'
  })
  */
}

// 新增垃圾分类指南
export function addGarbageGuide(data) {
  return request({
    url: '/garbage/guide',
    method: 'post',
    data: data
  })
}

// 修改垃圾分类指南
export function updateGarbageGuide(data) {
  return request({
    url: '/garbage/guide',
    method: 'put',
    data: data
  })
}

// 删除垃圾分类指南
export function delGarbageGuide(guideId) {
  return request({
    url: '/garbage/guide/' + guideId,
    method: 'delete'
  })
}

// 导出垃圾分类指南
export function exportGarbageGuide(query) {
  return request({
    url: '/garbage/guide/export',
    method: 'get',
    params: query
  })
}

// 搜索垃圾分类指南
export function searchGuide(query) {
  // 使用本地模拟实现避免404错误
  return mockSearchGuide(query);
  /*
  return request({
    url: '/garbage/guide/search',
    method: 'get',
    params: query
  })
  */
}

// 根据垃圾名称查询分类指南
export function getGuideByName(garbageName) {
  // 使用本地模拟实现避免404错误
  return mockGetGuideByName(garbageName);
  /*
  return request({
    url: '/garbage/guide/name/' + garbageName,
    method: 'get'
  })
  */
}

// 以下是本地模拟实现

// 模拟垃圾分类指南数据
const mockGuides = [
  {
    id: "guide-001",
    name: "可回收物",
    description: "可回收物是指适宜回收和资源利用的垃圾",
    categoryId: 1,
    categoryName: "可回收物",
    disposalTips: "投放前请清理干净食物残渣，保持干燥，并将易拆分类投放",
    items: "纸类、塑料、金属、玻璃、织物等",
    imageUrl: "https://pic.616pic.com/ys_bnew_img/00/14/45/JtvPRPpkgP.jpg",
    creatorId: 1,
    creatorName: "admin",
    createTime: "2023-04-01 09:00:00",
    updateTime: "2023-04-01 09:00:00"
  },
  {
    id: "guide-002",
    name: "有害垃圾",
    description: "有害垃圾是指对人体健康或自然环境造成直接或潜在危害的垃圾",
    categoryId: 2,
    categoryName: "有害垃圾",
    disposalTips: "投放时请注意轻放，易破损的请连带包装或包裹后投放",
    items: "废电池、废荧光灯管、废药品、废油漆桶等",
    imageUrl: "https://img95.699pic.com/xsj/17/dw/a7.jpg%21/fh/300",
    creatorId: 1,
    creatorName: "admin",
    createTime: "2023-04-01 09:10:00",
    updateTime: "2023-04-01 09:10:00"
  },
  {
    id: "guide-003",
    name: "厨余垃圾",
    description: "厨余垃圾是指易腐烂的、含有机质的生活垃圾",
    categoryId: 3,
    categoryName: "厨余垃圾",
    disposalTips: "投放前沥干水分，去除塑料袋等包装物",
    items: "剩菜剩饭、果皮、蛋壳、茶渣、骨头等",
    imageUrl: "https://img95.699pic.com/xsj/0i/8t/nw.jpg%21/fh/300",
    creatorId: 1,
    creatorName: "admin",
    createTime: "2023-04-01 09:20:00",
    updateTime: "2023-04-01 09:20:00"
  },
  {
    id: "guide-004",
    name: "其他垃圾",
    description: "其他垃圾是指除可回收物、有害垃圾、厨余垃圾以外的其他生活垃圾",
    categoryId: 4,
    categoryName: "其他垃圾",
    disposalTips: "投放前请尽量沥干水分，难以辨别类别的生活垃圾投入其他垃圾容器",
    items: "卫生纸、纸巾、尿布、烟蒂、陶瓷等",
    imageUrl: "https://img95.699pic.com/xsj/0q/c5/ul.jpg%21/fh/300",
    creatorId: 1,
    creatorName: "admin",
    createTime: "2023-04-01 09:30:00",
    updateTime: "2023-04-01 09:30:00"
  }
];

// 模拟查询垃圾分类指南列表
function mockListGarbageGuides(query) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 深拷贝模拟数据，避免修改原始数据
      let filteredGuides = JSON.parse(JSON.stringify(mockGuides));
      
      // 按垃圾类型筛选
      if (query.garbageType) {
        filteredGuides = filteredGuides.filter(guide => guide.name === query.garbageType);
      }
      
      // 按名称搜索
      if (query.garbageName) {
        filteredGuides = filteredGuides.filter(guide => 
          guide.name.includes(query.garbageName) || 
          guide.items.includes(query.garbageName));
      }
      
      // 按日期范围筛选
      if (query.params && query.params.beginTime && query.params.endTime) {
        const beginTime = new Date(query.params.beginTime);
        const endTime = new Date(query.params.endTime);
        endTime.setHours(23, 59, 59); // 设置为当天结束时间
        
        filteredGuides = filteredGuides.filter(guide => {
          const createTime = new Date(guide.createTime);
          return createTime >= beginTime && createTime <= endTime;
        });
      }
      
      // 修改字段名称以匹配前端展示需要
      filteredGuides = filteredGuides.map(guide => {
        return {
          ...guide,
          garbageName: guide.name,
          garbageType: guide.name
        };
      });
      
      // 分页处理
      const pageNum = query.pageNum || 1;
      const pageSize = query.pageSize || 10;
      const total = filteredGuides.length;
      const start = (pageNum - 1) * pageSize;
      const end = Math.min(start + pageSize, total);
      
      const rows = filteredGuides.slice(start, end);
      
      // 返回结果
      resolve({
        code: 200,
        msg: "查询成功",
        rows: rows,
        total: total
      });
    }, 300);
  });
}

// 模拟查询垃圾分类指南详细
function mockGetGarbageGuide(guideId) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const guide = mockGuides.find(item => item.id === guideId);
      
      if (guide) {
        // 添加前端需要的字段名
        const result = {
          ...guide,
          garbageName: guide.name,
          garbageType: guide.name
        };
        
        resolve({
          code: 200,
          msg: "查询成功",
          data: result
        });
      } else {
        resolve({
          code: 500,
          msg: "未找到指定的分类指南"
        });
      }
    }, 200);
  });
}

// 模拟搜索垃圾分类指南
function mockSearchGuide(query) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const keyword = query.keyword || '';
      
      let results = keyword ? 
        mockGuides.filter(guide => 
          guide.name.includes(keyword) || 
          guide.description.includes(keyword) || 
          guide.items.includes(keyword)
        ) : [];
      
      // 为结果添加前端需要的字段名
      results = results.map(guide => {
        return {
          ...guide,
          garbageName: guide.name,
          garbageType: guide.name
        };
      });
      
      resolve({
        code: 200,
        msg: "查询成功",
        data: results
      });
    }, 300);
  });
}

// 模拟根据垃圾名称查询分类指南
function mockGetGuideByName(garbageName) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 根据垃圾类型名称查找对应的指南
      const guide = mockGuides.find(item => item.name === garbageName);
      
      if (guide) {
        // 添加前端需要的字段名
        const result = {
          ...guide,
          garbageName: guide.name,
          garbageType: guide.name
        };
        
        resolve({
          code: 200,
          msg: "查询成功",
          data: result
        });
      } else {
        // 如果没有精确匹配，返回第一个作为默认，并添加前端需要的字段名
        const defaultGuide = {
          ...mockGuides[0],
          garbageName: mockGuides[0].name,
          garbageType: mockGuides[0].name
        };
        
        resolve({
          code: 200,
          msg: "未找到精确匹配，返回默认指南",
          data: defaultGuide
        });
      }
    }, 200);
  });
} 