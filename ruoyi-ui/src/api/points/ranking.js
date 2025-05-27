import request from '@/utils/request'

// 获取用户积分排行榜
export function getUserRanking(query) {
  return request({
    url: '/garbage/admin/statistics/user-points-ranking',
    method: 'get',
    params: query
  })
}

// 获取用户详情
export function getUserDetail(userId) {
  return request({
    url: '/points/ranking/detail/' + userId,
    method: 'get'
  })
} 