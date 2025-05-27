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
      <el-form-item label="图片" prop="imageUrl">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeImageUpload"
          :http-request="uploadImage">
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
        <div class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">立即创建</el-button>
        <el-button @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { addGuide } from "@/api/garbage/guide";

export default {
  name: "GuideAdd",
  data() {
    return {
      // 表单参数
      form: {
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
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 表单校验
      rules: {
        garbageName: [
          { required: true, message: "垃圾名称不能为空", trigger: "blur" }
        ],
        garbageType: [
          { required: true, message: "垃圾类型不能为空", trigger: "change" }
        ],
        disposalTips: [
          { required: true, message: "投放建议不能为空", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    // 取消按钮
    cancel() {
      this.$router.push({ path: "/garbage/guide" });
    },
    // 表单提交
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addGuide(this.form).then(response => {
            this.$modal.msgSuccess("新增成功");
            this.$router.push({ path: "/garbage/guide" });
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
    // 自定义上传
    uploadImage(options) {
      const file = options.file;
      // 将图片转为base64
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.imageUrl = reader.result;
        this.form.imageUrl = reader.result; // 直接使用base64作为图片URL
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