import request from '@/utils/request'

// 查询积分排行榜列表
export function listRanking(query) {
  return request({
    url: '/garbage/points/ranking/list',
    method: 'get',
    params: query
  })
}

// 获取积分排行榜前N名
export function getTopRanking(n) {
  return request({
    url: '/garbage/points/ranking/top/' + n,
    method: 'get'
  })
}

// 获取用户积分排名
export function getUserRanking(userId) {
  return request({
    url: '/garbage/points/ranking/user/' + userId,
    method: 'get'
  })
}

// 获取积分统计数据
export function getPointsStats() {
  return request({
    url: '/garbage/points/ranking/stats',
    method: 'get'
  })
}

// 获取用户详情
export function getUserDetail(userId) {
  return request({
    url: '/garbage/points/ranking/detail/' + userId,
    method: 'get'
  })
} 