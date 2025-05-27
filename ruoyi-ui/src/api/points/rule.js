import request from '@/utils/request'

// 获取积分规则
export function getPointsRule() {
  return request({
    url: '/points/rule',
    method: 'get'
  })
}

// 更新积分规则
export function updatePointsRule(data) {
  return request({
    url: '/points/rule',
    method: 'put',
    data: data
  })
} 