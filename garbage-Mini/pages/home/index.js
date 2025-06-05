import Message from 'tdesign-miniprogram/message/index';
import { getGarbageCategories, getStatistics, getEcoTips, getGarbageKnowledge } from '../../api/garbage';
import { showToast, showLoading, hideLoading } from '../../utils/util';

// 获取应用实例
const app = getApp();

Page({
  data: {
    enable: false,
    loading: true,
    // 轮播图数据
    swiperList: [
      {
        image: '/static/banner/banner1.jpg',
        description: '垃圾分类，从我做起',
      },
      {
        image: '/static/banner/banner2.jpg',
        description: '保护环境，人人有责',
      },
      {
        image: '/static/banner/banner3.jpg',
        description: '绿色环保，低碳生活',
      }
    ],
    // 垃圾分类数据
    categoryList: [],
    // 统计数据
    statistics: {
      totalUsers: 0,
      totalRecords: 0,
      totalWeight: 0,
      totalPoints: 0
    },
    // 用户信息
    userInfo: {},
    hasUserInfo: false,
    // 首页展示的知识内容
    knowledgeList: [
      {
        title: '垃圾分类小知识',
        content: '生活垃圾主要分为可回收物、有害垃圾、厨余垃圾和其他垃圾四大类',
        icon: '/static/images/knowledge1.png'
      },
      {
        title: '为什么要垃圾分类？',
        content: '垃圾分类可以减少土地资源的消耗，减轻处理的负担和降低处理成本',
        icon: '/static/images/knowledge2.png'
      }
    ],
    // 环保小贴士
    ecoTips: [
      '使用可重复使用的购物袋，减少塑料袋使用',
      '减少使用一次性餐具，自带水杯和饭盒',
      '旧衣物可以捐赠或回收，不要直接丢弃',
      '电子废弃物请投放到专门的回收点',
      '随手关灯，节约用水，低碳生活从点滴做起'
    ],
    currentTip: '',
    tipIndex: 0,
    error: null,
  },
  onLoad(option) {
    this.setData({ loading: true });
    
    if (wx.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true,
      });
    }
    if (option.oper) {
      let content = '';
      if (option.oper === 'release') {
        content = '发布成功';
      } else if (option.oper === 'save') {
        content = '保存成功';
      }
      this.showOperMsg(content);
    }
    
    // 初始化数据
    this.initPageData();
    
    // 启动环保小贴士轮播
    this.startTipRotation();

    // 检查用户登录状态
    this.checkLoginStatus();
  },
  
  // 初始化页面数据
  async initPageData() {
    showLoading('加载中');
    try {
      // 并行请求多个接口
      const [categoriesRes, statisticsRes, tipsRes, knowledgeRes] = await Promise.allSettled([
        this.fetchGarbageCategories(),
        this.fetchStatistics(),
        this.fetchEcoTips(),
        this.fetchKnowledge()
      ]);
      
      // 处理返回结果
      if (categoriesRes.status === 'rejected') {
        console.error('获取垃圾分类数据失败', categoriesRes.reason);
      }
      
      if (statisticsRes.status === 'rejected') {
        console.error('获取统计数据失败', statisticsRes.reason);
      }
      
      if (tipsRes.status === 'rejected') {
        console.error('获取环保小贴士失败', tipsRes.reason);
      }
      
      if (knowledgeRes.status === 'rejected') {
        console.error('获取垃圾分类知识失败', knowledgeRes.reason);
      }
      
      this.setData({ loading: false, error: null });
    } catch (error) {
      console.error('初始化数据失败', error);
      this.setData({ 
        loading: false, 
        error: { message: '数据加载失败，请下拉刷新重试' } 
      });
    } finally {
      hideLoading();
    }
  },
  
  onShow() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        value: 'home'
      });
    }
  },
  
  onPullDownRefresh() {
    this.refresh();
    wx.stopPullDownRefresh();
  },
  
  async refresh() {
    this.setData({
      enable: true,
      loading: true,
      error: null
    });

    await this.initPageData();

    setTimeout(() => {
      this.setData({
        enable: false
      });
    }, 500);
  },
  
  // 检查用户登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('access_token');
    if (token) {
      const userInfo = wx.getStorageSync('userInfo');
      if (userInfo) {
        this.setData({
          userInfo,
          hasUserInfo: true
        });
      }
    }
  },
  
  // 环保小贴士轮播
  startTipRotation() {
    const { ecoTips } = this.data;
    if (ecoTips.length > 0) {
      this.setData({
        currentTip: ecoTips[0],
        tipIndex: 0
      });
      
      // 设置定时器，每5秒切换一次贴士
      this.tipTimer = setInterval(() => {
        let nextIndex = (this.data.tipIndex + 1) % ecoTips.length;
        this.setData({
          currentTip: ecoTips[nextIndex],
          tipIndex: nextIndex
        });
      }, 5000);
    }
  },
  
  // 获取环保小贴士
  async fetchEcoTips() {
    try {
      const res = await getEcoTips();
      if (res.code === 200 && res.data && res.data.length > 0) {
        this.setData({
          ecoTips: res.data.map(item => item.content || item.tipContent || item)
        });
        // 重新启动轮播
        if (this.tipTimer) {
          clearInterval(this.tipTimer);
        }
        this.startTipRotation();
      }
      return res;
    } catch (error) {
      console.error('获取环保小贴士失败', error);
      // 使用默认数据
      return { code: 200, data: this.data.ecoTips };
    }
  },
  
  // 获取垃圾分类知识
  async fetchKnowledge() {
    try {
      const res = await getGarbageKnowledge();
      if (res.code === 200 && res.data && res.data.length > 0) {
        // 提取前两项作为首页展示
        const knowledgeList = res.data.slice(0, 2).map(item => ({
          title: item.title,
          content: item.summary || item.content?.substring(0, 50) || '了解更多垃圾分类知识',
          icon: item.imageUrl || '/static/images/knowledge1.png',
          id: item.id
        }));
        this.setData({ knowledgeList });
      }
      return res;
    } catch (error) {
      console.error('获取垃圾分类知识失败', error);
      // 使用默认数据
      return { code: 200, data: this.data.knowledgeList };
    }
  },
  
  onUnload() {
    // 清除定时器
    if (this.tipTimer) {
      clearInterval(this.tipTimer);
    }
  },
  
  showOperMsg(content) {
    Message.success({
      context: this,
      offset: [120, 32],
      duration: 4000,
      content,
    });
  },
  
  goRelease() {
    // 检查是否登录
    if (!wx.getStorageSync('access_token')) {
      showToast('请先登录');
      
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }
    
    wx.navigateTo({
      url: '/pages/record/upload',
    });
  },
  
  // 获取垃圾分类数据
  async fetchGarbageCategories() {
    try {
      const res = await getGarbageCategories();
      if (res.code === 200) {
        // 添加默认颜色
        const categoryColors = ['#4CAF50', '#F44336', '#FF9800', '#9C27B0'];
        const categoryList = res.data.map((item, index) => ({
          ...item,
          categoryColor: item.categoryColor || categoryColors[index % categoryColors.length]
        }));
        this.setData({ categoryList });
      }
      return res;
    } catch (error) {
      console.error('获取垃圾分类数据失败', error);
      throw error;
    }
  },
  
  // 获取统计数据
  async fetchStatistics() {
    try {
      const res = await getStatistics();
      if (res.code === 200) {
        // 处理后端返回的不同格式数据
        const statistics = {
          totalUsers: res.data.totalUsers || res.data.userCount || 0,
          totalRecords: res.data.totalRecords || res.data.recordCount || 0,
          totalWeight: res.data.totalWeight || res.data.weightTotal || 0,
          totalPoints: res.data.totalPoints || res.data.pointsTotal || 0
        };
        this.setData({ statistics });
      }
      return res;
    } catch (error) {
      console.error('获取统计数据失败', error);
      throw error;
    }
  },
  
  // 跳转到分类详情
  goToCategoryDetail(e) {
    const { category } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/guide/detail?id=${category.categoryId}&name=${category.categoryName}`
    });
  },
  
  // 跳转到搜索页面
  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/index'
    });
  },
  
  // 跳转到投递记录页面
  goToUploadRecord() {
    // 检查是否登录
    if (!wx.getStorageSync('access_token')) {
      showToast('请先登录');
      
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }
    
    wx.navigateTo({
      url: '/pages/record/upload'
    });
  },
  
  // 通用跳转方法
  navigateTo(e) {
    const { url } = e.currentTarget.dataset;
    
    // 检查是否需要登录权限
    if (url.includes('/pages/points/') && !wx.getStorageSync('access_token')) {
      showToast('请先登录');
      
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }
    
    wx.navigateTo({
      url
    });
  },
  
  // 跳转到知识详情
  goToKnowledge(e) {
    const { index } = e.currentTarget.dataset;
    const item = this.data.knowledgeList[index];
    const id = item.id || index;
    wx.navigateTo({
      url: `/pages/guide/knowledge?id=${id}`
    });
  }
});
