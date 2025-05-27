<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
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
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="审核状态" clearable size="small">
          <el-option label="待审核" value="1" />
          <el-option label="已通过" value="0" />
          <el-option label="已拒绝" value="2" />
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
          type="primary"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleBatchApprove"
          v-hasPermi="['garbage:record:audit']"
        >批量通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="multiple"
          @click="handleBatchReject"
          v-hasPermi="['garbage:record:audit']"
        >批量拒绝</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="image-grid">
      <el-card 
        v-for="item in imageList" 
        :key="item.recordId" 
        :class="{ 'selected-card': selectedIds.includes(item.recordId) }"
        @click.native="toggleSelection(item)"
      >
        <div class="image-container">
          <div class="image-wrapper">
            <el-image 
              :src="item.imageUrl" 
              fit="cover"
              :preview-src-list="[item.imageUrl]"
              @click.stop
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>
          <div class="image-status" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </div>
        </div>
        <div class="image-info">
          <p><strong>用户：</strong>{{ item.userName }}</p>
          <p>
            <strong>垃圾类型：</strong>
            <el-tag :type="getGarbageTypeTag(item.garbageType)" size="mini">
              {{ getGarbageTypeName(item.garbageType) }}
            </el-tag>
          </p>
          <p><strong>重量：</strong>{{ item.weight }}kg</p>
          <p><strong>投递时间：</strong>{{ parseTime(item.createTime) }}</p>
        </div>
        <div class="image-actions">
          <el-button 
            size="mini" 
            type="success" 
            icon="el-icon-check" 
            circle 
            @click.stop="handleApprove(item)"
            v-if="item.status === '1'"
          ></el-button>
          <el-button 
            size="mini" 
            type="danger" 
            icon="el-icon-close" 
            circle 
            @click.stop="handleReject(item)"
            v-if="item.status === '1'"
          ></el-button>
          <el-button 
            size="mini" 
            type="primary" 
            icon="el-icon-view" 
            circle 
            @click.stop="handleView(item)"
          ></el-button>
        </div>
      </el-card>
    </div>
    
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
          <el-col :span="24">
            <el-form-item label="投递时间">
              <el-input v-model="form.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="投递照片">
              <el-image 
                style="max-width: 100%; max-height: 300px;"
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

    <!-- 批量拒绝对话框 -->
    <el-dialog title="批量拒绝" :visible.sync="batchRejectVisible" width="500px" append-to-body>
      <el-form ref="batchRejectForm" :model="batchForm" label-width="100px">
        <el-form-item label="拒绝原因" prop="rejectReason">
          <el-input type="textarea" v-model="batchForm.rejectReason" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchReject">确 定</el-button>
        <el-button @click="batchRejectVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGarbageRecords, getGarbageRecord, auditGarbageRecord, batchAuditGarbageRecords } from "@/api/garbage/record";

