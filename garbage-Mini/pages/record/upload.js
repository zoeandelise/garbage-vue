import { getGarbageCategories, uploadGarbageImage, uploadGarbageRecord } from '~/api/garbage';

Page({
  data: {
    loading: false,
    uploadLoading: false,
    imageUrl: '',
    tempImagePath: '',
    categoryList: [],
    selectedCategory: '',
    selectedCategoryName: '',
    weight: '',
    remark: ''
  },

  onLoad() {
    this.fetchCategories();
  },

  // 获取垃圾分类类别
  async fetchCategories() {
    this.setData({ loading: true });
    try {
      const res = await getGarbageCategories();
      if (res.code === 200) {
        this.setData({ 
          categoryList: res.data,
          loading: false
        });
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

  // 选择垃圾分类
  selectCategory(e) {
    const { category } = e.currentTarget.dataset;
    this.setData({
      selectedCategory: category.categoryCode,
      selectedCategoryName: category.categoryName
    });
  },

  // 选择图片
  chooseImage() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      camera: 'back',
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({
          tempImagePath: tempFilePath
        });
      }
    });
  },

  // 输入重量
  inputWeight(e) {
    this.setData({
      weight: e.detail.value
    });
  },

  // 输入备注
  inputRemark(e) {
    this.setData({
      remark: e.detail.value
    });
  },

  // 上传垃圾投递记录
  async uploadRecord() {
    const { selectedCategory, tempImagePath, weight, remark } = this.data;
    
    // 表单验证
    if (!selectedCategory) {
      wx.showToast({
        title: '请选择垃圾分类',
        icon: 'none'
      });
      return;
    }
    
    if (!tempImagePath) {
      wx.showToast({
        title: '请上传垃圾照片',
        icon: 'none'
      });
      return;
    }
    
    if (!weight) {
      wx.showToast({
        title: '请输入垃圾重量',
        icon: 'none'
      });
      return;
    }
    
    if (isNaN(weight) || parseFloat(weight) <= 0) {
      wx.showToast({
        title: '重量必须是大于0的数字',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ uploadLoading: true });
    
    try {
      // 先上传图片
      const imageRes = await uploadGarbageImage(tempImagePath);
      if (imageRes.code !== 200) {
        throw new Error(imageRes.msg || '图片上传失败');
      }
      
      const imageUrl = imageRes.data || '';
      
      // 再提交记录
      const recordRes = await uploadGarbageRecord({
        categoryCode: selectedCategory,
        imageUrl: imageUrl,
        weight: parseFloat(weight),
        remark: remark
      });
      
      if (recordRes.code === 200) {
        wx.showToast({
          title: '投递记录提交成功',
          icon: 'success'
        });
        
        // 提交成功后返回记录列表页
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        throw new Error(recordRes.msg || '提交失败');
      }
    } catch (error) {
      console.error('提交垃圾投递记录失败', error);
      wx.showToast({
        title: error.message || '提交失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ uploadLoading: false });
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
}); 