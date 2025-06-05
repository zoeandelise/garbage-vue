import request from '@/utils/request'

// 查询垃圾分类指南列表
export function listGarbageGuides(query) {
  return request({
    url: '/garbage/guide/list',
    method: 'get',
    params: query
  })
}

// 查询垃圾分类指南详细
export function getGarbageGuide(guideId) {
  return request({
    url: '/garbage/guide/' + guideId,
    method: 'get'
  })
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

// 搜索垃圾分类指南（修复版）
export function searchGuide(query) {
  // 将garbageName映射为keyword参数
  const params = { ...query };
  if (params.garbageName && !params.keyword) {
    params.keyword = params.garbageName;
    delete params.garbageName;
  }
  
  // 修改为使用统一的搜索接口
  return request({
    url: '/garbage/search/keyword',
    method: 'get',
    params: params
  })
}

// 根据垃圾名称查询分类指南
export function getGuideByName(garbageName) {
  return request({
    url: '/garbage/guide/name/' + garbageName,
    method: 'get'
  })
} 