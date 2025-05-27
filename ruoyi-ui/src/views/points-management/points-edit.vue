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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList">
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="用户名称" align="center" prop="userName" />
      <el-table-column label="用户昵称" align="center" prop="nickName" />
      <el-table-column label="当前积分" align="center" prop="points" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
            v-hasPermi="['points:edit']"
          >调整积分</el-button>
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

    <!-- 调整积分对话框 -->
    <el-dialog :title="'调整积分 - ' + form.userName" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="form.userId" disabled />
        </el-form-item>
        <el-form-item label="用户名称">
          <el-input v-model="form.userName" disabled />
        </el-form-item>
        <el-form-item label="当前积分">
          <el-input v-model="form.points" disabled />
        </el-form-item>
        <el-form-item label="调整方式" prop="adjustType">
          <el-radio-group v-model="form.adjustType">
            <el-radio label="add">增加积分</el-radio>
            <el-radio label="subtract">减少积分</el-radio>
            <el-radio label="set">设置为</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分值" prop="adjustValue">
          <el-input-number v-model="form.adjustValue" :min="1" :max="10000" :step="10" step-strictly />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUsers, getUserPoints, adjustUserPoints } from "@/api/points/points";

export default {
  name: "PointsEdit",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户列表
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined
      },
      // 表单参数
      form: {
        userId: undefined,
        userName: undefined,
        points: 0,
        adjustType: "add",
        adjustValue: 10,
        reason: undefined
      },
      // 表单校验
      rules: {
        adjustType: [
          { required: true, message: "请选择调整方式", trigger: "change" }
        ],
        adjustValue: [
          { required: true, message: "请输入积分值", trigger: "blur" }
        ],
        reason: [
          { required: true, message: "请输入调整原因", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listUsers(this.queryParams).then(response => {
          this.userList = response.rows;
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 调整积分按钮操作 */
    handleEdit(row) {
      this.reset();
      const userId = row.userId;
      getUserPoints(userId).then(response => {
        this.form = response.data;
        this.form.adjustType = "add";
        this.form.adjustValue = 10;
        this.open = true;
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
        userId: undefined,
        userName: undefined,
        points: 0,
        adjustType: "add",
        adjustValue: 10,
        reason: undefined
      };
      this.resetForm("form");
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          adjustUserPoints(this.form).then(response => {
            this.$modal.msgSuccess("积分调整成功");
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
.box-card {
  margin-bottom: 20px;
}
</style> 