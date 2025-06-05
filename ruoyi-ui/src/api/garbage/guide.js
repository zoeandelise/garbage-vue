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
export function getGarbageGuide(id) {
  return request({
    url: '/garbage/guide/' + id,
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
export function delGarbageGuide(id) {
  return request({
    url: '/garbage/guide/' + id,
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
export function searchGarbageGuides(keyword) {
  return request({
    url: '/garbage/search/keyword',
    method: 'get',
    params: {
      keyword: encodeURIComponent(keyword)
    },
    errorHandler: true
  })
}

// 根据名称获取垃圾分类指南
export function getGarbageGuideByName(name) {
  return request({
    url: '/garbage/search/name',
    method: 'get',
    params: {
      name: encodeURIComponent(name)
    },
    errorHandler: true
  })
}

// 获取所有垃圾类型
export function getAllGarbageTypes() {
  return request({
    url: '/garbage/search/types',
    method: 'get',
    errorHandler: true
  })
}

// 根据分类获取垃圾分类指南
export function searchGarbageGuidesByCategory(category) {
  return request({
    url: '/garbage/search/list',
    method: 'get',
    params: {
      category: category
    },
    errorHandler: true
  })
} 