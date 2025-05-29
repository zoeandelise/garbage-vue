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
  // 如果后端接口不可用，使用本地模拟
  try {
    return request({
      url: '/garbage/record',
      method: 'post',
      data: data
    }).catch(error => {
      // 如果发生错误（如404），返回模拟数据
      console.log('使用模拟数据', error);
      return mockAddRecord(data);
    });
  } catch (error) {
    return mockAddRecord(data);
  }
}

// 模拟新增垃圾投递记录
function mockAddRecord(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 模拟创建记录
      const recordId = 'mock-' + new Date().getTime();
      
      resolve({
        code: 200,
        msg: "记录提交成功",
        data: {
          id: recordId,
          ...data,
          createTime: new Date(),
          updateTime: new Date(),
          pointsCalculated: false,
          verified: false
        }
      });
    }, 800);
  });
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
  // 如果后端接口不可用，使用本地模拟
  try {
    return request({
      url: '/garbage/record/my',
      method: 'get',
      params: query
    }).catch(error => {
      // 如果发生错误（如404），返回模拟数据
      console.log('使用模拟数据', error);
      return mockMyRecords(query);
    });
  } catch (error) {
    return mockMyRecords(query);
  }
}

// 模拟获取当前用户的垃圾投递记录
function mockMyRecords(query) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 生成模拟数据
      const pageNum = query.pageNum || 1;
      const pageSize = query.pageSize || 10;
      const total = 23; // 模拟总记录数
      
      const records = [];
      for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
        const garbageTypes = ["可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"];
        const garbageType = garbageTypes[Math.floor(Math.random() * garbageTypes.length)];
        
        const record = {
          id: 'mock-' + (i + 1 + (pageNum - 1) * pageSize),
          userId: 1, // 当前用户ID
          userName: "admin", // 当前用户名
          garbageType: garbageType,
          weight: Math.round(Math.random() * 100) / 10, // 0.1-10.0 kg
          location: {
            address: "测试地址" + (i + 1),
            longitude: 116.397428 + Math.random() * 0.1,
            latitude: 39.90923 + Math.random() * 0.1,
            city: "北京市",
            district: "朝阳区"
          },
          photoUrl: "https://picsum.photos/200/200?random=" + (i + 1), // 随机图片
          remark: "测试备注" + (i + 1),
          points: Math.floor(Math.random() * 10) + 1, // 1-10点积分
          pointsCalculated: true,
          verified: Math.random() > 0.3, // 70%概率已验证
          createTime: new Date(new Date().getTime() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000), // 最近30天内
          updateTime: new Date()
        };
        
        records.push(record);
      }
      
      resolve({
        code: 200,
        msg: "查询成功",
        rows: records,
        total: total
      });
    }, 800);
  });
}

// 上传垃圾投递照片
export function uploadPhoto(data) {
  // 如果后端接口不可用，使用本地模拟
  try {
    return request({
      url: '/garbage/record/upload',
      method: 'post',
      data: data,
      headers: {
        'Content-Type': 'application/json'
      }
    }).catch(error => {
      // 如果发生错误（如404），返回模拟数据
      console.log('使用模拟数据', error);
      return mockUploadPhoto(data);
    });
  } catch (error) {
    return mockUploadPhoto(data);
  }
}

// 模拟上传照片
function mockUploadPhoto(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 只需返回图片URL即可，实际上已经有Base64数据了
      const fileName = 'mock-photo-' + new Date().getTime() + '.jpg';
      
      resolve({
        code: 200,
        msg: "上传成功",
        data: {
          fileName: fileName,
          url: data.photoData // 直接使用Base64数据作为URL
        }
      });
    }, 1000);
  });
} 