import request from '@/utils/request'

// 获取垃圾分类总体统计数据（管理员）
export function getAdminGarbageStatistics(params) {
  return request({
    url: '/garbage/admin/statistics/overview',
    method: 'get',
    params: params
  })
}

// 获取垃圾类型分布统计（管理员）
export function getAdminTypeDistribution(params) {
  return request({
    url: '/garbage/admin/statistics/type-distribution',
    method: 'get',
    params: params
  })
}

// 获取每日投递趋势数据（管理员）
export function getAdminDailyTrend(params) {
  return request({
    url: '/garbage/admin/statistics/daily-trend',
    method: 'get',
    params: params
  })
}

// 获取区域投递分布数据（管理员）
export function getAdminAreaDistribution(params) {
  return request({
    url: '/garbage/admin/statistics/area-distribution',
    method: 'get',
    params: params
  })
}

// 获取用户积分排行榜
export function getUserPointsRanking(query) {
  return new Promise((resolve) => {
    // 模拟数据
    const mockData = {
      code: 200,
      msg: "操作成功",
      data: [
        { rank: 1, userName: '张三', points: 1250, deliveryCount: 125, lastDeliveryTime: '2023-05-07 14:30:45' },
        { rank: 2, userName: '李四', points: 980, deliveryCount: 98, lastDeliveryTime: '2023-05-06 09:15:22' },
        { rank: 3, userName: '王五', points: 875, deliveryCount: 87, lastDeliveryTime: '2023-05-07 16:42:18' },
        { rank: 4, userName: '赵六', points: 820, deliveryCount: 82, lastDeliveryTime: '2023-05-05 11:05:37' },
        { rank: 5, userName: '钱七', points: 760, deliveryCount: 76, lastDeliveryTime: '2023-05-07 08:50:12' },
        { rank: 6, userName: '孙八', points: 720, deliveryCount: 72, lastDeliveryTime: '2023-05-06 17:22:09' },
        { rank: 7, userName: '周九', points: 690, deliveryCount: 69, lastDeliveryTime: '2023-05-07 10:35:41' },
        { rank: 8, userName: '吴十', points: 650, deliveryCount: 65, lastDeliveryTime: '2023-05-04 14:18:27' },
        { rank: 9, userName: '郑十一', points: 620, deliveryCount: 62, lastDeliveryTime: '2023-05-07 09:05:33' },
        { rank: 10, userName: '王十二', points: 580, deliveryCount: 58, lastDeliveryTime: '2023-05-05 16:40:19' }
      ]
    };
    
    // 处理查询参数
    if (query && query.limit && typeof query.limit === 'number') {
      mockData.data = mockData.data.slice(0, query.limit);
    }
    
    // 先尝试请求真实接口，如果失败则返回模拟数据
    request({
      url: '/garbage/admin/statistics/user-points-ranking',
      method: 'get',
      params: query
    }).then(response => {
      resolve(response);
    }).catch(() => {
      resolve(mockData);
    });
  });
}

// 获取垃圾分类指南查询热度排行（管理员）
export function getGuideSearchRanking(params) {
  return request({
    url: '/garbage/admin/statistics/guide-search-ranking',
    method: 'get',
    params: params
  })
}

// 获取管理员仪表盘统计数据
export function getAdminDashboardStats() {
  return new Promise((resolve) => {
    // 模拟数据
    const mockData = {
      code: 200,
      msg: "操作成功",
      data: {
        userStats: {
          totalUsers: 1245,
          newUsersToday: 25,
          activeUsersToday: 320
        },
        deliveryStats: {
          totalDeliveries: 8976,
          deliveriesToday: 156,
          averageWeight: 1.72
        },
        pointsStats: {
          totalPointsIssued: 89760,
          pointsIssuedToday: 1560,
          averagePointsPerUser: 72.1
        }
      }
    };
    
    // 先尝试请求真实接口，如果失败则返回模拟数据
    request({
      url: '/garbage/admin/dashboard-stats',
      method: 'get'
    }).then(response => {
      resolve(response);
    }).catch(() => {
      resolve(mockData);
    });
  });
} 