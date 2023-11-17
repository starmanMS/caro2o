<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="daterangeCreateTime"
          style="width: 360px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.bus_audit_status"
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>

    <el-table v-loading="loading" :data="infoList">
      <el-table-column label="套餐名称" align="center" prop="serviceItemName" />
      <el-table-column label="套餐价格" align="center" prop="serviceItemPrice" />
      <el-table-column label="套餐备注" align="center" show-overflow-tooltip prop="serviceItemInfo" />
      <el-table-column label="审核备注" align="center" show-overflow-tooltip prop="info" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_audit_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleHistory(scope.row)"
            v-hasPermi="['audit:info:history']"
          >审批历史</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleProcessing(scope.row)"
            v-hasPermi="['audit:info:processing']"
          >进度查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['audit:info:remove']"
          >撤销</el-button>
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

    <!-- 添加或修改审核信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 历史信息弹窗 -->
    <el-dialog :title="history.title" :visible.sync="history.open" width="1000px" append-to-body>
      <el-table :data="history.list">
        <el-table-column label="名称" align="center" prop="name" />
        <el-table-column label="开始时间" align="center" prop="startTime" width="180"/>
        <el-table-column label="结束时间" align="center" prop="endTime" width="180"/>
        <el-table-column label="耗时" align="center" prop="duration" width="180"/>
        <el-table-column label="批注" align="center" show-overflow-tooltip prop="comment"/>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeHistoryDialog">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 查看进度 -->
    <el-dialog :title="processing.title" :visible.sync="processing.open" width="800px" append-to-body>
      <div v-html="processing.data"></div>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, getInfo, delInfo, listHistory, getProcessingImage } from "@/api/audit/info";

export default {
  name: "Info",
  dicts: ['bus_audit_status'],
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
      // 审核信息表格数据
      infoList: [],
      // 历史数据
      history: {
        list: [],
        title: '审批历史',
        open: false
      },
      // 查看进度
      processing: {
        data: null,
        title: '流程进度',
        open: false
      },
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 创建时间时间范围
      daterangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: null,
        createTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleProcessing(row) {
      getProcessingImage(row.instanceId).then(resp => {
        // {code: 200, msg: '', data: {}}
        // <xml>xxx
        this.processing.data = resp;
        this.processing.open = true;
      })
    },
    /** 展示历史记录弹窗 */
    handleHistory(row) {
      listHistory(row.instanceId).then(resp => {
        // 1. 将数据保存
        this.history.list = resp.data;
        // 2. 展示弹窗
        this.history.open = true;
      })
    },
    /** 关闭弹窗 */
    closeHistoryDialog() {
      this.history.list = [];
      this.history.open = false;
    },
    /** 查询审核信息列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
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
        serviceItemId: null,
        serviceItemName: null,
        serviceItemInfo: null,
        serviceItemPrice: null,
        instanceId: null,
        creatorId: null,
        info: null,
        status: null,
        createTime: null
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
      this.daterangeCreateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加审核信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改审核信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id;
      this.$modal.confirm('确认撤销"' + ids + '"的数据项？').then(function() {
        return delInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("撤销成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('audit/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
