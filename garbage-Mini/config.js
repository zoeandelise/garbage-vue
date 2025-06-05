module.exports = {
  // 是否使用模拟数据
  isMock: false,
  // 开发环境API地址，指向RuoYi后端地址
  baseUrl: 'http://localhost:81/dev-api',
  // 生产环境可在此配置
  // baseUrl: process.env.NODE_ENV === 'production' ? 'https://api.example.com/dev-api' : 'http://localhost:81/dev-api',
  // API版本
  apiVersion: 'v1',
  // 请求超时时间(毫秒)
  timeout: 10000,
  // 请求头
  headers: {
    'Content-Type': 'application/json'
  }
};
