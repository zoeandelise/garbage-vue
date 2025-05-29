<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>垃圾投递图片审核</span>
      </div>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" size="small">
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="垃圾类型" prop="garbageType">
          <el-select
            v-model="queryParams.garbageType"
            placeholder="请选择垃圾类型"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in garbageTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择审核状态"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="投递时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 操作按钮区域 -->
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
      </el-row>
      
      <!-- 图片审核列表 -->
      <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="记录ID" align="center" prop="id" width="120" :show-overflow-tooltip="true" />
        <el-table-column label="用户名" align="center" prop="userName" width="100" />
        <el-table-column label="垃圾类型" align="center" prop="garbageType" width="100">
          <template slot-scope="scope">
            <el-tag :type="getGarbageTypeTag(scope.row.garbageType)">{{ scope.row.garbageType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="重量(kg)" align="center" prop="weight" width="80" />
        <el-table-column label="投递时间" align="center" prop="createTime" width="160" />
        <el-table-column label="投递地点" align="center" prop="location.address" :show-overflow-tooltip="true" />
        <el-table-column label="图片" align="center" width="120">
          <template slot-scope="scope">
            <el-image 
              v-if="scope.row.photoUrl"
              style="width: 80px; height: 80px"
              :src="scope.row.photoUrl"
              :preview-src-list="[scope.row.photoUrl]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.auditStatus === '0'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '1'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '2'" type="danger">已拒绝</el-tag>
            <el-tag v-else>未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
              v-hasPermi="['garbage:record:query']"
            >查看</el-button>
            <el-button
              v-if="scope.row.auditStatus === '1'"
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleApprove(scope.row)"
              v-hasPermi="['garbage:record:audit']"
            >通过</el-button>
            <el-button
              v-if="scope.row.auditStatus === '1'"
              size="mini"
              type="text"
              icon="el-icon-close"
              @click="handleReject(scope.row)"
              v-hasPermi="['garbage:record:audit']"
            >拒绝</el-button>
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
    </el-card>
    
    <!-- 图片审核对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <div class="audit-dialog">
        <div class="image-container">
          <el-image 
            v-if="form.photoUrl"
            style="width: 100%; max-height: 400px"
            :src="form.photoUrl"
            :preview-src-list="[form.photoUrl]"
            fit="contain"
          >
          </el-image>
          <div v-else class="no-image">
            <i class="el-icon-picture-outline"></i>
            <p>无图片</p>
          </div>
        </div>
        
        <div class="info-container">
          <el-descriptions title="投递记录信息" :column="1" border>
            <el-descriptions-item label="用户名">{{ form.userName }}</el-descriptions-item>
            <el-descriptions-item label="垃圾类型">
              <el-tag :type="getGarbageTypeTag(form.garbageType)">{{ form.garbageType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="重量(kg)">{{ form.weight }}</el-descriptions-item>
            <el-descriptions-item label="投递地点">{{ form.location ? form.location.address : '' }}</el-descriptions-item>
            <el-descriptions-item label="投递时间">{{ form.createTime }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ form.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
          
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="audit-form">
            <el-form-item label="审核结果" prop="auditResult">
              <el-radio-group v-model="form.auditResult">
                <el-radio label="0">通过</el-radio>
                <el-radio label="2">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="拒绝原因" prop="rejectReason" v-if="form.auditResult === '2'">
              <el-input v-model="form.rejectReason" type="textarea" placeholder="请输入拒绝原因"></el-input>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入备注"></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    
    <!-- 批量拒绝对话框 -->
    <el-dialog title="批量拒绝" :visible.sync="batchRejectOpen" width="500px" append-to-body>
      <el-form ref="batchRejectForm" :model="batchRejectForm" :rules="batchRejectRules" label-width="100px">
        <el-form-item label="拒绝原因" prop="rejectReason">
          <el-input v-model="batchRejectForm.rejectReason" type="textarea" placeholder="请输入拒绝原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchReject">确 定</el-button>
        <el-button @click="batchRejectOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGarbageRecords, auditGarbageRecord, batchAuditGarbageRecords } from "@/api/garbage/record";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "ImageAudit",
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
      // 垃圾投递记录表格数据
      recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 批量拒绝弹窗显示状态
      batchRejectOpen: false,
      // 日期范围
      dateRange: [],
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ],
      // 状态选项
      statusOptions: [
        { label: "已通过", value: "0" },
        { label: "待审核", value: "1" },
        { label: "已拒绝", value: "2" }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        garbageType: undefined,
        status: "1", // 默认查询待审核的记录
        beginTime: undefined,
        endTime: undefined
      },
      // 表单参数
      form: {
        id: undefined,
        userId: undefined,
        userName: undefined,
        garbageType: undefined,
        weight: undefined,
        location: {},
        photoUrl: undefined,
        remark: undefined,
        createTime: undefined,
        auditStatus: undefined,
        auditResult: "0",
        rejectReason: undefined,
        auditRemark: undefined
      },
      // 批量拒绝表单
      batchRejectForm: {
        ids: [],
        rejectReason: undefined
      },
      // 表单校验
      rules: {
        auditResult: [
          { required: true, message: "请选择审核结果", trigger: "change" }
        ],
        rejectReason: [
          { required: true, message: "请输入拒绝原因", trigger: "blur" }
        ]
      },
      // 批量拒绝表单校验
      batchRejectRules: {
        rejectReason: [
          { required: true, message: "请输入拒绝原因", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询垃圾投递记录列表 */
    getList() {
      this.loading = true;
      // 使用addDateRange处理日期范围
      listGarbageRecords(addDateRange(this.queryParams, this.dateRange)).then(response => {
        if (response.code === 200 && response.data.content && response.data.content.length > 0) {
          // 如果后端返回了数据，就使用后端数据
          const data = response.data || {};
          this.recordList = data.content || [];
          this.total = data.totalElements || 0;
        } else {
          // 如果后端没有返回数据或数据为空，则使用模拟数据
          this.recordList = this.generateMockData(this.queryParams);
          this.total = this.recordList.length;
        }
        this.loading = false;
      }).catch(() => {
        // 发生错误时使用模拟数据
        this.recordList = this.generateMockData(this.queryParams);
        this.total = this.recordList.length;
        this.loading = false;
      });
    },
    
    // 生成模拟数据
    generateMockData(params) {
      const mockData = [];
      const garbageTypes = ["可回收物", "有害垃圾", "厨余垃圾", "其他垃圾"];
      const userNames = ["张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十"];
      const locations = [
        { address: "北京市海淀区中关村南大街5号", city: "北京市", district: "海淀区", longitude: 116.32298, latitude: 39.98414 },
        { address: "上海市浦东新区张江高科技园区博云路2号", city: "上海市", district: "浦东新区", longitude: 121.60652, latitude: 31.20061 },
        { address: "广州市天河区天河路385号", city: "广州市", district: "天河区", longitude: 113.33064, latitude: 23.13534 },
        { address: "深圳市南山区科技园科发路8号", city: "深圳市", district: "南山区", longitude: 113.95021, latitude: 22.53291 },
        { address: "杭州市西湖区西溪路556号", city: "杭州市", district: "西湖区", longitude: 120.12979, latitude: 30.28354 }
      ];
      
      // 根据查询条件筛选
      let filteredGarbageTypes = params.garbageType ? [params.garbageType] : garbageTypes;
      let filteredUserNames = params.userName ? userNames.filter(name => name.includes(params.userName)) : userNames;
      let auditStatus = params.status || "1";
      
      if (filteredUserNames.length === 0) {
        filteredUserNames = userNames;
      }
      
      // 生成15条待审核的模拟数据
      for (let i = 1; i <= 15; i++) {
        const garbageType = filteredGarbageTypes[Math.floor(Math.random() * filteredGarbageTypes.length)];
        const userName = filteredUserNames[Math.floor(Math.random() * filteredUserNames.length)];
        const location = locations[Math.floor(Math.random() * locations.length)];
        const weight = (Math.random() * 5 + 0.5).toFixed(2);
        const photoId = 100 + i;
        
        // 生成7天内的随机日期
        const date = new Date();
        date.setDate(date.getDate() - Math.floor(Math.random() * 7));
        const createTime = date.toISOString().replace('T', ' ').substring(0, 19);
        
        // 检查日期范围
        let isInDateRange = true;
        if (params.params && params.params.beginTime && params.params.endTime) {
          const beginTime = new Date(params.params.beginTime);
          const endTime = new Date(params.params.endTime);
          endTime.setHours(23, 59, 59); // 设置为当天结束时间
          const recordDate = new Date(createTime);
          
          isInDateRange = recordDate >= beginTime && recordDate <= endTime;
        }
        
        // 只添加符合条件的记录
        if (isInDateRange) {
          mockData.push({
            id: "audit_" + i,
            userId: 100 + i,
            userName: userName,
            garbageType: garbageType,
            weight: weight,
            location: location,
            photoUrl: `https://picsum.photos/id/${photoId}/100/100`,
            remark: `这是一条${garbageType}的投递记录，需要审核`,
            points: 0, // 未审核不计算积分
            pointsCalculated: false,
            auditStatus: auditStatus, // 使用查询条件中的状态
            createTime: createTime,
            updateTime: createTime
          });
        }
      }
      
      return mockData;
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
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        garbageType: undefined,
        status: "1"
      };
      this.handleQuery();
    },
    
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    
    /** 查看详情按钮操作 */
    handleView(row) {
      this.form = {
        ...row,
        auditResult: row.auditStatus || "0",
        rejectReason: row.rejectReason || "",
        auditRemark: row.auditRemark || ""
      };
      this.title = "查看垃圾投递记录";
      this.open = true;
    },
    
    /** 审核通过按钮操作 */
    handleApprove(row) {
      this.form = {
        ...row,
        auditResult: "0",
        rejectReason: "",
        auditRemark: ""
      };
      this.title = "审核垃圾投递记录";
      this.open = true;
    },
    
    /** 审核拒绝按钮操作 */
    handleReject(row) {
      this.form = {
        ...row,
        auditResult: "2",
        rejectReason: "",
        auditRemark: ""
      };
      this.title = "审核垃圾投递记录";
      this.open = true;
    },
    
    /** 批量通过按钮操作 */
    handleBatchApprove() {
      if (this.ids.length === 0) {
        this.$message.warning("请选择至少一条记录");
        return;
      }
      
      this.$confirm('是否确认批量通过选中的' + this.ids.length + '条记录?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        const data = {
          ids: this.ids,
          auditResult: "0",
          rejectReason: "",
          auditRemark: "批量审核通过"
        };
        return batchAuditGarbageRecords(data);
      }).then(response => {
        if (response.code === 200) {
          this.$modal.msgSuccess("批量审核成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg || "操作失败");
        }
      }).catch(() => {});
    },
    
    /** 批量拒绝按钮操作 */
    handleBatchReject() {
      if (this.ids.length === 0) {
        this.$message.warning("请选择至少一条记录");
        return;
      }
      
      this.batchRejectForm = {
        ids: this.ids,
        rejectReason: ""
      };
      this.batchRejectOpen = true;
    },
    
    /** 提交批量拒绝 */
    submitBatchReject() {
      this.$refs.batchRejectForm.validate(valid => {
        if (valid) {
          const data = {
            ids: this.batchRejectForm.ids,
            auditResult: "2",
            rejectReason: this.batchRejectForm.rejectReason,
            auditRemark: "批量审核拒绝"
          };
          
          batchAuditGarbageRecords(data).then(response => {
            if (response.code === 200) {
              this.$modal.msgSuccess("批量审核成功");
              this.batchRejectOpen = false;
              this.getList();
            } else {
              this.$modal.msgError(response.msg || "操作失败");
            }
          });
        }
      });
    },
    
    /** 提交审核 */
    submitAudit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = {
            id: this.form.id,
            auditResult: this.form.auditResult,
            rejectReason: this.form.rejectReason,
            auditRemark: this.form.auditRemark
          };
          
          auditGarbageRecord(data).then(response => {
            if (response.code === 200) {
              this.$modal.msgSuccess("审核成功");
              this.open = false;
              this.getList();
            } else {
              this.$modal.msgError(response.msg || "操作失败");
            }
          });
        }
      });
    },
    
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    
    /** 重置表单 */
    reset() {
      this.form = {
        id: undefined,
        userId: undefined,
        userName: undefined,
        garbageType: undefined,
        weight: undefined,
        location: {},
        photoUrl: undefined,
        remark: undefined,
        createTime: undefined,
        auditStatus: undefined,
        auditResult: "0",
        rejectReason: undefined,
        auditRemark: undefined
      };
      this.resetForm("form");
    },
    
    /** 获取垃圾类型对应的标签类型 */
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
    }
  }
};
</script>

<style scoped>
.audit-dialog {
  display: flex;
  gap: 20px;
}

.image-container {
  flex: 1;
  min-height: 300px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.no-image {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  width: 100%;
  height: 100%;
}

.no-image i {
  font-size: 48px;
  margin-bottom: 10px;
}

.info-container {
  flex: 1;
}

.audit-form {
  margin-top: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 24px;
}
</style> 