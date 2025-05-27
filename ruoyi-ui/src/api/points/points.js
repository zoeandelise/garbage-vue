import request from '@/utils/request'

// 查询积分记录列表
export function listPoints(query) {
  return request({
    url: '/points/list',
    method: 'get',
    params: query
  })
}

// 查询积分记录详细
export function getPointsDetail(recordId) {
  return request({
    url: '/points/detail/' + recordId,
    method: 'get'
  })
}

// 导出积分记录
export function exportPoints(query) {
  return request({
    url: '/points/export',
    method: 'get',
    params: query
  })
}

// 查询用户列表（用于积分管理）
export function listUsers(query) {
  return request({
    url: '/points/users',
    method: 'get',
    params: query
  })
}

// 获取用户积分信息
export function getUserPoints(userId) {
  return request({
    url: '/points/user/' + userId,
    method: 'get'
  })
}

// 调整用户积分
export function adjustUserPoints(data) {
  return request({
    url: '/points/adjust',
    method: 'post',
    data: data
  })
}

// 更新用户积分
export function updateUserPoints(data) {
  return request({
    url: '/points/update',
    method: 'post',
    data: data
  })
}

// 获取积分历史记录
export function getPointsHistory(query) {
  return request({
    url: '/points/history/list',
    method: 'get',
    params: query
  })
}

// 获取用户积分排行榜
export function getPointsRanking(query) {
  return request({
    url: '/points/ranking/list',
    method: 'get',
    params: query
  })
} 