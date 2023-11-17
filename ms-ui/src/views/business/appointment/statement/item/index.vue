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
          <el-descriptions-item label="优惠金额">
            <el-input
              style="width: 100px"
              type="number"
              v-model="statement.discountAmount"
              placeholder="请输入优惠金额"
              @blur="checkAmount"
            />
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
      <!-- 左下方：明细列表 -->
      <el-card class="box-card st-table">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleSave">保存</el-button>
        <el-button type="warning" icon="el-icon-refresh" size="mini" @click="doPrepay">支付</el-button>
        <el-table :data="items">
          <el-table-column label="名称" align="center" prop="itemName"/>
          <el-table-column label="价格" align="center" prop="itemPrice"/>
          <el-table-column label="数量" align="center" prop="itemQuantity"/>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-plus" @click="addNum(scope.row)" size="small"/>
              <el-button type="danger" icon="el-icon-minus" @click="subNum(scope.row)" size="small"/>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
    <!-- 右侧内容 -->
    <el-aside width="680px">
      <!-- 右上方：服务项查询表单 -->
      <el-card class="box-card st-info">
        <el-form :model="queryParams" ref="queryForm" size="small" label-width="100px">
          <el-form-item label="名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入服务项名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="是否套餐" prop="carPackage">
            <el-select v-model="queryParams.carPackage" placeholder="请选择是否套餐" clearable>
              <el-option
                v-for="dict in dict.type.bus_si_car_package"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="服务分类" prop="serviceCatalog">
            <el-select v-model="queryParams.serviceCatalog" placeholder="请选择服务分类" clearable>
              <el-option
                v-for="dict in dict.type.bus_si_service_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <!-- 右下方：服务项列表 -->
      <el-card class="box-card st-table">
        <el-table v-loading="serviceItemLoading" :data="serviceItemList">
          <el-table-column label="名称" align="center" prop="name"/>
          <el-table-column label="原价" align="center" prop="originalPrice"/>
          <el-table-column label="折扣价" align="center" prop="discountPrice"/>
          <el-table-column label="服务分类" align="center" prop="serviceCatalog">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.bus_si_service_type" :value="scope.row.serviceCatalog"/>
            </template>
          </el-table-column>
          <el-table-column label="是否套餐" align="center" prop="carPackage">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.bus_si_car_package" :value="scope.row.carPackage"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-plus" @click="addItem(scope.row)" size="small"/>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="serviceItemTotal>0"
          :total="serviceItemTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          layout="total, prev, pager, next"
          @pagination="getServiceItemList"
        />
      </el-card>
    </el-aside>
  </el-container>
</template>

<script>
  import {listServiceItem} from '@/api/appointment/serviceItem';
  import {prepay, addStatementItems, getStatement, getStatementItems} from "@/api/appointment/statement";

  export default {
    name: "StatementItem",
    dicts: ['bus_si_car_package', 'bus_si_service_type'],
    data() {
      return {
        queryParams: {
          pageNum: 1,
          pageSize: 5,
          name: null,
          carPackage: null,
          serviceCatalog: null,
          saleStatus: 1
        },
        serviceItemList: [], // 服务项列表
        serviceItemTotal: 0, // 服务项数量
        serviceItemLoading: false,
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

        // 如果当前的 statement 状态为已支付，自动关闭当前页面，并跳转到支付明细
        if (statement.status === 1) {
          this.$tab.closeOpenPage('/appointment/statement/details/' + this.statement.id)
        }
      });
      // 查询明细
      getStatementItems(statementId).then(resp => {
        this.items = resp.data || []
      });
      this.getServiceItemList() // 查询服务项列表
    },
    methods: {
      /* 发起支付请求 */
      doPrepay() {
        let _this = this;
        this.$modal.confirm('是否确认支付？').then(function () {
          return prepay(_this.statement.id);
        }).then(() => {
          // 提示支付成功
          _this.$modal.msgSuccess('支付成功');
          // 延迟一会跳转页面
          setTimeout(() => {
            _this.$tab.closeOpenPage('/appointment/statement/details/' + _this.statement.id)
          }, 500); // 500 毫秒后执行
        }).catch(() => {
        });
      },
      /** 查询服务项列表 */
      getServiceItemList() {
        this.serviceItemLoading = true;
        listServiceItem(this.queryParams).then(response => {
          this.serviceItemList = response.rows;
          this.serviceItemTotal = response.total;
          this.serviceItemLoading = false;
        });
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getServiceItemList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /**
       * 检查折扣金额的合法性
       */
      checkAmount() {
        let {discountAmount, totalAmount} = this.statement;
        if (discountAmount > totalAmount) {
          // 如果折扣金额大于总金额，提示折扣金额不能大于总金额
          this.$modal.alertWarning('折扣金额不能大于总金额');
          this.statement.discountAmount = 0; // 重置折扣金额
        }
      },
      /* 添加服务项到明细 */
      addItem(serviceItem) {
        // 基于当前服务项 id，查询已经有的所有明细，判断如果已经存在服务项，直接数量 +1
        for (let index in this.items) {
          let item = this.items[index];
          if (item.itemId === serviceItem.id) {
            item.itemQuantity += 1;
            // 重新计算总金额
            this.calcTotalAmount(item);
            return;
          }
        }

        // 将 row 转换为 item 对象，并将其加入的 items 数组
        let item = {
          itemId: serviceItem.id, // 服务项 id
          itemName: serviceItem.name, // 服务项名称
          itemPrice: serviceItem.discountPrice, // 服务项价格（折扣价）
          itemQuantity: 1, // 添加数量
          statementId: this.statement.id // 结算单 id
        };
        this.items.push(item); // 将明细数据加入数组
        // 重新计算总金额
        this.calcTotalAmount(item);
      },
      calcTotalAmount(item, type) {
        if (!type) {
          // 总金额 = 原来的总金额 + 此次添加服务的价格
          this.statement.totalAmount += item.itemPrice;
          return;
        }

        this.statement.totalAmount -= item.itemPrice;
      },
      addNum(item) {
        item.itemQuantity += 1;
        // 计算总金额
        this.calcTotalAmount(item)
      },
      subNum(item) {
        // 数量 -1
        item.itemQuantity -= 1;
        // 计算总金额
        this.calcTotalAmount(item, 'sub');
        // 判断数量如果为 0，直接删除当前数据
        if (item.itemQuantity === 0) {
          let index = this.items.indexOf(item); // 获取当前元素的索引
          this.items.splice(index, 1); // 删除当前位置的元素
        }
      },
      handleSave() {
        // 封装要提交的参数
        let data = {
          id: this.statement.id,
          discountAmount: this.statement.discountAmount,
          items: this.items
        };
        // 调用接口保存数据
        addStatementItems(data).then(resp => {
          this.$modal.alertSuccess('保存成功')
        });
      }
    }
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
