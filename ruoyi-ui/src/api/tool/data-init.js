import request from '@/utils/request'

// 初始化积分数据
export function initPointsData(data) {
  return request({
    url: '/data/init/points',
    method: 'post',
    params: data
  })
} 