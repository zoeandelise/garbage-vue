import { getGarbageGuideDetail } from '../../api/garbage';
import { showLoading, hideLoading, showToast } from '../../utils/util';

// 分类代码映射
const CATEGORY_MAP = {
  'recyclable': '可回收物',
  'hazardous': '有害垃圾',
  'household_food': '厨余垃圾',
  'other': '其他垃圾'
};

// 分类颜色映射
const CATEGORY_COLORS = {
  'recyclable': '#4CAF50',
  'hazardous': '#F44336',
  'household_food': '#FF9800',
  'other': '#9C27B0'
};

Page({
  data: {
    id: '',
    guideDetail: null,
    loading: false,
    error: null
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.setData({ id });
      this.fetchGuideDetail(id);
    } else {
      showToast('参数错误');
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  // 获取垃圾分类详情
  async fetchGuideDetail(id) {
    this.setData({ loading: true });
    showLoading('加载中');
    
    try {
      const res = await getGarbageGuideDetail(id);
      if (res.code === 200) {
        // 处理返回的数据
        const detail = res.data;
        
        // 添加分类名称和颜色
        const categoryName = CATEGORY_MAP[detail.category] || '未知分类';
        const categoryColor = CATEGORY_COLORS[detail.category] || '#4CAF50';
        
        // 处理图片路径
        let imageUrl = detail.image_url || '';
        if (imageUrl && !imageUrl.startsWith('http')) {
          imageUrl = `${getApp().globalData.baseUrl || ''}${imageUrl}`;
        }
        
        // 处理提示信息
        const tips = Array.isArray(detail.tips) ? detail.tips.join('\n') : detail.tips || '';
        
        this.setData({
          guideDetail: {
            ...detail,
            categoryName,
            categoryColor,
            imageUrl,
            tips,
            description: detail.detailed_description || detail.disposal_method || '',
            throwingMethod: detail.disposal_method || ''
          },
          loading: false,
          error: null
        });
      } else {
        this.setData({
          error: { message: res.msg || '获取详情失败' },
          loading: false
        });
        showToast(res.msg || '获取详情失败');
      }
    } catch (error) {
      console.error('获取垃圾分类详情失败', error);
      this.setData({
        error: { message: '网络错误，请稍后重试' },
        loading: false
      });
      showToast('获取详情失败，请重试');
    } finally {
      hideLoading();
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  },

  // 预览图片
  previewImage() {
    const { guideDetail } = this.data;
    if (guideDetail && guideDetail.imageUrl) {
      wx.previewImage({
        current: guideDetail.imageUrl,
        urls: [guideDetail.imageUrl]
      });
    }
  }
}); 