import request from '~/api/request';
import { searchGarbageGuides } from '~/api/garbage';
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
  'other': '#9C27B0',
  '可回收物': '#4CAF50',
  '有害垃圾': '#F44336',
  '厨余垃圾': '#FF9800',
  '其他垃圾': '#9C27B0'
};

// 添加英文名称映射
const EN_NAME_MAP = {
  '纸巾': 'tissue paper',
  '塑料瓶': 'plastic bottle',
  '电池': 'battery',
  '灯泡': 'light bulb',
  '果皮': 'fruit peel',
  '菜叶': 'vegetable leaves',
  '纸箱': 'cardboard box',
  '旧衣物': 'old clothes',
  '玻璃瓶': 'glass bottle',
  '易拉罐': 'can'
};

Page({
  data: {
    historyWords: [],
    popularWords: ['塑料瓶', '旧报纸', '厨余垃圾', '电池', '灯泡', '纸箱'],
    searchValue: '',
    dialog: {
      title: '确认删除当前历史记录',
      showCancelButton: true,
      message: '',
    },
    dialogShow: false,
    value: '',
    placeholder: '搜索垃圾名称',
    searchList: [],
    searchStatus: 'empty', // empty初始状态, searching搜索中, success搜索成功, fail搜索失败
    loading: false,
    // 详情展示相关
    showDetail: false,
    currentDetail: null,
    animationData: {}
  },

  deleteType: 0,
  deleteIndex: '',

  onShow() {
    this.queryHistory();
    this.queryPopular();
  },

  onLoad(options) {
    // 如果有传入搜索关键词，直接搜索
    if (options.keyword) {
      this.setData({
        value: options.keyword
      });
      this.onSearch();
    }
    
    // 获取本地存储的搜索历史
    this.loadSearchHistory();
    
    // 创建动画实例
    this.animation = wx.createAnimation({
      duration: 300,
      timingFunction: 'ease',
    });
  },

  /**
   * 查询历史记录
   * @returns {Promise<void>}
   */
  async queryHistory() {
    request('/api/searchHistory').then((res) => {
      const { code, data } = res;

      if (code === 200) {
        const { historyWords = [] } = data;
        this.setData({
          historyWords,
        });
      }
    });
  },

  /**
   * 查询热门搜索
   * @returns {Promise<void>}
   */
  async queryPopular() {
    request('/api/searchPopular').then((res) => {
      const { code, data } = res;

      if (code === 200) {
        const { popularWords = [] } = data;
        this.setData({
          popularWords,
        });
      }
    });
  },

  setHistoryWords(searchValue) {
    if (!searchValue) return;

    const { historyWords } = this.data;
    const index = historyWords.indexOf(searchValue);

    if (index !== -1) {
      historyWords.splice(index, 1);
    }
    historyWords.unshift(searchValue);

    this.setData({
      searchValue,
      historyWords,
    });
    // if (searchValue) {
    //     wx.navigateTo({
    //         url: `/pages/goods/result/index?searchValue=${searchValue}`,
    //     });
    // }
  },

  /**
   * 清空历史记录的再次确认框
   * 后期可能需要增加一个向后端请求的接口
   * @returns {Promise<void>}
   */
  confirm() {
    const { historyWords } = this.data;
    const { deleteType, deleteIndex } = this;

    if (deleteType === 0) {
      historyWords.splice(deleteIndex, 1);
      this.setData({
        historyWords,
        dialogShow: false,
      });
    } else {
      this.setData({ historyWords: [], dialogShow: false });
    }
  },

  /**
   * 取消清空历史记录
   * @returns {Promise<void>}
   */
  close() {
    this.setData({ dialogShow: false });
  },

  /**
   * 点击清空历史记录
   * @returns {Promise<void>}
   */
  handleClearHistory() {
    const { dialog } = this.data;
    this.deleteType = 1;
    this.setData({
      dialog: {
        ...dialog,
        message: '确认删除所有历史记录',
      },
      dialogShow: true,
    });
  },

  deleteCurr(e) {
    const { index } = e.currentTarget.dataset;
    const { dialog } = this.data;
    this.deleteIndex = index;
    this.deleteType = 0;
    this.setData({
      dialog: {
        ...dialog,
        message: '确认删除当前历史记录',
      },
      dialogShow: true,
    });
  },

  /**
   * 点击关键词跳转搜索
   * 后期需要增加跳转和后端请求接口
   * @returns {Promise<void>}
   */
  handleHistoryTap(e) {
    const { historyWords } = this.data;
    const { index } = e.currentTarget.dataset;
    const searchValue = historyWords[index || 0] || '';

    this.setHistoryWords(searchValue);
  },

  handlePopularTap(e) {
    const { popularWords } = this.data;
    const { index } = e.currentTarget.dataset;
    const searchValue = popularWords[index || 0] || '';

    this.setHistoryWords(searchValue);
  },

  /**
   * 提交搜索框内容
   * 后期需要增加跳转和后端请求接口
   * @returns {Promise<void>}
   */
  handleSubmit(e) {
    const { value } = e.detail;
    if (value.length === 0) return;

    this.setHistoryWords(value);
  },

  /**
   * 点击取消回到主页
   * @returns {Promise<void>}
   */
  actionHandle() {
    this.setData({
      searchValue: '',
    });
    wx.switchTab({ url: '/pages/home/index' });
  },

  onChange(e) {
    const { value } = e.detail;
    this.setData({ value });
  },

  onClear() {
    this.setData({
      value: '',
      searchList: [],
      searchStatus: 'empty'
    });
  },

  // 根据分类代码获取分类名称
  getCategoryName(category) {
    return CATEGORY_MAP[category] || '未知分类';
  },

  // 添加获取英文名称的方法
  getEnglishName(chineseName) {
    return EN_NAME_MAP[chineseName] || '';
  },

  async onSearch() {
    const { value } = this.data;
    if (!value.trim()) {
      showToast('请输入搜索内容');
      return;
    }

    this.setData({ 
      loading: true,
      searchStatus: 'searching',
      showDetail: false,
      currentDetail: null
    });
    
    showLoading('搜索中');

    try {
      const res = await searchGarbageGuides(value);
      
      if (res.code === 200) {
        // 处理搜索结果，添加分类颜色
        const searchList = (res.data || []).map(item => {
          // 根据分类代码或名称设置颜色
          const categoryColor = CATEGORY_COLORS[item.category] || 
                               CATEGORY_COLORS[item.categoryName] || 
                               '#4CAF50';
          
          // 处理图片路径
          let imageUrl = item.image_url || '';
          if (imageUrl && !imageUrl.startsWith('http')) {
            imageUrl = `${getApp().globalData.baseUrl || ''}${imageUrl}`;
          }
          
          // 处理提示信息
          const tips = Array.isArray(item.tips) ? item.tips.join('\n') : item.tips || '';
          
          // 获取英文名称
          const enName = item.en_name || this.getEnglishName(item.name);
          
          return {
            ...item,
            categoryColor,
            imageUrl,
            tips,
            enName,
            // 确保有分类名称
            categoryName: item.categoryName || this.getCategoryName(item.category),
            // 处理描述
            description: item.detailed_description || item.disposal_method || '',
            throwingMethod: item.disposal_method || ''
          };
        });
        
        // 创建列表动画效果
        this.animation.opacity(0).step();
        this.setData({
          animationData: this.animation.export()
        });
        
        setTimeout(() => {
          this.animation.opacity(1).step();
          this.setData({
            searchList,
            searchStatus: searchList.length > 0 ? 'success' : 'empty',
            loading: false,
            animationData: this.animation.export()
          });
        }, 100);
        
        // 添加到搜索历史
        this.addToHistory(value);
      } else {
        this.setData({
          searchStatus: 'fail',
          loading: false
        });
        showToast(res.msg || '搜索失败');
      }
    } catch (error) {
      console.error('搜索失败', error);
      this.setData({
        searchStatus: 'fail',
        loading: false
      });
      showToast('搜索失败，请稍后重试');
    } finally {
      hideLoading();
    }
  },

  onEnter() {
    this.onSearch();
  },

  // 显示垃圾详情
  showItemDetail(e) {
    const { index } = e.currentTarget.dataset;
    const detail = this.data.searchList[index];
    
    if (detail) {
      // 创建动画效果
      this.animation.opacity(0).translateY(20).step();
      
      this.setData({
        animationData: this.animation.export()
      });
      
      setTimeout(() => {
        this.animation.opacity(1).translateY(0).step();
        
        this.setData({
          showDetail: true,
          currentDetail: detail,
          animationData: this.animation.export()
        });
      }, 100);
    }
  },
  
  // 关闭详情
  closeDetail() {
    // 创建动画效果
    this.animation.opacity(0).translateY(20).step();
    
    this.setData({
      animationData: this.animation.export()
    });
    
    setTimeout(() => {
      this.animation.opacity(1).translateY(0).step();
      
      this.setData({
        showDetail: false,
        currentDetail: null,
        animationData: this.animation.export()
      });
    }, 300);
  },
  
  // 预览图片
  previewImage() {
    const { currentDetail } = this.data;
    if (currentDetail && currentDetail.imageUrl) {
      wx.previewImage({
        current: currentDetail.imageUrl,
        urls: [currentDetail.imageUrl],
        showmenu: true,
        success: () => {
          console.log('预览图片成功');
        },
        fail: (error) => {
          console.error('预览图片失败', error);
          showToast('预览图片失败');
        }
      });
    }
  },

  /**
   * 加载搜索历史
   */
  loadSearchHistory() {
    try {
      const historyWords = wx.getStorageSync('searchHistory') || [];
      this.setData({ historyWords });
    } catch (e) {
      console.error('获取搜索历史失败', e);
    }
  },
  
  /**
   * 保存搜索历史
   */
  saveSearchHistory() {
    try {
      wx.setStorageSync('searchHistory', this.data.historyWords);
    } catch (e) {
      console.error('保存搜索历史失败', e);
    }
  },

  /**
   * 添加搜索词到历史记录
   */
  addToHistory(searchValue) {
    if (!searchValue) return;

    const { historyWords } = this.data;
    const index = historyWords.indexOf(searchValue);

    if (index !== -1) {
      historyWords.splice(index, 1);
    }
    
    // 限制历史记录最多10条
    if (historyWords.length >= 10) {
      historyWords.pop();
    }
    
    historyWords.unshift(searchValue);

    this.setData({ historyWords });
    this.saveSearchHistory();
  },

  /**
   * 清空所有历史记录
   */
  clearAllHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清空所有搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({ historyWords: [] });
          this.saveSearchHistory();
        }
      }
    });
  },
  
  /**
   * 删除单条历史记录
   */
  deleteHistory(e) {
    const { index } = e.currentTarget.dataset;
    const { historyWords } = this.data;
    
    wx.showModal({
      title: '提示',
      content: '确定要删除这条搜索记录吗？',
      success: (res) => {
        if (res.confirm) {
          historyWords.splice(index, 1);
          this.setData({ historyWords });
          this.saveSearchHistory();
        }
      }
    });
  },

  /**
   * 点击历史记录项
   */
  onHistoryItemTap(e) {
    const { keyword } = e.currentTarget.dataset;
    this.setData({ value: keyword });
    this.onSearch();
  },
  
  /**
   * 设置搜索关键词
   */
  setSearchKeyword(e) {
    const { keyword } = e.currentTarget.dataset;
    this.setData({ value: keyword });
    this.onSearch();
  },

  /**
   * 返回上一页
   */
  onBack() {
    wx.navigateBack({
      delta: 1,
      fail: () => {
        wx.switchTab({ url: '/pages/home/index' });
      }
    });
  }
});
