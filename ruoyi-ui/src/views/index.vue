<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-icon user-icon">
            <svg-icon icon-class="peoples" />
          </div>
          <div class="card-content">
            <div class="card-title">总用户数</div>
            <div class="card-value">{{ dashboardData.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-icon delivery-icon">
            <svg-icon icon-class="form" />
          </div>
          <div class="card-content">
            <div class="card-title">投递总次数</div>
            <div class="card-value">{{ dashboardData.deliveryCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-icon weight-icon">
            <svg-icon icon-class="chart" />
          </div>
          <div class="card-content">
            <div class="card-title">垃圾总重量(kg)</div>
            <div class="card-value">{{ dashboardData.totalWeight || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="card-icon points-icon">
            <svg-icon icon-class="money" />
          </div>
          <div class="card-content">
            <div class="card-title">本月积分发放</div>
            <div class="card-value">{{ dashboardData.monthlyPoints || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>垃圾类型分布</span>
          </div>
          <div id="typeChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>每日投递趋势</span>
          </div>
          <div id="trendChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>用户积分排行榜（前10名）</span>
          </div>
          <el-table :data="rankingData" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80" align="center" />
            <el-table-column prop="userName" label="用户名" width="180" />
            <el-table-column prop="points" label="积分" width="120" align="center" />
            <el-table-column prop="deliveryCount" label="投递次数" width="120" align="center" />
            <el-table-column prop="lastDeliveryTime" label="最近投递时间" :formatter="formatDate" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getGarbageStatistics, getTypeDistribution, getDailyTrend } from "@/api/garbage/statistics"
import { getUserPointsRanking } from "@/api/garbage/adminStatistics"

export default {
  name: "Dashboard",
  data() {
    return {
      dashboardData: {
        userCount: 0,
        deliveryCount: 0,
        totalWeight: 0,
        monthlyPoints: 0
      },
      typeDistribution: [],
      dailyTrend: [],
      rankingData: [],
      typeChart: null,
      trendChart: null
    };
  },
  mounted() {
    this.getStatisticsData();
    this.getTypeDistributionData();
    this.getDailyTrendData();
    this.getRankingData();
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    this.typeChart && this.typeChart.dispose();
    this.trendChart && this.trendChart.dispose();
  },
  methods: {
    getStatisticsData() {
      getGarbageStatistics().then(response => {
        if (response.code === 200) {
          this.dashboardData = response.data || {
            userCount: 0,
            deliveryCount: 0,
            totalWeight: 0,
            monthlyPoints: 0
          };
        } else {
          // 使用模拟数据
          this.dashboardData = {
            userCount: 1245,
            deliveryCount: 8976,
            totalWeight: 15432.5,
            monthlyPoints: 24680
          };
        }
      }).catch(() => {
        // 使用模拟数据
        this.dashboardData = {
          userCount: 1245,
          deliveryCount: 8976,
          totalWeight: 15432.5,
          monthlyPoints: 24680
        };
      });
    },
    getTypeDistributionData() {
      getTypeDistribution().then(response => {
        if (response.code === 200) {
          this.typeDistribution = response.data || [];
          this.initTypeChart();
        } else {
          // 使用模拟数据
          this.typeDistribution = [
            { name: '可回收物', value: 45 },
            { name: '有害垃圾', value: 10 },
            { name: '厨余垃圾', value: 30 },
            { name: '其他垃圾', value: 15 }
          ];
          this.initTypeChart();
        }
      }).catch(() => {
        // 使用模拟数据
        this.typeDistribution = [
          { name: '可回收物', value: 45 },
          { name: '有害垃圾', value: 10 },
          { name: '厨余垃圾', value: 30 },
          { name: '其他垃圾', value: 15 }
        ];
        this.initTypeChart();
      });
    },
    getDailyTrendData() {
      getDailyTrend().then(response => {
        if (response.code === 200) {
          this.dailyTrend = response.data || [];
          this.initTrendChart();
        } else {
          // 使用模拟数据
          this.dailyTrend = [
            { date: '2023-05-01', count: 42 },
            { date: '2023-05-02', count: 38 },
            { date: '2023-05-03', count: 45 },
            { date: '2023-05-04', count: 50 },
            { date: '2023-05-05', count: 55 },
            { date: '2023-05-06', count: 48 },
            { date: '2023-05-07', count: 60 }
          ];
          this.initTrendChart();
        }
      }).catch(() => {
        // 使用模拟数据
        this.dailyTrend = [
          { date: '2023-05-01', count: 42 },
          { date: '2023-05-02', count: 38 },
          { date: '2023-05-03', count: 45 },
          { date: '2023-05-04', count: 50 },
          { date: '2023-05-05', count: 55 },
          { date: '2023-05-06', count: 48 },
          { date: '2023-05-07', count: 60 }
        ];
        this.initTrendChart();
      });
    },
    getRankingData() {
      getUserPointsRanking({ limit: 10 }).then(response => {
        if (response.code === 200) {
          this.rankingData = response.data || [];
        } else {
          // 使用模拟数据
          this.rankingData = [
            { rank: 1, userName: '张三', points: 1250, deliveryCount: 125, lastDeliveryTime: '2023-05-07 14:30:45' },
            { rank: 2, userName: '李四', points: 980, deliveryCount: 98, lastDeliveryTime: '2023-05-06 09:15:22' },
            { rank: 3, userName: '王五', points: 875, deliveryCount: 87, lastDeliveryTime: '2023-05-07 16:42:18' },
            { rank: 4, userName: '赵六', points: 820, deliveryCount: 82, lastDeliveryTime: '2023-05-05 11:05:37' },
            { rank: 5, userName: '钱七', points: 760, deliveryCount: 76, lastDeliveryTime: '2023-05-07 08:50:12' }
          ];
        }
      }).catch(() => {
        // 使用模拟数据
        this.rankingData = [
          { rank: 1, userName: '张三', points: 1250, deliveryCount: 125, lastDeliveryTime: '2023-05-07 14:30:45' },
          { rank: 2, userName: '李四', points: 980, deliveryCount: 98, lastDeliveryTime: '2023-05-06 09:15:22' },
          { rank: 3, userName: '王五', points: 875, deliveryCount: 87, lastDeliveryTime: '2023-05-07 16:42:18' },
          { rank: 4, userName: '赵六', points: 820, deliveryCount: 82, lastDeliveryTime: '2023-05-05 11:05:37' },
          { rank: 5, userName: '钱七', points: 760, deliveryCount: 76, lastDeliveryTime: '2023-05-07 08:50:12' }
        ];
      });
    },
    initTypeChart() {
      const chartDom = document.getElementById('typeChart');
      if (!chartDom) return;
      
      this.typeChart = echarts.init(chartDom);
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: this.typeDistribution.map(item => item.name)
        },
        series: [
          {
            name: '垃圾类型',
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
            data: this.typeDistribution.map(item => {
              // 为不同垃圾类型设置不同颜色
              const colors = {
                '可回收物': '#67C23A',
                '有害垃圾': '#F56C6C',
                '厨余垃圾': '#E6A23C',
                '其他垃圾': '#909399'
              };
              return {
                name: item.name,
                value: item.value,
                itemStyle: {
                  color: colors[item.name] || null
                }
              };
            })
          }
        ]
      };
      
      this.typeChart.setOption(option);
    },
    initTrendChart() {
      const chartDom = document.getElementById('trendChart');
      if (!chartDom) return;
      
      this.trendChart = echarts.init(chartDom);
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: this.dailyTrend.map(item => item.date)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.dailyTrend.map(item => item.count),
            type: 'line',
            smooth: true,
            areaStyle: {
              opacity: 0.3
            },
            itemStyle: {
              color: '#409EFF'
            },
            lineStyle: {
              width: 3
            }
          }
        ]
      };
      
      this.trendChart.setOption(option);
    },
    resizeCharts() {
      this.typeChart && this.typeChart.resize();
      this.trendChart && this.trendChart.resize();
    },
    formatDate(row, column) {
      return row.lastDeliveryTime || '-';
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  
  .dashboard-card {
    height: 120px;
    display: flex;
    align-items: center;
    
    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      
      .svg-icon {
        width: 30px;
        height: 30px;
        color: #fff;
      }
      
      &.user-icon {
        background-color: #409EFF;
      }
      
      &.delivery-icon {
        background-color: #67C23A;
      }
      
      &.weight-icon {
        background-color: #E6A23C;
      }
      
      &.points-icon {
        background-color: #F56C6C;
      }
    }
    
    .card-content {
      .card-title {
        font-size: 14px;
        color: #606266;
      }
      
      .card-value {
        font-size: 24px;
        font-weight: bold;
        margin-top: 5px;
      }
    }
  }
}
</style>

