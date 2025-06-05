import { getGarbageKnowledgeDetail } from '../../api/garbage';
import { showLoading, hideLoading } from '../../utils/util';

Page({
  data: {
    id: null,
    knowledgeDetail: {},
    loading: true,
    error: null
  },

  onLoad(options) {
    const { id } = options;
    if (!id) {
      this.setData({
        error: { message: '缺少必要的参数' },
        loading: false
      });
      return;
    }

    this.setData({ id });
    this.fetchKnowledgeDetail(id);
  },

  async fetchKnowledgeDetail(id) {
    showLoading('加载中');
    try {
      const res = await getGarbageKnowledgeDetail(id);
      if (res.code === 200) {
        // 处理内容，将HTML内容转换为Mini Program适用的格式
        let content = res.data.content || '';
        if (content) {
          // 可以在这里进行一些内容处理，如果需要的话
          content = content.replace(/<img/g, '<img style="max-width:100%;height:auto" ');
        }

        this.setData({
          knowledgeDetail: {
            ...res.data,
            content,
            createTime: res.data.createTime || new Date().toLocaleDateString()
          },
          loading: false,
          error: null
        });
      } else {
        this.setData({
          error: { message: res.msg || '获取知识详情失败' },
          loading: false
        });
      }
    } catch (error) {
      console.error('获取知识详情失败', error);
      this.setData({
        error: { message: '网络错误，请稍后重试' },
        loading: false
      });
    } finally {
      hideLoading();
    }
  },

  onShareAppMessage() {
    const { id } = this.data;
    const { title } = this.data.knowledgeDetail;
    return {
      title: title || '垃圾分类知识 - 城市垃圾分类',
      path: `/pages/guide/knowledge?id=${id}`
    };
  },

  onPullDownRefresh() {
    const { id } = this.data;
    if (id) {
      this.fetchKnowledgeDetail(id);
    }
    wx.stopPullDownRefresh();
  }
}); 