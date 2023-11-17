<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"/>
    </el-row>

    <el-table v-loading="loading" :data="infoList">
      <el-table-column label="套餐名称" align="center" prop="serviceItemName"/>
      <el-table-column label="套餐价格" align="center" prop="serviceItemPrice"/>
      <el-table-column label="套餐备注" align="center" show-overflow-tooltip prop="serviceItemInfo"/>
      <el-table-column label="审核备注" align="center" show-overflow-tooltip prop="info"/>
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
          >审批历史
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleProcessing(scope.row)"
            v-hasPermi="['audit:info:processing']"
          >进度查看</el-button>
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
        <el-form-item label="审批意见" prop="passed">
          <el-select v-model="form.passed" placeholder="请选择审批意见">
            <el-option label="同意" :value="true"/>
            <el-option label="拒绝" :value="false"/>
          </el-select>
        </el-form-item>
        <el-form-item label="批注" prop="info">
          <el-input type="textarea" v-model="form.info" placeholder="请输入批注"/>
        </el-form-item>
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
      <div v-html="processing.data"/>
    </el-dialog>
  </div>
</template>

<script>
  import {doneList} from "@/api/audit/done";
  import { listHistory, getProcessingImage } from "@/api/audit/info";

  export default {
    name: "doneList",
    dicts: ['bus_audit_status'],
    data() {
      return {
        // 遮罩层
        loading: true,
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
        title: "流程审核",
        // 是否显示弹出层
        open: false,
        form: {},
        rules: {
          passed: [
            {required: true, message: '请选择是否通过', trigger: 'change'}
          ],
          info: [{required: true, message: '请输入批注', trigger: 'blur'}]
        },
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10
        },
      };
    },
    created() {
      this.getList();
    },
    methods: {
      handleProcessing(row) {
        getProcessingImage(row.instanceId).then(resp => {
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
      /** 查询待办任务列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        doneList(this.queryParams).then(response => {
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
          auditId: null,
          passed: true,
          info: null
        };
        this.resetForm("form");
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
      },
      /** 提交按钮 */
      submitForm() {
        doAudit(this.form).then(() => {
          // 提示成功
          this.$modal.msgSuccess('提交成功');
          // 刷新表格
          this.getList();
          // 关闭弹窗
          this.open = false;
        });
      }
    }
  };
</script>
