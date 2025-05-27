<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>积分规则配置</span>
      </div>
      
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-divider content-position="left">垃圾投递积分规则</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="可回收物积分/kg" prop="recyclablePoints">
              <el-input-number v-model="form.recyclablePoints" :min="0" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有害垃圾积分/kg" prop="hazardousPoints">
              <el-input-number v-model="form.hazardousPoints" :min="0" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="厨余垃圾积分/kg" prop="kitchenPoints">
              <el-input-number v-model="form.kitchenPoints" :min="0" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="其他垃圾积分/kg" prop="otherPoints">
              <el-input-number v-model="form.otherPoints" :min="0" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">每日投递限制</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每日最大投递次数" prop="dailyDeliveryLimit">
              <el-input-number v-model="form.dailyDeliveryLimit" :min="1" :max="50" :step="1" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每日最大积分上限" prop="dailyPointsLimit">
              <el-input-number v-model="form.dailyPointsLimit" :min="0" :max="1000" :step="10" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">积分奖励规则</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="连续投递奖励天数" prop="continuousDays">
              <el-input-number v-model="form.continuousDays" :min="0" :max="30" :step="1" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="连续投递奖励积分" prop="continuousBonus">
              <el-input-number v-model="form.continuousBonus" :min="0" :max="500" :step="10" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="月度排名前三奖励" prop="monthlyTopThreeBonus">
              <el-input-number v-model="form.monthlyTopThreeBonus" :min="0" :max="1000" :step="10" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="首次使用奖励积分" prop="firstUseBonus">
              <el-input-number v-model="form.firstUseBonus" :min="0" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">积分兑换规则</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="积分兑换比例(积分/元)" prop="pointsExchangeRate">
              <el-input-number v-model="form.pointsExchangeRate" :min="1" :max="100" :step="1" step-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低兑换积分" prop="minExchangePoints">
              <el-input-number v-model="form.minExchangePoints" :min="0" :max="1000" :step="10" step-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="积分规则说明" prop="ruleDescription">
              <el-input
                type="textarea"
                v-model="form.ruleDescription"
                :rows="4"
                placeholder="请输入积分规则说明文字"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存配置</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getPointsRule, updatePointsRule } from "@/api/points/rule";

export default {
  name: "PointsRule",
  data() {
    return {
      // 表单参数
      form: {
        // 垃圾投递积分规则
        recyclablePoints: 10,
        hazardousPoints: 15,
        kitchenPoints: 5,
        otherPoints: 3,
        
        // 每日投递限制
        dailyDeliveryLimit: 10,
        dailyPointsLimit: 200,
        
        // 积分奖励规则
        continuousDays: 7,
        continuousBonus: 50,
        monthlyTopThreeBonus: 100,
        firstUseBonus: 20,
        
        // 积分兑换规则
        pointsExchangeRate: 10,
        minExchangePoints: 100,
        
        // 规则说明
        ruleDescription: ""
      },
      // 表单校验
      rules: {
        recyclablePoints: [
          { required: true, message: "可回收物积分不能为空", trigger: "blur" }
        ],
        hazardousPoints: [
          { required: true, message: "有害垃圾积分不能为空", trigger: "blur" }
        ],
        kitchenPoints: [
          { required: true, message: "厨余垃圾积分不能为空", trigger: "blur" }
        ],
        otherPoints: [
          { required: true, message: "其他垃圾积分不能为空", trigger: "blur" }
        ],
        dailyDeliveryLimit: [
          { required: true, message: "每日最大投递次数不能为空", trigger: "blur" }
        ],
        dailyPointsLimit: [
          { required: true, message: "每日最大积分上限不能为空", trigger: "blur" }
        ],
        ruleDescription: [
          { required: true, message: "积分规则说明不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getRule();
  },
  methods: {
    /** 获取积分规则 */
    getRule() {
      getPointsRule().then(response => {
        if (response.code === 200) {
          this.form = response.data;
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updatePointsRule(this.form).then(response => {
            this.$modal.msgSuccess("保存成功");
          });
        }
      });
    },
    /** 重置按钮 */
    resetForm() {
      this.getRule();
    }
  }
};
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
</style> 