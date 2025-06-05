<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="垃圾名称" prop="garbageName">
        <el-input v-model="form.garbageName" placeholder="请输入垃圾名称" />
      </el-form-item>
      <el-form-item label="垃圾类型" prop="garbageType">
        <el-select v-model="form.garbageType" placeholder="请选择垃圾类型">
          <el-option
            v-for="dict in garbageTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="投放建议" prop="disposalTips">
        <el-input v-model="form.disposalTips" type="textarea" placeholder="请输入投放建议" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" @change="handleRemarkChange" />
      </el-form-item>
      <el-form-item label="图片" prop="imageUrl">
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :headers="headers"
          :show-file-list="false"
          :on-success="handleImageSuccess"
          :before-upload="beforeImageUpload">
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
        <div class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getGarbageGuide, updateGarbageGuide } from "@/api/garbage/guide";
import { getToken } from "@/utils/auth";

export default {
  name: "GuideEdit",
  data() {
    return {
      // 表单参数
      form: {
        id: null,
        garbageName: null,
        garbageType: null,
        disposalTips: null,
        imageUrl: null,
        remark: null
      },
      // 图片URL
      imageUrl: '',
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
      // 上传图片地址
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      // 设置上传的请求头部
      headers: { Authorization: "Bearer " + getToken() },
      // 服务器基础URL
      baseUrl: process.env.VUE_APP_BASE_API,
      // 表单校验
      rules: {
        remark: [
          { required: true, message: "备注不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    const id = this.$route.params.id;
    this.getGuideInfo(id);
  },
  methods: {
    /** 获取垃圾分类指南详细信息 */
    getGuideInfo(id) {
      console.log("获取编辑详情，ID:", id);
      getGarbageGuide(id).then(response => {
        console.log("获取编辑详情响应:", response);
        if (response.code === 200) {
          this.form = response.data || this.form;
          // 设置图片URL
          if (this.form.imageUrl) {
            this.imageUrl = this.getImageUrl(this.form.imageUrl);
          }
          
          // 如果名称或类型为空，尝试从备注中提取
          if (!this.form.garbageName) {
            this.form.garbageName = this.getNameFromRemark(this.form.remark);
          }
          
          if (!this.form.garbageType) {
            this.form.garbageType = this.getTypeValueFromRemark(this.form.remark);
          }
          
          if (!this.form.disposalTips && this.form.garbageType) {
            this.form.disposalTips = this.disposalTipsMap[this.form.garbageType];
          }
          
          console.log("设置编辑数据:", this.form);
        } else {
          this.$message.error("获取详情失败: " + (response.msg || "未知错误"));
        }
      }).catch(error => {
        console.error("获取编辑详情出错:", error);
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
    
    // 处理备注变化
    handleRemarkChange(value) {
      // 如果名称为空，尝试从备注中提取
      if (!this.form.garbageName) {
        this.form.garbageName = this.getNameFromRemark(value);
      }
      
      // 如果类型为空，尝试从备注中提取
      if (!this.form.garbageType) {
        this.form.garbageType = this.getTypeValueFromRemark(value);
        
        // 如果投放建议为空，根据类型设置默认建议
        if (!this.form.disposalTips && this.form.garbageType) {
          this.form.disposalTips = this.disposalTipsMap[this.form.garbageType];
        }
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
    
    // 取消按钮
    cancel() {
      this.$router.push({ path: "/garbage/guide" });
    },
    
    // 表单提交
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 如果名称为空，尝试从备注中提取
          if (!this.form.garbageName) {
            this.form.garbageName = this.getNameFromRemark(this.form.remark);
          }
          
          // 如果类型为空，尝试从备注中提取
          if (!this.form.garbageType) {
            this.form.garbageType = this.getTypeValueFromRemark(this.form.remark);
          }
          
          // 如果投放建议为空，根据类型设置默认建议
          if (!this.form.disposalTips && this.form.garbageType) {
            this.form.disposalTips = this.disposalTipsMap[this.form.garbageType];
          }
          
          console.log("提交修改数据:", this.form);
          updateGarbageGuide(this.form).then(response => {
            console.log("修改响应:", response);
            this.$modal.msgSuccess("修改成功");
            this.$router.push({ path: "/garbage/guide" });
          }).catch(error => {
            console.error("修改失败:", error);
            this.$modal.msgError("修改失败: " + (error.message || JSON.stringify(error)));
          });
        }
      });
    },
    
    // 上传前图片校验
    beforeImageUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!');
      }
      if (!isLt10M) {
        this.$message.error('上传图片大小不能超过 10MB!');
      }
      
      return isJPG && isLt10M;
    },
    
    // 图片上传成功处理
    handleImageSuccess(res, file) {
      if (res.code === 200) {
        this.imageUrl = URL.createObjectURL(file.raw);
        this.form.imageUrl = res.url;
      } else {
        this.$message.error("上传图片失败: " + (res.msg || "未知错误"));
      }
    }
  }
};
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 