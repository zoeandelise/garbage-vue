import request from './request';

// 获取垃圾分类列表
export function getGarbageCategories() {
  return request.get('/garbage/category/listAll');
}

// 搜索垃圾分类指南
export function searchGarbageGuides(keyword) {
  return request.get('/garbage/search/keyword', { keyword }).catch(err => {
    if (err.code === 404) {
      return request.get('/garbage/search', { keyword });
    }
    throw err;
  });
}

// 根据分类获取垃圾指南列表
export function getGarbageGuidesByCategory(category) {
  return request.get('/garbage/search/list', { category });
}

// 获取所有垃圾指南
export function getAllGarbageGuides() {
  return request.get('/garbage/search/list');
}

// 获取垃圾指南详情
export function getGarbageGuideDetail(id) {
  if (!id) {
    return Promise.reject({ code: 400, msg: '缺少ID参数' });
  }
  
  return request.get(`/garbage/search/detail/${id}`).catch(err => {
    // 如果接口不存在，尝试其他可能的路径
    if (err.code === 404) {
      return request.get(`/garbage/detail/${id}`);
    }
    throw err;
  });
}

// 上传垃圾投递记录
export function uploadGarbageRecord(data) {
  return request.post('/garbage/record', data);
}

// 获取用户垃圾投递记录
export function getUserGarbageRecords(params) {
  return request.get('/garbage/record/user', params);
}

// 获取用户积分
export function getUserPoints() {
  return request.get('/garbage/points/user');
}

// 获取积分排行榜
export function getPointsRanking(type = 'total') {
  return request.get('/garbage/points/ranking', { type });
}

// 获取统计数据
export function getStatistics() {
  // 兼容若依接口，也可能是 /garbage/statistics 
  return request.get('/garbage/statistics/public').catch(err => {
    // 如果接口不存在，尝试调用另一个可能的接口
    if (err.code === 404) {
      return request.get('/garbage/statistics');
    }
    throw err;
  });
}

// 上传垃圾分类图片
export function uploadGarbageImage(filePath, formData = {}) {
  return request.upload('/garbage/record/upload', filePath, 'file', formData);
}

// 获取垃圾分类知识
export function getGarbageKnowledge() {
  return request.get('/garbage/knowledge/list');
}

// 获取垃圾分类知识详情
export function getGarbageKnowledgeDetail(id) {
  return request.get(`/garbage/knowledge/detail/${id}`);
}

// 获取最新垃圾分类政策
export function getGarbagePolicies() {
  return request.get('/garbage/policy/list');
}

// 获取环保小贴士
export function getEcoTips() {
  return request.get('/garbage/tips/list');
}

// 获取用户最近投递记录
export function getRecentRecords(limit = 5) {
  return request.get('/garbage/record/recent', { limit });
}

// 提交垃圾分类识别
export function identifyGarbage(data) {
  return request.post('/garbage/identify', data);
}

// 提交用户反馈
export function submitFeedback(data) {
  return request.post('/garbage/feedback', data);
} 