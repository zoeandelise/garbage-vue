<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾分类指南查询</span>
      </div>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" size="small">
        <el-form-item label="垃圾名称" prop="garbageName">
          <el-input
            v-model="queryParams.garbageName"
            placeholder="请输入垃圾名称"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="垃圾类型" prop="garbageType">
          <el-select
            v-model="queryParams.garbageType"
            placeholder="请选择垃圾类型"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in garbageTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 分类指南卡片区域 -->
      <div v-loading="loading">
        <!-- 搜索结果展示 -->
        <div v-if="guideList.length > 0" class="guide-cards">
          <el-card 
            v-for="guide in guideList" 
            :key="guide.id" 
            class="guide-card"
            :body-style="{ padding: '0px' }"
            shadow="hover"
            @click.native="handleViewDetail(guide)"
          >
            <div class="guide-header" :class="getHeaderClass(guide.garbageType)">
              <h3>{{ guide.garbageName }}</h3>
              <el-tag :type="getGarbageTypeTag(guide.garbageType)">{{ guide.garbageType }}</el-tag>
            </div>
            <div class="guide-content">
              <div v-if="guide.imageUrl" class="guide-image">
                <el-image 
                  :src="guide.imageUrl" 
                  fit="cover"
                  :preview-src-list="[guide.imageUrl]">
                </el-image>
              </div>
              <div class="guide-info">
                <p class="guide-tips"><strong>投放建议：</strong>{{ guide.disposalTips }}</p>
                <p v-if="guide.remark" class="guide-remark"><strong>备注：</strong>{{ guide.remark }}</p>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 无搜索结果时显示 -->
        <el-empty 
          v-if="!loading && guideList.length === 0" 
          description="暂无数据，请尝试其他搜索条件">
        </el-empty>
        
        <!-- 分类指南汇总卡片 -->
        <div v-if="showDefaultGuides" class="type-guide-section">
          <h2 class="section-title">垃圾分类指南</h2>
          <div class="type-cards">
            <el-card 
              v-for="type in typeGuides" 
              :key="type.type" 
              class="type-card"
              :body-style="{ padding: '0px' }"
              shadow="hover"
            >
              <div class="type-header" :class="getHeaderClass(type.type)">
                <h3>{{ type.type }}</h3>
              </div>
              <div class="type-content">
                <div class="type-image">
                  <el-image :src="type.imageUrl" fit="cover"></el-image>
                </div>
                <div class="type-info">
                  <p><strong>定义：</strong>{{ type.definition }}</p>
                  <p><strong>投放要求：</strong>{{ type.requirements }}</p>
                  <p><strong>常见物品：</strong>{{ type.examples }}</p>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <pagination
        v-if="guideList.length > 0"
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
    
    <!-- 指南详情对话框 -->
    <el-dialog 
      :title="'垃圾分类详情 - ' + currentGuide.garbageName" 
      :visible.sync="dialogVisible" 
      width="600px"
      center>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="垃圾名称">{{ currentGuide.garbageName }}</el-descriptions-item>
        <el-descriptions-item label="垃圾类型">
          <el-tag :type="getGarbageTypeTag(currentGuide.garbageType)">{{ currentGuide.garbageType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投放建议">{{ currentGuide.disposalTips }}</el-descriptions-item>
        <el-descriptions-item label="备注说明" v-if="currentGuide.remark">{{ currentGuide.remark }}</el-descriptions-item>
        <el-descriptions-item label="图片" v-if="currentGuide.imageUrl">
          <el-image 
            style="width: 100%; max-height: 300px;" 
            :src="currentGuide.imageUrl" 
            :preview-src-list="[currentGuide.imageUrl]">
          </el-image>
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listGarbageGuides, searchGuide } from "@/api/garbage/guide";

export default {
  name: "GuideSearch",
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 指南列表
      guideList: [],
      // 是否显示默认分类指南
      showDefaultGuides: true,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        garbageName: undefined,
        garbageType: undefined
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 对话框显示
      dialogVisible: false,
      // 当前选中的指南
      currentGuide: {},
      // 分类指南汇总数据
      typeGuides: [
        {
          type: "可回收物",
          definition: "可回收物是指适宜回收和资源利用的生活废弃物",
          requirements: "轻投轻放；清洁干燥，避免污染；废纸尽量平整；立体包装物请清空内容物并清洁后压扁投放；有尖锐边角的，应包裹后投放",
          examples: "废纸张、废塑料、废玻璃、废金属、废织物等",
          imageUrl: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png"
        },
        {
          type: "有害垃圾",
          definition: "有害垃圾是指对人体健康或者自然环境造成直接或潜在危害的生活废弃物",
          requirements: "投放时请注意轻放；易破损的请连同包装或包裹后轻放；如果是液体，请密封后投放",
          examples: "废电池、废荧光灯管、废药品、废油漆及其容器等",
          imageUrl: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png"
        },
        {
          type: "厨余垃圾",
          definition: "厨余垃圾是指日常生活中产生的食物残渣、菜叶等易腐烂的生物质废弃物",
          requirements: "沥干水分；盛放在容器中密封投放；有包装物的厨余垃圾应将包装物去除后分类投放",
          examples: "剩菜剩饭、骨头、菜根菜叶、果皮、蛋壳、畜禽产品内脏等",
          imageUrl: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png"
        },
        {
          type: "其他垃圾",
          definition: "其他垃圾是指除可回收物、有害垃圾、厨余垃圾以外的其他生活废弃物",
          requirements: "投放前尽量沥干水分；难以辨别类别的生活垃圾投入其他垃圾容器内",
          examples: "餐巾纸、纸尿裤、烟蒂、陶瓷碎片、卫生间废纸等",
          imageUrl: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png"
        }
      ]
    };
  },
  created() {
    // 可以根据URL参数预先设置搜索条件
    const { keyword, type } = this.$route.query;
    if (keyword) {
      this.queryParams.garbageName = keyword;
      this.handleQuery();
    } else if (type) {
      this.queryParams.garbageType = type;
      this.handleQuery();
    }
  },
  methods: {
    /** 查询垃圾分类指南列表 */
    getList() {
      this.loading = true;
      listGarbageGuides(this.queryParams).then(response => {
        if (response.code === 200) {
          const data = response.data || {};
          this.guideList = data.content || [];
          this.total = data.totalElements || 0;
          this.showDefaultGuides = this.guideList.length === 0;
        } else {
          this.guideList = [];
          this.total = 0;
          this.$message.error(response.msg || '获取数据失败');
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      if (this.queryParams.garbageName) {
        // 如果有名称搜索，使用模糊搜索接口
        this.loading = true;
        searchGuide(this.queryParams).then(response => {
          if (response.code === 200) {
            const data = response.data || {};
            this.guideList = data.content || [];
            this.total = data.totalElements || 0;
            this.showDefaultGuides = this.guideList.length === 0;
          } else {
            this.guideList = [];
            this.total = 0;
            this.$message.error(response.msg || '搜索失败');
          }
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      } else {
        // 否则使用常规列表接口
        this.getList();
      }
    },
    
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        garbageName: undefined,
        garbageType: undefined
      };
      this.handleQuery();
    },
    
    /** 查看详情按钮操作 */
    handleViewDetail(guide) {
      this.currentGuide = guide;
      this.dialogVisible = true;
    },
    
    /** 获取垃圾类型对应的标签类型 */
    getGarbageTypeTag(type) {
      switch (type) {
        case "可回收物":
          return "success";
        case "有害垃圾":
          return "danger";
        case "厨余垃圾":
          return "warning";
        case "其他垃圾":
          return "info";
        default:
          return "";
      }
    },
    
    /** 获取垃圾类型对应的样式类 */
    getHeaderClass(type) {
      switch (type) {
        case "可回收物":
          return "recyclable";
        case "有害垃圾":
          return "hazardous";
        case "厨余垃圾":
          return "kitchen";
        case "其他垃圾":
          return "other";
        default:
          return "";
      }
    }
  }
};
</script>

<style scoped>
.guide-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.guide-card {
  width: calc(50% - 10px);
  transition: all 0.3s;
  cursor: pointer;
}

.guide-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.guide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  color: #fff;
}

.guide-header h3 {
  margin: 0;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.guide-content {
  display: flex;
  padding: 15px;
  min-height: 120px;
}

.guide-image {
  width: 100px;
  height: 100px;
  margin-right: 15px;
  flex-shrink: 0;
}

.guide-info {
  flex: 1;
  overflow: hidden;
}

.guide-tips, .guide-remark {
  margin: 0 0 10px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #606266;
}

.guide-remark {
  color: #909399;
  font-size: 13px;
}

/* 分类指南样式 */
.type-guide-section {
  margin: 20px 0;
}

.section-title {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  position: relative;
  padding-left: 15px;
}

.section-title:before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background-color: #409EFF;
  border-radius: 2px;
}

.type-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.type-card {
  width: calc(50% - 10px);
  transition: all 0.3s;
}

.type-header {
  padding: 12px 15px;
  color: #fff;
  text-align: center;
}

.type-header h3 {
  margin: 0;
  font-size: 18px;
}

.type-content {
  display: flex;
  padding: 15px;
  min-height: 150px;
}

.type-image {
  width: 120px;
  height: 120px;
  margin-right: 15px;
  flex-shrink: 0;
}

.type-info {
  flex: 1;
}

.type-info p {
  margin: 0 0 10px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #606266;
}

/* 垃圾类型颜色 */
.recyclable {
  background-color: #67C23A;
}

.hazardous {
  background-color: #F56C6C;
}

.kitchen {
  background-color: #E6A23C;
}

.other {
  background-color: #909399;
}

@media screen and (max-width: 768px) {
  .guide-card,
  .type-card {
    width: 100%;
  }
}
</style> 