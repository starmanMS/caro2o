<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入服务项名称"
          clearable
          @keyup.enter.native="handleQuery"
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
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option
            v-for="dict in dict.type.bus_si_audit_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="上架状态" prop="saleStatus">
        <el-select v-model="queryParams.saleStatus" placeholder="请选择上架状态" clearable>
          <el-option
            v-for="dict in dict.type.bus_si_sale_status"
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['appointment:serviceItem:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-upload2"
          size="mini"
          v-hasPermi="['appointment:serviceItem:auditInfo']"
          @click="handleStartAudit"
          :disabled="!startAudit"
        >发起审核
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>

    <el-table v-loading="loading" :data="serviceItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="服务项名称" align="center" prop="name"/>
      <el-table-column label="服务项原价" align="center" prop="originalPrice"/>
      <el-table-column label="服务项折扣价" align="center" prop="discountPrice"/>
      <el-table-column label="是否套餐" align="center" prop="carPackage">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_si_car_package" :value="scope.row.carPackage"/>
        </template>
      </el-table-column>
      <el-table-column label="服务分类" align="center" prop="serviceCatalog">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_si_service_type" :value="scope.row.serviceCatalog"/>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" align="center" prop="saleStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_si_sale_status" :value="scope.row.saleStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_si_audit_status" :value="scope.row.auditStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注信息" align="center" prop="info" :show-overflow-tooltip="true"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            :disabled="scope.row.saleStatus === 1 || scope.row.auditStatus === 1"
            v-hasPermi="['appointment:serviceItem:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            :icon="scope.row.saleStatus === 0 ? 'el-icon-top' : 'el-icon-bottom'"
            @click="handleUpdateSaleStatus(scope.row.id, scope.row.saleStatus === 0)"
            v-hasPermi="['appointment:serviceItem:saleStatus']"
          >{{ scope.row.saleStatus === 0 ? '上架' : '下架' }}
          </el-button>
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

    <!-- 添加或修改服务项对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="服务项名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务项名称"/>
        </el-form-item>
        <el-form-item label="服务项原价" prop="originalPrice">
          <el-input v-model="form.originalPrice" placeholder="请输入服务项原价"/>
        </el-form-item>
        <el-form-item label="服务项折扣价" prop="discountPrice">
          <el-input v-model="form.discountPrice" placeholder="请输入服务项折扣价"/>
        </el-form-item>
        <el-form-item label="是否套餐" prop="carPackage">
          <el-radio-group v-model="form.carPackage">
            <el-radio
              v-for="dict in dict.type.bus_si_car_package"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入备注信息"/>
        </el-form-item>
        <el-form-item label="服务分类" prop="serviceCatalog">
          <el-select v-model="form.serviceCatalog" placeholder="请选择服务分类">
            <el-option
              v-for="dict in dict.type.bus_si_service_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="auditInfo.title" :visible.sync="auditInfo.open" width="600px" append-to-body>
      <el-form ref="auditForm" :model="auditInfo.form" :rules="auditInfo.rules" label-width="120px">
        <el-form-item label="服务项名称" prop="name">
          <el-input v-model="auditInfo.form.serviceItem.name" disabled placeholder="请输入服务项名称"/>
        </el-form-item>
        <el-form-item label="服务项原价" prop="originalPrice">
          <el-input v-model="auditInfo.form.serviceItem.originalPrice" disabled placeholder="请输入服务项原价"/>
        </el-form-item>
        <el-form-item label="服务项折扣价" prop="discountPrice">
          <el-input v-model="auditInfo.form.serviceItem.discountPrice" disabled placeholder="请输入服务项折扣价"/>
        </el-form-item>
        <el-form-item label="审核人（店长）" prop="shopOwners">
          <el-select v-model="auditInfo.form.shopOwnerId" placeholder="请选择店长">
            <el-option
              v-for="user in auditInfo.form.shopOwners"
              :key="user.userId"
              :label="user.nickName"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="auditInfo.form.serviceItem.discountPrice >= auditInfo.form.limitDiscountPrice"
                      label="审核人（财务）" prop="financial">
          <el-select v-model="auditInfo.form.financialId" placeholder="请选择财务">
            <el-option
              v-for="user in auditInfo.form.finances"
              :key="user.userId"
              :label="user.nickName"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息" prop="info">
          <el-input v-model="auditInfo.form.info" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="doStartAudit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    addServiceItem,
    getServiceItem,
    listServiceItem,
    updateSaleStatus,
    updateServiceItem,
    getServiceItemAuditInfo
  } from "@/api/appointment/serviceItem";
  import {startAudit} from '@/api/audit/info'

  export default {
    name: "ServiceItem",
    dicts: ['bus_si_audit_status', 'bus_si_sale_status', 'bus_si_car_package', 'bus_si_service_type'],
    data() {
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 是否允许开始审核
        startAudit: false,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 服务项表格数据
        serviceItemList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
          carPackage: null,
          serviceCatalog: null,
          auditStatus: null,
          saleStatus: null
        },
        auditInfo: {
          title: '发起审核',
          form: {
            serviceItem: {},
            shopOwners: [],
            finances: [],
            info: null,
            shopOwnerId: null,
            financialId: null,
            limitDiscountPrice: null
          },
          open: false,
          rules: {
            name: [
              {required: true, message: '服务项名称不能为空', trigger: 'blur'}
            ],
            originalPrice: [
              {required: true, message: '服务项原价不能为空', trigger: 'blur'}
            ],
            discountPrice: [
              {required: true, message: '服务项折扣价不能为空', trigger: 'blur'}
            ]
          }
        },
        // 当前选中的对象
        current: {},
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          name: [
            {required: true, message: '服务项名称不能为空', trigger: 'blur'}
          ],
          originalPrice: [
            {required: true, message: '服务项原价不能为空', trigger: 'blur'}
          ],
          discountPrice: [
            {required: true, message: '服务项折扣价不能为空', trigger: 'blur'}
          ],
          carPackage: [
            {required: true, message: '请选择是否套餐', trigger: 'blur'}
          ],
          serviceCatalog: [
            {required: true, message: '请选择服务分类', trigger: 'change'}
          ],
          info: [
            {required: true, message: '备注信息不能为空', trigger: 'blur'}
          ],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 发起审核 */
      doStartAudit() {
        let {form} = this.auditInfo;
        let param = {
          serviceItemId: form.serviceItem.id,
          shopOwnerId: form.shopOwnerId,
          financialId: form.financialId,
          info: form.info
        };
        // 发请求
        startAudit(param).then(() => {
          // 提示发起审核成功
          this.$modal.msgSuccess('发起审核成功');
          // 刷新表格
          this.getList();
          // 关闭弹窗
          this.auditInfo.open = false;
          // 跳转到审核信息页面
          this.$router.push('/audit/packageInfo')
        });
      },
      /** 发起审核弹窗 */
      handleStartAudit() {
        getServiceItemAuditInfo(this.current.id).then(resp => {
          // 显示弹框
          this.auditInfo.open = true;
          // 存储当前对象
          this.auditInfo.form = resp.data;
          console.log(resp.data)
        });
      },
      /** 查询服务项列表 */
      getList() {
        this.loading = true;
        listServiceItem(this.queryParams).then(response => {
          this.serviceItemList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.auditInfo.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          name: null,
          originalPrice: null,
          discountPrice: null,
          carPackage: 0,
          info: null,
          createTime: null,
          serviceCatalog: null,
          auditStatus: null,
          saleStatus: null
        };
        this.auditInfo.form = {
          serviceItem: {},
          shopOwners: [],
          finances: [],
          info: null,
          shopOwnerId: null,
          financialId: null,
          limitDiscountPrice: null
        };
        this.resetForm("form");
        this.resetForm('auditForm')
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
      // 多选框选中数据
      handleSelectionChange(selection) {
        // 如果没选 || 选中的元素大于 1 个 || 如果状态 != 初始化 且 != 审核拒绝，都不允许发起审核
        if (selection.length !== 1 || !(selection[0].auditStatus === 0 || selection[0].auditStatus === 3)) {
          this.startAudit = false;
          this.current = {};
          return
        }

        // 除了以上情况，都允许审核
        this.startAudit = true;
        this.current = selection[0];
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加服务项";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids;
        getServiceItem(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改服务项";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateServiceItem(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addServiceItem(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 上下架按钮操作 */
      handleUpdateSaleStatus(id, status) {
        let text = status ? '上架' : '下架';
        this.$modal.confirm('是否将该服务项设置为' + text).then(function () {
          return updateSaleStatus(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess(text + "成功");
        }).catch(() => {
        });
      }
    }
  };
</script>
