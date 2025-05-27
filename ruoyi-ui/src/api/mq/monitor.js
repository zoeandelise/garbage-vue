import request from '@/utils/request'

// 获取消息队列状态
export function getMqStatus() {
  return request({
    url: '/mq/status',
    method: 'get'
  })
}

// 获取队列列表
export function getQueueList() {
  return request({
    url: '/mq/queues',
    method: 'get'
  })
}

// 获取队列详情
export function getQueueDetail(queueName) {
  return request({
    url: '/mq/queue/' + queueName,
    method: 'get'
  })
}

// 清空队列
export function clearQueue(queueName) {
  return request({
    url: '/mq/queue/' + queueName + '/clear',
    method: 'post'
  })
}

// 重启队列
export function restartQueue(queueName) {
  return request({
    url: '/mq/queue/' + queueName + '/restart',
    method: 'post'
  })
}

// 获取错误消息列表
export function getErrorMessages(query) {
  return request({
    url: '/mq/errors',
    method: 'get',
    params: query
  })
}

// 获取错误消息详情
export function getErrorDetail(messageId) {
  return request({
    url: '/mq/error/' + messageId,
    method: 'get'
  })
}

// 重试消息
export function retryMessage(messageId) {
  return request({
    url: '/mq/error/' + messageId + '/retry',
    method: 'post'
  })
}

// 删除错误消息
export function deleteErrorMessage(messageId) {
  return request({
    url: '/mq/error/' + messageId,
    method: 'delete'
  })
}

// 导出错误消息
export function exportErrorMessages(query) {
  return request({
    url: '/mq/errors/export',
    method: 'get',
    params: query
  })
} 