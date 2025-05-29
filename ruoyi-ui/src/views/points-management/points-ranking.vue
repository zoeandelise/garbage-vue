<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>积分排行榜</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="text"
              icon="el-icon-refresh"
              @click="getList"
            >刷新</el-button>
          </div>

          <el-table
            v-loading="loading"
            :data="rankingList"
            border
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="排名"
              width="80"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="userName"
              label="用户名"
              width="120"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="nickName"
              label="昵称"
              width="120"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="totalPoints"
              label="积分"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                <span class="points-value">{{ scope.row.totalPoints }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="积分等级"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag :type="getPointsLevelType(scope.row.totalPoints)">
                  {{ getPointsLevelName(scope.row.totalPoints) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>积分统计</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="text"
              icon="el-icon-refresh"
              @click="getPointsStats"
            >刷新</el-button>
          </div>
          
          <div class="stats-item">
            <div class="stats-label">总增加积分：</div>
            <div class="stats-value">{{ stats.totalPointsAdded || 0 }}</div>
          </div>
          
          <div class="stats-item">
            <div class="stats-label">总减少积分：</div>
            <div class="stats-value">{{ stats.totalPointsReduced || 0 }}</div>
          </div>
          
          <div class="stats-item">
            <div class="stats-label">净积分：</div>
            <div class="stats-value">{{ stats.netPoints || 0 }}</div>
          </div>
          
          <el-divider content-position="left">积分来源分布</el-divider>
          
          <div class="chart-container" v-if="stats.pointsBySource">
            <div ref="sourceChart" style="width: 100%; height: 300px;"></div>
          </div>
          
          <el-divider content-position="left">月度积分趋势</el-divider>
          
          <div class="chart-container" v-if="stats.pointsByMonth">
            <div ref="monthChart" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listRanking, getPointsStats } from "@/api/points/ranking";
import * as echarts from 'echarts';

export default {
  name: "PointsRanking",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 排行榜表格数据
      rankingList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 积分统计数据
      stats: {},
      // 图表实例
      sourceChart: null,
      monthChart: null
    };
  },
  created() {
    this.getList();
    this.getPointsStats();
  },
  methods: {
    /** 查询排行榜列表 */
    getList() {
      this.loading = true;
      listRanking(this.queryParams).then(response => {
        this.rankingList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取积分统计数据 */
    getPointsStats() {
      getPointsStats().then(response => {
        this.stats = response.data;
        this.$nextTick(() => {
          this.initSourceChart();
          this.initMonthChart();
        });
      });
    },
    /** 初始化积分来源分布图表 */
    initSourceChart() {
      if (!this.stats.pointsBySource) {
        return;
      }
      
      // 积分来源名称映射
      const sourceNames = {
        1: '垃圾投递',
        2: '管理员调整',
        3: '积分兑换'
      };
      
      // 准备图表数据
      const chartData = Object.entries(this.stats.pointsBySource).map(([source, points]) => {
        return {
          name: sourceNames[source] || `来源${source}`,
          value: points
        };
      });
      
      // 初始化图表
      if (this.sourceChart) {
        this.sourceChart.dispose();
      }
      
      this.sourceChart = echarts.init(this.$refs.sourceChart);
      
      // 设置图表选项
      this.sourceChart.setOption({
        title: {
          text: '积分来源分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: chartData.map(item => item.name)
        },
        series: [
          {
            name: '积分来源',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: chartData
          }
        ]
      });
    },
    /** 初始化月度积分趋势图表 */
    initMonthChart() {
      if (!this.stats.pointsByMonth) {
        return;
      }
      
      // 准备图表数据
      const months = Object.keys(this.stats.pointsByMonth).sort();
      const pointsData = months.map(month => this.stats.pointsByMonth[month]);
      
      // 初始化图表
      if (this.monthChart) {
        this.monthChart.dispose();
      }
      
      this.monthChart = echarts.init(this.$refs.monthChart);
      
      // 设置图表选项
      this.monthChart.setOption({
        title: {
          text: '月度积分趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: months
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '积分',
            type: 'line',
            data: pointsData,
            smooth: true,
            markPoint: {
              data: [
                { type: 'max', name: '最大值' },
                { type: 'min', name: '最小值' }
              ]
            }
          }
        ]
      });
    },
    /** 获取积分等级类型 */
    getPointsLevelType(points) {
      if (points >= 1000) {
        return 'danger';
      } else if (points >= 500) {
        return 'warning';
      } else if (points >= 100) {
        return 'success';
      } else {
        return 'info';
      }
    },
    /** 获取积分等级名称 */
    getPointsLevelName(points) {
      if (points >= 1000) {
        return '钻石会员';
      } else if (points >= 500) {
        return '金牌会员';
      } else if (points >= 100) {
        return '银牌会员';
      } else {
        return '普通会员';
      }
    }
  }
};
</script>

<style scoped>
.points-value {
  font-weight: bold;
  color: #409EFF;
}
.stats-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.stats-label {
  width: 100px;
  text-align: right;
  margin-right: 10px;
  color: #606266;
}
.stats-value {
  font-weight: bold;
  font-size: 18px;
  color: #409EFF;
}
.chart-container {
  margin-top: 20px;
  margin-bottom: 20px;
}
</style> 