<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>消息队列状态概览</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="refreshData">刷新</el-button>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="status-card">
                <div class="status-icon">
                  <i class="el-icon-s-platform" :class="mqStatus.status === 'running' ? 'running' : 'stopped'"></i>
                </div>
                <div class="status-info">
                  <div class="status-title">消息队列状态</div>
                  <div class="status-value" :class="mqStatus.status === 'running' ? 'running' : 'stopped'">
                    {{ mqStatus.status === 'running' ? '运行中' : '已停止' }}
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card">
                <div class="status-icon">
                  <i class="el-icon-message" style="color: #409EFF;"></i>
                </div>
                <div class="status-info">
                  <div class="status-title">队列总数</div>
                  <div class="status-value">{{ mqStatus.queueCount }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card">
                <div class="status-icon">
                  <i class="el-icon-chat-dot-round" style="color: #67C23A;"></i>
                </div>
                <div class="status-info">
                  <div class="status-title">消息总数</div>
                  <div class="status-value">{{ mqStatus.messageCount }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="status-card">
                <div class="status-icon">
                  <i class="el-icon-warning" :class="mqStatus.errorCount > 0 ? 'error' : ''"></i>
                </div>
                <div class="status-info">
                  <div class="status-title">错误消息数</div>
                  <div class="status-value" :class="mqStatus.errorCount > 0 ? 'error' : ''">{{ mqStatus.errorCount }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>队列状态列表</span>
          </div>
          <el-table v-loading="loading" :data="queueList" style="width: 100%">
            <el-table-column label="队列名称" prop="queueName" align="center" />
            <el-table-column label="消息数量" prop="messageCount" align="center" />
            <el-table-column label="消费者数量" prop="consumerCount" align="center" />
            <el-table-column label="状态" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
                  {{ scope.row.status === 'active' ? '活跃' : '非活跃' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="150">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewQueueDetail(scope.row)"
                >查看详情</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="clearQueue(scope.row)"
                  v-if="scope.row.messageCount > 0"
                >清空队列</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>消息处理统计</span>
          </div>
          <div class="chart-container">
            <div id="messageChart" style="width: 100%; height: 350px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 队列详情对话框 -->
    <el-dialog :title="'队列详情 - ' + queueDetail.queueName" :visible.sync="queueDialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="队列名称">{{ queueDetail.queueName }}</el-descriptions-item>
        <el-descriptions-item label="消息数量">{{ queueDetail.messageCount }}</el-descriptions-item>
        <el-descriptions-item label="消费者数量">{{ queueDetail.consumerCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="queueDetail.status === 'active' ? 'success' : 'danger'">
            {{ queueDetail.status === 'active' ? '活跃' : '非活跃' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ queueDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后活跃时间">{{ queueDetail.lastActiveTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="center">消息处理统计</el-divider>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-value">{{ queueDetail.processedCount }}</div>
            <div class="stat-title">已处理消息</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-value">{{ queueDetail.pendingCount }}</div>
            <div class="stat-title">待处理消息</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-value">{{ queueDetail.errorCount }}</div>
            <div class="stat-title">错误消息</div>
          </div>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button @click="queueDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="restartQueue(queueDetail)" v-if="queueDetail.status !== 'active'">重启队列</el-button>
        <el-button type="danger" @click="clearQueue(queueDetail)" v-if="queueDetail.messageCount > 0">清空队列</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { 
  getMqStatus, 
  getQueueList, 
  getQueueDetail, 
  clearQueue, 
  restartQueue
} from "@/api/mq/monitor";

export default {
  name: "QueueStatus",
  data() {
    return {
      // 加载状态
      loading: true,
      // 消息队列状态
      mqStatus: {
        status: 'running',
        queueCount: 0,
        messageCount: 0,
        errorCount: 0
      },
      // 队列列表
      queueList: [],
      // 队列详情对话框
      queueDialogVisible: false,
      queueDetail: {},
      // 图表实例
      messageChart: null
    };
  },
  created() {
    this.getStatusData();
    this.getQueueData();
  },
  mounted() {
    this.$nextTick(() => {
      this.initMessageChart();
      window.addEventListener('resize', this.resizeChart);
    });
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart);
    if (this.messageChart) {
      this.messageChart.dispose();
    }
  },
  methods: {
    /** 获取消息队列状态数据 */
    getStatusData() {
      getMqStatus().then(response => {
        this.mqStatus = response.data;
      });
    },
    /** 获取队列列表数据 */
    getQueueData() {
      this.loading = true;
      getQueueList().then(response => {
        this.queueList = response.data;
        this.loading = false;
        this.updateChartData();
      });
    },
    /** 刷新数据 */
    refreshData() {
      this.getStatusData();
      this.getQueueData();
    },
    /** 查看队列详情 */
    viewQueueDetail(row) {
      getQueueDetail(row.queueName).then(response => {
        this.queueDetail = response.data;
        this.queueDialogVisible = true;
      });
    },
    /** 清空队列 */
    clearQueue(row) {
      this.$modal.confirm('是否确认清空队列"' + row.queueName + '"中的所有消息？').then(() => {
        return clearQueue(row.queueName);
      }).then(() => {
        this.$modal.msgSuccess("清空队列成功");
        this.refreshData();
        if (this.queueDialogVisible) {
          this.queueDialogVisible = false;
        }
      }).catch(() => {});
    },
    /** 重启队列 */
    restartQueue(row) {
      this.$modal.confirm('是否确认重启队列"' + row.queueName + '"？').then(() => {
        return restartQueue(row.queueName);
      }).then(() => {
        this.$modal.msgSuccess("重启队列成功");
        this.refreshData();
        this.queueDialogVisible = false;
      }).catch(() => {});
    },
    /** 初始化消息处理图表 */
    initMessageChart() {
      this.messageChart = echarts.init(document.getElementById('messageChart'));
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['已处理', '待处理', '错误']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '已处理',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: [],
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '待处理',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: [],
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '错误',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: [],
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      };
      this.messageChart.setOption(option);
    },
    /** 更新图表数据 */
    updateChartData() {
      if (!this.messageChart) {
        return;
      }
      
      const queueNames = this.queueList.map(queue => queue.queueName);
      const processedData = this.queueList.map(queue => queue.processedCount || 0);
      const pendingData = this.queueList.map(queue => queue.pendingCount || 0);
      const errorData = this.queueList.map(queue => queue.errorCount || 0);
      
      this.messageChart.setOption({
        xAxis: {
          data: queueNames
        },
        series: [
          {
            name: '已处理',
            data: processedData
          },
          {
            name: '待处理',
            data: pendingData
          },
          {
            name: '错误',
            data: errorData
          }
        ]
      });
    },
    /** 调整图表大小 */
    resizeChart() {
      if (this.messageChart) {
        this.messageChart.resize();
      }
    }
  }
};
</script>

<style scoped>
.status-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 4px;
  background-color: #f9f9f9;
  height: 100px;
}

.status-icon {
  font-size: 48px;
  margin-right: 20px;
}

.status-icon i {
  color: #909399;
}

.status-icon i.running {
  color: #67C23A;
}

.status-icon i.stopped {
  color: #F56C6C;
}

.status-icon i.error {
  color: #F56C6C;
}

.status-info {
  flex: 1;
}

.status-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.status-value.running {
  color: #67C23A;
}

.status-value.stopped {
  color: #F56C6C;
}

.status-value.error {
  color: #F56C6C;
}

.chart-container {
  position: relative;
  width: 100%;
  height: 350px;
}

.stat-card {
  text-align: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #606266;
}
</style> 