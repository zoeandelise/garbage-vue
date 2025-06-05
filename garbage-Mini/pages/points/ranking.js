import { getPointsRanking } from '~/api/garbage';

Page({
  data: {
    loading: false,
    rankingList: [],
    rankingType: 'total', // total, month, week
    tabs: [
      { value: 'total', label: '总积分榜' },
      { value: 'month', label: '月积分榜' },
      { value: 'week', label: '周积分榜' }
    ]
  },

  onLoad() {
    this.fetchRanking(this.data.rankingType);
  },

  onPullDownRefresh() {
    this.fetchRanking(this.data.rankingType);
    wx.stopPullDownRefresh();
  },

  // 获取积分排行榜
  async fetchRanking(type) {
    this.setData({ loading: true });
    try {
      const res = await getPointsRanking(type);
      if (res.code === 200) {
        this.setData({
          rankingList: res.data || []
        });
      } else {
        wx.showToast({
          title: res.msg || '获取排行榜失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取积分排行榜失败', error);
      wx.showToast({
        title: '获取排行榜失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 切换排行榜类型
  onTabChange(e) {
    const type = e.currentTarget.dataset.value;
    this.setData({ rankingType: type });
    this.fetchRanking(type);
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
}); 