<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾分类指南详情</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="垃圾名称">{{ guide.garbageName || getNameFromRemark(guide.remark) || '未命名' }}</el-descriptions-item>
        <el-descriptions-item label="垃圾类型">
          <el-tag :type="getGarbageTypeTag(guide.garbageType || getTypeValueFromRemark(guide.remark))">
            {{ getTypeFromRemark(guide.remark) || guide.garbageType || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投放建议" :span="2">{{ guide.disposalTips || getDisposalTipsFromType(getTypeValueFromRemark(guide.remark)) }}</el-descriptions-item>
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
          :src="getImageUrl(guide.imageUrl)"
          :preview-src-list="[getImageUrl(guide.imageUrl)]">
        </el-image>
      </div>
      
      <div v-if="guide.items && guide.items.length > 0" class="items-container">
        <h4>包含物品</h4>
        <el-tag 
          v-for="(item, index) in guide.items" 
          :key="index"
          style="margin-right: 10px; margin-bottom: 10px;">
          {{ item }}
        </el-tag>
      </div>
      
      <div v-if="guide.tips" class="tips-container">
        <h4>小贴士</h4>
        <p>{{ guide.tips }}</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getGarbageGuide } from "@/api/garbage/guide";
import { parseTime } from "@/utils/ruoyi";

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
        imageUrl: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "recyclable" },
        { label: "有害垃圾", value: "hazardous" },
        { label: "厨余垃圾", value: "kitchen" },
        { label: "其他垃圾", value: "other" }
      ],
      // 垃圾类型映射（中文名称到英文值）
      garbageTypeMap: {
        "纸巾": "other",
        "畜禽内脏": "kitchen",
        "陶瓷": "other",
        "尿不湿": "other",
        "果皮": "kitchen",
        "烟头": "other",
        "卫生纸": "other",
        "蛋壳": "kitchen",
        "菜根菜叶": "kitchen",
        "金属": "recyclable",
        "电池": "hazardous",
        "骨头": "kitchen",
        "荧光灯": "hazardous",
        "玻璃": "recyclable",
        "药品": "hazardous",
        "剩饭剩菜": "kitchen",
        "油漆": "hazardous",
        "纸张": "recyclable",
        "塑料": "recyclable",
        "纺织物": "recyclable"
      },
      // 投放建议
      disposalTipsMap: {
        "recyclable": "可回收物应清洁干燥，分类投放。纸类应保持平整；金属、塑料、玻璃应清洁后投放；纺织物应保持清洁。",
        "hazardous": "有害垃圾应专门收集，特殊处理。废电池、废灯管、废药品、废油漆等应分类投放到有害垃圾收集点。",
        "kitchen": "厨余垃圾应沥干水分后投放，包装物应去除后分类投放。果皮、菜叶、剩饭剩菜等易腐烂的食物垃圾应及时处理。",
        "other": "其他垃圾应投放到其他垃圾收集容器。尽量沥干水分，减少垃圾体积。"
      },
      // 服务器基础URL
      baseUrl: process.env.VUE_APP_BASE_API
    };
  },
  created() {
    this.guideId = this.$route.params.id;
    this.getGuideDetail();
  },
  methods: {
    /** 获取垃圾分类指南详细信息 */
    getGuideDetail() {
      console.log("获取详情，ID:", this.guideId);
      getGarbageGuide(this.guideId).then(response => {
        console.log("获取详情响应:", response);
        if (response.code === 200) {
          this.guide = response.data || this.guide;
          console.log("设置详情数据:", this.guide);
        } else {
          this.$message.error("获取详情失败: " + (response.msg || "未知错误"));
        }
      }).catch(error => {
        console.error("获取详情出错:", error);
        this.$message.error("获取详情出错: " + (error.message || JSON.stringify(error)));
      });
    },
    
    // 从备注中提取垃圾名称
    getNameFromRemark(remark) {
      if (!remark) return '';
      
      // 尝试从"XX分类指南"中提取名称
      const match = remark.match(/^(.+)分类指南$/);
      return match ? match[1] : '';
    },
    
    // 从备注中获取垃圾类型值
    getTypeValueFromRemark(remark) {
      if (!remark) return '';
      
      const name = this.getNameFromRemark(remark);
      return name ? (this.garbageTypeMap[name] || '') : '';
    },
    
    // 从备注中推断垃圾类型
    getTypeFromRemark(remark) {
      if (!remark) return '';
      
      const typeValue = this.getTypeValueFromRemark(remark);
      if (typeValue) {
        const typeOption = this.garbageTypeOptions.find(opt => opt.value === typeValue);
        return typeOption ? typeOption.label : '';
      }
      
      return '';
    },
    
    // 根据垃圾类型获取投放建议
    getDisposalTipsFromType(type) {
      return this.disposalTipsMap[type] || '';
    },
    
    // 获取垃圾类型对应的标签类型
    getGarbageTypeTag(type) {
      switch (type) {
        case "recyclable":
          return "success";
        case "hazardous":
          return "danger";
        case "kitchen":
          return "warning";
        case "other":
          return "info";
        default:
          return "info";
      }
    },
    
    // 获取完整的图片URL
    getImageUrl(imageUrl) {
      if (!imageUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // 否则拼接基础URL
      return this.baseUrl + imageUrl;
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
.items-container {
  margin-top: 20px;
}
.tips-container {
  margin-top: 20px;
}
</style> 