import request from '@/utils/request'

// 查询垃圾分类列表
export function listCategory(query) {
  return request({
    url: '/garbage/category/list',
    method: 'get',
    params: query
  })
}

// 查询所有启用的垃圾分类
export function listAllCategories() {
  return request({
    url: '/garbage/category/listAll',
    method: 'get'
  })
}

// 查询垃圾分类详细
export function getCategory(id) {
  return request({
    url: '/garbage/category/' + id,
    method: 'get'
  })
}

// 新增垃圾分类
export function addCategory(data) {
  return request({
    url: '/garbage/category',
    method: 'post',
    data: data
  })
}

// 修改垃圾分类
export function updateCategory(data) {
  return request({
    url: '/garbage/category',
    method: 'put',
    data: data
  })
}

// 删除垃圾分类
export function delCategory(id) {
  return request({
    url: '/garbage/category/' + id,
    method: 'delete'
  })
}

// 校验分类名称是否唯一
export function checkCategoryNameUnique(data) {
  return request({
    url: '/garbage/category/checkCategoryNameUnique',
    method: 'get',
    params: data
  })
}

// 校验分类编码是否唯一
export function checkCategoryCodeUnique(data) {
  return request({
    url: '/garbage/category/checkCategoryCodeUnique',
    method: 'get',
    params: data
  })
} 