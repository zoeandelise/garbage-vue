<template>
  <div class="app-container garbage-guide-container">
    <!-- 页面标题和介绍 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">垃圾分类管理</h2>
        <p class="page-subtitle">管理垃圾分类指南数据，助力环保行动</p>
      </div>
      <div class="header-decoration">
        <i class="el-icon-guide header-icon"></i>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="80px">
        <el-form-item label="垃圾名称" prop="garbageName">
          <el-input
            v-model="queryParams.garbageName"
            placeholder="请输入垃圾名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="垃圾类型" prop="garbageType">
          <el-select v-model="queryParams.garbageType" placeholder="请选择垃圾类型" clearable size="small">
            <el-option
              v-for="dict in garbageTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            size="small"
            style="width: 240px"
            value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery" class="search-btn">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-toolbar">
      <div class="left-actions">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          @click="handleAdd"
          v-hasPermi="['garbage:guide:add']"
          class="add-btn"
        >新增分类指南</el-button>
        <el-button
          type="success"
          icon="el-icon-edit"
          size="small"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['garbage:guide:edit']"
          class="edit-btn"
        >修改</el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['garbage:guide:remove']"
          class="delete-btn"
        >删除</el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 视图切换 -->
    <div class="view-toggle">
      <el-radio-group v-model="viewType" size="small">
        <el-radio-button label="card">卡片视图</el-radio-button>
        <el-radio-button label="table">表格视图</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 卡片视图 -->
    <div v-if="viewType === 'card'" class="card-view">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(item, index) in guideList" :key="index" class="card-col">
          <el-card 
            shadow="hover" 
            class="guide-card"
            :class="'guide-card-' + getGarbageTypeTag(item.garbageType)"
          >
            <!-- 卡片操作按钮 -->
            <div class="card-actions">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-view"
                circle
                @click="handleView(item)"
                v-hasPermi="['garbage:guide:query']"
                title="查看"
              ></el-button>
              <el-button
                size="mini"
                type="success"
                icon="el-icon-edit"
                circle
                @click="handleUpdate(item)"
                v-hasPermi="['garbage:guide:edit']"
                title="修改"
              ></el-button>
              <el-button
                size="mini"
                type="danger"
                icon="el-icon-delete"
                circle
                @click="handleDelete(item)"
                v-hasPermi="['garbage:guide:remove']"
                title="删除"
              ></el-button>
            </div>
            
            <!-- 卡片媒体区 -->
            <div class="card-media">
              <el-image 
                v-if="item.imageUrl" 
                class="card-image"
                :src="getImageUrl(item.imageUrl)" 
                :preview-src-list="[getImageUrl(item.imageUrl)]"
                fit="cover"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div v-else class="no-image">
                <i class="el-icon-picture-outline"></i>
              </div>
            </div>
            
            <!-- 卡片内容区 -->
            <div class="card-content">
              <div class="card-header">
                <h3 class="card-title">{{ item.garbageName || getNameFromRemark(item.remark) || '未命名' }}</h3>
                <el-tag 
                  :type="getGarbageTypeTag(item.garbageType)"
                  effect="dark"
                  size="medium"
                  class="type-tag"
                >
                  {{ getTypeFromRemark(item.remark) || item.garbageType || '未知' }}
                </el-tag>
              </div>
              <div class="card-description" v-if="item.remark">
                {{ item.remark }}
              </div>
              <div class="card-footer">
                <span class="card-time">
                  <i class="el-icon-time"></i>
                  {{ item.createTime }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <!-- 空状态 -->
      <el-empty v-if="guideList.length === 0" description="暂无数据"></el-empty>
    </div>

    <!-- 表格视图 -->
    <el-card v-if="viewType === 'table'" class="table-card" shadow="hover">
      <el-table 
        v-loading="loading" 
        :data="guideList" 
        @selection-change="handleSelectionChange"
        :header-cell-style="{background:'#f8f9fb', color:'#606266'}"
        border
        stripe
        highlight-current-row
        class="guide-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="100" v-if="false" />
        <el-table-column label="垃圾名称" align="center" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div class="garbage-name">
              {{ scope.row.garbageName || getNameFromRemark(scope.row.remark) || '未命名' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="垃圾类型" align="center" width="120">
          <template slot-scope="scope">
            <el-tag 
              :type="getGarbageTypeTag(scope.row.garbageType)"
              effect="dark"
              size="medium"
              class="type-tag"
            >
              {{ getTypeFromRemark(scope.row.remark) || scope.row.garbageType || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column label="图片" align="center" width="100">
          <template slot-scope="scope">
            <el-image 
              v-if="scope.row.imageUrl" 
              style="width: 60px; height: 60px; border-radius: 4px; object-fit: cover;"
              :src="getImageUrl(scope.row.imageUrl)" 
              :preview-src-list="[getImageUrl(scope.row.imageUrl)]">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-else class="no-image">
              <i class="el-icon-picture-outline"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-view"
              circle
              @click="handleView(scope.row)"
              v-hasPermi="['garbage:guide:query']"
              title="查看"
            ></el-button>
            <el-button
              size="mini"
              type="success"
              icon="el-icon-edit"
              circle
              @click="handleUpdate(scope.row)"
              v-hasPermi="['garbage:guide:edit']"
              title="修改"
            ></el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              circle
              @click="handleDelete(scope.row)"
              v-hasPermi="['garbage:guide:remove']"
              title="删除"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
      
    <!-- 分页 -->
    <div class="pagination-container">
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script>
import { listGarbageGuides, getGarbageGuide, delGarbageGuide } from "@/api/garbage/guide";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "Guide",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 视图类型：card卡片，table表格
      viewType: 'card',
      // 垃圾分类指南表格数据
      guideList: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        garbageName: null,
        garbageType: null
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "recyclable" },
        { label: "有害垃圾", value: "hazardous" },
        { label: "厨余垃圾", value: "kitchen" },
        { label: "其他垃圾", value: "other" }
      ],
      // 垃圾类型映射（中文名称到英文值）
      garbageTypeMap: {
        "纸巾": "other",
        "畜禽内脏": "kitchen",
        "陶瓷": "other",
        "尿不湿": "other",
        "果皮": "kitchen",
        "烟头": "other",
        "卫生纸": "other",
        "蛋壳": "kitchen",
        "菜根菜叶": "kitchen",
        "金属": "recyclable",
        "电池": "hazardous",
        "骨头": "kitchen",
        "荧光灯": "hazardous",
        "玻璃": "recyclable",
        "药品": "hazardous",
        "剩饭剩菜": "kitchen",
        "油漆": "hazardous",
        "纸张": "recyclable",
        "塑料": "recyclable",
        "纺织物": "recyclable"
      },
      // 服务器基础URL
      baseUrl: process.env.VUE_APP_BASE_API
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询垃圾分类指南列表 */
    getList() {
      this.loading = true;
      console.log("发送查询请求，参数:", this.queryParams);
      listGarbageGuides(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        console.log("获取列表响应 (原始):", response);
        
        // 尝试检测响应的数据结构
        if (response.data && (response.data.rows || response.data.total)) {
          console.log("检测到嵌套数据结构，使用response.data");
          this.guideList = response.data.rows || [];
          this.total = response.data.total || 0;
        } else {
          console.log("使用标准响应结构");
          this.guideList = response.rows || [];
          this.total = response.total || 0;
        }
        
        console.log("处理后的数据:", this.guideList);
        console.log("总数:", this.total);
        
        this.loading = false;
      }).catch((error) => {
        console.error("获取列表失败:", error);
        this.guideList = [];
        this.total = 0;
        this.loading = false;
      });
    },
    
    // 从备注中提取垃圾名称
    getNameFromRemark(remark) {
      if (!remark) return '';
      
      // 尝试从"XX分类指南"中提取名称
      const match = remark.match(/^(.+)分类指南$/);
      return match ? match[1] : '';
    },
    
    // 从备注中推断垃圾类型
    getTypeFromRemark(remark) {
      if (!remark) return '';
      
      const name = this.getNameFromRemark(remark);
      if (name && this.garbageTypeMap[name]) {
        const typeValue = this.garbageTypeMap[name];
        const typeOption = this.garbageTypeOptions.find(opt => opt.value === typeValue);
        return typeOption ? typeOption.label : '';
      }
      
      return '';
    },
    
    // 获取垃圾类型对应的标签类型
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
          return "info"; // 默认为info类型
      }
    },
    
    // 获取完整的图片URL
    getImageUrl(imageUrl) {
      if (!imageUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // 否则拼接基础URL
      return this.baseUrl + imageUrl;
    },
    
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({ path: "/garbage/guide/add" });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/guide/edit/${id}` });
    },
    /** 查看按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/guide/detail/${id}` });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除垃圾分类指南编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delGarbageGuide(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 添加日期范围
    addDateRange(params, dateRange) {
      return addDateRange(params, dateRange);
    }
  }
};
</script>

