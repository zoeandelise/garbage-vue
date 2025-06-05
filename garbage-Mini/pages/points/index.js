import { getUserPoints } from '~/api/garbage';

Page({
  data: {
    loading: false,
    userPoints: {
      totalPoints: 0,
      monthPoints: 0,
      weekPoints: 0,
      pointsRank: 0
    },
    pointsHistory: []
  },

  onLoad() {
    this.fetchUserPoints();
  },

  onPullDownRefresh() {
    this.fetchUserPoints();
    wx.stopPullDownRefresh();
  },

  // 获取用户积分信息
  async fetchUserPoints() {
    this.setData({ loading: true });
    try {
      const res = await getUserPoints();
      if (res.code === 200) {
        this.setData({
          userPoints: res.data.pointsInfo || {
            totalPoints: 0,
            monthPoints: 0,
            weekPoints: 0,
            pointsRank: 0
          },
          pointsHistory: res.data.pointsRecords || []
        });
      } else {
        wx.showToast({
          title: res.msg || '获取积分信息失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取用户积分信息失败', error);
      wx.showToast({
        title: '获取积分信息失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 跳转到积分排行榜
  goToRanking() {
    wx.navigateTo({
      url: '/pages/points/ranking'
    });
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
}); 