import request from '@/utils/request'

// 获取积分规则
export function getPointsRule() {
  return request({
    url: '/garbage/points/rule',
    method: 'get'
  })
}

// 更新积分规则
export function updatePointsRule(data) {
  return request({
    url: '/garbage/points/rule',
    method: 'put',
    data: data
  })
} 