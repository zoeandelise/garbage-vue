import { getGarbageCategories } from '~/api/garbage';

Page({
  data: {
    categoryList: [],
    loading: false
  },

  onLoad() {
    this.fetchCategories();
  },

  onShow() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        value: 'guide'
      });
    }
  },

  onPullDownRefresh() {
    this.fetchCategories();
    wx.stopPullDownRefresh();
  },

  // 获取垃圾分类类别
  async fetchCategories() {
    this.setData({ loading: true });
    try {
      const res = await getGarbageCategories();
      if (res.code === 200) {
        this.setData({ categoryList: res.data });
      } else {
        wx.showToast({
          title: res.msg || '获取分类失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取垃圾分类失败', error);
      wx.showToast({
        title: '获取分类失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 跳转到分类详情
  goToCategoryDetail(e) {
    const { category } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/guide/category?category=${category.categoryCode}&name=${category.categoryName}`
    });
  },

  // 跳转到搜索页面
  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/index'
    });
  }
}); 