const app = getApp();

Component({
  data: {
    value: '', // 初始值设置为空，避免第一次加载时闪烁
    list: [
      {
        icon: 'home',
        value: 'home',
        label: '首页',
      },
      {
        icon: 'book-open',
        value: 'guide',
        label: '分类指南',
      },
      {
        icon: 'layers',
        value: 'record',
        label: '投递记录',
      },
      {
        icon: 'user',
        value: 'my',
        label: '我的',
      },
    ],
  },
  lifetimes: {
    ready() {
      const pages = getCurrentPages();
      const curPage = pages[pages.length - 1];
      if (curPage) {
        const nameRe = /pages\/(\w+)\/index/.exec(curPage.route);
        if (nameRe === null) return;
        if (nameRe[1] && nameRe) {
          this.setData({
            value: nameRe[1],
          });
        }
      }
    },
  },
  methods: {
    handleChange(e) {
      const { value } = e.detail;
      wx.switchTab({ url: `/pages/${value}/index` });
    }
  },
});
