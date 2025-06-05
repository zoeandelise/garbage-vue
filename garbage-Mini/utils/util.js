/**
 * 格式化时间
 * @param {Date} date 日期对象
 * @param {String} format 格式字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @return {String} 格式化后的时间字符串
 */
const formatTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();

  const formatNumber = n => {
    n = n.toString();
    return n[1] ? n : `0${n}`;
  };

  format = format
    .replace('YYYY', year)
    .replace('MM', formatNumber(month))
    .replace('DD', formatNumber(day))
    .replace('HH', formatNumber(hour))
    .replace('mm', formatNumber(minute))
    .replace('ss', formatNumber(second));

  return format;
};

/**
 * 复制文件到本地临时路径，用于预览
 * @param {String} path 文件路径
 * @param {String} name 文件名称
 * @return {String} 临时文件路径
 */
const getLocalUrl = (path, name) => {
  try {
    const fs = wx.getFileSystemManager();
    const tempFileName = `${wx.env.USER_DATA_PATH}/${name}`;
    fs.copyFileSync(path, tempFileName);
    return tempFileName;
  } catch (err) {
    console.error('复制文件失败：', err);
    return path; // 如果复制失败，返回原路径
  }
};

/**
 * 文件大小格式化
 * @param {Number} size 文件大小，单位字节
 * @return {String} 格式化后的文件大小
 */
const formatFileSize = size => {
  if (size < 1024) {
    return size + 'B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + 'KB';
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + 'MB';
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + 'GB';
  }
};

/**
 * 防抖函数
 * @param {Function} func 要执行的函数
 * @param {Number} wait 延迟时间，单位毫秒
 * @return {Function} 防抖后的函数
 */
const debounce = (func, wait = 500) => {
  let timer;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      func.apply(this, args);
    }, wait);
  };
};

/**
 * 节流函数
 * @param {Function} func 要执行的函数
 * @param {Number} wait 等待时间，单位毫秒
 * @return {Function} 节流后的函数
 */
const throttle = (func, wait = 500) => {
  let timer = null;
  let previous = 0;
  return function(...args) {
    const now = Date.now();
    const remaining = wait - (now - previous);
    if (remaining <= 0 || remaining > wait) {
      if (timer) {
        clearTimeout(timer);
        timer = null;
      }
      previous = now;
      func.apply(this, args);
    } else if (!timer) {
      timer = setTimeout(() => {
        previous = Date.now();
        timer = null;
        func.apply(this, args);
      }, remaining);
    }
  };
};

/**
 * 显示加载中提示
 * @param {String} title 提示内容
 */
const showLoading = (title = '加载中') => {
  wx.showLoading({
    title,
    mask: true
  });
};

/**
 * 隐藏加载中提示
 */
const hideLoading = () => {
  wx.hideLoading();
};

/**
 * 显示消息提示
 * @param {String} title 提示内容
 * @param {String} icon 图标类型，可选值：success, error, loading, none
 * @param {Number} duration 提示显示时间，单位毫秒
 */
const showToast = (title, icon = 'none', duration = 2000) => {
  wx.showToast({
    title,
    icon,
    duration
  });
};

/**
 * 显示确认弹窗
 * @param {String} title 标题
 * @param {String} content 内容
 * @param {Boolean} showCancel 是否显示取消按钮
 * @return {Promise} Promise对象
 */
const showModal = (title, content, showCancel = true) => {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title,
      content,
      showCancel,
      success(res) {
        if (res.confirm) {
          resolve(true);
        } else if (res.cancel) {
          resolve(false);
        }
      },
      fail(err) {
        reject(err);
      }
    });
  });
};

/**
 * 检查用户是否已登录
 * @return {Boolean} 是否已登录
 */
const checkLogin = () => {
  const token = wx.getStorageSync('access_token');
  return !!token;
};

/**
 * 跳转到登录页面
 */
const navigateToLogin = () => {
  wx.navigateTo({
    url: '/pages/login/login'
  });
};

/**
 * 权限检查并提示
 * @param {Function} callback 权限通过后的回调函数
 */
const checkPermission = (callback) => {
  if (checkLogin()) {
    callback();
  } else {
    showModal('提示', '该功能需要登录后使用，是否前往登录？').then(res => {
      if (res) {
        navigateToLogin();
      }
    });
  }
};

/**
 * 获取用户头像
 * @return {Promise} Promise对象
 */
const getUserAvatar = () => {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        resolve(res.tempFilePaths[0]);
      },
      fail(err) {
        reject(err);
      }
    });
  });
};

/**
 * 获取当前位置
 * @return {Promise} Promise对象
 */
const getLocation = () => {
  return new Promise((resolve, reject) => {
    wx.getLocation({
      type: 'gcj02',
      success(res) {
        resolve(res);
      },
      fail(err) {
        reject(err);
      }
    });
  });
};

/**
 * 计算时间差
 * @param {Date|String|Number} time 时间对象、时间字符串或时间戳
 * @return {String} 格式化后的时间差
 */
const timeAgo = (time) => {
  if (!time) return '';
  
  const date = typeof time === 'object' ? time : new Date(time);
  const now = new Date();
  const diff = (now - date) / 1000;
  
  if (diff < 60) {
    return '刚刚';
  } else if (diff < 3600) {
    return Math.floor(diff / 60) + '分钟前';
  } else if (diff < 3600 * 24) {
    return Math.floor(diff / 3600) + '小时前';
  } else if (diff < 3600 * 24 * 30) {
    return Math.floor(diff / (3600 * 24)) + '天前';
  } else if (diff < 3600 * 24 * 365) {
    return Math.floor(diff / (3600 * 24 * 30)) + '个月前';
  } else {
    return Math.floor(diff / (3600 * 24 * 365)) + '年前';
  }
};

/**
 * 随机生成指定长度的字符串
 * @param {Number} length 字符串长度
 * @return {String} 随机字符串
 */
const randomString = (length = 16) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

module.exports = {
  formatTime,
  formatFileSize,
  debounce,
  throttle,
  showLoading,
  hideLoading,
  showToast,
  showModal,
  checkLogin,
  navigateToLogin,
  checkPermission,
  getUserAvatar,
  getLocation,
  timeAgo,
  randomString,
  getLocalUrl
};
