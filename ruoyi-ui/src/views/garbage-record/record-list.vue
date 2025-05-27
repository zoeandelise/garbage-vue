<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName" v-if="!userId">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
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
      <el-form-item label="投递状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="投递状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="投递时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
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

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录编号" align="center" prop="recordId" />
      <el-table-column label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" v-if="!userId" />
      <el-table-column label="垃圾类型" align="center" prop="garbageType">
        <template slot-scope="scope">
          <el-tag :type="garbageTypeTagType(scope.row.garbageType)">
            {{ garbageTypeFormat(scope.row.garbageType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="垃圾重量(kg)" align="center" prop="weight" />
      <el-table-column label="获得积分" align="center" prop="points" />
      <el-table-column label="投递状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : scope.row.status === '1' ? 'warning' : 'danger'">
            {{ statusFormat(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="投递时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-if="scope.row.status === '1'"
          >审核</el-button>
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

    <!-- 查看投递详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="记录编号">
              <el-input v-model="form.recordId" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称">
              <el-input v-model="form.userName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="垃圾类型">
              <el-input v-model="form.garbageTypeName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="垃圾重量(kg)">
              <el-input v-model="form.weight" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="获得积分">
              <el-input v-model="form.points" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投递状态">
              <el-input v-model="form.statusName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="投递时间">
              <el-input v-model="form.createTime" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理时间" v-if="form.status !== '1'">
              <el-input v-model="form.updateTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="投递照片">
              <el-image 
                style="width: 100px; height: 100px"
                :src="form.imageUrl" 
                :preview-src-list="[form.imageUrl]">
              </el-image>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.status === '1'">
          <el-col :span="24">
            <el-form-item label="审核结果" prop="auditResult">
              <el-radio-group v-model="form.auditResult">
                <el-radio label="pass">通过</el-radio>
                <el-radio label="reject">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.status === '1' && form.auditResult === 'reject'">
          <el-col :span="24">
            <el-form-item label="拒绝原因" prop="rejectReason">
              <el-input type="textarea" v-model="form.rejectReason" placeholder="请输入拒绝原因" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="form.status === '1'">确 定</el-button>
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGarbageRecords, getGarbageRecord, auditGarbageRecord } from "@/api/garbage/record";

export default {
  name: "RecordList",
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
      // 投递记录表格数据
      recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 垃圾类型数据字典
      garbageTypeOptions: [
        { dictLabel: "可回收物", dictValue: "1" },
        { dictLabel: "有害垃圾", dictValue: "2" },
        { dictLabel: "厨余垃圾", dictValue: "3" },
        { dictLabel: "其他垃圾", dictValue: "4" }
      ],
      // 状态数据字典
      statusOptions: [
        { dictLabel: "已完成", dictValue: "0" },
        { dictLabel: "待审核", dictValue: "1" },
        { dictLabel: "已拒绝", dictValue: "2" }
      ],
      // 用户ID（如果从用户管理跳转过来）
      userId: undefined,
      // 用户名称
      userNameParam: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        garbageType: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        auditResult: [
          { required: true, message: "请选择审核结果", trigger: "change" }
        ],
        rejectReason: [
          { required: true, message: "请输入拒绝原因", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    const { userId, userName } = this.$route.query;
    if (userId) {
      this.userId = userId;
      this.userNameParam = userName;
      this.queryParams.userId = userId;
    }
    this.getList();
  },
  methods: {
    /** 查询投递记录列表 */
    getList() {
      this.loading = true;
      listGarbageRecords(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.recordList = response.rows;
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
    // 投递状态字典翻译
    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        recordId: undefined,
        userId: undefined,
        userName: undefined,
        garbageType: undefined,
        garbageTypeName: undefined,
        weight: undefined,
        points: undefined,
        status: undefined,
        statusName: undefined,
        imageUrl: undefined,
        createTime: undefined,
        updateTime: undefined,
        auditResult: 'pass',
        rejectReason: undefined
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
      this.dateRange = [];
      this.resetForm("queryForm");
      if (this.userId) {
        this.queryParams.userId = this.userId;
      }
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      const recordId = row.recordId || this.ids[0];
      getGarbageRecord(recordId).then(response => {
        this.form = response.data;
        this.form.garbageTypeName = this.garbageTypeFormat(this.form.garbageType);
        this.form.statusName = this.statusFormat(this.form.status);
        this.open = true;
        this.title = "查看投递记录";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const recordId = row.recordId || this.ids[0];
      getGarbageRecord(recordId).then(response => {
        this.form = response.data;
        this.form.garbageTypeName = this.garbageTypeFormat(this.form.garbageType);
        this.form.statusName = this.statusFormat(this.form.status);
        this.form.auditResult = 'pass';
        this.open = true;
        this.title = "审核投递记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const params = {
            recordId: this.form.recordId,
            auditResult: this.form.auditResult,
            rejectReason: this.form.rejectReason
          };
          
          auditGarbageRecord(params).then(response => {
            this.$modal.msgSuccess("审核成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('garbage/record/export', {
        ...this.queryParams
      }, `garbage_record_${new Date().getTime()}.xlsx`);
    }
  }
};
</script> 