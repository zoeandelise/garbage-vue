<template>
  <div class="app-container garbage-statistics-container">
    <!-- 页面标题和介绍 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">垃圾分类统计</h2>
        <p class="page-subtitle">数据可视化分析，助力环保决策</p>
      </div>
      <div class="header-decoration">
        <i class="el-icon-data-analysis header-icon"></i>
      </div>
    </div>

    <!-- 日期筛选卡片 -->
    <el-card class="filter-card" shadow="hover">
      <el-row type="flex" justify="space-between" align="middle">
        <el-col :span="12">
          <div class="filter-title">
            <i class="el-icon-date"></i>
            <span>选择统计时间范围</span>
          </div>
        </el-col>
        <el-col :span="12" style="text-align: right;">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handleDateRangeChange"
            style="width: 350px;"
            class="date-range-picker"
          ></el-date-picker>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计概览卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-primary">
          <div class="stat-icon">
            <i class="el-icon-s-data"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总投递次数</div>
            <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-progress">
              <el-progress :percentage="getPercentage(statistics.totalCount, 2000)" :show-text="false" :stroke-width="8" color="#42b983"></el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-success">
          <div class="stat-icon">
            <i class="el-icon-shopping-cart-full"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总投递重量(kg)</div>
            <div class="stat-value">{{ statistics.totalWeight || 0 }}</div>
            <div class="stat-progress">
              <el-progress :percentage="getPercentage(statistics.totalWeight, 5000)" :show-text="false" :stroke-width="8" color="#e6a23c"></el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-warning">
          <div class="stat-icon">
            <i class="el-icon-medal"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">总积分发放</div>
            <div class="stat-value">{{ statistics.totalPoints || 0 }}</div>
            <div class="stat-progress">
              <el-progress :percentage="getPercentage(statistics.totalPoints, 10000)" :show-text="false" :stroke-width="8" color="#f56c6c"></el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-danger">
          <div class="stat-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-title">参与用户数</div>
            <div class="stat-value">{{ statistics.userCount || 0 }}</div>
            <div class="stat-progress">
              <el-progress :percentage="getPercentage(statistics.userCount, 500)" :show-text="false" :stroke-width="8" color="#409eff"></el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-header">
            <span class="chart-title">
              <i class="el-icon-pie-chart"></i>
              垃圾类型分布
            </span>
            <el-tooltip content="展示不同垃圾类型的分布比例" placement="top">
              <i class="el-icon-question info-icon"></i>
            </el-tooltip>
          </div>
          <div class="chart-loading" v-if="loading.typeDistribution">
            <el-skeleton animated :rows="5" />
          </div>
          <div id="typeChart" class="chart-container" v-else></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-header">
            <span class="chart-title">
              <i class="el-icon-trend-charts"></i>
              每日投递趋势
            </span>
            <el-tooltip content="展示每日垃圾投递量的变化趋势" placement="top">
              <i class="el-icon-question info-icon"></i>
            </el-tooltip>
          </div>
          <div class="chart-loading" v-if="loading.dailyTrend">
            <el-skeleton animated :rows="5" />
          </div>
          <div id="trendChart" class="chart-container" v-else></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card map-card" shadow="hover">
          <div slot="header" class="chart-header">
            <span class="chart-title">
              <i class="el-icon-map-location"></i>
              区域投递热力图
            </span>
            <el-tooltip content="展示不同区域垃圾投递的热力分布" placement="top">
              <i class="el-icon-question info-icon"></i>
            </el-tooltip>
          </div>
          <div class="chart-loading" v-if="loading.areaDistribution">
            <el-skeleton animated :rows="8" />
          </div>
          <div id="areaChart" class="chart-container map-container" v-else></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// 引入 ECharts 主模块
import * as echarts from 'echarts'
import { 
  getGarbageStatistics, 
  getTypeDistribution, 
  getDailyTrend, 
  getAreaDistribution 
} from "@/api/garbage/statistics";

