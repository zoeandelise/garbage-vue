import { getUserGarbageRecords } from '~/api/garbage';

Page({
  data: {
    recordList: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    totalCount: 0
  },

  onLoad() {
    this.fetchRecords(true);
  },

  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      recordList: []
    });
    this.fetchRecords(true);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.fetchRecords();
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

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
}); 