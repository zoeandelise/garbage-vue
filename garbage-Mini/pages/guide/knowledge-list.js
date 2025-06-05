import { getGarbageKnowledge } from '../../api/garbage';
import { showLoading, hideLoading } from '../../utils/util';

Page({
  data: {
    knowledgeList: [],
    loading: true,
    error: null
  },

  onLoad() {
    this.fetchKnowledgeList();
  },

  async fetchKnowledgeList() {
    showLoading('加载中');
    try {
      const res = await getGarbageKnowledge();
      if (res.code === 200) {
        const knowledgeList = res.data.map(item => ({
          id: item.id,
          title: item.title,
          summary: item.summary || item.content?.substring(0, 100) || '查看详情',
          imageUrl: item.imageUrl || '/static/images/knowledge1.png',
          date: item.createTime || new Date().toLocaleDateString()
        }));
        this.setData({
          knowledgeList,
          loading: false,
          error: null
        });
      } else {
        this.setData({
          error: { message: res.msg || '获取知识列表失败' },
          loading: false
        });
      }
    } catch (error) {
      console.error('获取知识列表失败', error);
      this.setData({
        error: { message: '网络错误，请稍后重试' },
        loading: false
      });
    } finally {
      hideLoading();
    }
  },

  onPullDownRefresh() {
    this.fetchKnowledgeList();
    wx.stopPullDownRefresh();
  },

  goToDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/guide/knowledge?id=${id}`
    });
  },

  onShareAppMessage() {
    return {
      title: '垃圾分类知识库 - 城市垃圾分类',
      path: '/pages/guide/knowledge-list'
    };
  }
}); 