<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>积分数据初始化</span>
      </div>
      <el-form :model="form" ref="form" label-width="120px">
        <el-form-item label="每个用户记录数">
          <el-input-number v-model="form.count" :min="1" :max="100" :step="1" step-strictly></el-input-number>
          <span class="tips">每个用户生成的积分记录数量（1-100）</span>
        </el-form-item>
        <el-form-item label="清除现有数据">
          <el-switch v-model="form.clear"></el-switch>
          <span class="tips">是否清除现有积分记录数据（谨慎操作）</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">生成数据</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-divider></el-divider>

    <el-card class="box-card" v-if="result.success">
      <div slot="header" class="clearfix">
        <span>初始化结果</span>
      </div>
      <div class="result-info">
        <el-row>
          <el-col :span="8">
            <div class="result-item">
              <div class="result-label">总记录数：</div>
              <div class="result-value">{{ result.data.count }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="result-label">用户数量：</div>
              <div class="result-value">{{ result.data.userCount }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="result-label">每用户记录数：</div>
              <div class="result-value">{{ result.data.recordsPerUser }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
import { initPointsData } from "@/api/tool/data-init";

export default {
  name: "DataInit",
  data() {
    return {
      // 表单参数
      form: {
        count: 20,
        clear: false
      },
      // 加载状态
      loading: false,
      // 初始化结果
      result: {
        success: false,
        data: {}
      }
    };
  },
  methods: {
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$confirm(
            '是否确认生成积分数据？' + (this.form.clear ? '将清除现有数据！' : ''),
            "警告",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning"
            }
          ).then(() => {
            this.loading = true;
            initPointsData(this.form).then(response => {
              this.$modal.msgSuccess("数据初始化成功");
              this.result.success = true;
              this.result.data = response.data;
              this.loading = false;
            }).catch(() => {
              this.loading = false;
            });
          });
        }
      });
    },
    /** 重置按钮 */
    resetForm() {
      this.form = {
        count: 20,
        clear: false
      };
      this.result = {
        success: false,
        data: {}
      };
    }
  }
};
</script>

<style scoped>
.tips {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
.result-info {
  padding: 20px 0;
}
.result-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.result-label {
  width: 100px;
  text-align: right;
  margin-right: 10px;
  color: #606266;
}
.result-value {
  font-weight: bold;
  font-size: 18px;
  color: #409EFF;
}
</style> 