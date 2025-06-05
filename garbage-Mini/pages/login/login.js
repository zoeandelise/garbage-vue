import request from '../../api/request';

Page({
  data: {
    phoneNumber: '',
    isPhoneNumber: false,
    isCheck: false,
    isSubmit: false,
    isPasswordLogin: true,  // 默认使用密码登录
    passwordInfo: {
      username: '',  // 改为username与后端一致
      password: '',
    },
    radioValue: '',
    loading: false,  // 添加loading状态
  },

  /* 自定义功能函数 */
  changeSubmit() {
    if (this.data.isPasswordLogin) {
      if (this.data.passwordInfo.username !== '' && this.data.passwordInfo.password !== '' && this.data.isCheck) {
        this.setData({ isSubmit: true });
      } else {
        this.setData({ isSubmit: false });
      }
    } else if (this.data.isPhoneNumber && this.data.isCheck) {
      this.setData({ isSubmit: true });
    } else {
      this.setData({ isSubmit: false });
    }
  },

  // 手机号变更
  onPhoneInput(e) {
    const isPhoneNumber = /^[1][3,4,5,7,8,9][0-9]{9}$/.test(e.detail.value);
    this.setData({
      isPhoneNumber,
      phoneNumber: e.detail.value,
    });
    this.changeSubmit();
  },

  // 用户协议选择变更
  onCheckChange(e) {
    const { value } = e.detail;
    this.setData({
      radioValue: value,
      isCheck: value === 'agree',
    });
    this.changeSubmit();
  },

  // 用户名/账号输入变更
  onAccountChange(e) {
    this.setData({ passwordInfo: { ...this.data.passwordInfo, username: e.detail.value } });
    this.changeSubmit();
  },

  // 密码输入变更
  onPasswordChange(e) {
    this.setData({ passwordInfo: { ...this.data.passwordInfo, password: e.detail.value } });
    this.changeSubmit();
  },

  // 切换登录方式
  changeLogin() {
    this.setData({ isPasswordLogin: !this.data.isPasswordLogin, isSubmit: false });
  },

  // 跳转到注册页面
  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/index'
    });
  },

  // 登录方法
  async login() {
    if (!this.data.isSubmit) {
      return;
    }

    this.setData({ loading: true });

    try {
      if (this.data.isPasswordLogin) {
        // 使用账号密码登录
        const loginData = {
          username: this.data.passwordInfo.username,
          password: this.data.passwordInfo.password,
        };

        const res = await request.post('/login', loginData);
        
        if (res.code === 200) {
          // 登录成功，存储token
          wx.setStorageSync('access_token', res.token);
          
          // 获取用户信息
          this.getUserInfo();
          
          wx.showToast({
            title: '登录成功',
            icon: 'success',
            duration: 1500
          });
          
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/home/home'
            });
          }, 1500);
        } else {
          wx.showToast({
            title: res.msg || '登录失败',
            icon: 'none',
            duration: 2000
          });
        }
      } else {
        // 使用手机号验证码登录
        wx.navigateTo({
          url: `/pages/loginCode/loginCode?phoneNumber=${this.data.phoneNumber}`,
        });
      }
    } catch (error) {
      console.error('登录失败', error);
      wx.showToast({
        title: '登录失败，请重试',
        icon: 'none',
        duration: 2000
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 获取用户信息
  async getUserInfo() {
    try {
      const res = await request.get('/getInfo');
      if (res.code === 200) {
        wx.setStorageSync('userInfo', res.user);
      }
    } catch (error) {
      console.error('获取用户信息失败', error);
    }
  },
});
