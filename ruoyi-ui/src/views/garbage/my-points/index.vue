<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的总积分</span>
          </div>
          <div class="total-points">
            <h1>{{ totalPoints }}</h1>
            <p>可用于兑换奖励或公益捐赠</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>积分消费</span>
          </div>
          <el-form :inline="true" :model="consumeForm" ref="consumeForm" :rules="rules">
            <el-form-item label="积分数量" prop="points">
              <el-input-number v-model="consumeForm.points" :min="1" :max="totalPoints" :disabled="totalPoints <= 0"></el-input-number>
            </el-form-item>
            <el-form-item label="用途" prop="remark">
              <el-select v-model="consumeForm.remark" placeholder="请选择用途" :disabled="totalPoints <= 0">
                <el-option label="兑换礼品" value="兑换礼品"></el-option>
                <el-option label="公益捐赠" value="公益捐赠"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleConsume" :disabled="totalPoints <= 0">消费积分</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>积分记录</span>
        <el-form :inline="true" style="float: right;">
          <el-form-item label="积分类型">
            <el-select v-model="queryParams.type" placeholder="全部" clearable size="small" @change="handleQuery">
              <el-option label="增加积分" value="1"></el-option>
              <el-option label="减少积分" value="2"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <el-table v-loading="loading" :data="pointsList" style="width: 100%">
        <el-table-column prop="createTime" label="时间" width="180" align="center"></el-table-column>
        <el-table-column prop="points" label="积分变动" width="100" align="center">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.type === 1 ? '#67C23A' : '#F56C6C' }">
              {{ scope.row.type === 1 ? '+' : '-' }}{{ scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'">
              {{ scope.row.type === 1 ? '增加' : '减少' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" align="center"></el-table-column>
        <el-table-column prop="businessId" label="关联记录" width="220" align="center" :show-overflow-tooltip="true"></el-table-column>
      </el-table>
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getPointsList"
      />
    </el-card>
  </div>
</template>

<script>
import { getMyPoints, getMyTotalPoints, myConsumePoints } from "@/api/garbage/points";

export default {
  name: "MyPoints",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总积分
      totalPoints: 0,
      // 积分列表
      pointsList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null
      },
      // 消费表单
      consumeForm: {
        points: 1,
        remark: '兑换礼品'
      },
      // 表单校验
      rules: {
        points: [
          { required: true, message: "积分数量不能为空", trigger: "blur" }
        ],
        remark: [
          { required: true, message: "用途不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getTotalPoints();
    this.getPointsList();
  },
  methods: {
    // 获取总积分
    getTotalPoints() {
      getMyTotalPoints().then(response => {
        this.totalPoints = response.data;
      });
    },
    // 获取积分列表
    getPointsList() {
      this.loading = true;
      getMyPoints(this.queryParams).then(response => {
        this.pointsList = response.data.content;
        this.total = response.data.totalElements;
        this.loading = false;
      });
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getPointsList();
    },
    // 消费积分
    handleConsume() {
      this.$refs["consumeForm"].validate(valid => {
        if (valid) {
          if (this.consumeForm.points > this.totalPoints) {
            this.$message.error("积分不足");
            return;
          }
          
          myConsumePoints(this.consumeForm).then(response => {
            this.$modal.msgSuccess("积分消费成功");
            this.getTotalPoints();
            this.getPointsList();
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.total-points {
  text-align: center;
  padding: 20px 0;
}
.total-points h1 {
  font-size: 36px;
  color: #409EFF;
  margin: 0;
  padding: 0;
}
.total-points p {
  margin-top: 10px;
  color: #909399;
}
</style> 