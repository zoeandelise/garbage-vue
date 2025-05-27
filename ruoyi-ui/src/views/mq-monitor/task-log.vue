<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="队列名称" prop="queueName">
        <el-select v-model="queryParams.queueName" placeholder="请选择队列" clearable size="small">
          <el-option
            v-for="queue in queueOptions"
            :key="queue.queueName"
            :label="queue.queueName"
            :value="queue.queueName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="错误类型" prop="errorType">
        <el-input
          v-model="queryParams.errorType"
          placeholder="请输入错误类型"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="成功" value="success" />
          <el-option label="失败" value="error" />
          <el-option label="处理中" value="processing" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理时间">
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['mq:log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['mq:log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="errorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID" align="center" prop="messageId" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="队列名称" align="center" prop="queueName" />
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'success' ? 'success' : scope.row.status === 'processing' ? 'warning' : 'danger'">
            {{ scope.row.status === 'success' ? '成功' : scope.row.status === 'processing' ? '处理中' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误类型" align="center" prop="errorType" :show-overflow-tooltip="true" />
      <el-table-column label="错误消息" align="center" prop="errorMessage" :show-overflow-tooltip="true" />
      <el-table-column label="重试次数" align="center" prop="retryCount" />
      <el-table-column label="处理时间" align="center" prop="processTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.processTime) }}</span>
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRetry(scope.row)"
            v-if="scope.row.status === 'error'"
          >重试</el-button>
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

    <!-- 错误消息详情对话框 -->
    <el-dialog :title="'错误消息详情'" :visible.sync="dialogVisible" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="消息ID">{{ errorDetail.messageId }}</el-descriptions-item>
        <el-descriptions-item label="队列名称">{{ errorDetail.queueName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="errorDetail.status === 'success' ? 'success' : errorDetail.status === 'processing' ? 'warning' : 'danger'">
            {{ errorDetail.status === 'success' ? '成功' : errorDetail.status === 'processing' ? '处理中' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="错误类型">{{ errorDetail.errorType }}</el-descriptions-item>
        <el-descriptions-item label="错误消息">{{ errorDetail.errorMessage }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ errorDetail.processTime }}</el-descriptions-item>
        <el-descriptions-item label="重试次数">{{ errorDetail.retryCount }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="center">消息内容</el-divider>

      <pre class="message-content">{{ errorDetail.messageContent }}</pre>

      <el-divider content-position="center" v-if="errorDetail.status === 'error'">错误堆栈</el-divider>

      <pre class="error-stack" v-if="errorDetail.status === 'error'">{{ errorDetail.errorStack }}</pre>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleRetry(errorDetail)" v-if="errorDetail.status === 'error'">重试消息</el-button>
        <el-button type="danger" @click="handleDelete(errorDetail)">删除消息</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getErrorMessages, 
  getErrorDetail, 
  retryMessage, 
  deleteErrorMessage,
  getQueueList,
  exportErrorMessages
} from "@/api/mq/monitor";

export default {
  name: "TaskLog",
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
      // 错误消息表格数据
      errorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 日期范围
      dateRange: [],
      // 队列选项
      queueOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        queueName: undefined,
        errorType: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined
      },
      // 详情数据
      errorDetail: {}
    };
  },
  created() {
    this.getQueueOptions();
    this.getList();
  },
  methods: {
    /** 获取队列选项 */
    getQueueOptions() {
      getQueueList().then(response => {
        this.queueOptions = response.data;
      });
    },
    /** 查询错误消息列表 */
    getList() {
      this.loading = true;
      getErrorMessages(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.errorList = response.rows;
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.messageId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      getErrorDetail(row.messageId).then(response => {
        this.errorDetail = response.data;
        this.dialogVisible = true;
      });
    },
    /** 重试消息按钮操作 */
    handleRetry(row) {
      const messageId = row.messageId;
      this.$modal.confirm('是否确认重试消息"' + messageId + '"？').then(() => {
        return retryMessage(messageId);
      }).then(() => {
        this.$modal.msgSuccess("重试消息成功");
        this.getList();
        if (this.dialogVisible) {
          this.dialogVisible = false;
        }
      }).catch(() => {});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const messageIds = row.messageId || this.ids;
      this.$modal.confirm('是否确认删除消息"' + messageIds + '"？').then(() => {
        return deleteErrorMessage(messageIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
        if (this.dialogVisible) {
          this.dialogVisible = false;
        }
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.addDateRange(this.queryParams, this.dateRange);
      this.$modal.confirm('是否确认导出所有错误消息数据？').then(() => {
        this.exportLoading = true;
        return exportErrorMessages(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.message-content, .error-stack {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.error-stack {
  color: #F56C6C;
}
</style> 