export default {
  name: "Statistics",
  data() {
    return {
      dateRange: [],
      statistics: {
        totalCount: 0,
        totalWeight: 0,
        totalPoints: 0,
        userCount: 0,
        typeDistribution: [],
        dailyTrend: [],
        areaDistribution: []
      },
      typeChart: null,
      trendChart: null,
      areaChart: null,
      loading: {
        overview: false,
        typeDistribution: false,
        dailyTrend: false,
        areaDistribution: false
      }
    };
  },
  mounted() {
    this.getStatistics();
    // 监听窗口大小变化，调整图表大小
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    // 销毁图表实例
    this.typeChart && this.typeChart.dispose();
    this.trendChart && this.trendChart.dispose();
    this.areaChart && this.areaChart.dispose();
  },
  methods: {
    // 获取统计数据
    getStatistics() {
      this.getOverviewData();
      this.getTypeDistributionData();
      this.getDailyTrendData();
      this.getAreaDistributionData();
    },
    // 获取概览数据
    getOverviewData() {
      this.loading.overview = true;
      const params = this.getDateRangeParams();
      getGarbageStatistics(params).then(response => {
        if (response.code === 200) {
          const data = response.data || {};
          this.statistics.totalCount = data.totalCount || 0;
          this.statistics.totalWeight = data.totalWeight || 0;
          this.statistics.totalPoints = data.totalPoints || 0;
          this.statistics.userCount = data.userCount || 0;
        } else {
          this.$message.error(response.msg || '获取统计数据失败');
          // 使用模拟数据
          this.statistics.totalCount = 1256;
          this.statistics.totalWeight = 3254.5;
          this.statistics.totalPoints = 6280;
          this.statistics.userCount = 328;
        }
        this.loading.overview = false;
      }).catch(() => {
        this.loading.overview = false;
        // 使用模拟数据
        this.statistics.totalCount = 1256;
        this.statistics.totalWeight = 3254.5;
        this.statistics.totalPoints = 6280;
        this.statistics.userCount = 328;
      });
    },
    // 获取垃圾类型分布数据
    getTypeDistributionData() {
      this.loading.typeDistribution = true;
      const params = this.getDateRangeParams();
      getTypeDistribution(params).then(response => {
        if (response.code === 200) {
          this.statistics.typeDistribution = response.data || [];
          this.initTypeChart();
        } else {
          this.$message.error(response.msg || '获取垃圾类型分布数据失败');
          // 使用模拟数据
          this.statistics.typeDistribution = [
            { name: '可回收物', value: 45 },
            { name: '有害垃圾', value: 10 },
            { name: '厨余垃圾', value: 30 },
            { name: '其他垃圾', value: 15 }
          ];
          this.initTypeChart();
        }
        this.loading.typeDistribution = false;
      }).catch(() => {
        this.loading.typeDistribution = false;
        // 使用模拟数据
        this.statistics.typeDistribution = [
          { name: '可回收物', value: 45 },
          { name: '有害垃圾', value: 10 },
          { name: '厨余垃圾', value: 30 },
          { name: '其他垃圾', value: 15 }
        ];
        this.initTypeChart();
      });
    },
    // 获取每日投递趋势数据
    getDailyTrendData() {
      this.loading.dailyTrend = true;
      const params = this.getDateRangeParams();
      getDailyTrend(params).then(response => {
        if (response.code === 200) {
          this.statistics.dailyTrend = response.data || [];
          this.initTrendChart();
        } else {
          this.$message.error(response.msg || '获取每日投递趋势数据失败');
          // 使用模拟数据
          this.statistics.dailyTrend = [
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
        this.loading.dailyTrend = false;
      }).catch(() => {
        this.loading.dailyTrend = false;
        // 使用模拟数据
        this.statistics.dailyTrend = [
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
    // 获取区域投递分布数据
    getAreaDistributionData() {
      this.loading.areaDistribution = true;
      const params = this.getDateRangeParams();
      getAreaDistribution(params).then(response => {
        if (response.code === 200) {
          this.statistics.areaDistribution = response.data || [];
          this.initAreaChart();
        } else {
          this.$message.error(response.msg || '获取区域投递分布数据失败');
          // 使用模拟数据
          this.statistics.areaDistribution = [
            { name: '东城区', value: 120 },
            { name: '西城区', value: 95 },
            { name: '朝阳区', value: 180 },
            { name: '海淀区', value: 210 },
            { name: '丰台区', value: 85 },
            { name: '石景山区', value: 65 }
          ];
          this.initAreaChart();
        }
        this.loading.areaDistribution = false;
      }).catch(() => {
        this.loading.areaDistribution = false;
        // 使用模拟数据
        this.statistics.areaDistribution = [
          { name: '东城区', value: 120 },
          { name: '西城区', value: 95 },
          { name: '朝阳区', value: 180 },
          { name: '海淀区', value: 210 },
          { name: '丰台区', value: 85 },
          { name: '石景山区', value: 65 }
        ];
        this.initAreaChart();
      });
    },
    // 获取日期范围参数
    getDateRangeParams() {
      const params = {};
      if (this.dateRange && this.dateRange.length === 2) {
        params.beginTime = this.dateRange[0];
        params.endTime = this.dateRange[1];
      }
      return params;
    },
    // 初始化所有图表
    initCharts() {
      this.$nextTick(() => {
        this.initTypeChart();
        this.initTrendChart();
        this.initAreaChart();
      });
    },
    // 初始化垃圾类型分布图表
    initTypeChart() {
      const chartDom = document.getElementById('typeChart');
      if (!chartDom) return;
      
      this.typeChart = echarts.init(chartDom);
      
      const colors = ['#67C23A', '#F56C6C', '#E6A23C', '#909399'];
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: this.statistics.typeDistribution.map(item => item.name)
        },
        series: [
          {
            name: '垃圾类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
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
            data: this.statistics.typeDistribution.map((item, index) => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: colors[index % colors.length]
              }
            })),
            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDelay: function (idx) {
              return Math.random() * 200;
            }
          }
        ]
      };
      
      this.typeChart.setOption(option);
    },
    // 初始化每日投递趋势图表
    initTrendChart() {
      const chartDom = document.getElementById('trendChart');
      if (!chartDom) return;
      
      if (!this.trendChart) {
        this.trendChart = echarts.init(chartDom);
      }
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: this.statistics.dailyTrend.map(item => item.date)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.statistics.dailyTrend.map(item => item.count),
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
    // 初始化区域投递热力图
    initAreaChart() {
      const chartDom = document.getElementById('areaChart');
      if (!chartDom) return;
      
      if (!this.areaChart) {
        this.areaChart = echarts.init(chartDom);
      }
      
      // 这里应该使用实际的地图数据
      // 简化版使用柱状图代替
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: this.statistics.areaDistribution.map(item => item.name)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '投递数量',
            type: 'bar',
            data: this.statistics.areaDistribution.map(item => item.value),
            itemStyle: {
              color: function(params) {
                const colorList = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#5470c6', '#9a60b4'];
                return colorList[params.dataIndex % colorList.length];
              }
            }
          }
        ]
      };
      
      this.areaChart.setOption(option);
    },
    // 调整图表大小
    resizeCharts() {
      this.typeChart && this.typeChart.resize();
      this.trendChart && this.trendChart.resize();
      this.areaChart && this.areaChart.resize();
    },
    // 日期范围变化处理
    handleDateRangeChange() {
      this.getStatistics();
    },
    // 根据值获取百分比，用于进度条显示
    getPercentage(value, maxValue) {
      if (!value || !maxValue) return 0;
      const percentage = (value / maxValue) * 100;
      return percentage > 100 ? 100 : percentage;
    }
  }
};
</script>

