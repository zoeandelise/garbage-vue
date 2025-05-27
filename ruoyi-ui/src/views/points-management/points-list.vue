<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分类型" prop="pointsType">
        <el-select v-model="queryParams.pointsType" placeholder="请选择积分类型" clearable size="small">
          <el-option label="投递积分" value="1" />
          <el-option label="消费积分" value="2" />
          <el-option label="系统奖励" value="3" />
          <el-option label="活动奖励" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="积分时间">
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
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['points:list:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pointsList">
      <el-table-column label="记录ID" align="center" prop="recordId" />
      <el-table-column label="用户名称" align="center" prop="userName" />
      <el-table-column label="积分变化" align="center">
        <template slot-scope="scope">
          <span :class="scope.row.changeValue > 0 ? 'text-success' : 'text-danger'">
            {{ scope.row.changeValue > 0 ? '+' : '' }}{{ scope.row.changeValue }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="变更后积分" align="center" prop="afterPoints" />
      <el-table-column label="积分类型" align="center">
        <template slot-scope="scope">
          <el-tag :type="getPointsTypeTag(scope.row.pointsType)">
            {{ getPointsTypeName(scope.row.pointsType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="积分时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看详情</el-button>
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

    <!-- 查看详情对话框 -->
    <el-dialog :title="'积分记录详情'" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="记录ID">
              <el-input v-model="form.recordId" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户ID">
              <el-input v-model="form.userId" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称">
              <el-input v-model="form.userName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="积分变化">
              <el-input v-model="form.changeValue" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="变更后积分">
              <el-input v-model="form.afterPoints" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="积分类型">
              <el-input v-model="form.pointsTypeName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="积分时间">
              <el-input v-model="form.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input type="textarea" v-model="form.description" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.relatedId">
          <el-col :span="24">
            <el-form-item label="关联记录">
              <el-input v-model="form.relatedId" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPoints, getPointsDetail, exportPoints } from "@/api/points/points";

export default {
  name: "PointsList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 积分记录表格数据
      pointsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        pointsType: undefined,
        beginTime: undefined,
        endTime: undefined
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询积分记录列表 */
    getList() {
      this.loading = true;
      listPoints(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.pointsList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
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
    /** 获取积分类型名称 */
    getPointsTypeName(type) {
      const types = {
        '1': '投递积分',
        '2': '消费积分',
        '3': '系统奖励',
        '4': '活动奖励'
      };
      return types[type] || '未知类型';
    },
    /** 获取积分类型标签样式 */
    getPointsTypeTag(type) {
      if (type === '1') return 'success';
      if (type === '2') return 'danger';
      if (type === '3') return 'warning';
      if (type === '4') return 'info';
      return 'info';
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      this.reset();
      const recordId = row.recordId;
      getPointsDetail(recordId).then(response => {
        this.form = response.data;
        this.form.pointsTypeName = this.getPointsTypeName(this.form.pointsType);
        this.open = true;
      });
    },
    // 表单重置
    reset() {
      this.form = {
        recordId: undefined,
        userId: undefined,
        userName: undefined,
        changeValue: undefined,
        afterPoints: undefined,
        pointsType: undefined,
        pointsTypeName: undefined,
        description: undefined,
        relatedId: undefined,
        createTime: undefined
      };
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.addDateRange(this.queryParams, this.dateRange);
      this.$modal.confirm('是否确认导出所有积分记录数据？').then(() => {
        this.exportLoading = true;
        return exportPoints(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}
</style> 