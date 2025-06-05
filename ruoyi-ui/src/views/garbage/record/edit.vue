<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
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
      <el-form-item label="重量(kg)" prop="weight">
        <el-input-number v-model="form.weight" :precision="2" :step="0.1" :min="0" />
      </el-form-item>
      <el-form-item label="投递地点" prop="location.address">
        <el-input v-model="form.location.address" placeholder="请输入投递地点" />
      </el-form-item>
      <el-form-item label="城市" prop="location.city">
        <el-input v-model="form.location.city" placeholder="请输入城市" />
      </el-form-item>
      <el-form-item label="区县" prop="location.district">
        <el-input v-model="form.location.district" placeholder="请输入区县" />
      </el-form-item>
      <el-form-item label="地理位置">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="经度" prop="location.longitude">
              <el-input v-model="form.location.longitude" placeholder="请输入经度" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="location.latitude">
              <el-input v-model="form.location.latitude" placeholder="请输入纬度" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" size="mini" @click="getLocation">获取当前位置</el-button>
      </el-form-item>
      <el-form-item label="照片" prop="photoUrl">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforePhotoUpload"
          :http-request="uploadPhoto">
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
        <div class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getGarbageRecord, updateRecord } from "@/api/garbage/record";

export default {
  name: "RecordEdit",
  data() {
    return {
      // 表单参数
      form: {
        id: null,
        userId: null,
        userName: null,
        garbageType: null,
        weight: 0,
        location: {
          longitude: null,
          latitude: null,
          address: null,
          city: null,
          district: null
        },
        photoUrl: null,
        remark: null,
        points: null,
        pointsCalculated: false
      },
      // 图片URL
      imageUrl: '',
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 表单校验
      rules: {
        garbageType: [
          { required: true, message: "垃圾类型不能为空", trigger: "change" }
        ],
        weight: [
          { required: true, message: "重量不能为空", trigger: "blur" }
        ],
        "location.address": [
          { required: true, message: "投递地点不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    const id = this.$route.params.id;
    this.getRecordInfo(id);
  },
  methods: {
    /** 获取垃圾投递记录详细信息 */
    getRecordInfo(id) {
      getGarbageRecord(id).then(response => {
        if (response.code === 200 && response.data) {
          this.form = response.data;
          // 如果没有location对象，创建一个空对象
          if (!this.form.location) {
            this.form.location = {
              longitude: null,
              latitude: null,
              address: null,
              city: null,
              district: null
            };
          }
          // 设置图片URL
          if (this.form.photoUrl) {
            this.imageUrl = this.getImageUrl(this.form.photoUrl);
          }
        } else {
          this.$modal.msgError("获取记录详情失败");
          this.cancel();
        }
      }).catch(error => {
        console.error("获取记录详情失败:", error);
        this.$modal.msgError("获取记录详情失败，请返回重试");
        this.cancel();
      });
    },
    // 取消按钮
    cancel() {
      this.$router.push({ path: "/garbage/record" });
    },
    // 表单提交
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const loading = this.$loading({
            lock: true,
            text: '保存中...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          
          updateRecord(this.form).then(response => {
            loading.close();
            if (response.code === 200) {
              this.$modal.msgSuccess("修改成功");
              this.$router.push({ path: "/garbage/record" });
            } else {
              this.$modal.msgError(response.msg || "修改失败");
            }
          }).catch(error => {
            loading.close();
            console.error("修改记录失败:", error);
            this.$modal.msgError("修改失败，请重试");
          });
        }
      });
    },
    // 获取当前位置
    getLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          position => {
            this.form.location.longitude = position.coords.longitude;
            this.form.location.latitude = position.coords.latitude;
            this.$message.success("获取位置成功");
            
            // 这里可以调用地图API根据经纬度获取详细地址信息
            // 例如调用百度地图、高德地图等API
          },
          error => {
            this.$message.error("获取位置失败，请手动输入");
            console.error(error);
          }
        );
      } else {
        this.$message.error("浏览器不支持地理定位");
      }
    },
    // 上传前图片校验
    beforePhotoUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt10M) {
        this.$message.error('上传头像图片大小不能超过 10MB!');
      }
      
      return isJPG && isLt10M;
    },
    // 自定义上传
    uploadPhoto(options) {
      const file = options.file;
      uploadPhoto(file).then(response => {
        if (response.code === 200) {
          this.imageUrl = process.env.VUE_APP_BASE_API + response.data.url;
          this.form.photoUrl = response.data.url;
          this.$message.success("上传成功");
          options.onSuccess();
        } else {
          this.$message.error(response.msg || "上传失败");
          options.onError();
        }
      }).catch(error => {
        console.error("上传出错", error);
        this.$message.error("上传过程中发生错误");
        options.onError();
      });
    },
    // 验证图片URL是否有效
    isValidImageUrl(url) {
      if (!url) return false;
      // 支持相对路径、绝对路径和base64
      return url.startsWith('http://') || 
             url.startsWith('https://') || 
             url.startsWith('/profile/') ||
             url.startsWith('data:image/');
    },
    
    // 获取图片URL
    getImageUrl(url) {
      if (!url) return null;
      
      // 如果是完整URL或base64，直接返回
      if (url.startsWith('http://') || 
          url.startsWith('https://') ||
          url.startsWith('data:image/')) {
        return url;
      }
      
      // 如果是相对路径，拼接基础URL
      if (url.startsWith('/profile/')) {
        // 根据实际部署情况，可能需要拼接完整域名
        return process.env.VUE_APP_BASE_API + url;
      }
      
      return null;
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