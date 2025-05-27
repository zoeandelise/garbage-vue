import request from '@/utils/request'

// 获取垃圾分类总体统计数据
export function getGarbageStatistics(params) {
  return request({
    url: '/garbage/statistics/overview',
    method: 'get',
    params: params
  })
}

// 获取垃圾类型分布统计
export function getTypeDistribution(params) {
  return request({
    url: '/garbage/statistics/type-distribution',
    method: 'get',
    params: params
  })
}

// 获取每日投递趋势数据
export function getDailyTrend(params) {
  return request({
    url: '/garbage/statistics/daily-trend',
    method: 'get',
    params: params
  })
}

// 获取区域投递分布数据
export function getAreaDistribution(params) {
  return request({
    url: '/garbage/statistics/area-distribution',
    method: 'get',
    params: params
  })
}

// 获取用户积分排行榜
export function getUserPointsRanking(params) {
  return request({
    url: '/garbage/admin/statistics/user-points-ranking',
    method: 'get',
    params: params
  })
}

// 获取垃圾分类指南查询热度排行
export function getGuideSearchRanking(params) {
  return request({
    url: '/garbage/admin/statistics/guide-search-ranking',
    method: 'get',
    params: params
  })
} 