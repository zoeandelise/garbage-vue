import request from '@/utils/request'

// 查询积分记录列表
export function listPoints(query) {
  return request({
    url: '/garbage/points/record/list',
    method: 'get',
    params: query
  })
}

// 查询积分记录详细
export function getPoints(id) {
  return request({
    url: '/garbage/points/record/' + id,
    method: 'get'
  })
}

// 获取当前用户的积分记录
export function getMyPoints(query) {
  return request({
    url: '/garbage/points/my',
    method: 'get',
    params: query
  })
}

// 获取当前用户的总积分
export function getMyTotalPoints() {
  return request({
    url: '/garbage/points/my/total',
    method: 'get'
  })
}

// 消费积分
export function consumePoints(data) {
  return request({
    url: '/garbage/points/consume',
    method: 'post',
    params: data
  })
}

// 当前用户消费积分
export function myConsumePoints(data) {
  return request({
    url: '/garbage/points/my/consume',
    method: 'post',
    params: data
  })
} 