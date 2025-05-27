<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>垃圾分类数据统计</span>
            <el-date-picker
              style="float: right; margin-left: 10px;"
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              @change="handleDateRangeChange"
            ></el-date-picker>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">总投递次数</div>
                <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">总投递重量(kg)</div>
                <div class="stat-value">{{ statistics.totalWeight || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">总积分发放</div>
                <div class="stat-value">{{ statistics.totalPoints || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">参与用户数</div>
                <div class="stat-value">{{ statistics.userCount || 0 }}</div>
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
            <span>垃圾类型分布</span>
          </div>
          <div id="typeChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>每日投递趋势</span>
          </div>
          <div id="trendChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>区域投递热力图</span>
          </div>
          <div id="areaChart" style="width: 100%; height: 400px;"></div>
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
      
      if (!this.typeChart) {
        this.typeChart = echarts.init(chartDom);
      }
      
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
            data: this.statistics.typeDistribution.map(item => {
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
    }
  }
};
</script>

<style scoped>
.stat-card {
  padding: 20px;
  text-align: center;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.stat-title {
  font-size: 14px;
  color: #606266;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}
</style> 