<style lang="scss" scoped>
.garbage-statistics-container {
  padding: 20px;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.page-header {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border-radius: 12px;
  padding: 24px 30px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 20px rgba(66, 185, 131, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.page-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon {
  font-size: 50px;
  opacity: 0.8;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.filter-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.filter-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
}

.filter-title i {
  margin-right: 8px;
  color: #42b983;
}

.date-range-picker {
  border-radius: 4px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  height: 100%;
  transition: all 0.3s;
  margin-bottom: 20px;
  overflow: hidden;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-card-primary {
  border-left: 4px solid #42b983;
}

.stat-card-success {
  border-left: 4px solid #e6a23c;
}

.stat-card-warning {
  border-left: 4px solid #f56c6c;
}

.stat-card-danger {
  border-left: 4px solid #409eff;
}

.stat-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 48px;
  opacity: 0.1;
  color: #000;
}

.stat-info {
  position: relative;
  z-index: 1;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.stat-progress {
  margin-top: 10px;
}

.chart-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  transition: all 0.3s;
}

.chart-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.chart-header {
  display: flex;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
}

.chart-title i {
  margin-right: 8px;
  color: #42b983;
}

.info-icon {
  margin-left: 8px;
  color: #909399;
  cursor: help;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.map-container {
  height: 500px;
}

.chart-loading {
  padding: 20px;
  height: 350px;
}

.map-card .chart-loading {
  height: 500px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .page-header {
    padding: 20px;
    flex-direction: column;
    text-align: center;
  }
  
  .header-decoration {
    margin-top: 15px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .chart-container {
    height: 300px;
  }
  
  .map-container {
    height: 400px;
  }
}
</style> 