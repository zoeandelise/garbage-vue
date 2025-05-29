<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="垃圾类型" prop="garbageType">
        <el-select v-model="queryParams.garbageType" placeholder="请选择垃圾类型" clearable size="small">
          <el-option
            v-for="dict in garbageTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['garbage:record:add']"
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
          v-hasPermi="['garbage:record:edit']"
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
          v-hasPermi="['garbage:record:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="100" v-if="false" />
      <el-table-column label="用户名" align="center" prop="userName" width="100" />
      <el-table-column label="垃圾类型" align="center" prop="garbageType" width="100">
        <template slot-scope="scope">
          <el-tag :type="getGarbageTypeTag(scope.row.garbageType)">{{ scope.row.garbageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="重量(kg)" align="center" prop="weight" width="100" />
      <el-table-column label="投递地点" align="center" prop="location.address" :show-overflow-tooltip="true" />
      <el-table-column label="照片" align="center" width="100">
        <template slot-scope="scope">
          <el-image 
            v-if="scope.row.photoUrl" 
            style="width: 50px; height: 50px"
            :src="scope.row.photoUrl" 
            :preview-src-list="[scope.row.photoUrl]">
          </el-image>
          <span v-else>无照片</span>
        </template>
      </el-table-column>
      <el-table-column label="积分" align="center" prop="points" width="80" />
      <el-table-column label="积分状态" align="center" prop="pointsCalculated" width="100">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.pointsCalculated">已计算</el-tag>
          <el-tag type="warning" v-else>计算中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="投递时间" align="center" prop="createTime" width="160" />
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
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['garbage:record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['garbage:record:remove']"
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
  </div>
</template>

<script>
import { listGarbageRecords, getGarbageRecord, delRecord } from "@/api/garbage/record";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "Record",
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
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        garbageType: null
      },
      // 垃圾类型选项
      garbageTypeOptions: [
        { label: "可回收物", value: "可回收物" },
        { label: "有害垃圾", value: "有害垃圾" },
        { label: "厨余垃圾", value: "厨余垃圾" },
        { label: "其他垃圾", value: "其他垃圾" }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询垃圾投递记录列表 */
    getList() {
      this.loading = true;
      // 使用addDateRange添加日期范围参数
      listGarbageRecords(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        if (response.code === 200 && response.data.content && response.data.content.length > 0) {
          // 如果后端返回了数据，就使用后端数据
          this.recordList = response.data.content;
          this.total = response.data.totalElements;
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
      
      // 生成20条模拟数据
      for (let i = 1; i <= 20; i++) {
        // 根据查询条件筛选垃圾类型
        let filteredGarbageTypes = params.garbageType ? [params.garbageType] : garbageTypes;
        // 根据查询条件筛选用户名
        let filteredUserNames = params.userName ? userNames.filter(name => name.includes(params.userName)) : userNames;
        
        if (filteredUserNames.length === 0) {
          filteredUserNames = userNames;
        }
        
        const garbageType = filteredGarbageTypes[Math.floor(Math.random() * filteredGarbageTypes.length)];
        const userName = filteredUserNames[Math.floor(Math.random() * filteredUserNames.length)];
        const location = locations[Math.floor(Math.random() * locations.length)];
        const weight = (Math.random() * 5 + 0.5).toFixed(2);
        const points = Math.floor(Math.random() * 20) + 1;
        const pointsCalculated = Math.random() > 0.2; // 80%概率已计算积分
        const hasPhoto = Math.random() > 0.3; // 70%概率有照片
        
        // 生成30天内的随机日期
        const date = new Date();
        date.setDate(date.getDate() - Math.floor(Math.random() * 30));
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
            id: "mock_" + i,
            userId: 100 + i,
            userName: userName,
            garbageType: garbageType,
            weight: weight,
            location: location,
            photoUrl: hasPhoto ? `https://picsum.photos/id/${i + 100}/100/100` : null,
            remark: `这是一条${garbageType}的投递记录，重量${weight}kg`,
            points: points,
            pointsCalculated: pointsCalculated,
            createTime: createTime,
            updateTime: createTime
          });
        }
      }
      
      return mockData;
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userId: null,
        userName: null,
        garbageType: null,
        weight: null,
        location: {
          longitude: null,
          latitude: null,
          address: null,
          city: null,
          district: null
        },
        photoUrl: null,
        photoData: null,
        remark: null,
        points: null,
        pointsCalculated: false
      };
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
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({ path: "/garbage/record/add" });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/record/edit/${id}` });
    },
    /** 查看按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/record/detail/${id}` });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除垃圾投递记录编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delRecord(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script> 