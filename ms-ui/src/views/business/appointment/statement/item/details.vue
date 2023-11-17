<template>
  <el-container>
    <!-- 左侧内容 -->
    <el-main>
      <!-- 左上方：客户基础信息 -->
      <el-card class="box-card st-info">
        <el-descriptions title="客户信息">
          <el-descriptions-item label="客户姓名">{{ statement.customerName }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ statement.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="车牌号码">{{ statement.licensePlate }}</el-descriptions-item>
          <el-descriptions-item label="汽车类型">{{ statement.carSeries }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">
            <dict-tag :options="dict.type.bus_si_service_type" :value="statement.serviceType"/>
          </el-descriptions-item>
          <el-descriptions-item label="到店时间">{{ statement.actualArrivalTime }}</el-descriptions-item>
          <el-descriptions-item label="总消费金额">{{ statement.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">{{ statement.totalAmount - statement.discountAmount }}
          </el-descriptions-item>
          <el-descriptions-item label="优惠金额">{{ statement.discountAmount }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <!-- 左下方：明细列表 -->
      <el-card class="box-card st-table">
        <el-table :data="items">
          <el-table-column label="名称" align="center" prop="itemName"/>
          <el-table-column label="价格" align="center" prop="itemPrice"/>
          <el-table-column label="数量" align="center" prop="itemQuantity"/>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
  import {getStatement, getStatementItems} from "@/api/appointment/statement";

  export default {
    name: "StatementItemDetails",
    dicts: ['bus_si_service_type'],
    data() {
      return {
        statement: {}, // 结算单对象
        items: [] // 结算单明细列表
      };
    },
    created() {
      // 从路由对象中获取参数
      // 路由地址：/appointment/statement/item/:statementId(\\d+)
      // Vue-Router：会自动将路由地址中 :statementId 中的名称作为参数名称，将此位置的值作为参数值
      // 将参数封装在 Vue.$route.params 对象中 params = {statementId: 1}
      const statementId = this.$route.params && this.$route.params.statementId;
      // this.statement.id = statementId; // 保存当前 statementId
      // 基于结算单 id 查询结算单信息
      getStatement(statementId).then(resp => {
        this.statement = resp.data || {};
      });
      // 查询明细
      getStatementItems(statementId).then(resp => {
        this.items = resp.data || []
      });
    },
    methods: {}
  };
</script>

<style scoped>
  .el-select {
    width: 100%;
  }

  .el-descriptions {
    margin-top: 30px;
  }

  .box-card {
    margin-top: 10px;
  }

  .el-aside {
    padding: 20px 20px 20px 0;
    background: none;
  }

  .st-info {
    height: 220px;
  }

  .st-table {
    height: 550px;
  }
</style>
