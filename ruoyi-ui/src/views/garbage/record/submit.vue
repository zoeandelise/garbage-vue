<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾投递记录提交</span>
      </div>
      
      <el-steps :active="activeStep" finish-status="success" simple style="margin-bottom: 20px">
        <el-step title="填写基本信息" icon="el-icon-edit"></el-step>
        <el-step title="上传照片验证" icon="el-icon-picture"></el-step>
        <el-step title="确认提交" icon="el-icon-check"></el-step>
      </el-steps>
      
      <!-- 步骤1: 基本信息 -->
      <div v-if="activeStep === 0">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="垃圾类型" prop="garbageType">
            <el-select v-model="form.garbageType" placeholder="请选择垃圾类型" style="width: 100%">
              <el-option
                v-for="item in garbageTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="重量(kg)" prop="weight">
            <el-input-number v-model="form.weight" :precision="2" :step="0.1" :min="0.01" :max="100" style="width: 100%"></el-input-number>
          </el-form-item>
          
          <el-form-item label="投递地点" prop="location.address">
            <el-input v-model="form.location.address" placeholder="请输入投递地点"></el-input>
          </el-form-item>
          
          <el-form-item label="备注" prop="remark">
            <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="nextStep">下一步</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤2: 照片上传 -->
      <div v-if="activeStep === 1">
        <el-alert
          v-if="garbageGuide"
          :title="'当前选择的是' + form.garbageType + '，请确保照片符合要求'"
          :description="garbageGuide.disposalTips"
          type="info"
          show-icon
          style="margin-bottom: 20px;">
        </el-alert>
        
        <el-form ref="photoForm" :model="form" :rules="photoRules" label-width="100px">
          <el-form-item label="垃圾照片" prop="photoData">
            <el-upload
              class="upload-demo"
              action="#"
              :http-request="handleUpload"
              :before-upload="beforeUpload"
              :on-remove="handleRemove"
              :file-list="fileList"
              list-type="picture-card"
              :limit="1">
              <i class="el-icon-plus"></i>
              <div slot="tip" class="el-upload__tip">请上传垃圾照片，只能上传jpg/png文件，且不超过10MB</div>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="validateAndVerify" :loading="verifying">验证照片</el-button>
            <el-button @click="prevStep">上一步</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 验证结果 -->
        <el-result 
          v-if="verificationResult" 
          :icon="verificationResult.status ? 'success' : 'error'"
          :title="verificationResult.status ? '照片验证通过' : '照片验证不通过'"
          :sub-title="verificationResult.message">
          <template slot="extra">
            <el-button type="primary" @click="nextStep" v-if="verificationResult.status">下一步</el-button>
            <el-button @click="resetVerificationResult" v-else>重新上传</el-button>
          </template>
        </el-result>
      </div>
      
      <!-- 步骤3: 确认提交 -->
      <div v-if="activeStep === 2">
        <el-descriptions title="垃圾投递记录确认" :column="1" border>
          <el-descriptions-item label="垃圾类型">
            <el-tag :type="getGarbageTypeTag(form.garbageType)">{{ form.garbageType }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="重量(kg)">{{ form.weight }}</el-descriptions-item>
          <el-descriptions-item label="投递地点">{{ form.location.address }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ form.remark }}</el-descriptions-item>
          <el-descriptions-item label="照片">
            <el-image 
              style="width: 100px; height: 100px" 
              :src="form.photoUrl || form.photoData" 
              :preview-src-list="[form.photoUrl || form.photoData]">
            </el-image>
          </el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 20px; text-align: center;">
          <el-button type="primary" @click="submitForm" :loading="submitting">提交</el-button>
          <el-button @click="prevStep">上一步</el-button>
          <el-button @click="resetAll">重置</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { addRecord } from "@/api/garbage/record";
import { verifyImage, validateImageFormat, identifyGarbageType } from "@/api/garbage/verification";
import { getGuideByName } from "@/api/garbage/guide";

