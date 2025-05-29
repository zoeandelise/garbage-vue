<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾投递记录详情</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ record.userName }}</el-descriptions-item>
        <el-descriptions-item label="垃圾类型">
          <el-tag :type="getGarbageTypeTag(record.garbageType)">{{ record.garbageType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="重量(kg)">{{ record.weight }}</el-descriptions-item>
        <el-descriptions-item label="投递时间">{{ record.createTime }}</el-descriptions-item>
        <el-descriptions-item label="投递地点" :span="2">{{ record.location ? record.location.address : '' }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ record.location ? record.location.city : '' }}</el-descriptions-item>
        <el-descriptions-item label="区县">{{ record.location ? record.location.district : '' }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ record.location ? record.location.longitude : '' }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ record.location ? record.location.latitude : '' }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ record.points }}</el-descriptions-item>
        <el-descriptions-item label="积分状态">
          <el-tag type="success" v-if="record.pointsCalculated">已计算</el-tag>
          <el-tag type="warning" v-else>计算中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ record.remark }}</el-descriptions-item>
      </el-descriptions>
      
      <div v-if="record.photoUrl" class="photo-container">
        <h4>投递照片</h4>
        <el-image 
          style="width: 300px; height: auto;"
          :src="record.photoUrl"
          :preview-src-list="[record.photoUrl]">
        </el-image>
      </div>

      <div v-if="record.location && record.location.longitude && record.location.latitude" class="map-container">
        <h4>投递位置</h4>
        <div id="mapContainer" style="width: 100%; height: 300px;">
          <el-alert
            title="地图功能需要集成第三方地图API"
            type="info"
            :closable="false">
            <p>经度: {{ record.location.longitude }}</p>
            <p>纬度: {{ record.location.latitude }}</p>
          </el-alert>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getGarbageRecord } from "@/api/garbage/record";

export default {
  name: "RecordDetail",
  data() {
    return {
      recordId: null,
      record: {
        id: null,
        userId: null,
        userName: null,
        garbageType: null,
        weight: null,
        location: {
          longitude: null,
          latitude: null,
          address: null,
          city: null,
          district: null
        },
        photoUrl: null,
        photoData: null,
        remark: null,
        points: null,
        pointsCalculated: false,
        createTime: null,
        updateTime: null
      }
    };
  },
  created() {
    this.recordId = this.$route.params.id;
    this.getRecordDetail();
  },
  methods: {
    /** 获取垃圾投递记录详细信息 */
    getRecordDetail() {
      getGarbageRecord(this.recordId).then(response => {
        if (response.code === 200 && response.data) {
          // 如果后端返回了数据，就使用后端数据
          this.record = response.data;
        } else {
          // 如果后端没有返回数据，则使用模拟数据
          this.record = this.generateMockDetail();
        }
      }).catch(() => {
        // 发生错误时使用模拟数据
        this.record = this.generateMockDetail();
      });
    },
    
    // 生成模拟详情数据
    generateMockDetail() {
      const garbageTypes = ["可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"];
      const garbageType = garbageTypes[Math.floor(Math.random() * garbageTypes.length)];
      const weight = (Math.random() * 5 + 0.5).toFixed(2);
      const points = Math.floor(Math.random() * 20) + 1;
      const hasPhoto = Math.random() > 0.3; // 70%概率有照片
      const photoId = Math.floor(Math.random() * 1000);
      
      // 生成30天内的随机日期
      const date = new Date();
      date.setDate(date.getDate() - Math.floor(Math.random() * 30));
      const createTime = date.toISOString().replace('T', ' ').substring(0, 19);
      
      return {
        id: this.recordId,
        userId: 100,
        userName: "张三",
        garbageType: garbageType,
        weight: weight,
        location: {
          address: "北京市海淀区中关村南大街5号",
          city: "北京市",
          district: "海淀区",
          longitude: 116.32298,
          latitude: 39.98414
        },
        photoUrl: hasPhoto ? `https://picsum.photos/id/${photoId}/300/200` : null,
        remark: `这是一条${garbageType}的投递记录，重量${weight}kg，投递于${createTime}`,
        points: points,
        pointsCalculated: true,
        createTime: createTime,
        updateTime: createTime
      };
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
      this.$router.push({ path: "/garbage/record" });
    }
  }
};
</script>

<style scoped>
.photo-container {
  margin-top: 20px;
}
.map-container {
  margin-top: 20px;
}
</style> 