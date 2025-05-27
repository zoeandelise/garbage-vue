<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>我的投递记录</span>
        <el-button
          style="float: right; margin-left: 10px;"
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleAdd"
        >新增投递</el-button>
        <el-form :inline="true" style="float: right;">
          <el-form-item label="垃圾类型">
            <el-select v-model="queryParams.garbageType" placeholder="全部" clearable size="small" @change="handleQuery">
              <el-option
                v-for="dict in garbageTypeOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table v-loading="loading" :data="recordList" style="width: 100%">
        <el-table-column label="垃圾类型" align="center" prop="garbageType" width="100">
          <template slot-scope="scope">
            <el-tag :type="getGarbageTypeTag(scope.row.garbageType)">{{ scope.row.garbageType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="重量(kg)" align="center" prop="weight" width="100" />
        <el-table-column label="投递地点" align="center" prop="location.address" :show-overflow-tooltip="true" />
        <el-table-column label="照片" align="center" width="80">
          <template slot-scope="scope">
            <el-image 
              v-if="scope.row.photoUrl" 
              style="width: 50px; height: 50px"
              :src="scope.row.photoUrl" 
              :preview-src-list="[scope.row.photoUrl]">
            </el-image>
            <span v-else>无照片</span>
          </template>
        </el-table-column>
        <el-table-column label="积分" align="center" prop="points" width="80">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.pointsCalculated">{{ scope.row.points }}</el-tag>
            <el-tag type="info" v-else>计算中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="投递时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
    
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>本月投递次数</span>
          </div>
          <div class="stat-value">{{ stats.monthCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>本月累计重量(kg)</span>
          </div>
          <div class="stat-value">{{ stats.monthWeight || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>累计投递次数</span>
          </div>
          <div class="stat-value">{{ stats.totalCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>累计获得积分</span>
          </div>
          <div class="stat-value">{{ stats.totalPoints || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getMyRecords } from "@/api/garbage/record";

export default {
  name: "MyRecord",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 垃圾投递记录表格数据
      recordList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        garbageType: null
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 统计数据
      stats: {
        monthCount: 0,
        monthWeight: 0,
        totalCount: 0,
        totalPoints: 0
      }
    };
  },
  created() {
    this.getList();
    this.calculateStats();
  },
  methods: {
    /** 查询我的垃圾投递记录列表 */
    getList() {
      this.loading = true;
      getMyRecords(this.queryParams).then(response => {
        this.recordList = response.data.content;
        this.total = response.data.totalElements;
        this.loading = false;
        
        // 计算统计数据
        this.calculateStats();
      });
    },
    // 计算统计数据
    calculateStats() {
      // 在实际应用中，可以从后端获取统计数据
      // 这里简单模拟一下
      this.stats = {
        monthCount: Math.floor(Math.random() * 20) + 1,
        monthWeight: (Math.random() * 10 + 1).toFixed(1),
        totalCount: Math.floor(Math.random() * 100) + 1,
        totalPoints: Math.floor(Math.random() * 500) + 50
      };
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
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({ path: "/garbage/record/add" });
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.$router.push({ path: `/garbage/record/detail/${row.id}` });
    }
  }
};
</script>

<style scoped>
.stat-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  color: #409EFF;
  padding: 10px 0;
}
</style> 