<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>用户积分排行榜</span>
        <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="refreshRanking">刷新</el-button>
      </div>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="总积分排行" name="total">
          <el-table v-loading="loading" :data="rankingList" style="width: 100%">
            <el-table-column label="排名" align="center" width="80">
              <template slot-scope="scope">
                <template v-if="scope.$index < 3">
                  <img :src="getRankingIcon(scope.$index)" class="ranking-icon" />
                </template>
                <template v-else>
                  {{ scope.$index + 1 }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="用户头像" align="center" width="80">
              <template slot-scope="scope">
                <el-avatar :size="40" :src="scope.row.avatar || defaultAvatar"></el-avatar>
              </template>
            </el-table-column>
            <el-table-column label="用户昵称" prop="nickName" align="center" />
            <el-table-column label="总积分" prop="totalPoints" align="center" sortable />
            <el-table-column label="本月积分" prop="monthPoints" align="center" sortable />
            <el-table-column label="本周积分" prop="weekPoints" align="center" sortable />
            <el-table-column label="投递次数" prop="deliveryCount" align="center" sortable />
            <el-table-column label="操作" align="center" width="180">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewUserDetail(scope.row)"
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
        </el-tab-pane>
        
        <el-tab-pane label="本月排行" name="month">
          <el-table v-loading="loading" :data="rankingList" style="width: 100%">
            <el-table-column label="排名" align="center" width="80">
              <template slot-scope="scope">
                <template v-if="scope.$index < 3">
                  <img :src="getRankingIcon(scope.$index)" class="ranking-icon" />
                </template>
                <template v-else>
                  {{ scope.$index + 1 }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="用户头像" align="center" width="80">
              <template slot-scope="scope">
                <el-avatar :size="40" :src="scope.row.avatar || defaultAvatar"></el-avatar>
              </template>
            </el-table-column>
            <el-table-column label="用户昵称" prop="nickName" align="center" />
            <el-table-column label="本月积分" prop="monthPoints" align="center" sortable />
            <el-table-column label="总积分" prop="totalPoints" align="center" sortable />
            <el-table-column label="本月投递次数" prop="monthDeliveryCount" align="center" sortable />
            <el-table-column label="操作" align="center" width="180">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewUserDetail(scope.row)"
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
        </el-tab-pane>
        
        <el-tab-pane label="本周排行" name="week">
          <el-table v-loading="loading" :data="rankingList" style="width: 100%">
            <el-table-column label="排名" align="center" width="80">
              <template slot-scope="scope">
                <template v-if="scope.$index < 3">
                  <img :src="getRankingIcon(scope.$index)" class="ranking-icon" />
                </template>
                <template v-else>
                  {{ scope.$index + 1 }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="用户头像" align="center" width="80">
              <template slot-scope="scope">
                <el-avatar :size="40" :src="scope.row.avatar || defaultAvatar"></el-avatar>
              </template>
            </el-table-column>
            <el-table-column label="用户昵称" prop="nickName" align="center" />
            <el-table-column label="本周积分" prop="weekPoints" align="center" sortable />
            <el-table-column label="总积分" prop="totalPoints" align="center" sortable />
            <el-table-column label="本周投递次数" prop="weekDeliveryCount" align="center" sortable />
            <el-table-column label="操作" align="center" width="180">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewUserDetail(scope.row)"
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
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog :title="'用户详情 - ' + userDetail.nickName" :visible.sync="dialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ userDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ userDetail.nickName }}</el-descriptions-item>
        <el-descriptions-item label="总积分">{{ userDetail.totalPoints }}</el-descriptions-item>
        <el-descriptions-item label="本月积分">{{ userDetail.monthPoints }}</el-descriptions-item>
        <el-descriptions-item label="本周积分">{{ userDetail.weekPoints }}</el-descriptions-item>
        <el-descriptions-item label="总投递次数">{{ userDetail.deliveryCount }}</el-descriptions-item>
        <el-descriptions-item label="本月投递次数">{{ userDetail.monthDeliveryCount }}</el-descriptions-item>
        <el-descriptions-item label="本周投递次数">{{ userDetail.weekDeliveryCount }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ userDetail.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="center">最近投递记录</el-divider>

      <el-table :data="userDetail.recentRecords" style="width: 100%">
        <el-table-column label="垃圾类型" align="center">
          <template slot-scope="scope">
            <el-tag :type="getGarbageTypeTag(scope.row.garbageType)">
              {{ getGarbageTypeName(scope.row.garbageType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="垃圾重量(kg)" prop="weight" align="center" />
        <el-table-column label="获得积分" prop="points" align="center" />
        <el-table-column label="投递时间" prop="createTime" align="center" />
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="goToUserManagement">查看用户管理</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRanking, getUserDetail } from "@/api/points/ranking";

export default {
  name: "RankingList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 排行榜数据
      rankingList: [],
      // 总条数
      total: 0,
      // 当前激活的标签页
      activeTab: 'total',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rankType: 'total'
      },
      // 默认头像
      defaultAvatar: require("@/assets/images/profile.jpg"),
      // 排名图标
      rankingIcons: [
        require("@/assets/images/ranking/gold.svg"),
        require("@/assets/images/ranking/silver.svg"),
        require("@/assets/images/ranking/bronze.svg")
      ],
      // 对话框可见性
      dialogVisible: false,
      // 用户详情
      userDetail: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询排行榜列表 */
    getList() {
      this.loading = true;
      listRanking(this.queryParams).then(response => {
        this.rankingList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 刷新排行榜 */
    refreshRanking() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 标签页切换 */
    handleTabClick() {
      this.queryParams.rankType = this.activeTab;
      this.refreshRanking();
    },
    /** 获取排名图标 */
    getRankingIcon(index) {
      return this.rankingIcons[index];
    },
    /** 查看用户详情 */
    viewUserDetail(row) {
      getUserDetail(row.userId).then(response => {
        this.userDetail = response.data;
        this.dialogVisible = true;
      });
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
    /** 跳转到用户管理 */
    goToUserManagement() {
      this.$router.push({
        path: '/user-management/user-list',
        query: { userId: this.userDetail.userId }
      });
      this.dialogVisible = false;
    }
  }
};
</script>

<style scoped>
.ranking-icon {
  width: 30px;
  height: 30px;
}
</style> 