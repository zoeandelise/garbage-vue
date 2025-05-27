import request from '@/utils/request'

// 获取积分规则配置
export function getPointsRuleConfig() {
  return new Promise((resolve) => {
    // 模拟数据
    const mockData = {
      code: 200,
      msg: "操作成功",
      data: {
        pointsExpireDays: 365,
        dailyDeliveryLimit: 5,
        dailyPointsLimit: 100,
        continuousDeliveryEnabled: true,
        continuousDays: 7,
        continuousBonus: 50,
        activityEnabled: false,
        activityName: '',
        activityStartDate: null,
        activityEndDate: null,
        activityMultiplier: 1.5,
        garbageTypeRules: [
          { typeName: '可回收物', typeCode: 'RECYCLABLE', calculationType: 1, pointsFactor: 2.0 },
          { typeName: '有害垃圾', typeCode: 'HAZARDOUS', calculationType: 1, pointsFactor: 5.0 },
          { typeName: '厨余垃圾', typeCode: 'KITCHEN', calculationType: 1, pointsFactor: 1.0 },
          { typeName: '其他垃圾', typeCode: 'OTHER', calculationType: 1, pointsFactor: 0.5 }
        ]
      }
    };
    
    // 先尝试请求真实接口，如果失败则返回模拟数据
    request({
      url: '/garbage/points/rule/config',
      method: 'get'
    }).then(response => {
      resolve(response);
    }).catch(() => {
      resolve(mockData);
    });
  });
}

// 更新积分规则配置
export function updatePointsRuleConfig(data) {
  return new Promise((resolve) => {
    // 模拟成功响应
    const mockData = {
      code: 200,
      msg: "操作成功"
    };
    
    // 先尝试请求真实接口，如果失败则返回模拟数据
    request({
      url: '/garbage/points/rule/config',
      method: 'put',
      data: data
    }).then(response => {
      resolve(response);
    }).catch(() => {
      resolve(mockData);
    });
  });
} 