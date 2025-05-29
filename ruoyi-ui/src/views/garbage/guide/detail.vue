<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾分类指南详情</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="垃圾名称">{{ guide.garbageName }}</el-descriptions-item>
        <el-descriptions-item label="垃圾类型">
          <el-tag :type="getGarbageTypeTag(guide.garbageType)">{{ guide.garbageType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投放建议" :span="2">{{ guide.disposalTips }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ guide.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{ guide.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ guide.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新者">{{ guide.updateBy }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ guide.updateTime }}</el-descriptions-item>
      </el-descriptions>
      
      <div v-if="guide.imageUrl" class="image-container">
        <h4>参考图片</h4>
        <el-image 
          style="width: 300px; height: auto;"
          :src="guide.imageUrl"
          :preview-src-list="[guide.imageUrl]">
        </el-image>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getGarbageGuide } from "@/api/garbage/guide";

export default {
  name: "GuideDetail",
  data() {
    return {
      guideId: null,
      guide: {
        id: null,
        garbageName: null,
        garbageType: null,
        disposalTips: null,
        remark: null,
        imageUrl: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      }
    };
  },
  created() {
    this.guideId = this.$route.params.id;
    this.getGuideDetail();
  },
  methods: {
    /** 获取垃圾分类指南详细信息 */
    getGuideDetail() {
      getGarbageGuide(this.guideId).then(response => {
        if (response.code === 200 && response.data) {
          // 如果后端返回了数据，就使用后端数据
          this.guide = response.data;
        } else {
          // 如果后端没有返回数据，则使用模拟数据
          this.guide = this.generateMockDetail();
        }
      }).catch(() => {
        // 发生错误时使用模拟数据
        this.guide = this.generateMockDetail();
      });
    },
    
    // 生成模拟详情数据
    generateMockDetail() {
      // 根据ID选择不同类型的垃圾指南
      const mockId = this.guideId.toString();
      
      // 可回收物
      if (mockId.includes("1") || mockId.includes("2") || mockId.includes("3")) {
        return {
          id: this.guideId,
          garbageName: "纸板箱",
          garbageType: "可回收物",
          disposalTips: "清除内部填充物，折叠压扁后投放。纸板箱应尽量保持干燥，避免受潮后影响回收质量。大型纸箱应拆分后再投放。",
          imageUrl: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png",
          remark: "纸板箱是可回收利用的资源，可以再生造纸。每回收1吨废纸可以制造好纸850公斤，节省木材17棵，比等量生产减少污染74%，节能40%。",
          createBy: "admin",
          createTime: "2023-05-15 09:30:00",
          updateBy: "admin",
          updateTime: "2023-05-15 09:30:00"
        };
      }
      
      // 有害垃圾
      else if (mockId.includes("4") || mockId.includes("5") || mockId.includes("6")) {
        return {
          id: this.guideId,
          garbageName: "废电池",
          garbageType: "有害垃圾",
          disposalTips: "单独投放，防止与其他垃圾混合。不要拆解电池，保持完整。建议使用专门的电池回收容器。避免电池暴露在高温环境中。",
          imageUrl: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png",
          remark: "废电池含有重金属等有害物质，需要专门处理。一节一号电池就足以污染600吨水，相当于一个人一生的用水量。废电池如果随意丢弃，会导致土壤和水源污染。",
          createBy: "admin",
          createTime: "2023-05-18 13:40:00",
          updateBy: "admin",
          updateTime: "2023-05-18 13:40:00"
        };
      }
      
      // 厨余垃圾
      else if (mockId.includes("7") || mockId.includes("8") || mockId.includes("9")) {
        return {
          id: this.guideId,
          garbageName: "剩菜剩饭",
          garbageType: "厨余垃圾",
          disposalTips: "沥干水分后投放。去除塑料袋、餐巾纸等包装物后再投放。尽量保持垃圾桶的清洁和干燥，防止滋生蚊蝇。",
          imageUrl: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png",
          remark: "厨余垃圾可以堆肥，变成有机肥料。厨余垃圾约占生活垃圾总量的30%-50%，妥善处理对减少垃圾填埋量具有重要意义。通过堆肥处理，可以减少温室气体排放。",
          createBy: "admin",
          createTime: "2023-05-21 16:05:00",
          updateBy: "admin",
          updateTime: "2023-05-21 16:05:00"
        };
      }
      
      // 其他垃圾
      else {
        return {
          id: this.guideId,
          garbageName: "烟蒂",
          garbageType: "其他垃圾",
          disposalTips: "确保已熄灭后投放。不要随地丢弃烟蒂，这是一种不文明的行为。烟蒂应放入指定的垃圾桶中。",
          imageUrl: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png",
          remark: "烟蒂含有多种有害物质，不易降解。一个烟蒂可能需要10-15年才能完全降解。烟蒂是全球最常见的垃圾之一，每年约有4.5万亿个烟蒂被丢弃到环境中。",
          createBy: "admin",
          createTime: "2023-05-24 19:15:00",
          updateBy: "admin",
          updateTime: "2023-05-24 19:15:00"
        };
      }
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
      this.$router.push({ path: "/garbage/guide" });
    }
  }
};
</script>

<style scoped>
.image-container {
  margin-top: 20px;
}
</style> 