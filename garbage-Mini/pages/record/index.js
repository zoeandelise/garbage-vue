import { getUserGarbageRecords } from '~/api/garbage';
import { getUserInfo } from '~/api/user';

Page({
  data: {
    recordList: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    totalCount: 0,
    loggedIn: false
  },

  onLoad() {
    this.checkLoginStatus();
  },

  onShow() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        value: 'record'
      });
    }
    
    if (this.data.loggedIn) {
      this.setData({
        pageNum: 1,
        recordList: []
      });
      this.fetchRecords(true);
    }
  },

  onPullDownRefresh() {
    if (this.data.loggedIn) {
      this.setData({
        pageNum: 1,
        recordList: []
      });
      this.fetchRecords(true);
    }
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && this.data.loggedIn) {
      this.fetchRecords();
    }
  },

  // 检查登录状态
  async checkLoginStatus() {
    const token = wx.getStorageSync('access_token');
    if (token) {
      try {
        await getUserInfo();
        this.setData({ loggedIn: true });
        this.fetchRecords(true);
      } catch (error) {
        this.setData({ loggedIn: false });
      }
    } else {
      this.setData({ loggedIn: false });
    }
  },

  // 获取垃圾投递记录
  async fetchRecords(reset = false) {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    try {
      const { pageNum, pageSize } = this.data;
      const res = await getUserGarbageRecords({
        pageNum,
        pageSize
      });
      
      if (res.code === 200) {
        const records = res.rows || [];
        const total = res.total || 0;
        
        this.setData({
          recordList: reset ? records : [...this.data.recordList, ...records],
          totalCount: total,
          hasMore: pageNum * pageSize < total,
          pageNum: pageNum + 1
        });
      } else {
        wx.showToast({
          title: res.msg || '获取记录失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取垃圾投递记录失败', error);
      wx.showToast({
        title: '获取记录失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 跳转到记录详情
  goToDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/record/detail?id=${id}`
    });
  },

  // 跳转到垃圾投递页面
  goToUpload() {
    if (!this.data.loggedIn) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    wx.navigateTo({
      url: '/pages/record/upload'
    });
  },

  // 跳转到登录页面
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  }
}); 