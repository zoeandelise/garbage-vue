<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>消息队列监控</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="refreshData">刷新</el-button>
          </div>
          <el-row>
            <el-col :span="24">
              <div class="overview">
                <h2>概览</h2>
                <p>消息队列监控模块提供了实时监控消息队列状态、队列管理和错误消息处理等功能。</p>
                <p>您可以通过下面的功能链接进入相应模块。</p>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" class="card-row">
            <el-col :span="12">
              <el-card class="feature-card" shadow="hover" @click.native="goToPath('/monitor/mq/queue-status')">
                <div class="feature-icon">
                  <i class="el-icon-s-platform"></i>
                </div>
                <div class="feature-content">
                  <h3>队列状态</h3>
                  <p>查看消息队列运行状态、队列列表及统计数据</p>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card class="feature-card" shadow="hover" @click.native="goToPath('/monitor/mq/task-log')">
                <div class="feature-icon">
                  <i class="el-icon-warning"></i>
                </div>
                <div class="feature-content">
                  <h3>任务日志</h3>
                  <p>查看错误消息日志，进行重试或清理操作</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getMqStatus } from "@/api/mq/monitor";

export default {
  name: "MqMonitor",
  data() {
    return {
      // 消息队列状态
      mqStatus: {}
    };
  },
  created() {
    this.getStatusData();
  },
  methods: {
    /** 获取消息队列状态数据 */
    getStatusData() {
      getMqStatus().then(response => {
        this.mqStatus = response.data;
      });
    },
    /** 刷新数据 */
    refreshData() {
      this.getStatusData();
    },
    /** 跳转到指定路径 */
    goToPath(path) {
      this.$router.push(path);
    }
  }
};
</script>

<style scoped>
.overview {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.overview h2 {
  margin-top: 0;
  color: #303133;
}

.card-row {
  margin-top: 20px;
}

.feature-card {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  height: 150px;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 48px;
  color: #409EFF;
  margin-right: 20px;
}

.feature-content {
  flex: 1;
}

.feature-content h3 {
  margin-top: 0;
  color: #303133;
}

.feature-content p {
  color: #606266;
  margin-bottom: 0;
}
</style> 