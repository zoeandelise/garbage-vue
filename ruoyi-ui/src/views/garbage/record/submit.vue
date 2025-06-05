<template>
  <div class="app-container garbage-submit-container">
    <!-- 页面标题和介绍 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">垃圾投递记录提交</h2>
        <p class="page-subtitle">记录您的环保行动，获取积分奖励</p>
      </div>
      <div class="header-decoration">
        <i class="el-icon-upload header-icon"></i>
      </div>
    </div>
    
    <el-card class="main-card" shadow="hover">
      <!-- 步骤导航 -->
      <el-steps :active="activeStep" finish-status="success" class="steps-nav">
        <el-step title="填写基本信息" icon="el-icon-edit"></el-step>
        <el-step title="上传照片验证" icon="el-icon-picture"></el-step>
        <el-step title="确认提交" icon="el-icon-check"></el-step>
      </el-steps>
      
      <!-- 步骤1: 基本信息 -->
      <div v-if="activeStep === 0" class="step-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="submit-form">
          <el-form-item label="垃圾类型" prop="garbageType">
            <el-select v-model="form.garbageType" placeholder="请选择垃圾类型" class="full-width">
              <el-option
                v-for="item in garbageTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="重量(kg)" prop="weight">
            <el-input-number v-model="form.weight" :precision="2" :step="0.1" :min="0.01" :max="100" class="full-width"></el-input-number>
          </el-form-item>
          
          <el-form-item label="投递地点" prop="location.address">
            <el-input v-model="form.location.address" placeholder="请输入投递地点">
              <template slot="append">
                <el-button type="primary" @click="getLocation" icon="el-icon-location-outline">定位</el-button>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="备注" prop="remark">
            <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息" rows="4"></el-input>
          </el-form-item>
          
          <el-form-item class="form-buttons">
            <el-button type="primary" @click="nextStep" icon="el-icon-arrow-right" class="action-button">下一步</el-button>
            <el-button @click="resetForm" icon="el-icon-refresh-left" class="action-button">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤2: 上传照片 -->
      <div v-if="activeStep === 1" class="step-container">
        <el-form ref="photoForm" :model="form" :rules="photoRules" label-width="100px" class="submit-form">
          <div class="upload-container">
            <div class="upload-area">
              <el-upload
                class="upload-box"
                action="#"
                :show-file-list="false"
                :before-upload="beforePhotoUpload"
                :http-request="uploadPhoto"
                :multiple="false"
                drag>
                <i v-if="!form.photoUrl" class="el-icon-upload upload-icon"></i>
                <div v-if="!form.photoUrl" class="upload-text">
                  <div>点击或拖拽文件到此处上传</div>
                  <div class="upload-hint">支持JPG、PNG格式，不超过10MB</div>
                </div>
                <div v-if="form.photoUrl" class="preview-container">
                  <img :src="getImageUrl(form.photoUrl)" class="preview-image">
                </div>
              </el-upload>
            </div>
            
            <div class="verification-result" v-if="verificationResult">
              <div class="result-title">
                <i :class="['result-icon', verificationResult.success ? 'el-icon-success success-icon' : 'el-icon-warning warning-icon']"></i>
                <span>{{ verificationResult.success ? '验证成功' : '验证提示' }}</span>
              </div>
              <div class="result-content">
                <p>{{ verificationResult.message }}</p>
                <div v-if="verificationResult.detectedType" class="detected-type">
                  <span>系统识别为：</span>
                  <el-tag :type="getGarbageTypeTag(verificationResult.detectedType)" effect="dark">{{ verificationResult.detectedType }}</el-tag>
                </div>
              </div>
              <div v-if="garbageGuide" class="guide-info">
                <div class="guide-title">垃圾分类指南</div>
                <div class="guide-content">
                  <p>{{ garbageGuide.remark }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <el-form-item class="form-buttons">
            <el-button type="primary" @click="verifyPhoto" :loading="verifying" :disabled="!form.photoUrl" icon="el-icon-check" class="action-button">验证照片</el-button>
            <el-button type="success" @click="nextStep" :disabled="!verificationResult || !verificationResult.success" icon="el-icon-arrow-right" class="action-button">下一步</el-button>
            <el-button @click="prevStep" icon="el-icon-arrow-left" class="action-button">上一步</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤3: 确认提交 -->
      <div v-if="activeStep === 2" class="step-container">
        <div class="confirmation-container">
          <div class="confirmation-header">
            <i class="el-icon-document-checked confirmation-icon"></i>
            <h3 class="confirmation-title">垃圾投递记录确认</h3>
          </div>
          
          <el-descriptions border :column="1" class="confirmation-details">
            <el-descriptions-item label="垃圾类型">
              <el-tag :type="getGarbageTypeTag(form.garbageType)" effect="dark" size="medium">{{ form.garbageType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="重量(kg)">
              <span class="weight-value">{{ form.weight }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="投递地点">
              <div class="location-value">
                <i class="el-icon-location location-icon"></i>
                {{ form.location.address }}
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="备注" v-if="form.remark">
              <div class="remark-value">{{ form.remark }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="照片">
              <el-image 
                style="width: 150px; height: 150px; border-radius: 4px; object-fit: cover;" 
                :src="getImageUrl(form.photoUrl)" 
                :preview-src-list="[getImageUrl(form.photoUrl)]">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="estimated-points" v-if="verificationResult && verificationResult.estimatedPoints">
            <div class="points-label">预计可获得积分</div>
            <div class="points-value">{{ verificationResult.estimatedPoints }}</div>
          </div>
          
          <!-- 提交按钮区域 - 增强可见性 -->
          <div class="submit-action-container">
            <el-button type="primary" @click="submitForm" :loading="submitting" icon="el-icon-upload2" class="submit-button">
              <span class="submit-text">确认提交</span>
            </el-button>
            <div class="action-buttons">
              <el-button @click="prevStep" icon="el-icon-arrow-left" class="action-button">上一步</el-button>
              <el-button @click="resetAll" icon="el-icon-refresh" class="action-button">重置</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 提交成功提示 -->
      <div v-if="activeStep === 3" class="step-container">
        <div class="success-container">
          <i class="el-icon-circle-check success-icon"></i>
          <h3 class="success-title">提交成功！</h3>
          <p class="success-message">您的垃圾投递记录已成功提交，感谢您为环保事业做出的贡献！</p>
          <div class="success-points" v-if="form.points">
            <div class="points-label">获得积分</div>
            <div class="points-value">{{ form.points }}</div>
          </div>
          <div class="success-actions">
            <el-button type="primary" @click="goToList" icon="el-icon-view" class="action-button">查看记录列表</el-button>
            <el-button @click="resetAll" icon="el-icon-plus" class="action-button">继续添加</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { addRecord, uploadPhoto } from "@/api/garbage/record";
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
        remark: '',
        points: null
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
        photoUrl: [
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
      garbageGuide: null,
      // 图片URL
      imageUrl: ''
    };
  },
  created() {
    // 如果定位可用，自动获取当前位置
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(this.setPosition);
    }
  },
  methods: {
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
    
    // 获取完整的图片URL
    getImageUrl(imageUrl) {
      if (!imageUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // 否则拼接基础URL
      return process.env.VUE_APP_BASE_API + imageUrl;
    },
    
    // 下一步
    nextStep() {
      if (this.activeStep === 0) {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.activeStep++;
          }
        });
      } else if (this.activeStep === 1) {
        this.$refs.photoForm.validate((valid) => {
          if (valid) {
            this.activeStep++;
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
    
    // 重置表单
    resetForm() {
      this.$refs.form.resetFields();
    },
    
    // 重置所有
    resetAll() {
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
        remark: '',
        points: null
      };
      this.verificationResult = null;
      this.garbageGuide = null;
      this.activeStep = 0;
    },
    
    // 获取当前位置
    getLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(this.setPosition, (error) => {
          this.$message.error("获取位置失败: " + error.message);
        });
      } else {
        this.$message.error("您的浏览器不支持地理定位");
      }
    },
    
    // 设置位置
    setPosition(position) {
      this.form.location.longitude = position.coords.longitude;
      this.form.location.latitude = position.coords.latitude;
      
      // 使用逆地理编码获取地址
      this.getAddressFromCoords(position.coords.latitude, position.coords.longitude);
    },
    
    // 根据坐标获取地址
    getAddressFromCoords(lat, lng) {
      // 这里可以接入地图API进行逆地理编码
      // 简化处理，使用示例地址
      this.form.location.address = `位置(${lat.toFixed(6)}, ${lng.toFixed(6)})`;
      this.form.location.city = "示例城市";
      this.form.location.district = "示例区县";
    },
    
    // 照片上传前的验证
    beforePhotoUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt10M = file.size / 1024 / 1024 < 10;
      
      if (!isImage) {
        this.$message.error('只能上传JPG/PNG格式的图片!');
        return false;
      }
      
      if (!isLt10M) {
        this.$message.error('图片大小不能超过10MB!');
        return false;
      }
      
      return true;
    },
    
    // 上传照片
    uploadPhoto(options) {
      const formData = new FormData();
      formData.append('file', options.file);
      
      this.verifying = true;
      uploadPhoto(options.file).then(response => {
        if (response.code === 200) {
          // 正确处理返回的URL
          if (response.data && typeof response.data === 'object') {
            this.form.photoUrl = response.data.url || response.data.fullUrl || '';
          } else {
            this.form.photoUrl = response.data || response.url || '';
          }
          this.$message.success('照片上传成功');
          this.verifying = false;
        } else {
          this.$message.error(response.msg || '照片上传失败');
          this.verifying = false;
        }
      }).catch(() => {
        this.$message.error('照片上传失败');
        this.verifying = false;
      });
    },
    
    // 验证照片
    verifyPhoto() {
      if (!this.form.photoUrl) {
        this.$message.warning('请先上传照片');
        return;
      }
      
      this.verifying = true;
      
      // 直接进行垃圾类型识别，跳过格式验证（因为已经上传成功，格式应该是正确的）
      identifyGarbageType(this.form.photoUrl).then(response => {
        if (response.code === 200) {
          // 注意：这里需要兼容不同的API返回格式
          const detectedType = response.data.garbageType || response.data.identifiedType;
          const confidence = response.data.confidence || 0;
          
          // 获取垃圾分类指南
          if (detectedType) {
            return getGuideByName(detectedType).then(guideResponse => {
              const success = detectedType === this.form.garbageType;
              const estimatedPoints = Math.round(this.form.weight * 10);
              
              this.verificationResult = {
                success: success,
                message: success 
                  ? `成功识别为${detectedType}，与您选择的类型一致！` 
                  : `系统识别为${detectedType}，与您选择的类型不一致，请确认。`,
                detectedType: detectedType,
                confidence: confidence,
                estimatedPoints: estimatedPoints
              };
              
              if (guideResponse.code === 200 && guideResponse.data) {
                this.garbageGuide = guideResponse.data;
              }
              
              this.verifying = false;
            }).catch(error => {
              console.error("获取垃圾分类指南失败:", error);
              this.verifying = false;
              
              // 即使获取指南失败，仍然设置验证结果
              const success = detectedType === this.form.garbageType;
              const estimatedPoints = Math.round(this.form.weight * 10);
              
              this.verificationResult = {
                success: success,
                message: success 
                  ? `成功识别为${detectedType}，与您选择的类型一致！` 
                  : `系统识别为${detectedType}，与您选择的类型不一致，请确认。`,
                detectedType: detectedType,
                confidence: confidence,
                estimatedPoints: estimatedPoints
              };
            });
          } else {
            this.verificationResult = {
              success: false,
              message: '无法识别垃圾类型，请确保照片清晰可见。',
              detectedType: null,
              confidence: 0
            };
            this.verifying = false;
          }
        } else {
          this.$message.error(response.msg || '垃圾类型识别失败');
          this.verifying = false;
        }
      }).catch(error => {
        console.error("垃圾类型识别失败:", error);
        this.$message.error('垃圾类型识别失败，请重试');
        this.verifying = false;
      });
    },
    
    // 提交表单
    submitForm() {
      // 检查必要字段是否已填写
      if (!this.form.garbageType) {
        this.$message.warning('请选择垃圾类型');
        return;
      }
      
      if (!this.form.weight || this.form.weight <= 0) {
        this.$message.warning('请输入有效的重量');
        return;
      }
      
      if (!this.form.location.address) {
        this.$message.warning('请输入投递地点');
        return;
      }
      
      if (!this.form.photoUrl) {
        this.$message.warning('请上传垃圾照片');
        return;
      }
      
      this.submitting = true;
      
      // 确保积分信息被正确传递
      if (this.verificationResult && this.verificationResult.estimatedPoints) {
        this.form.estimatedPoints = this.verificationResult.estimatedPoints;
      }
      
      addRecord(this.form).then(response => {
        if (response.code === 200) {
          this.form.points = response.data.points || this.form.estimatedPoints || 0;
          this.$message.success('提交成功');
          this.activeStep = 3; // 进入成功页面
        } else {
          this.$message.error(response.msg || '提交失败');
        }
        this.submitting = false;
      }).catch((error) => {
        console.error('提交失败:', error);
        this.$message.error('提交失败，请稍后重试');
        this.submitting = false;
      });
    },
    
    // 跳转到列表页
    goToList() {
      this.$router.push({ path: "/garbage/record" });
    }
  }
};
</script>

<style lang="scss" scoped>
.garbage-submit-container {
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

.main-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.main-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.steps-nav {
  margin-bottom: 30px;
  padding: 20px 0;
}

.step-container {
  padding: 10px;
}

.submit-form {
  max-width: 800px;
  margin: 0 auto;
}

.full-width {
  width: 100%;
}

.form-buttons {
  margin-top: 30px;
  text-align: center;
}

.action-button {
  padding: 10px 20px;
  transition: all 0.3s;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.submit-button {
  padding: 12px 24px;
  font-size: 16px;
  transition: all 0.3s;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.upload-area {
  display: flex;
  justify-content: center;
}

.upload-box {
  width: 100%;
  max-width: 400px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
}

.upload-box:hover {
  border-color: #42b983;
}

.upload-icon {
  font-size: 48px;
  color: #8c939d;
}

.upload-text {
  margin-top: 10px;
  color: #606266;
}

.upload-hint {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.preview-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.verification-result {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-top: 20px;
  border-left: 4px solid #42b983;
}

.result-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 500;
}

.result-icon {
  margin-right: 8px;
  font-size: 20px;
}

.success-icon {
  color: #67C23A;
}

.warning-icon {
  color: #E6A23C;
}

.result-content {
  color: #606266;
  margin-bottom: 10px;
}

.detected-type {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.guide-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e0e0e0;
}

.guide-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.guide-content {
  color: #606266;
  font-size: 14px;
}

.confirmation-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.confirmation-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.confirmation-icon {
  font-size: 28px;
  color: #42b983;
  margin-right: 10px;
}

.confirmation-title {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.confirmation-details {
  margin-bottom: 30px;
}

.weight-value {
  font-weight: 500;
  color: #303133;
}

.location-value {
  display: flex;
  align-items: center;
}

.location-icon {
  margin-right: 5px;
  color: #67c23a;
}

.remark-value {
  white-space: pre-line;
}

.estimated-points {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  color: white;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
}

.points-label {
  font-size: 16px;
  margin-bottom: 5px;
}

.points-value {
  font-size: 32px;
  font-weight: 600;
}

.success-container {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 80px;
  color: #67C23A;
  margin-bottom: 20px;
}

.success-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 15px;
}

.success-message {
  color: #606266;
  font-size: 16px;
  margin-bottom: 30px;
}

.success-points {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  color: white;
  border-radius: 8px;
  padding: 15px;
  width: 200px;
  margin: 0 auto 30px;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 新增样式 - 提交按钮区域 */
.submit-action-container {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.submit-button {
  padding: 15px 30px;
  font-size: 18px;
  width: 80%;
  max-width: 300px;
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border-color: #33a06f;
  border-radius: 50px;
  box-shadow: 0 8px 15px rgba(66, 185, 131, 0.3);
  transition: all 0.3s;
  animation: pulse 2s infinite;
}

.submit-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(66, 185, 131, 0.4);
}

.submit-text {
  font-weight: 600;
  letter-spacing: 1px;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(66, 185, 131, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(66, 185, 131, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(66, 185, 131, 0);
  }
}

.action-buttons {
  display: flex;
  gap: 15px;
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
  
  .upload-container {
    flex-direction: column;
  }
  
  .success-actions {
    flex-direction: column;
    gap: 10px;
  }
}
</style>