<style lang="scss" scoped>
.garbage-guide-container {
  padding: 20px;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.page-header {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border-radius: 12px;
  padding: 24px 30px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 20px rgba(66, 185, 131, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.page-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon {
  font-size: 50px;
  opacity: 0.8;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.search-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.action-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.left-actions {
  display: flex;
  gap: 10px;
}

.view-toggle {
  margin-bottom: 20px;
  text-align: right;
}

.card-view {
  margin-bottom: 20px;
}

.card-col {
  margin-bottom: 20px;
}

.guide-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  position: relative;
  border: none;
}

.guide-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  
  .card-actions {
    opacity: 1;
  }
}

.guide-card-success {
  border-top: 4px solid #67C23A;
}

.guide-card-danger {
  border-top: 4px solid #F56C6C;
}

.guide-card-warning {
  border-top: 4px solid #E6A23C;
}

.guide-card-info {
  border-top: 4px solid #909399;
}

.card-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  gap: 5px;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 5px;
  border-radius: 4px;
}

.card-media {
  height: 180px;
  overflow: hidden;
  background-color: #f5f7fa;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.guide-card:hover .card-image {
  transform: scale(1.05);
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 36px;
  background-color: #f5f7fa;
}

.card-content {
  padding: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  height: 40px;
}

.card-footer {
  color: #909399;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.card-time {
  display: flex;
  align-items: center;
}

.card-time i {
  margin-right: 5px;
}

.table-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.guide-table {
  margin: 10px 0;
}

.type-tag {
  padding: 6px 12px;
  border-radius: 4px;
}

.garbage-name {
  font-weight: 500;
  color: #303133;
}

.no-image {
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 20px;
}

.pagination-container {
  padding: 15px 0 5px;
  text-align: right;
}

.search-btn, .reset-btn, .add-btn, .edit-btn, .delete-btn {
  border-radius: 4px;
  transition: all 0.3s;
  padding: 8px 16px;
}

.search-btn:hover, .add-btn:hover, .edit-btn:hover, .delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

/* 响应式设计 */
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
    font-size: 24px;
  }
  
  .action-toolbar {
    flex-direction: column;
    gap: 10px;
  }
  
  .left-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style> 