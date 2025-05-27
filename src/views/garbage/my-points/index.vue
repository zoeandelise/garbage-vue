<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的积分</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="points-card">
                <div class="points-title">当前总积分</div>
                <div class="points-value">{{ totalPoints }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="points-card">
                <div class="points-title">本月获得积分</div>
                <div class="points-value">{{ monthlyPoints }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="points-card">
                <div class="points-title">累计获得积分</div>
                <div class="points-value">{{ totalEarnedPoints }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>积分记录</span>
            <el-button
              style="float: right; margin-left: 10px;"
              type="primary"
              size="small"
              @click="showConsumeDialog"
            >消费积分</el-button>
          </div>
          <el-table
            v-loading="loading"
            :data="pointsList"
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
              width="60"
            />
            <el-table-column
              prop="pointsChange"
              label="积分变动"
              width="100"
            >
              <template slot-scope="scope">
                <span :class="{ 'text-success': scope.row.pointsType === 1, 'text-danger': scope.row.pointsType === 2 }">
                  {{ scope.row.pointsType === 1 ? '+' : '-' }}{{ scope.row.pointsChange }}
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="时间"
              width="160"
            />
            <el-table-column
              prop="remark"
              label="备注"
            />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listMyPoints, getPoints } from "@/api/garbage/points";

export default {
  name: "MyPoints",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 积分列表
      pointsList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 消费积分对话框显示状态
      consumeDialogVisible: false,
      // 当前积分
      currentPoints: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询积分列表 */
    getList() {
      this.loading = true;
      listMyPoints(this.queryParams).then(response => {
        if (response.code === 200) {
          const data = response.data || {};
          this.pointsList = data.content || [];
          this.total = data.totalElements || 0;
        } else {
          this.pointsList = [];
          this.total = 0;
          this.$message.error(response.msg || '获取数据失败');
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 消费积分 */
    showConsumeDialog(row) {
      this.currentPoints = { ...row };
      this.consumeDialogVisible = true;
    }
  }
};
</script> 