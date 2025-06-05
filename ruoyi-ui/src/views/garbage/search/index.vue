<template>
  <div class="app-container garbage-search-container">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">垃圾分类智能查询</h2>
        <p class="page-subtitle">保护环境，从正确分类开始</p>
      </div>
      <div class="header-decoration">
        <i class="el-icon-delete header-icon"></i>
      </div>
    </div>
    
    <div class="search-container">
      <div class="search-wrapper">
        <el-input
          placeholder="输入垃圾名称，例如：纸张、塑料瓶、果皮..."
          v-model="searchKeyword"
          class="search-input"
          prefix-icon="el-icon-search"
          clearable
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
        </el-input>
        <div class="search-tips">常见搜索：纸张、塑料瓶、果皮、电池</div>
      </div>
    </div>

    <div v-if="searched && !loading.search" class="action-buttons">
      <el-button type="info" icon="el-icon-back" @click="resetSearch">返回首页</el-button>
      <el-button type="primary" icon="el-icon-refresh" @click="handleSearch" :disabled="!searchKeyword">重新搜索</el-button>
    </div>

    <div v-if="loading.search" class="loading-container">
      <div class="loading-animation">
        <div class="loading-circle"></div>
        <span>正在查询中...</span>
      </div>
      <el-skeleton :rows="6" animated />
    </div>

    <transition name="fade-in">
      <div v-if="!loading.search && searchResults.length > 0" class="result-container">
        <el-card v-for="(item, index) in searchResults" :key="index" class="result-card" shadow="hover">
          <div class="result-header">
            <div class="result-title">
              <h3>{{ item.name || getNameFromRemark(item.remark) || '未知垃圾' }}</h3>
              <el-tag :type="getGarbageTypeTag(item.category)" effect="dark" size="medium" class="type-tag">
                {{ getGarbageTypeName(item.category || getCategoryFromRemark(item.remark) || 'other') }}
              </el-tag>
            </div>
          </div>
          <div class="result-content">
            <div class="result-image" v-if="item.image_url">
              <el-image 
                :src="getImageUrl(item.image_url)" 
                fit="cover"
                :preview-src-list="[getImageUrl(item.image_url)]">
                <div slot="error" class="image-error">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
            <div class="result-info">
              <div class="info-section disposal">
                <h4><i class="el-icon-s-opportunity"></i> 投放建议</h4>
                <p>{{ item.disposal_method || '暂无投放建议' }}</p>
              </div>
              
              <div v-if="item.detailed_description" class="info-section description">
                <h4><i class="el-icon-document"></i> 详细描述</h4>
                <p>{{ item.detailed_description }}</p>
              </div>
              
              <div v-if="item.tips && item.tips.length > 0" class="info-section tips">
                <h4><i class="el-icon-light-rain"></i> 小贴士</h4>
                <ul>
                  <li v-for="(tip, tipIndex) in item.tips" :key="tipIndex">{{ tip }}</li>
                </ul>
              </div>
              
              <div v-if="item.included_items && item.included_items.length > 0" class="info-section items">
                <h4><i class="el-icon-collection-tag"></i> 包含物品</h4>
                <div class="tag-container">
                  <el-tag 
                    v-for="(includedItem, itemIndex) in item.included_items" 
                    :key="itemIndex"
                    size="small"
                    effect="plain"
                    class="included-item-tag">
                    {{ includedItem }}
                  </el-tag>
                </div>
              </div>
              
              <div v-if="item.remark" class="info-section remark">
                <h4><i class="el-icon-info"></i> 备注</h4>
                <p>{{ item.remark }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </transition>

    <transition name="fade-in">
      <div v-if="!loading.search && searched && searchResults.length === 0" class="empty-container">
        <div class="empty-content">
          <i class="el-icon-search empty-icon"></i>
          <h3 class="empty-title">未找到相关垃圾分类信息</h3>
          <p class="empty-text">请尝试使用其他关键词，或点击下方常见垃圾类型</p>
          <el-button type="primary" @click="resetSearch" class="empty-button">返回首页</el-button>
        </div>
      </div>
    </transition>

    <transition name="fade-in">
      <div v-if="!loading.search && !searched" class="guide-container">
        <h3 class="section-title"><i class="el-icon-guide"></i> 垃圾分类指南</h3>
        <el-row :gutter="20">
          <el-col :xs="12" :sm="12" :md="6" :lg="6" v-for="(type, index) in garbageTypes" :key="index">
            <div class="type-card" :class="'type-card-' + type.value" @click="searchByType(type.value)">
              <div class="type-icon">
                <i :class="type.icon"></i>
              </div>
              <div class="type-content">
                <div class="type-name">{{ type.label }}</div>
                <div class="type-description">{{ type.description }}</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="category-examples">
          <h3 class="section-title"><i class="el-icon-tickets"></i> 垃圾分类示例</h3>
          <el-tabs type="border-card" class="category-tabs">
            <el-tab-pane v-for="(type, index) in garbageTypes" :key="index" :label="type.label">
              <div class="example-container">
                <div class="example-list">
                  <div class="example-item" v-for="(example, exIndex) in categoryExamples[type.value]" :key="exIndex">
                    <div class="example-icon" :class="'example-icon-' + type.value">
                      <i :class="type.icon"></i>
                    </div>
                    <div class="example-content">
                      <p>{{ example }}</p>
                    </div>
                  </div>
                </div>
                <div class="example-more">
                  <el-button type="text" @click="searchByType(type.value)">查看更多{{ type.label }}...</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="common-garbage">
          <h3 class="section-title"><i class="el-icon-collection"></i> 常见垃圾分类</h3>
          <div class="tag-cloud">
            <div 
              v-for="item in commonGarbage" 
              :key="item" 
              @click="searchByName(item)" 
              class="common-tag" 
              :class="'tag-' + (item.length % 4)">
              {{ item }}
            </div>
          </div>
        </div>
        
        <div class="tips-section">
          <h3 class="section-title"><i class="el-icon-bulb"></i> 垃圾分类小贴士</h3>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <div class="tip-card tip-card-recyclable">
                <div class="tip-icon"><i class="el-icon-s-goods"></i></div>
                <h4>可回收物</h4>
                <p>保持干净、整洁，避免污染。纸类尽量平整，金属和玻璃清洁后投放。</p>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <div class="tip-card tip-card-hazardous">
                <div class="tip-icon"><i class="el-icon-warning"></i></div>
                <h4>有害垃圾</h4>
                <p>单独投放，特殊处理。废电池、灯管、药品等应投放到专门的收集点。</p>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <div class="tip-card tip-card-kitchen">
                <div class="tip-icon"><i class="el-icon-food"></i></div>
                <h4>厨余垃圾</h4>
                <p>沥干水分后投放，包装物应去除。果皮、菜叶等易腐烂垃圾应及时处理。</p>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="quiz-section">
          <h3 class="section-title"><i class="el-icon-trophy"></i> 垃圾分类小测验</h3>
          <div class="quiz-card">
            <h4>您知道这些常见物品应该如何分类吗？</h4>
            <div class="quiz-items">
              <div class="quiz-item" v-for="(item, index) in quizItems" :key="index" @click="searchByName(item.name)">
                <span class="quiz-name">{{ item.name }}</span>
                <span class="quiz-question">是什么垃圾？</span>
                <el-button size="mini" type="primary" round>查询答案</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { searchGarbageGuides, searchGarbageGuidesByCategory } from "@/api/garbage/guide";
import request from '@/utils/request';

export default {
  name: "GarbageSearch",
  data() {
    return {
      searchKeyword: "",
      searchResults: [],
      loading: {
        search: false,
        examples: false,
        quiz: false,
        common: false
      },
      searched: false,
      garbageTypes: [
        { 
          label: "可回收物", 
          value: "recyclable", 
          icon: "el-icon-s-goods", 
          colorClass: "recyclable",
          description: "适合回收再利用的物品，如纸张、塑料、金属、玻璃等"
        },
        { 
          label: "有害垃圾", 
          value: "hazardous", 
          icon: "el-icon-warning", 
          colorClass: "hazardous",
          description: "对环境有害的垃圾，如电池、灯管、药品、油漆等"
        },
        { 
          label: "厨余垃圾", 
          value: "kitchen", 
          icon: "el-icon-food", 
          colorClass: "kitchen",
          description: "易腐烂的食物垃圾，如剩饭剩菜、果皮、菜叶等"
        },
        { 
          label: "其他垃圾", 
          value: "other", 
          icon: "el-icon-delete", 
          colorClass: "other",
          description: "除可回收物、有害垃圾、厨余垃圾外的其他生活垃圾"
        }
      ],
      commonGarbage: [
        // 可回收物
        "纸张", "报纸", "书籍", "纸板箱", "塑料瓶", "塑料玩具", "塑料桶", 
        "玻璃瓶", "玻璃杯", "易拉罐", "金属罐", "铁钉", "不锈钢餐具", 
        "旧衣物", "布料", "洗发水瓶", "牙膏盒", "酸奶盒", "饮料瓶",
        // 有害垃圾
        "电池", "充电宝", "荧光灯", "灯管", "药品", "药瓶", "油漆桶", 
        "杀虫剂", "过期化妆品", "水银温度计", "指甲油", "染发剂", "打火机", 
        // 厨余垃圾
        "果皮", "果核", "茶叶渣", "菜根菜叶", "剩饭剩菜", "骨头", "蛋壳", 
        "鱼刺", "过期食品", "咖啡渣", "动物内脏", "面包", "蛋糕", "月饼",
        // 其他垃圾
        "纸巾", "尿不湿", "烟头", "陶瓷", "一次性筷子", "牙签", "创可贴", 
        "花盆", "碎玻璃", "化妆棉", "橡皮泥", "口香糖", "灰尘", "头发"
      ],
      // 按垃圾类型分类的示例
      categoryExamples: {
        recyclable: [
          "报纸 - 平整后投放到可回收物收集点",
          "塑料瓶 - 清空内容物，压扁后回收",
          "玻璃瓶 - 清洗干净后投放，注意避免破碎",
          "旧衣物 - 干净完好的可捐赠，其余投放到专门回收箱"
        ],
        hazardous: [
          "废电池 - 投放到专门的电池回收箱",
          "过期药品 - 送至药店或医院的药品回收点",
          "荧光灯管 - 完整投放到有害垃圾收集点",
          "油漆桶 - 确保封闭好，防止残留物泄漏"
        ],
        kitchen: [
          "果皮 - 沥干水分后投放",
          "剩饭剩菜 - 去除包装后投放",
          "茶叶渣 - 沥干后投放",
          "鸡骨头 - 属于厨余垃圾，不要与其他垃圾混放"
        ],
        other: [
          "纸巾 - 使用后属于其他垃圾",
          "烟头 - 确保完全熄灭后投放",
          "尿不湿 - 包裹好后投放",
          "陶瓷碎片 - 包裹后投放，防止割伤收运人员"
        ]
      },
      // 服务器基础URL
      baseUrl: process.env.VUE_APP_BASE_API,
      // 小测验问题
      quizItems: [
        { name: "湿纸巾", category: "other" },
        { name: "废弃食用油", category: "kitchen" },
        { name: "碎玻璃", category: "other" },
        { name: "快递纸箱", category: "recyclable" },
        { name: "过期药片", category: "hazardous" },
        { name: "一次性筷子", category: "other" }
      ],
      exampleItems: [], // 分类示例
      commonItems: {
        recyclable: [], // 可回收物
        hazardous: [], // 有害垃圾
        kitchen: [], // 厨余垃圾
        other: [] // 其他垃圾
      },
      activeTab: 'examples', // 当前活跃的标签页
    };
  },
  created() {
    // 加载分类示例和测验项目
    this.loadExamples();
    this.loadQuizItems();
    this.loadCommonItems();
  },
  methods: {
    handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.$message({
          message: "请输入垃圾名称",
          type: "warning",
          showClose: true
        });
        return;
      }
      
      this.loading.search = true;
      this.searched = true;
      
      console.log("搜索关键词:", this.searchKeyword);
      
      searchGarbageGuides(this.searchKeyword)
        .then(response => {
          this.loading.search = false;
          console.log("搜索响应:", response);
          
          if (response.code === 200) {
            this.searchResults = response.data || [];
            console.log("搜索结果:", this.searchResults);
            
            if (this.searchResults.length === 0) {
              this.$message({
                message: "未找到相关垃圾分类信息",
                type: "info",
                showClose: true
              });
            }
          } else {
            this.$message({
              message: response.msg || "搜索失败",
              type: "error",
              showClose: true
            });
            this.searchResults = [];
          }
        })
        .catch(error => {
          this.loading.search = false;
          console.error("搜索出错:", error);
          
          // 尝试解析错误信息
          let errorMsg = "搜索出错，请稍后重试";
          if (error && error.message) {
            errorMsg = "搜索出错: " + error.message;
          }
          
          this.$message({
            message: errorMsg,
            type: "error",
            showClose: true,
            duration: 5000
          });
          this.searchResults = [];
        });
    },
    
    resetSearch() {
      this.searchKeyword = "";
      this.searchResults = [];
      this.searched = false;
    },
    
    searchByType(type) {
      this.searchKeyword = this.getGarbageTypeName(type);
      this.handleSearch();
    },
    
    searchByName(name) {
      this.searchKeyword = name;
      this.handleSearch();
    },
    
    getGarbageTypeName(type) {
      const typeObj = this.garbageTypes.find(item => item.value === type);
      return typeObj ? typeObj.label : type;
    },
    
    getGarbageTypeTag(type) {
      switch (type) {
        case "recyclable":
          return "success";
        case "hazardous":
          return "danger";
        case "kitchen":
          return "warning";
        case "other":
          return "info";
        default:
          return "";
      }
    },
    
    getImageUrl(imageUrl) {
      if (!imageUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // 否则拼接基础URL
      return this.baseUrl + imageUrl;
    },
    
    // 从备注中提取垃圾名称
    getNameFromRemark(remark) {
      if (!remark) return '';
      
      // 尝试从备注中提取"名称：xxx"格式的内容
      const nameMatch = remark.match(/名称[：:]\s*([^,，;；\n]+)/);
      return nameMatch ? nameMatch[1].trim() : '';
    },
    
    // 从备注中提取垃圾类型
    getCategoryFromRemark(remark) {
      if (!remark) return '';
      
      // 尝试从备注中提取"类型：xxx"格式的内容
      const typeMatch = remark.match(/类型[：:]\s*([^,，;；\n]+)/);
      if (!typeMatch) return '';
      
      const typeText = typeMatch[1].trim();
      
      // 将中文类型转换为英文值
      if (typeText.includes('可回收')) return 'recyclable';
      if (typeText.includes('有害')) return 'hazardous';
      if (typeText.includes('厨余')) return 'kitchen';
      if (typeText.includes('其他')) return 'other';
      
      return '';
    },
    
    // 为标签云生成随机效果
    tagEffect(item) {
      // 根据字符串生成一个稳定的数字
      const hash = item.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
      return hash % 2 === 0 ? 'plain' : 'dark';
    },
    
    // 为标签云生成随机类型
    tagType(item) {
      // 根据字符串生成一个稳定的数字
      const hash = item.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
      const types = ['', 'success', 'info', 'warning', 'danger'];
      return types[hash % types.length];
    },
    
    // 加载示例数据
    loadExamples() {
      this.loading.examples = true;
      
      searchGarbageGuidesByCategory('example')
        .then(response => {
          this.loading.examples = false;
          console.log("示例数据响应:", response);
          
          if (response.code === 200) {
            this.exampleItems = response.data || [];
            console.log("处理后的示例数据:", this.exampleItems);
            
            if (this.exampleItems.length === 0) {
              this.$message({
                message: "未找到分类示例数据",
                type: "info",
                showClose: true
              });
            }
          } else {
            this.$message({
              message: response.msg || "加载示例数据失败",
              type: "error",
              showClose: true
            });
            this.exampleItems = [];
          }
        })
        .catch(error => {
          this.loading.examples = false;
          console.error("加载示例数据出错:", error);
          
          this.$message({
            message: "加载示例数据失败，请稍后重试",
            type: "error",
            showClose: true
          });
          this.exampleItems = [];
        });
    },
    
    // 加载测验数据
    loadQuizItems() {
      this.loading.quiz = true;
      
      searchGarbageGuidesByCategory('quiz')
        .then(response => {
          this.loading.quiz = false;
          console.log("测验数据响应:", response);
          
          if (response.code === 200) {
            this.quizItems = response.data || [];
            console.log("处理后的测验数据:", this.quizItems);
            
            if (this.quizItems.length === 0) {
              this.$message({
                message: "未找到测验题目数据",
                type: "info",
                showClose: true
              });
            }
          } else {
            this.$message({
              message: response.msg || "加载测验数据失败",
              type: "error",
              showClose: true
            });
            this.quizItems = [];
          }
        })
        .catch(error => {
          this.loading.quiz = false;
          console.error("加载测验数据出错:", error);
          
          this.$message({
            message: "加载测验数据失败，请稍后重试",
            type: "error",
            showClose: true
          });
          this.quizItems = [];
        });
    },
    
    // 加载常见垃圾数据
    loadCommonItems() {
      const categories = ['recyclable', 'hazardous', 'kitchen', 'other'];
      const categoryNames = {
        'recyclable': '可回收物',
        'hazardous': '有害垃圾',
        'kitchen': '厨余垃圾',
        'other': '其他垃圾'
      };
      
      // 设置所有类别的加载状态
      this.loading.common = true;
      
      // 创建所有请求的数组
      const requests = categories.map(category => {
        return searchGarbageGuidesByCategory(category)
          .then(response => {
            console.log(`${categoryNames[category]}数据响应:`, response);
            
            if (response.code === 200) {
              this.commonItems[category] = response.data || [];
              console.log(`处理后的${categoryNames[category]}数据:`, this.commonItems[category]);
            } else {
              console.warn(`${categoryNames[category]}数据加载失败:`, response.msg);
              this.commonItems[category] = [];
            }
          })
          .catch(error => {
            console.error(`${categoryNames[category]}数据加载出错:`, error);
            this.commonItems[category] = [];
          });
      });
      
      // 等待所有请求完成
      Promise.all(requests)
        .then(() => {
          this.loading.common = false;
          console.log("所有常见垃圾数据加载完成");
          
          // 检查是否有数据
          const hasData = categories.some(category => this.commonItems[category].length > 0);
          if (!hasData) {
            this.$message({
              message: "未找到常见垃圾数据",
              type: "info",
              showClose: true
            });
          }
        })
        .catch(error => {
          this.loading.common = false;
          console.error("加载常见垃圾数据过程中发生错误:", error);
          
          this.$message({
            message: "加载常见垃圾数据失败，请稍后重试",
            type: "error",
            showClose: true
          });
        });
    },
    
    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab;
    }
  }
};
</script>

