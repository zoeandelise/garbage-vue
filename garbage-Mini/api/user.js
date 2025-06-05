import request from './request';

// 用户登录
export function login(data) {
  return request.post('/login', data);
}

// 用户注册
export function register(data) {
  return request.post('/register', data);
}

// 获取用户信息
export function getUserInfo() {
  return request.get('/getInfo');
}

// 退出登录
export function logout() {
  return request.post('/logout');
}

// 获取验证码
export function getVerificationCode(mobile) {
  return request.get('/getVerificationCode', { mobile });
}

// 微信登录
export function wxLogin(code) {
  return request.post('/wxLogin', { code });
}

// 修改用户信息
export function updateUserInfo(data) {
  return request.put('/system/user/profile', data);
}

// 上传用户头像
export function uploadAvatar(filePath) {
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: request.baseUrl + '/system/user/profile/avatar',
      filePath,
      name: 'avatarfile',
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
} 