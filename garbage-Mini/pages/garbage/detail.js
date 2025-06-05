import { getGarbageGuideDetail } from '../../api/garbage';

Page({
  data: {
    loading: true,
    detail: null,
    error: false
  },

  onLoad(options) {
    if (options.id) {
      this.fetchGarbageDetail(options.id);
    } else {
      this.setData({
        loading: false,
        error: true
      });
      wx.showToast({
        title: '参数错误',
        icon: 'error'
      });
    }
  },

  onPullDownRefresh() {
    if (this.data.detail && this.data.detail.id) {
      this.fetchGarbageDetail(this.data.detail.id);
    }
    wx.stopPullDownRefresh();
  },

  async fetchGarbageDetail(id) {
    try {
      this.setData({ loading: true, error: false });
      const res = await getGarbageGuideDetail(id);
      if (res.code === 200) {
        this.setData({
          detail: res.data,
          loading: false
        });
      } else {
        this.setData({
          loading: false,
          error: true
        });
        wx.showToast({
          title: res.msg || '获取详情失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取垃圾分类详情失败', error);
      this.setData({
        loading: false,
        error: true
      });
      wx.showToast({
        title: '获取详情失败',
        icon: 'none'
      });
    }
  },

  // 预览图片
  previewImage() {
    const { detail } = this.data;
    if (detail && detail.imageUrl) {
      wx.previewImage({
        urls: [detail.imageUrl],
        current: detail.imageUrl
      });
    }
  },

  // 导航到相关垃圾
  navigateToRelated(e) {
    const id = e.currentTarget.dataset.id;
    if (id) {
      wx.navigateTo({
        url: `/pages/garbage/detail?id=${id}`
      });
    }
  },

  goBack() {
    wx.navigateBack();
  },

  // 分享功能
  onShareAppMessage() {
    const { detail } = this.data;
    if (detail) {
      return {
        title: `${detail.name}属于${detail.categoryName}垃圾`,
        path: `/pages/garbage/detail?id=${detail.id}`
      };
    }
    return {
      title: '垃圾分类指南详情',
      path: '/pages/garbage/index'
    };
  }
}); 