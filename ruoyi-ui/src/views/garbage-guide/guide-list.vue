<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="垃圾名称" prop="garbageName">
        <el-input
          v-model="queryParams.garbageName"
          placeholder="请输入垃圾名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="垃圾类型" prop="garbageType">
        <el-select v-model="queryParams.garbageType" placeholder="垃圾类型" clearable size="small">
          <el-option
            v-for="dict in garbageTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="guideList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="指南编号" align="center" prop="guideId" />
      <el-table-column label="垃圾名称" align="center" prop="garbageName" :show-overflow-tooltip="true" />
      <el-table-column label="垃圾类型" align="center" prop="garbageType">
        <template slot-scope="scope">
          <el-tag :type="garbageTypeTagType(scope.row.garbageType)">
            {{ garbageTypeFormat(scope.row.garbageType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="投放要求" align="center" prop="disposalRequirement" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改垃圾分类指南对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="垃圾名称" prop="garbageName">
          <el-input v-model="form.garbageName" placeholder="请输入垃圾名称" />
        </el-form-item>
        <el-form-item label="垃圾类型" prop="garbageType">
          <el-select v-model="form.garbageType" placeholder="请选择垃圾类型">
            <el-option
              v-for="dict in garbageTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投放要求" prop="disposalRequirement">
          <el-input type="textarea" v-model="form.disposalRequirement" placeholder="请输入投放要求" />
        </el-form-item>
        <el-form-item label="详细说明" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入详细说明" rows="4" />
        </el-form-item>
        <el-form-item label="垃圾图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadImgUrl"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload">
            <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 垃圾分类指南导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">仅允许导入xls、xlsx格式文件。</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGarbageGuides, getGarbageGuide, delGarbageGuide, addGarbageGuide, updateGarbageGuide } from "@/api/garbage/guide";
import { getToken } from "@/utils/auth";

export default {
  name: "GuideList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 垃圾分类指南表格数据
      guideList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 垃圾类型数据字典
      garbageTypeOptions: [
        { dictLabel: "可回收物", dictValue: "1" },
        { dictLabel: "有害垃圾", dictValue: "2" },
        { dictLabel: "厨余垃圾", dictValue: "3" },
        { dictLabel: "其他垃圾", dictValue: "4" }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        garbageName: undefined,
        garbageType: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        garbageName: [
          { required: true, message: "垃圾名称不能为空", trigger: "blur" }
        ],
        garbageType: [
          { required: true, message: "垃圾类型不能为空", trigger: "change" }
        ],
        disposalRequirement: [
          { required: true, message: "投放要求不能为空", trigger: "blur" }
        ]
      },
      // 上传图片地址
      uploadImgUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      // 设置上传的请求头部
      headers: { Authorization: "Bearer " + getToken() },
      // 垃圾分类指南导入参数
      upload: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/garbage/guide/importData"
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询垃圾分类指南列表 */
    getList() {
      this.loading = true;
      listGarbageGuides(this.queryParams).then(response => {
          this.guideList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 垃圾类型字典翻译
    garbageTypeFormat(garbageType) {
      return this.selectDictLabel(this.garbageTypeOptions, garbageType);
    },
    // 垃圾类型标签样式
    garbageTypeTagType(garbageType) {
      if (garbageType === '1') return 'success';
      if (garbageType === '2') return 'danger';
      if (garbageType === '3') return 'warning';
      return 'info';
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        guideId: undefined,
        garbageName: undefined,
        garbageType: undefined,
        disposalRequirement: undefined,
        description: undefined,
        imageUrl: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.guideId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加垃圾分类指南";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const guideId = row.guideId || this.ids[0];
      getGarbageGuide(guideId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改垃圾分类指南";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.guideId !== undefined) {
            updateGarbageGuide(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addGarbageGuide(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const guideIds = row.guideId || this.ids;
      this.$modal.confirm('是否确认删除垃圾分类指南编号为"' + guideIds + '"的数据项？').then(function() {
        return delGarbageGuide(guideIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('garbage/guide/export', {
        ...this.queryParams
      }, `garbage_guide_${new Date().getTime()}.xlsx`);
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "垃圾分类指南导入";
      this.upload.open = true;
    },
    /** 文件上传中处理 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    /** 提交上传文件 */
    submitFileForm() {
      this.$refs.upload.submit();
    },
    // 图片上传成功处理
    handleImageSuccess(res, file) {
      if (res.code === 200) {
        this.form.imageUrl = res.url;
      } else {
        this.$message.error("上传图片失败");
      }
    },
    // 上传前图片格式校验
    beforeImageUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  }
};
</script>

<style scoped>
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