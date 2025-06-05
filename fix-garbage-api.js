// 搜索垃圾分类指南
export function searchGuide(query) {
  // 确保查询参数中包含keyword
  if (query.garbageName && !query.keyword) {
    query.keyword = query.garbageName;
  }
  
  return request({
    url: '/garbage/guide/search',
    method: 'get',
    params: query
  });
} 