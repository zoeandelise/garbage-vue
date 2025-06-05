<template>
  <div class="app-container garbage-record-container">
    <!-- 页面标题和介绍 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">垃圾投递记录</h2>
        <p class="page-subtitle">查询和管理用户的垃圾投递情况，支持积分核算</p>
      </div>
      <div class="header-decoration">
        <i class="el-icon-receiving header-icon"></i>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名"
            clearable
            size="small"
            prefix-icon="el-icon-user"
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
        <el-form-item label="投递时间">
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
          v-hasPermi="['garbage:record:add']"
          class="add-btn"
        >新增记录</el-button>
        <el-button
          type="success"
          icon="el-icon-edit"
          size="small"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['garbage:record:edit']"
          class="edit-btn"
        >修改</el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['garbage:record:remove']"
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

    <!-- 投递记录统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon stat-icon-total">
            <i class="el-icon-s-data"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总投递记录</div>
            <div class="stat-value">{{ total || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon stat-icon-points">
            <i class="el-icon-medal"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总积分</div>
            <div class="stat-value">{{ getTotalPoints() }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon stat-icon-weight">
            <i class="el-icon-shopping-cart-full"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总重量(kg)</div>
            <div class="stat-value">{{ getTotalWeight() }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon stat-icon-users">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">参与用户数</div>
            <div class="stat-value">{{ getUniqueUsers() }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 卡片视图 -->
    <div v-if="viewType === 'card'" class="card-view">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(item, index) in recordList" :key="index" class="card-col">
          <el-card 
            shadow="hover" 
            class="record-card"
            :class="'record-card-' + getGarbageTypeTag(item.garbageType)"
          >
            <!-- 卡片操作按钮 -->
            <div class="card-actions">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-view"
                circle
                @click="handleView(item)"
                v-hasPermi="['garbage:record:query']"
                title="查看"
              ></el-button>
              <el-button
                size="mini"
                type="success"
                icon="el-icon-edit"
                circle
                @click="handleUpdate(item)"
                v-hasPermi="['garbage:record:edit']"
                title="修改"
              ></el-button>
              <el-button
                size="mini"
                type="danger"
                icon="el-icon-delete"
                circle
                @click="handleDelete(item)"
                v-hasPermi="['garbage:record:remove']"
                title="删除"
              ></el-button>
            </div>
            
            <!-- 卡片顶部 -->
            <div class="card-header">
              <div class="user-info">
                <i class="el-icon-user-solid user-icon"></i>
                <span class="user-name">{{ item.userName }}</span>
              </div>
              <el-tag 
                :type="getGarbageTypeTag(item.garbageType)"
                effect="dark"
                size="medium"
                class="type-tag"
              >{{ item.garbageType }}</el-tag>
            </div>
            
            <!-- 卡片媒体区 -->
            <div class="card-media">
              <el-image 
                v-if="isValidImageUrl(item.photoUrl)" 
                class="card-image"
                :src="getImageUrl(item.photoUrl)" 
                :preview-src-list="[getImageUrl(item.photoUrl)]"
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
              <div class="detail-item">
                <span class="detail-label"><i class="el-icon-shopping-cart-full"></i> 重量：</span>
                <span class="detail-value weight-value">{{ item.weight }} kg</span>
              </div>
              
              <div class="detail-item">
                <span class="detail-label"><i class="el-icon-medal"></i> 积分：</span>
                <span class="detail-value points-value">{{ item.points }}</span>
                <el-tag size="mini" type="success" effect="dark" v-if="item.pointsCalculated" style="margin-left: 5px;">已计算</el-tag>
                <el-tag size="mini" type="warning" effect="dark" v-else style="margin-left: 5px;">计算中</el-tag>
              </div>
              
              <div class="detail-item">
                <span class="detail-label"><i class="el-icon-location"></i> 地点：</span>
                <span class="detail-value location-value">{{ item.location ? item.location.address : '未知地点' }}</span>
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
      <el-empty v-if="recordList.length === 0" description="暂无数据"></el-empty>
    </div>

    <!-- 表格视图 -->
    <el-card v-if="viewType === 'table'" class="table-card" shadow="hover">
      <el-table 
        v-loading="loading" 
        :data="recordList" 
        @selection-change="handleSelectionChange"
        :header-cell-style="{background:'#f8f9fb', color:'#606266'}"
        border
        stripe
        highlight-current-row
        class="record-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="100" v-if="false" />
        <el-table-column label="用户名" align="center" prop="userName" width="120">
          <template slot-scope="scope">
            <div class="user-name">
              <i class="el-icon-user-solid user-icon"></i>
              {{ scope.row.userName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="垃圾类型" align="center" prop="garbageType" width="120">
          <template slot-scope="scope">
            <el-tag 
              :type="getGarbageTypeTag(scope.row.garbageType)"
              effect="dark"
              size="medium"
              class="type-tag"
            >{{ scope.row.garbageType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="重量(kg)" align="center" prop="weight" width="100">
          <template slot-scope="scope">
            <span class="weight-value">{{ scope.row.weight }}</span>
          </template>
        </el-table-column>
        <el-table-column label="投递地点" align="center" prop="location.address" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div class="location">
              <i class="el-icon-location location-icon"></i>
              {{ scope.row.location ? scope.row.location.address : '未知地点' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" align="center" width="100">
          <template slot-scope="scope">
            <el-image 
              v-if="isValidImageUrl(scope.row.photoUrl)"
              style="width: 60px; height: 60px; border-radius: 4px; object-fit: cover;"
              :src="getImageUrl(scope.row.photoUrl)"
              :preview-src-list="[getImageUrl(scope.row.photoUrl)]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-else class="no-image">
              <i class="el-icon-picture-outline"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="积分" align="center" prop="points" width="80">
          <template slot-scope="scope">
            <span class="points-value">{{ scope.row.points }}</span>
          </template>
        </el-table-column>
        <el-table-column label="积分状态" align="center" prop="pointsCalculated" width="100">
          <template slot-scope="scope">
            <el-tag type="success" effect="dark" v-if="scope.row.pointsCalculated">已计算</el-tag>
            <el-tag type="warning" effect="dark" v-else>计算中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="投递时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-view"
              circle
              @click="handleView(scope.row)"
              v-hasPermi="['garbage:record:query']"
              title="查看"
            ></el-button>
            <el-button
              size="mini"
              type="success"
              icon="el-icon-edit"
              circle
              @click="handleUpdate(scope.row)"
              v-hasPermi="['garbage:record:edit']"
              title="修改"
            ></el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              circle
              @click="handleDelete(scope.row)"
              v-hasPermi="['garbage:record:remove']"
              title="删除"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
    </el-card>
  </div>
</template>

<script>
import { listGarbageRecords, getGarbageRecord, delRecord } from "@/api/garbage/record";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "Record",
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
      // 视图类型：card卡片，table表格
      viewType: 'card',
      // 总条数
      total: 0,
      // 垃圾投递记录表格数据
      recordList: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        garbageType: null
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询垃圾投递记录列表 */
    getList() {
      this.loading = true;
      // 使用addDateRange添加日期范围参数
      listGarbageRecords(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        if (response.code === 200 && response.data.content && response.data.content.length > 0) {
          // 如果后端返回了数据，就使用后端数据
          this.recordList = response.data.content;
          this.total = response.data.totalElements;
        } else if (response.code === 200 && response.rows) {
          // 兼容其他数据格式
          this.recordList = response.rows;
          this.total = response.total;
        } else {
          this.recordList = [];
          this.total = 0;
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userName: null,
        garbageType: null,
        weight: null,
        location: null,
        photoUrl: null,
        points: null,
        pointsCalculated: false,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
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
    // 添加日期范围
    addDateRange(params, dateRange) {
      return addDateRange(params, dateRange);
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({ path: "/garbage/record/submit" });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const recordId = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/record/edit/${recordId}` });
    },
    /** 详情按钮操作 */
    handleView(row) {
      const recordId = row.id;
      this.$router.push({ path: `/garbage/record/detail/${recordId}` });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const recordIds = row.id || this.ids;
      this.$confirm('是否确认删除垃圾投递记录?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          return delRecord(recordIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    // 获取垃圾类型对应的标签类型
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
    // 获取图片URL
    getImageUrl(imageUrl) {
      if (!imageUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // 否则拼接基础URL
      return process.env.VUE_APP_BASE_API + imageUrl;
    },
    // 验证图片URL是否有效
    isValidImageUrl(url) {
      return url && (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/'));
    },
    // 计算总积分
    getTotalPoints() {
      return this.recordList.reduce((sum, record) => sum + (record.points || 0), 0);
    },
    // 计算总重量
    getTotalWeight() {
      return this.recordList.reduce((sum, record) => sum + (record.weight || 0), 0).toFixed(2);
    },
    // 计算唯一用户数
    getUniqueUsers() {
      const uniqueUsers = new Set(this.recordList.map(record => record.userName));
      return uniqueUsers.size;
    }
  }
};
</script>

<style lang="scss" scoped>
.garbage-record-container {
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

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  margin-bottom: 20px;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 28px;
  color: white;
}

.stat-icon-total {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
}

.stat-icon-points {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
}

.stat-icon-weight {
  background: linear-gradient(135deg, #e6a23c 0%, #d39331 100%);
}

.stat-icon-users {
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.table-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.record-table {
  margin: 10px 0;
}

.user-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-icon {
  margin-right: 5px;
  color: #409eff;
}

.location {
  display: flex;
  align-items: center;
  justify-content: center;
}

.location-icon {
  margin-right: 5px;
  color: #67c23a;
}

.type-tag {
  padding: 6px 12px;
  border-radius: 4px;
}

.weight-value {
  font-weight: 500;
  color: #303133;
}

.points-value {
  font-weight: 500;
  color: #f56c6c;
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

.card-view {
  margin-bottom: 20px;
}

.card-col {
  margin-bottom: 20px;
}

.record-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  position: relative;
  border: none;
}

.record-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  
  .card-actions {
    opacity: 1;
  }
}

.record-card-success {
  border-top: 4px solid #67C23A;
}

.record-card-danger {
  border-top: 4px solid #F56C6C;
}

.record-card-warning {
  border-top: 4px solid #E6A23C;
}

.record-card-info {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-icon {
  color: #409eff;
  margin-right: 5px;
}

.user-name {
  font-weight: 500;
  color: #303133;
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

.record-card:hover .card-image {
  transform: scale(1.05);
}

.card-content {
  padding: 15px;
}

.detail-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.detail-label {
  color: #909399;
  font-size: 14px;
  margin-right: 5px;
  display: flex;
  align-items: center;
}

.detail-label i {
  margin-right: 3px;
}

.detail-value {
  color: #606266;
  font-size: 14px;
}

.location-value {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.card-footer {
  margin-top: 15px;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
}

.card-time {
  display: flex;
  align-items: center;
}

.card-time i {
  margin-right: 5px;
}
</style> 