<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电话号码" prop="customerPhone">
        <el-input
          v-model="queryParams.customerPhone"
          placeholder="请输入客户联系方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车牌号码" prop="licensePlate">
        <el-input
          v-model="queryParams.licensePlate"
          placeholder="请输入车牌号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 新增等按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['appointment:info:add']"
        >新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表表格 -->
    <el-table v-loading="loading" :data="infoList">
      <el-table-column label="客户姓名" align="center" prop="customerName"/>
      <el-table-column label="客户联系方式" align="center" prop="customerPhone"/>
      <el-table-column label="预约时间" align="center" prop="appointmentTime" width="180"/>
      <el-table-column label="实际到店时间" align="center" prop="actualArrivalTime" width="180"/>
      <el-table-column label="车牌号码" align="center" prop="licensePlate"/>
      <el-table-column label="汽车类型" align="center" prop="carSeries"/>
      <el-table-column label="服务类型" align="center" prop="serviceType">
        <!-- TODO vue 中的一个功能：插槽 => slot -->
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_service_type" :value="scope.row.serviceType"/>
        </template>
      </el-table-column>
      <el-table-column label="备注信息" align="center" prop="info"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_appointment_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-if="scope.row.status === 0"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['appointment:info:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-goods"
            v-if="scope.row.status === 0"
            @click="handleArrival(scope.row)"
            v-hasPermi="['appointment:info:edit']"
          >到店
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-bank-card"
            v-if="[1,4,5].indexOf(scope.row.status) >= 0"
            @click="doCreateStatement(scope.row)"
          >结算单
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-if="scope.row.status === 0"
            @click="handleCancel(scope.row)"
            v-hasPermi="['appointment:info:remove']"
          >取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页条 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改养修信息预约对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="客户姓名" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户姓名"/>
        </el-form-item>
        <el-form-item label="客户联系方式" prop="customerPhone">
          <el-input v-model="form.customerPhone" placeholder="请输入客户联系方式"/>
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker clearable
                          v-model="form.appointmentTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择预约时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="车牌号码" prop="licensePlate">
          <el-input v-model="form.licensePlate" placeholder="请输入车牌号码"/>
        </el-form-item>
        <el-form-item label="汽车类型" prop="carSeries">
          <el-input v-model="form.carSeries" placeholder="请输入汽车类型"/>
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select clearable v-model="form.serviceType" placeholder="请选择汽车类型">
            <el-option
              v-for="dict in dict.type.bus_service_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入备注信息"/>
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
  import {
    addInfo,
    arrivalInfo,
    cancelInfo,
    createStatement,
    getInfo,
    listInfo,
    updateInfo,
    listHistory
  } from "@/api/appointment/info";
  import {validLicensePlate, validPhone} from '@/utils/validate'

  export default {
    name: "Info",
    dicts: [
      'bus_service_type',
      'bus_appointment_status'
    ],
    data() {
      let validatePhone = (rule, value, callback) => {
        if (!validPhone(value)) {
          // 校验失败
          callback(new Error('手机号格式有误!'));
        } else {
          // 校验成功
          callback();
        }
      };

      let validateLicensePlate = (rule, value, callback) => {
        if (!validLicensePlate(value)) {
          // 校验失败
          callback(new Error('车牌号格式错误!'));
          return;
        }

        // 校验成功
        callback();
      };

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
        // 养修信息预约表格数据
        infoList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          customerName: null,
          customerPhone: null,
          licensePlate: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          customerName: [
            {required: true, message: '请输入客户姓名', trigger: 'blur'}
          ],
          customerPhone: [
            {required: true, message: '请输入客户联系方式', trigger: 'blur'},
            {validator: validatePhone, trigger: 'blur'}
          ],
          appointmentTime: [
            {required: true, message: '请选择预约时间', trigger: 'change'}
          ],
          licensePlate: [
            {required: true, message: '请输入车牌号', trigger: 'blur'},
            {validator: validateLicensePlate, trigger: 'blur'}
          ],
          carSeries: [
            {required: true, message: '请输入车辆类型', trigger: 'blur'}
          ],
          serviceType: [
            {required: true, message: '请选择服务类型', trigger: 'change'}
          ]
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 创建结算单 */
      doCreateStatement(row) {
        createStatement(row.id).then(resp => {
          let statement = resp.data;
          // 判断结算单状态是否为已支付
          let url = `/appointment/statement/item/${statement.id}`;
          if (statement.status === 1) {
            url = `/appointment/statement/details/${statement.id}`;
          }
          // 路由跳转
          this.$router.push(url);
        })
      },
      /** 查询养修信息预约列表 */
      getList() {
        this.loading = true;
        listInfo(this.queryParams).then(response => {
          this.infoList = response.rows;
          this.total = response.total;
          this.loading = false;
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
          id: null,
          customerName: null,
          customerPhone: null,
          appointmentTime: null,
          actualArrivalTime: null,
          licensePlate: null,
          carSeries: null,
          serviceType: null,
          createTime: null,
          info: null,
          status: null
        };
        this.resetForm("form");
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
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加养修信息预约";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getInfo(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改养修信息预约";
        });
      },
      handleArrival(row) {
        this.$modal.confirm('是否确认客户已到店？').then(function () {
          return arrivalInfo(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("客户已到店");
        }).catch(() => {
        });
      },
      handleCancel(row) {
        this.$modal.confirm('是否确认取消预约？').then(function () {
          return cancelInfo(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("取消预约成功");
        }).catch(() => {
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateInfo(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addInfo(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      }
    }
  };
</script>
