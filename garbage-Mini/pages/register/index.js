const app = getApp();
import request from '../../api/request';

Page({
  data: {
    loading: false,
    username: '',
    nickname: '',
    password: '',
    confirmPassword: '',
    phonenumber: '',
    code: '',       // 验证码
    uuid: '',       // 验证码唯一标识
    showPassword: false,
    showConfirmPassword: false,
    captchaUrl: '', // 验证码图片URL
  },

  onLoad() {
    // 加载验证码
    this.refreshCaptcha();
  },

  // 刷新验证码
  refreshCaptcha() {
    const timestamp = new Date().getTime();
    const uuid = this.generateUUID();
    this.setData({
      uuid,
      captchaUrl: `${request.baseUrl}/captchaImage?type=math&uuid=${uuid}&t=${timestamp}`
    });
  },

  // 生成UUID
  generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  },

  // 输入用户名
  onUsernameInput(e) {
    this.setData({
      username: e.detail.value
    });
  },

  // 输入昵称
  onNicknameInput(e) {
    this.setData({
      nickname: e.detail.value
    });
  },

  // 输入密码
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },

  // 输入确认密码
  onConfirmPasswordInput(e) {
    this.setData({
      confirmPassword: e.detail.value
    });
  },

  // 输入手机号
  onPhoneInput(e) {
    this.setData({
      phonenumber: e.detail.value
    });
  },

  // 输入验证码
  onCaptchaInput(e) {
    this.setData({
      code: e.detail.value
    });
  },

  // 切换密码显示状态
  togglePasswordVisibility() {
    this.setData({
      showPassword: !this.data.showPassword
    });
  },

  // 切换确认密码显示状态
  toggleConfirmPasswordVisibility() {
    this.setData({
      showConfirmPassword: !this.data.showConfirmPassword
    });
  },

  // 注册
  register() {
    const { username, nickname, password, confirmPassword, phonenumber, code, uuid } = this.data;
    
    // 表单验证
    if (!username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      });
      return;
    }
    
    if (!nickname) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      });
      return;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }
    
    if (password !== confirmPassword) {
      wx.showToast({
        title: '两次密码输入不一致',
        icon: 'none'
      });
      return;
    }
    
    if (!phonenumber) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return;
    }
    
    if (!/^1\d{10}$/.test(phonenumber)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return;
    }
    
    if (!code) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ loading: true });
    
    // 调用注册接口
    const registerData = {
      username,
      nickName: nickname,
      password,
      phonenumber,
      code,
      uuid
    };

    request.post('/register', registerData)
      .then(res => {
        if (res.code === 200) {
          wx.showToast({
            title: '注册成功',
            icon: 'success',
            duration: 1500
          });
          
          // 注册成功后跳转到登录页面
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          this.refreshCaptcha(); // 刷新验证码
          throw new Error(res.msg || '注册失败');
        }
      })
      .catch(err => {
        console.error('注册失败', err);
        this.setData({ loading: false });
        
        wx.showToast({
          title: err.message || '注册失败，请重试',
          icon: 'none'
        });
        
        this.refreshCaptcha(); // 刷新验证码
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // 返回登录页
  goBack() {
    wx.navigateBack();
  },
  
  // 查看用户协议
  showUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '感谢您使用城市垃圾分类小程序。本协议是您与我们之间关于使用本小程序服务所订立的协议。使用本小程序即表示您已阅读并同意本协议的所有条款。',
      confirmText: '我知道了',
      showCancel: false
    });
  },
  
  // 查看隐私政策
  showPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '我们非常重视您的个人信息和隐私保护。本小程序收集的信息将用于提供服务、改进用户体验和进行必要的统计分析。我们承诺依法保护您的个人信息安全。',
      confirmText: '我知道了',
      showCancel: false
    });
  }
}); 