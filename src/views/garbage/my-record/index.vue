<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>我的垃圾投递记录</span>
      </div>
      <el-table
        v-loading="loading"
        :data="recordList"
        style="width: 100%"
      >
        <el-table-column
          type="index"
          label="序号"
          width="60"
        />
        <el-table-column
          prop="garbageType"
          label="垃圾类型"
          width="120"
        />
        <el-table-column
          prop="weight"
          label="重量(kg)"
          width="100"
        />
        <el-table-column
          prop="points"
          label="积分"
          width="80"
        />
        <el-table-column
          prop="location.address"
          label="投递地点"
        />
        <el-table-column
          prop="createTime"
          label="投递时间"
          width="160"
        />
        <el-table-column
          label="操作"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="viewDetail(scope.row)"
            >查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog :title="'投递记录详情'" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="垃圾类型">{{ currentRecord.garbageType }}</el-descriptions-item>
        <el-descriptions-item label="重量(kg)">{{ currentRecord.weight }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ currentRecord.points }}</el-descriptions-item>
        <el-descriptions-item label="投递地点">{{ currentRecord.location ? currentRecord.location.address : '' }}</el-descriptions-item>
        <el-descriptions-item label="投递时间">{{ currentRecord.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentRecord.remark }}</el-descriptions-item>
        <el-descriptions-item label="照片" v-if="currentRecord.photoUrl">
          <el-image 
            style="width: 100%; max-height: 300px;" 
            :src="currentRecord.photoUrl" 
            :preview-src-list="[currentRecord.photoUrl]">
          </el-image>
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listMyRecords, getRecord } from "@/api/garbage/record";

export default {
  name: "MyRecord",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 记录列表
      recordList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 对话框显示状态
      dialogVisible: false,
      // 当前记录
      currentRecord: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询记录列表 */
    getList() {
      this.loading = true;
      listMyRecords(this.queryParams).then(response => {
        if (response.code === 200) {
          const data = response.data || {};
          this.recordList = data.content || [];
          this.total = data.totalElements || 0;
        } else {
          this.recordList = [];
          this.total = 0;
          this.$message.error(response.msg || '获取数据失败');
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 查看详情 */
    viewDetail(row) {
      this.currentRecord = { ...row };
      this.dialogVisible = true;
    }
  }
};
</script> 