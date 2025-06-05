import config from '../config';

const { baseUrl, timeout, headers } = config;
const delay = config.isMock ? 500 : 0;

// 最大重试次数
const MAX_RETRY_COUNT = 3;

// 统一处理请求
function request(url, method = 'GET', data = {}, retryCount = 0) {
  const header = {
    ...headers,
  };
  
  // 获取token，有就丢进请求头
  const tokenString = wx.getStorageSync('access_token');
  if (tokenString) {
    header.Authorization = `Bearer ${tokenString}`;
  }
  
  return new Promise((resolve, reject) => {
    wx.showLoading({
      title: '加载中',
      mask: true
    });
    
    wx.request({
      url: baseUrl + url,
      method,
      data,
      dataType: 'json',
      header,
      timeout: timeout || 10000,  // 默认10秒超时
      success(res) {
        setTimeout(() => {
          wx.hideLoading();
          
          // 若依后端接口统一返回code和msg
          if (res.data.code === 200) {
            resolve(res.data);
          } else if (res.data.code === 401) {
            // 处理未授权情况
            wx.removeStorageSync('access_token');
            wx.removeStorageSync('userInfo');
            wx.showToast({
              title: '登录已过期，请重新登录',
              icon: 'none',
              duration: 2000
            });
            
            setTimeout(() => {
              wx.navigateTo({
                url: '/pages/login/login'
              });
            }, 1000);
            
            reject(res.data);
          } else {
            // 其他错误情况
            wx.showToast({
              title: res.data.msg || '请求失败',
              icon: 'none',
              duration: 2000
            });
            reject(res.data);
          }
        }, delay);
      },
      fail(err) {
        // 网络错误或超时，尝试重试
        if (retryCount < MAX_RETRY_COUNT) {
          console.log(`请求失败，正在进行第${retryCount + 1}次重试`, err);
          
          // 延迟重试，避免立即重试可能导致的连续失败
          setTimeout(() => {
            request(url, method, data, retryCount + 1)
              .then(resolve)
              .catch(reject);
          }, 1000 * Math.pow(2, retryCount));  // 指数退避策略
          
          return;
        }
        
        setTimeout(() => {
          wx.hideLoading();
          wx.showToast({
            title: '网络异常，请检查网络连接',
            icon: 'none',
            duration: 2000
          });
          reject(err);
        }, delay);
      },
      complete() {
        // 某些情况下可能不会自动关闭loading
        setTimeout(() => {
          wx.hideLoading();
        }, delay + 500);
      }
    });
  });
}

// 导出请求方法
const api = {
  get: (url, data) => request(url, 'GET', data),
  post: (url, data) => request(url, 'POST', data),
  put: (url, data) => request(url, 'PUT', data),
  delete: (url, data) => request(url, 'DELETE', data),
  // 上传文件
  upload: (url, filePath, name = 'file', formData = {}) => {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: baseUrl + url,
        filePath,
        name,
        formData,
        header: {
          Authorization: `Bearer ${wx.getStorageSync('access_token')}`
        },
        success(res) {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(data);
          } else {
            reject(data);
          }
        },
        fail(err) {
          reject(err);
        }
      });
    });
  },
  baseUrl
};

export default api;
