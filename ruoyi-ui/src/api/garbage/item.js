import request from '@/utils/request'

// 查询垃圾物品列表
export function listItem(query) {
  return request({
    url: '/garbage/item/list',
    method: 'get',
    params: query
  })
}

// 查询垃圾物品详细
export function getItem(id) {
  return request({
    url: '/garbage/item/' + id,
    method: 'get'
  })
}

// 新增垃圾物品
export function addItem(data) {
  return request({
    url: '/garbage/item',
    method: 'post',
    data: data
  })
}

// 修改垃圾物品
export function updateItem(data) {
  return request({
    url: '/garbage/item',
    method: 'put',
    data: data
  })
}

// 删除垃圾物品
export function delItem(id) {
  return request({
    url: '/garbage/item/' + id,
    method: 'delete'
  })
}

// 获取分类下的物品列表
export function getItemsByCategory(categoryId) {
  return request({
    url: '/garbage/item/category/' + categoryId,
    method: 'get'
  })
}

// 搜索垃圾物品
export function searchItems(keyword) {
  return request({
    url: '/garbage/item/search/' + keyword,
    method: 'get'
  })
}

// 校验物品名称是否唯一
export function checkItemNameUnique(data) {
  return request({
    url: '/garbage/item/checkItemNameUnique',
    method: 'get',
    params: data
  })
} 