export default {
  name: "ImageAudit",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      selectedIds: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 图片列表
      imageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 批量拒绝对话框可见性
      batchRejectVisible: false,
      // 日期范围
      dateRange: [],
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
        pageSize: 12,
        userName: undefined,
        garbageType: undefined,
        status: "1", // 默认查询待审核的记录
        beginTime: undefined,
        endTime: undefined
      },
      // 表单参数
      form: {},
      // 批量操作表单
      batchForm: {
        ids: [],
        auditResult: "",
        rejectReason: ""
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询投递记录列表 */
    getList() {
      this.loading = true;
      listGarbageRecords(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.imageList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
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
      this.queryParams.status = "1"; // 重置为默认查询待审核的记录
      this.handleQuery();
    },
    /** 获取垃圾类型名称 */
    getGarbageTypeName(type) {
      const types = {
        '1': '可回收物',
        '2': '有害垃圾',
        '3': '厨余垃圾',
        '4': '其他垃圾'
      };
      return types[type] || '未知类型';
    },
    /** 获取垃圾类型标签样式 */
    getGarbageTypeTag(type) {
      if (type === '1') return 'success';
      if (type === '2') return 'danger';
      if (type === '3') return 'warning';
      return 'info';
    },
    /** 获取状态文本 */
    getStatusText(status) {
      if (status === '0') return '已通过';
      if (status === '1') return '待审核';
      if (status === '2') return '已拒绝';
      return '未知状态';
    },
    /** 获取状态样式类 */
    getStatusClass(status) {
      if (status === '0') return 'status-passed';
      if (status === '1') return 'status-pending';
      if (status === '2') return 'status-rejected';
      return '';
    },
    /** 切换选中状态 */
    toggleSelection(item) {
      const index = this.selectedIds.indexOf(item.recordId);
      if (index === -1) {
        this.selectedIds.push(item.recordId);
      } else {
        this.selectedIds.splice(index, 1);
      }
      this.multiple = this.selectedIds.length === 0;
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      const recordId = row.recordId;
      getGarbageRecord(recordId).then(response => {
        this.form = response.data;
        this.form.garbageTypeName = this.getGarbageTypeName(this.form.garbageType);
        this.form.statusName = this.getStatusText(this.form.status);
        this.form.auditResult = 'pass';
        this.open = true;
        this.title = "查看投递记录";
      });
    },
    /** 通过按钮操作 */
    handleApprove(row) {
      this.$modal.confirm('是否确认通过该投递记录？').then(() => {
        const params = {
          recordId: row.recordId,
          auditResult: 'pass'
        };
        return auditGarbageRecord(params);
      }).then(() => {
        this.$modal.msgSuccess("审核通过成功");
        this.getList();
      }).catch(() => {});
    },
    /** 拒绝按钮操作 */
    handleReject(row) {
      this.reset();
      const recordId = row.recordId;
      getGarbageRecord(recordId).then(response => {
        this.form = response.data;
        this.form.garbageTypeName = this.getGarbageTypeName(this.form.garbageType);
        this.form.statusName = this.getStatusText(this.form.status);
        this.form.auditResult = 'reject';
        this.open = true;
        this.title = "拒绝投递记录";
      });
    },
    /** 批量通过按钮操作 */
    handleBatchApprove() {
      this.$modal.confirm('是否确认通过选中的' + this.selectedIds.length + '条投递记录？').then(() => {
        const params = {
          ids: this.selectedIds,
          auditResult: 'pass'
        };
        return batchAuditGarbageRecords(params);
      }).then(() => {
        this.$modal.msgSuccess("批量审核通过成功");
        this.selectedIds = [];
        this.multiple = true;
        this.getList();
      }).catch(() => {});
    },
    /** 批量拒绝按钮操作 */
    handleBatchReject() {
      this.batchForm.ids = this.selectedIds;
      this.batchForm.auditResult = 'reject';
      this.batchForm.rejectReason = '';
      this.batchRejectVisible = true;
    },
    /** 提交批量拒绝 */
    submitBatchReject() {
      if (!this.batchForm.rejectReason) {
        this.$message.warning("请输入拒绝原因");
        return;
      }
      
      batchAuditGarbageRecords(this.batchForm).then(() => {
        this.$modal.msgSuccess("批量拒绝成功");
        this.batchRejectVisible = false;
        this.selectedIds = [];
        this.multiple = true;
        this.getList();
      });
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
    }
  }
};
</script>

<style scoped>
.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  grid-gap: 20px;
  margin-bottom: 20px;
}

.el-card {
  transition: all 0.3s;
  cursor: pointer;
}

.el-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.selected-card {
  border: 2px solid #409EFF;
}

.image-container {
  position: relative;
}

.image-wrapper {
  height: 200px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.el-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 30px;
}

.image-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
}

.status-pending {
  background-color: #E6A23C;
}

.status-passed {
  background-color: #67C23A;
}

.status-rejected {
  background-color: #F56C6C;
}

.image-info {
  margin-top: 10px;
  font-size: 14px;
}

.image-info p {
  margin: 5px 0;
}

.image-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.image-actions .el-button {
  margin-left: 5px;
}
</style> 