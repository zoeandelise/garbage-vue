<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>积分记录详情</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ points.userName }}</el-descriptions-item>
        <el-descriptions-item label="积分变动">
          <span :style="{ color: points.pointsType === 1 ? '#67C23A' : '#F56C6C' }">
            {{ points.pointsType === 1 ? '+' : '-' }}{{ points.pointsChange }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="points.pointsType === 1 ? 'success' : 'danger'">
            {{ points.pointsType === 1 ? '获取' : '消费' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联记录">{{ points.referenceId }}</el-descriptions-item>
        <el-descriptions-item label="说明" :span="2">{{ points.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{ points.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ points.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 如果是获取积分，显示关联的垃圾投递记录 -->
      <div v-if="points.pointsType === 1 && points.referenceRecord" class="related-record">
        <h4>关联垃圾投递记录</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="垃圾类型">
            <el-tag :type="getGarbageTypeTag(points.referenceRecord.garbageType)">
              {{ points.referenceRecord.garbageType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="重量(kg)">{{ points.referenceRecord.weight }}</el-descriptions-item>
          <el-descriptions-item label="投递地点" :span="2">
            {{ points.referenceRecord.location ? points.referenceRecord.location.address : '' }}
          </el-descriptions-item>
          <el-descriptions-item label="投递时间">{{ points.referenceRecord.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getPoints } from "@/api/garbage/points";

export default {
  name: "PointsDetail",
  data() {
    return {
      pointsId: null,
      points: {
        id: null,
        userId: null,
        userName: null,
        pointsType: null,
        pointsChange: null,
        referenceId: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        referenceRecord: null
      }
    };
  },
  created() {
    this.pointsId = this.$route.params.id;
    this.getPointsDetail();
  },
  methods: {
    /** 获取积分记录详细信息 */
    getPointsDetail() {
      getPoints(this.pointsId).then(response => {
        this.points = response.data;
      });
    },
    // 获取垃圾类型对应的标签类型
    getGarbageTypeTag(type) {
      switch (type) {
        case "可回收物":
          return "success";
        case "有害垃圾":
          return "danger";
        case "厨余垃圾":
          return "warning";
        case "其他垃圾":
          return "info";
        default:
          return "";
      }
    },
    // 返回列表页
    goBack() {
      this.$router.push({ path: "/garbage/points" });
    }
  }
};
</script>

<style scoped>
.related-record {
  margin-top: 20px;
}
</style> 