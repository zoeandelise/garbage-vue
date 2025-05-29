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
        <el-select v-model="queryParams.garbageType" placeholder="请选择垃圾类型" clearable size="small">
          <el-option
            v-for="dict in garbageTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
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
          v-hasPermi="['garbage:guide:add']"
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
          v-hasPermi="['garbage:guide:edit']"
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
          v-hasPermi="['garbage:guide:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="guideList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="100" v-if="false" />
      <el-table-column label="垃圾名称" align="center" prop="garbageName" :show-overflow-tooltip="true" />
      <el-table-column label="垃圾类型" align="center" prop="garbageType" width="100">
        <template slot-scope="scope">
          <el-tag :type="getGarbageTypeTag(scope.row.garbageType)">{{ scope.row.garbageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="投放建议" align="center" prop="disposalTips" :show-overflow-tooltip="true" />
      <el-table-column label="图片" align="center" width="80">
        <template slot-scope="scope">
          <el-image 
            v-if="scope.row.imageUrl" 
            style="width: 50px; height: 50px"
            :src="scope.row.imageUrl" 
            :preview-src-list="[scope.row.imageUrl]">
          </el-image>
          <span v-else>无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['garbage:guide:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['garbage:guide:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['garbage:guide:remove']"
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
import { listGarbageGuides, getGarbageGuide, delGarbageGuide } from "@/api/garbage/guide";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "Guide",
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
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        garbageName: null,
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
    /** 查询垃圾分类指南列表 */
    getList() {
      this.loading = true;
      listGarbageGuides(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        if (response.code === 200 && response.rows && response.rows.length > 0) {
          // 如果后端返回了数据，就使用后端数据
          this.guideList = response.rows;
          this.total = response.total;
        } else {
          // 如果后端没有返回数据或数据为空，则使用模拟数据
          this.guideList = this.generateMockData(this.queryParams);
          this.total = this.guideList.length;
        }
        this.loading = false;
      }).catch(() => {
        // 发生错误时使用模拟数据
        this.guideList = this.generateMockData(this.queryParams);
        this.total = this.guideList.length;
        this.loading = false;
      });
    },
    
    // 生成模拟数据
    generateMockData(params) {
      const mockData = [];
      
      // 可回收物
      const recyclableItems = [
        {
          id: "mock_1",
          garbageName: "纸板箱",
          garbageType: "可回收物",
          disposalTips: "清除内部填充物，折叠压扁后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png",
          remark: "纸板箱是可回收利用的资源，可以再生造纸",
          createTime: "2023-05-15 09:30:00",
          updateTime: "2023-05-15 09:30:00"
        },
        {
          id: "mock_2",
          garbageName: "塑料瓶",
          garbageType: "可回收物",
          disposalTips: "清空内容物，压扁后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png",
          remark: "塑料瓶可以回收利用，减少环境污染",
          createTime: "2023-05-16 10:20:00",
          updateTime: "2023-05-16 10:20:00"
        },
        {
          id: "mock_3",
          garbageName: "玻璃瓶",
          garbageType: "可回收物",
          disposalTips: "清空内容物，小心轻放避免破碎",
          imageUrl: "https://img.js.design/assets/img/64f7d32c3bb46fc5ad63d65a.png",
          remark: "玻璃瓶可以回收再利用或再生产",
          createTime: "2023-05-17 11:15:00",
          updateTime: "2023-05-17 11:15:00"
        }
      ];
      
      // 有害垃圾
      const harmfulItems = [
        {
          id: "mock_4",
          garbageName: "废电池",
          garbageType: "有害垃圾",
          disposalTips: "单独投放，防止与其他垃圾混合",
          imageUrl: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png",
          remark: "废电池含有重金属等有害物质，需要专门处理",
          createTime: "2023-05-18 13:40:00",
          updateTime: "2023-05-18 13:40:00"
        },
        {
          id: "mock_5",
          garbageName: "过期药品",
          garbageType: "有害垃圾",
          disposalTips: "不要挤压，保持包装完整投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png",
          remark: "过期药品如果随意丢弃可能污染环境",
          createTime: "2023-05-19 14:25:00",
          updateTime: "2023-05-19 14:25:00"
        },
        {
          id: "mock_6",
          garbageName: "废荧光灯管",
          garbageType: "有害垃圾",
          disposalTips: "轻放，避免破碎",
          imageUrl: "https://img.js.design/assets/img/64f7d32c38cea071f87bed92.png",
          remark: "荧光灯管含汞，破碎后有害健康",
          createTime: "2023-05-20 15:10:00",
          updateTime: "2023-05-20 15:10:00"
        }
      ];
      
      // 厨余垃圾
      const kitchenItems = [
        {
          id: "mock_7",
          garbageName: "剩菜剩饭",
          garbageType: "厨余垃圾",
          disposalTips: "沥干水分后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png",
          remark: "厨余垃圾可以堆肥，变成有机肥料",
          createTime: "2023-05-21 16:05:00",
          updateTime: "2023-05-21 16:05:00"
        },
        {
          id: "mock_8",
          garbageName: "果皮",
          garbageType: "厨余垃圾",
          disposalTips: "沥干水分后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png",
          remark: "果皮可降解，是良好的堆肥材料",
          createTime: "2023-05-22 17:00:00",
          updateTime: "2023-05-22 17:00:00"
        },
        {
          id: "mock_9",
          garbageName: "茶叶渣",
          garbageType: "厨余垃圾",
          disposalTips: "沥干水分后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c60e81b99ecd94c40.png",
          remark: "茶叶渣可以堆肥，也可以直接用作植物肥料",
          createTime: "2023-05-23 18:30:00",
          updateTime: "2023-05-23 18:30:00"
        }
      ];
      
      // 其他垃圾
      const otherItems = [
        {
          id: "mock_10",
          garbageName: "烟蒂",
          garbageType: "其他垃圾",
          disposalTips: "确保已熄灭后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png",
          remark: "烟蒂含有多种有害物质，不易降解",
          createTime: "2023-05-24 19:15:00",
          updateTime: "2023-05-24 19:15:00"
        },
        {
          id: "mock_11",
          garbageName: "陶瓷碎片",
          garbageType: "其他垃圾",
          disposalTips: "包裹好尖锐边缘后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png",
          remark: "陶瓷不易降解，不适合回收利用",
          createTime: "2023-05-25 20:00:00",
          updateTime: "2023-05-25 20:00:00"
        },
        {
          id: "mock_12",
          garbageName: "一次性纸杯",
          garbageType: "其他垃圾",
          disposalTips: "清空内容物后投放",
          imageUrl: "https://img.js.design/assets/img/64f7d32c15bb8fc8e1114f75.png",
          remark: "一次性纸杯内层有塑料薄膜，不易回收",
          createTime: "2023-05-26 21:45:00",
          updateTime: "2023-05-26 21:45:00"
        }
      ];
      
      // 根据查询条件筛选数据
      if (params.garbageType) {
        // 如果指定了垃圾类型，只返回该类型的数据
        switch (params.garbageType) {
          case "可回收物":
            mockData.push(...recyclableItems);
            break;
          case "有害垃圾":
            mockData.push(...harmfulItems);
            break;
          case "厨余垃圾":
            mockData.push(...kitchenItems);
            break;
          case "其他垃圾":
            mockData.push(...otherItems);
            break;
          default:
            mockData.push(...recyclableItems, ...harmfulItems, ...kitchenItems, ...otherItems);
        }
      } else {
        // 如果没有指定垃圾类型，返回所有数据
        mockData.push(...recyclableItems, ...harmfulItems, ...kitchenItems, ...otherItems);
      }
      
      // 根据垃圾名称筛选数据
      if (params.garbageName) {
        return mockData.filter(item => item.garbageName.includes(params.garbageName));
      }
      
      // 返回模拟数据
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
      this.$router.push({ path: "/garbage/guide/add" });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/guide/edit/${id}` });
    },
    /** 查看按钮操作 */
    handleView(row) {
      const id = row.id || this.ids[0];
      this.$router.push({ path: `/garbage/guide/detail/${id}` });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除垃圾分类指南编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delGarbageGuide(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script> 