import request from '@/utils/request'

// 查询垃圾投递记录列表
export function listGarbageRecords(query) {
  return request({
    url: '/garbage/record/list',
    method: 'get',
    params: query
  })
}

// 查询垃圾投递记录详细
export function getGarbageRecord(recordId) {
  return request({
    url: '/garbage/record/' + recordId,
    method: 'get'
  })
}

// 审核垃圾投递记录
export function auditGarbageRecord(data) {
  return request({
    url: '/garbage/record/audit',
    method: 'post',
    data: data
  })
}

// 批量审核垃圾投递记录
export function batchAuditGarbageRecords(data) {
  return request({
    url: '/garbage/record/batchAudit',
    method: 'post',
    data: data
  })
}

// 导出垃圾投递记录
export function exportGarbageRecord(query) {
  return request({
    url: '/garbage/record/export',
    method: 'get',
    params: query
  })
}

// 新增垃圾投递记录
export function addRecord(data) {
  return request({
    url: '/garbage/record',
    method: 'post',
    data: data
  })
}

// 修改垃圾投递记录
export function updateRecord(data) {
  return request({
    url: '/garbage/record',
    method: 'put',
    data: data
  })
}

// 删除垃圾投递记录
export function delRecord(id) {
  return request({
    url: '/garbage/record/' + id,
    method: 'delete'
  })
}

// 获取当前用户的垃圾投递记录
export function getMyRecords(query) {
  return request({
    url: '/garbage/record/my',
    method: 'get',
    params: query
  })
}

// 上传垃圾投递照片
export function uploadPhoto(data) {
  return request({
    url: '/garbage/record/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 