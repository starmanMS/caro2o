<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="部署时间">
        <el-date-picker
          v-model="daterangeDeployTime"
          style="width: 360px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="流程类型" prop="bpmnType">
        <el-select v-model="queryParams.bpmnType" placeholder="请选择流程类型" clearable>
          <el-option
            v-for="dict in dict.type.bus_flow_audit_type"
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
          icon="el-icon-refresh"
          size="mini"
          @click="bpmnDesigner = true"
          v-hasPermi="['workflow:bpmnInfo:deploy']"
        >设计流程图
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleDeploy"
          v-hasPermi="['flow:bpmnInfo:add']"
        >流程定义部署
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bpmnInfoList">
      <el-table-column label="流程名称" align="center" prop="bpmnLabel"/>
      <el-table-column label="流程类型" align="center" prop="bpmnType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bus_flow_audit_type" :value="scope.row.bpmnType"/>
        </template>
      </el-table-column>
      <el-table-column label="流程定义key" align="center" prop="processDefinitionKey"/>
      <el-table-column label="部署时间" align="center" prop="deployTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deployTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="version"/>
      <el-table-column label="描述信息" align="center" prop="info"/>
      <el-table-column label="流程文件" align="center" prop="bpmnFile">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="handlePreview(scope.row)"
          >查看
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="流程图" align="center" prop="bpmnImg">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="handleImage(scope.row)"
          >查看
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['flow:bpmnInfo:remove']"
          >撤销
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

    <!-- 添加或修改流程定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="流程类型" prop="bpmnType">
          <el-select v-model="form.bpmnType" placeholder="请选择流程类型">
            <el-option
              v-for="dict in dict.type.bus_flow_audit_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="流程定义文件" prop="bpmnFile">
          <el-upload
            ref="upload"
            :limit="1"
            :on-change="handleChange"
            :on-remove="handleRemove"
            :action="uploadUrl"
            :file-list="fileList"
            :auto-upload="false">

            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <div slot="tip" class="el-upload__tip">只能上传bpmn/xml/zip文件，且不超过5M</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述信息" prop="info">
          <el-input type="textarea" v-model="form.info" placeholder="请输入描述信息"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 预览界面 -->
    <el-dialog :title="preview.title" :visible.sync="preview.open" width="80%" top="5vh" append-to-body
               class="scrollbar">
      <pre><code class="hljs" v-html="highlightedCode('xml')"/></pre>
    </el-dialog>

    <!-- 预览界面 -->
    <el-dialog :title="bpmnImage.title" :visible.sync="bpmnImage.open" width="80%" top="5vh" append-to-body
               class="scrollbar">
      <div v-if="bpmnImage.svg" v-html="bpmnImage.data"></div>
      <img v-else :src="bpmnImage.data" alt="流程图">
    </el-dialog>

    <!-- 流程图设计 -->
    <el-dialog title="流程图设计" :visible.sync="bpmnDesigner" width="1200px" append-to-body>
      <vue-bpmn style="overflow: hidden; height: 700px;" product="activiti"/>
    </el-dialog>
  </div>
</template>

<script>
  import {delBpmnInfo, deployBpmn, getBpmnFile, listBpmnInfo} from "@/api/flow/bpmnInfo";
  import hljs from "highlight.js/lib/highlight";
  import "highlight.js/styles/github-gist.css";
  import VueBpmn from '@/components/bpmn/VueBpmn';

  hljs.registerLanguage("xml", require("highlight.js/lib/languages/xml"));

  export default {
    name: "BpmnInfo",
    dicts: ['bus_flow_audit_type'],
    components: {
      VueBpmn
    },
    data() {
      return {
        bpmnDesigner: false,
        bpmnImage: {
          open: false,
          title: '流程图',
          data: null
        },
        preview: {
          title: '流程文件',
          open: false,
          code: '<h1>这是一个测试的代码块</h1>'
        },
        // 文件上传地址
        uploadUrl: '',
        // 文件上传列表
        fileList: [],
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
        // 流程定义表格数据
        bpmnInfoList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 描述信息时间范围
        daterangeDeployTime: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          bpmnType: null,
          deployTime: null,
        },
        // 表单参数
        form: {
          bpmnType: null,
          bpmnFile: null,
          info: null
        },
        // 表单校验
        rules: {
          bpmnType: [
            {required: true, message: '请选择审核类型', trigger: 'change'}
          ],
          info: [
            {required: true, message: '请输入备注信息', trigger: 'blur'}
          ]
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      handleImage(row) {
        getBpmnFile(row.id, 'image').then(resp => {
          let regex = /(<\?xml\b[^>]*>[^<]*)?(?:<! .*? >[^<]*)*(<svg|<!DOCTYPE svg)/;
          this.bpmnImage.data = resp;
          this.bpmnImage.svg = false;

          if (resp.search(regex) >= 0) {
            this.bpmnImage.svg = true;
          }

          this.bpmnImage.open = true;
        });
      },
      handlePreview(row) {
        // 发送请求到后端获取 xml 代码
        getBpmnFile(row.id, 'xml').then(resp => {
          this.preview.code = resp;
          this.preview.open = true;
        });
      },
      /** 高亮显示 */
      highlightedCode(language) {
        const result = hljs.highlight(language, this.preview.code || "", true);
        return result.value || '&nbsp;';
      },
      handleBeforeUpload(file) {
        // 1. 获取文件后缀
        let fileNameArr = file.name.split('.');
        let fileExt = fileNameArr[fileNameArr.length - 1];
        // 2. 判断是否为允许的后缀类型
        if (['bpmn', 'zip', 'xml'].indexOf(fileExt) < 0) {
          // 3. 如果类型不匹配，提示错误消息
          this.$modal.msgError('文件类型不匹配');
          return false;
        }

        return true;
      },
      /** 查询流程定义列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeDeployTime && '' != this.daterangeDeployTime) {
          this.queryParams.params["beginDeployTime"] = this.daterangeDeployTime[0];
          this.queryParams.params["endDeployTime"] = this.daterangeDeployTime[1];
        }
        listBpmnInfo(this.queryParams).then(response => {
          this.bpmnInfoList = response.rows;
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
          bpmnType: null,
          info: null
        };
        this.fileList = [];
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.daterangeDeployTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 部署流程定义 */
      handleDeploy() {
        this.reset();
        this.open = true;
        this.title = "部署流程定义";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getBpmnInfo(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改流程定义";
        });
      },
      handleChange(file, fileList) {
        this.fileList = fileList;
      },
      handleRemove(file) {
        this.fileList = []
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => { // 参数校验
          if (valid) {
            // 校验文件是否正确
            if (this.fileList.length === 0) {
              this.$modal.msgError('请选择要部署的文件');
              return false;
            }
            if (!this.handleBeforeUpload(this.fileList[0])) { // 文件上传之前的判断
              return false;
            }

            // 提交数据
            deployBpmn({
              ...this.form, // 对象展开，快速将 form 对象中的所有属性全部加入到新的对象中
              bpmnFile: this.fileList[0].raw // 二进制文件数据
            }).then(() => {
              this.$modal.msgSuccess('部署流程定义成功');
              this.open = false; // 关闭弹窗
              this.getList(); // 刷新表格
            });
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认撤销流程定义编号为"' + ids + '"的数据项？').then(function () {
          return delBpmnInfo(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("撤销成功");
        }).catch(() => {
        });
      }
    }
  };
</script>