<style scoped>
.garbage-search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.page-header {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  color: white;
  box-shadow: 0 8px 20px rgba(66, 185, 131, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 10px 0;
  letter-spacing: 1px;
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon {
  font-size: 60px;
  opacity: 0.8;
}

.search-container {
  margin-bottom: 40px;
}

.search-wrapper {
  max-width: 700px;
  margin: 0 auto;
}

.search-input {
  width: 100%;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

.search-input >>> .el-input__inner {
  height: 55px;
  line-height: 55px;
  font-size: 16px;
  border: none;
}

.search-input >>> .el-input-group__append {
  background-color: #42b983;
  border: none;
}

.search-input >>> .el-input-group__append button {
  height: 55px;
  padding: 0 25px;
  font-size: 16px;
  border: none;
  background-color: transparent;
}

.search-tips {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.loading-container {
  padding: 40px 20px;
  text-align: center;
}

.loading-animation {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.loading-circle {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(66, 185, 131, 0.2);
  border-top-color: #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-animation span {
  color: #42b983;
  font-size: 16px;
}

.result-container {
  margin-top: 20px;
}

.result-card {
  margin-bottom: 30px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  border: none;
}

.result-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.result-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}

.result-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-title h3 {
  margin: 0;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}

.type-tag {
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 16px;
}

.result-content {
  display: flex;
  flex-wrap: wrap;
}

.result-image {
  width: 180px;
  height: 180px;
  margin-right: 30px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 40px;
}

.result-info {
  flex-grow: 1;
  min-width: 300px;
}

.info-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #ebeef5;
}

.info-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.info-section h4 {
  font-size: 18px;
  color: #303133;
  margin-top: 0;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  font-weight: 600;
}

.info-section h4 i {
  margin-right: 10px;
  color: #42b983;
}

.info-section p {
  margin: 0;
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
}

.info-section ul {
  margin: 0;
  padding-left: 25px;
  color: #606266;
}

.info-section ul li {
  margin-bottom: 8px;
  line-height: 1.6;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
}

.included-item-tag {
  margin-right: 10px;
  margin-bottom: 10px;
  padding: 6px 12px;
  border-radius: 16px;
}

.empty-container {
  padding: 80px 0;
  text-align: center;
}

.empty-content {
  max-width: 500px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 60px;
  color: #c0c4cc;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 22px;
  color: #303133;
  margin-bottom: 15px;
}

.empty-text {
  color: #909399;
  margin-bottom: 25px;
}

.empty-button {
  padding: 12px 25px;
  font-size: 16px;
  border-radius: 25px;
}

.guide-container {
  margin-top: 40px;
}

.section-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  font-weight: 600;
}

.section-title i {
  margin-right: 12px;
  color: #42b983;
  font-size: 26px;
}

.type-card {
  display: flex;
  align-items: center;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
  height: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.type-card-recyclable {
  background: linear-gradient(135deg, #67C23A 0%, #4d9e21 100%);
}

.type-card-hazardous {
  background: linear-gradient(135deg, #F56C6C 0%, #e74c3c 100%);
}

.type-card-kitchen {
  background: linear-gradient(135deg, #E6A23C 0%, #d39331 100%);
}

.type-card-other {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
}

.type-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.type-icon {
  font-size: 40px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.type-content {
  flex: 1;
}

.type-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.type-description {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.5;
}

.common-garbage {
  margin-top: 50px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.common-tag {
  margin: 10px;
  cursor: pointer;
  font-size: 15px;
  padding: 10px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.tag-0 {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
}

.tag-1 {
  background: linear-gradient(135deg, #409EFF 0%, #3a8ee6 100%);
}

.tag-2 {
  background: linear-gradient(135deg, #E6A23C 0%, #d39331 100%);
}

.tag-3 {
  background: linear-gradient(135deg, #F56C6C 0%, #e74c3c 100%);
}

.common-tag:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.tips-section {
  margin-top: 50px;
}

.tip-card {
  border-radius: 12px;
  padding: 25px;
  height: 100%;
  margin-bottom: 25px;
  text-align: center;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  color: white;
}

.tip-card-recyclable {
  background: linear-gradient(135deg, #67C23A 0%, #4d9e21 100%);
}

.tip-card-hazardous {
  background: linear-gradient(135deg, #F56C6C 0%, #e74c3c 100%);
}

.tip-card-kitchen {
  background: linear-gradient(135deg, #E6A23C 0%, #d39331 100%);
}

.tip-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.tip-icon {
  font-size: 40px;
  margin-bottom: 15px;
}

.tip-card h4 {
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 15px;
  font-weight: 600;
}

.tip-card p {
  margin: 0;
  line-height: 1.8;
  font-size: 15px;
  opacity: 0.9;
}

.fade-in-enter-active, .fade-in-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-in-enter, .fade-in-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media screen and (max-width: 768px) {
  .page-header {
    padding: 20px;
    flex-direction: column;
    text-align: center;
  }
  
  .header-decoration {
    margin-top: 15px;
  }
  
  .page-title {
    font-size: 26px;
  }
  
  .result-image {
    width: 120px;
    height: 120px;
    margin-right: 20px;
  }
  
  .result-title h3 {
    font-size: 18px;
  }
  
  .info-section h4 {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .common-tag {
    padding: 8px 15px;
    font-size: 14px;
  }
}

@media screen and (max-width: 576px) {
  .page-header {
    padding: 15px;
  }
  
  .header-icon {
    font-size: 40px;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .result-content {
    flex-direction: column;
  }
  
  .result-image {
    width: 100%;
    height: 200px;
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .search-input >>> .el-input__inner {
    height: 45px;
    line-height: 45px;
  }
  
  .search-input >>> .el-input-group__append button {
    height: 45px;
    padding: 0 15px;
  }
  
  .type-card {
    padding: 15px;
  }
  
  .type-icon {
    font-size: 30px;
  }
  
  .common-tag {
    margin: 6px;
  }
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  gap: 15px;
}

.action-buttons .el-button {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.category-examples {
  margin-top: 50px;
}

.category-tabs {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.example-container {
  padding: 15px 0;
}

.example-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.example-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  border-radius: 10px;
  background-color: #f8f9fa;
  transition: all 0.3s;
}

.example-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.example-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  flex-shrink: 0;
}

.example-icon-recyclable {
  background: linear-gradient(135deg, #67C23A 0%, #4d9e21 100%);
}

.example-icon-hazardous {
  background: linear-gradient(135deg, #F56C6C 0%, #e74c3c 100%);
}

.example-icon-kitchen {
  background: linear-gradient(135deg, #E6A23C 0%, #d39331 100%);
}

.example-icon-other {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
}

.example-content {
  flex: 1;
}

.example-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.example-more {
  text-align: center;
}

.quiz-section {
  margin-top: 50px;
  margin-bottom: 30px;
}

.quiz-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.quiz-card h4 {
  text-align: center;
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 18px;
  color: #303133;
}

.quiz-items {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.quiz-item {
  background-color: white;
  border-radius: 10px;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.quiz-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.quiz-name {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
  margin-bottom: 5px;
}

.quiz-question {
  color: #909399;
  margin-bottom: 15px;
  font-size: 14px;
}

@media screen and (max-width: 768px) {
  .example-list {
    grid-template-columns: 1fr;
  }
  
  .quiz-items {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}
</style> 