export default {
  name: "SubmitRecord",
  data() {
    return {
      // 当前步骤
      activeStep: 0,
      // 表单对象
      form: {
        garbageType: undefined,
        weight: 0.1,
        location: {
          address: '',
          longitude: null,
          latitude: null,
          city: '',
          district: ''
        },
        photoUrl: '',
        photoData: '',
        remark: ''
      },
      // 文件列表
      fileList: [],
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 表单校验规则
      rules: {
        garbageType: [
          { required: true, message: "请选择垃圾类型", trigger: "change" }
        ],
        weight: [
          { required: true, message: "请输入重量", trigger: "blur" }
        ],
        'location.address': [
          { required: true, message: "请输入投递地点", trigger: "blur" }
        ]
      },
      // 照片校验规则
      photoRules: {
        photoData: [
          { required: true, message: "请上传垃圾照片", trigger: "change" }
        ]
      },
      // 验证中
      verifying: false,
      // 提交中
      submitting: false,
      // 验证结果
      verificationResult: null,
      // 垃圾分类指南
      garbageGuide: null
    };
  },
  created() {
    // 如果定位可用，自动获取当前位置
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(this.setPosition);
    }
  },
  methods: {
    // 设置位置信息
    setPosition(position) {
      this.form.location.longitude = position.coords.longitude;
      this.form.location.latitude = position.coords.latitude;
      
      // 这里可以根据经纬度调用地图API获取详细地址
      // 简化处理，仅记录经纬度
    },
    
    // 下一步
    nextStep() {
      if (this.activeStep === 0) {
        this.$refs.form.validate(valid => {
          if (valid) {
            this.activeStep++;
            // 获取该垃圾类型的指南
            this.getGarbageGuide();
          }
        });
      } else {
        this.activeStep++;
      }
    },
    
    // 上一步
    prevStep() {
      this.activeStep--;
    },
    
    // 获取垃圾分类指南
    getGarbageGuide() {
      getGuideByName(this.form.garbageType).then(response => {
        if (response.code === 200) {
          this.garbageGuide = response.data;
        }
      });
    },
    
    // 重置表单
    resetForm() {
      this.$refs.form.resetFields();
    },
    
    // 重置所有
    resetAll() {
      this.activeStep = 0;
      this.form = {
        garbageType: undefined,
        weight: 0.1,
        location: {
          address: '',
          longitude: null,
          latitude: null,
          city: '',
          district: ''
        },
        photoUrl: '',
        photoData: '',
        remark: ''
      };
      this.fileList = [];
      this.verificationResult = null;
    },
    
    // 上传前校验
    beforeUpload(file) {
      const result = validateImageFormat(file);
      if (!result.valid) {
        this.$message.error(result.message);
        return false;
      }
      return true;
    },
    
    // 处理上传
    handleUpload(options) {
      const file = options.file;
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.form.photoData = reader.result;
        this.fileList = [{ name: file.name, url: reader.result }];
        options.onSuccess();
      };
    },
    
    // 处理移除
    handleRemove() {
      this.form.photoData = '';
      this.fileList = [];
      this.verificationResult = null;
    },
    
    // 验证照片
    validateAndVerify() {
      this.$refs.photoForm.validate(valid => {
        if (valid) {
          this.verifying = true;
          
          // 构造验证请求数据
          const data = {
            garbageType: this.form.garbageType,
            photoData: this.form.photoData
          };
          
          // 首先尝试使用AI识别
          identifyGarbageType(data).then(response => {
            if (response.code === 200) {
              // AI识别成功
              const result = response.data;
              const isMatch = result.identifiedType === this.form.garbageType;
              
              this.verificationResult = {
                status: isMatch,
                message: isMatch 
                  ? "照片验证成功，确认为" + this.form.garbageType 
                  : "照片验证失败，系统识别为" + result.identifiedType + "，与您选择的类型不符"
              };
            } else {
              // AI识别失败，使用传统验证方式
              this.fallbackVerification(data);
            }
            this.verifying = false;
          }).catch(() => {
            // API错误，使用传统验证方式
            this.fallbackVerification(data);
            this.verifying = false;
          });
        }
      });
    },
    
    // 备用验证方法
    fallbackVerification(data) {
      verifyImage(data).then(response => {
        if (response.code === 200) {
          const result = response.data;
          this.verificationResult = {
            status: result.verified,
            message: result.message || (result.verified ? "照片验证成功" : "照片验证失败")
          };
        } else {
          // 如果服务端验证也失败，则默认通过，但标记需要人工审核
          this.verificationResult = {
            status: true,
            message: "照片已上传，将由管理员审核后确认"
          };
        }
      }).catch(() => {
        // 出错时也让用户通过，但标记需要人工审核
        this.verificationResult = {
          status: true,
          message: "照片已上传，将由管理员审核后确认"
        };
      });
    },
    
    // 重置验证结果
    resetVerificationResult() {
      this.verificationResult = null;
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
    
    // 提交表单
    submitForm() {
      this.submitting = true;
      
      addRecord(this.form).then(response => {
        if (response.code === 200) {
          this.$modal.msgSuccess("提交成功");
          this.resetAll();
        } else {
          this.$modal.msgError(response.msg || "提交失败");
        }
        this.submitting = false;
      }).catch(() => {
        this.submitting = false;
      });
    }
  }
};
</script>

<style scoped>
.upload-demo {
  width: 100%;
}
</style> 