<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择积分类型" clearable size="small">
          <el-option label="增加积分" value="1" />
          <el-option label="减少积分" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
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
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pointsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" v-if="false" />
      <el-table-column label="用户名" align="center" prop="userName" width="100" />
      <el-table-column label="积分变动" align="center" prop="points" width="100">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.type === 1 ? '#67C23A' : '#F56C6C' }">
            {{ scope.row.type === 1 ? '+' : '-' }}{{ scope.row.points }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'">
            {{ scope.row.type === 1 ? '增加' : '减少' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="说明" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="关联记录" align="center" prop="businessId" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['garbage:points:query']"
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
  </div>
</template>

<script>
import { listPoints, getPoints } from "@/api/garbage/points";

export default {
  name: "Points",
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
      // 积分记录表格数据
      pointsList: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        type: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询积分记录列表 */
    getList() {
      this.loading = true;
      listPoints(this.queryParams).then(response => {
        this.pointsList = response.data.content;
        this.total = response.data.totalElements;
        this.loading = false;
      });
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
    /** 查看按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/points/record/detail/${id}` });
    }
  }
};
</script> 