import { getGarbageGuidesByCategory } from '~/api/garbage';

Page({
  data: {
    category: '',
    categoryName: '',
    guideList: [],
    loading: false
  },

  onLoad(options) {
    const { category, name } = options;
    this.setData({ 
      category,
      categoryName: name || '垃圾分类详情'
    });
    
    this.fetchGarbageGuides(category);
  },

  onPullDownRefresh() {
    this.fetchGarbageGuides(this.data.category);
    wx.stopPullDownRefresh();
  },

  // 获取垃圾分类指南列表
  async fetchGarbageGuides(category) {
    if (!category) return;
    
    this.setData({ loading: true });
    try {
      const res = await getGarbageGuidesByCategory(category);
      if (res.code === 200) {
        this.setData({ guideList: res.data || [] });
      } else {
        wx.showToast({
          title: res.msg || '获取分类指南失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取垃圾分类指南失败', error);
      wx.showToast({
        title: '获取分类指南失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 跳转到详情
  goToDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/guide/detail?id=${id}`
    });
  },

  // 搜索
  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/index'
    });
  }
}); 