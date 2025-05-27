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
import { getGuide } from "@/api/garbage/guide";

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
      getGuide(this.guideId).then(response => {
        this.guide = response.data;
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