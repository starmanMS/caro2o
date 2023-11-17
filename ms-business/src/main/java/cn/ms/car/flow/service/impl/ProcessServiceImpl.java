package cn.ms.car.flow.service.impl;

import cn.ms.car.flow.domain.BusBpmnInfo;
import cn.ms.car.flow.service.IProcessService;
import cn.ms.car.audit.domain.BusCarPackageAudit;
import cn.ms.car.audit.domain.vo.HandleAuditParam;
import cn.ms.car.audit.domain.vo.HistoryTask;
import cn.ms.car.common.utils.DateUtils;
import cn.ms.car.common.utils.IOUtils;
import cn.ms.car.common.utils.SecurityUtils;
import cn.ms.car.common.utils.StringUtils;
import cn.ms.car.common.utils.file.FileUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Service
public class ProcessServiceImpl implements IProcessService {

    public static final String ZIP_FILE_NAME = "zip";
    public static final List<String> ALLOW_EXT = Arrays.asList(ZIP_FILE_NAME, "bpmn", "xml");

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Override
    public BusBpmnInfo deploy(String bpmnFileName, InputStream bpmnFileStream) {
        // 文件类型：xxx.bpmn / xxx.zip
        String suffix = FileUtils.getFileSuffix(bpmnFileName);
        Assert.state(ALLOW_EXT.contains(suffix), "文件上传格式有误，仅支持 bpmn/xml/zip 格式的文件");

        DeploymentBuilder deployment = repositoryService.createDeployment();
        // 判断文件类型
        if (ZIP_FILE_NAME.equals(suffix)) {
            // zip 压缩包的文件
            deployment.addZipInputStream(new ZipInputStream(bpmnFileStream));
        } else {
            // 普通的 bpmn 文件
            deployment.addInputStream(bpmnFileName, bpmnFileStream);
        }

        // 部署流程定义
        Deployment deploy = deployment
                .disableSchemaValidation() /* 禁止检查 xml 文件规范 */
                .name(FileUtils.getNameNotSuffix(bpmnFileName)) /* 部署名称 */
                .deploy(); /* 部署操作 */

        // 结果对象
        BusBpmnInfo result = new BusBpmnInfo();
        result.setDeployTime(deploy.getDeploymentTime());

        // 查询流程定义数据
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .singleResult();

        Assert.notNull(definition, "部署资源异常");

        result.setBpmnLabel(definition.getName());
        result.setProcessDefinitionKey(definition.getKey());
        result.setVersion((long) definition.getVersion());
        return result;
    }

    @Override
    public InputStream getResourceAsStream(String processDefinitionKey, Long version, String type) {
        // 1. 查询流程定义对象
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .processDefinitionVersion(version.intValue())
                .singleResult();

        // 2. 获取流程资源文件
        if ("xml".equals(type)) {
            // 获取流程资源文件
            return repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        }

        // 3. 获取图片资源
        if (StringUtils.isNotEmpty(definition.getDiagramResourceName())) {
            String resourceName = definition.getDiagramResourceName();
            String suffix = FileUtils.getFileSuffix(resourceName);
            InputStream stream = repositoryService.getResourceAsStream(definition.getDeploymentId(), resourceName);

            // 如果之前上传的图片也是 svg 格式的，就不需要做 base64 的编码
            if ("svg".equals(suffix)) {
                return stream;
            }


            String base64 = IOUtils.inputStream2Base64(stream);
            String content = "data:image/png;base64," + base64;
            return new ByteArrayInputStream(content.getBytes());
        }

        // 4. 动态生成流程图
        ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        // 获取指定流程定义的 bpmn model 对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definition.getId());
        // 生成流程图，指定模型对象与字体
        return generator.generateDiagram(bpmnModel, "宋体", "宋体", "宋体");
    }

    @Override
    public void deleteDefinition(String processDefinitionKey, Long version) {
        // 查询到流程定义对象
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .processDefinitionVersion(version.intValue())
                .singleResult();

        // 调用 RepositoryService 进行删除
        repositoryService.deleteDeployment(definition.getDeploymentId());
    }

    @Override
    public BpmnModel getBpmnModel(String definitionKey, int version) {
        // 基于流程定义 key 和 version 获取流程定义对象
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(definitionKey)
                .processDefinitionVersion(version)
                .singleResult();

        // 获取指定流程定义的 bpmn 模型对象
        return repositoryService.getBpmnModel(definition.getId());
    }

    @Override
    public String startProcessInstance(String definitionKey, String businessKey, Map<String, Object> variables) {
        // 1. 调用 `RuntimeService` 开始流程实例（流程定义 key，版本号，业务标识，流程变量）
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(definitionKey, businessKey, variables);
        // 2. 返回新增的流程实例 id
        return instance.getId();
    }

    @Override
    public void deleteProcessInstance(String instanceId, String reason) {
        runtimeService.deleteProcessInstance(instanceId, reason);
    }

    @Override
    public List<String> selectBusinessKeyList(String processDefinitionKey, Long userId) {
        // 1. 基于流程定义 key + 版本以及当前用户 id 作为负责人，查询任务列表 => TaskService
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(userId + "")
                .list();

        if (CollectionUtils.isEmpty(list)) {
            // 如果查不到待办任务，直接返回空数据
            return Collections.emptyList();
        }

        // 2. 通过任务列表得到流程实例 id 列表
        Set<String> instanceIdList = new HashSet<>();
        for (Task task : list) {
            instanceIdList.add(task.getProcessInstanceId());
        }

        // 3. 基于流程实例 id 列表，查询流程实例列表，得到业务标识列表 => RuntimeService
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .processInstanceIds(instanceIdList)
                .list();

        // 4. 将流程实例集合转换为 businessKey 集合
        List<String> businessKey = new ArrayList<>();
        for (ProcessInstance instance : instances) {
            businessKey.add(instance.getBusinessKey());
        }
        return businessKey;
    }

    public List<String> selectBusinessKeyList1(String processDefinitionKey, Long userId) {
        return taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(userId + "")
                .list() // TODO 可能为空，会出现空指针
                .stream() // List<Task> => Stream<Task>
                .map(task ->
                        // TODO 存在性能问题，可以进行批量查询
                        runtimeService.createProcessInstanceQuery()
                                .processInstanceId(task.getProcessInstanceId()).singleResult()) // Stream<Task> => Stream<ProcessInstance>
                .map(ProcessInstance::getBusinessKey) // Stream<ProcessInstance> => Stream<String> TODO 有可能没有查到流程实例对象，也会空指针
                .collect(Collectors.toList()); // Stream<String> => List<String>
    }

    public List<String> selectBusinessKeyList2(String processDefinitionKey, Long userId) {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(userId + "")
                .list();

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        Set<String> instanceIdList = list.stream() // List<Task> => Stream<Task>
                .map(Task::getProcessInstanceId) // Stream<Task> => Stream<String>
                .collect(Collectors.toSet());

        return runtimeService.createProcessInstanceQuery()
                .processInstanceIds(instanceIdList)
                .list() // List<ProcessInstance>
                .stream() // List<ProcessInstance> => Stream<ProcessInstance>
                .map(ProcessInstance::getBusinessKey) // Stream<ProcessInstance> => Stream<String>
                .collect(Collectors.toList());
    }

    @Override
    public boolean handleTaskByInstanceIdAndAssignee(String instanceId, Long userId, HandleAuditParam param) {
        // 1. 基于流程实例 id + 当前用户 id 查询待办任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(instanceId)
                .taskAssignee(userId + "")
                .singleResult();
        Assert.notNull(task, "处理失败，任务不存在");

        // 2. 处理任务（流程变量），添加批注（根据状态添加）
        Map<String, Object> variables = new HashMap<>();
        // 是否通过
        variables.put("shopOwnerAudit", param.isPassed());
        // 添加批注
        String username = SecurityUtils.getUsername();
        String msg = username + "[" + (param.isPassed() ? "通过" : "拒绝") + "], " + param.getInfo();
        taskService.addComment(task.getId(), instanceId, msg);
        // 完成任务
        taskService.complete(task.getId(), variables);
        // 3. 查询是否还有下一个节点
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        return instance == null;
    }

    @Override
    public List<String> selectBusinessKeyList(String processDefinitionKey, Long userId, String type) {
        // 1. 根据流程信息对象 + 当前用户查询已完成的历史任务
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(userId + "");

        // 如果是已办，查询所有已经完成的任务
        if (BusCarPackageAudit.DONE_LIST.equals(type)) {
            taskInstanceQuery.finished(); // end_time is not null
        } else {
            // 如果是待办，查询所有没完成的任务
            taskInstanceQuery.unfinished(); // end_time is null
        }

        List<HistoricTaskInstance> historyTask = taskInstanceQuery.list();

        // 2. 如果查不到，直接返回空集合
        if (CollectionUtils.isEmpty(historyTask)) {
            return Collections.emptyList();
        }

        // 3. 将任务对象转换为流程实例 id 集合
        Set<String> processInstanceIds = historyTask.stream()
                .map(HistoricTaskInstance::getProcessInstanceId) // HistoricTaskInstance => processInstanceId
                .collect(Collectors.toSet());

        // 4. 根据流程实例 id 集合，查询流程实例集合
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceIds(processInstanceIds)
                .list();

        // 5. 将流程实例集合，转换为 businessKey 集合
        return historicProcessInstances.stream()
                .map(HistoricProcessInstance::getBusinessKey) // HistoricProcessInstance => businessKey
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoryTask> selectHistoryTaskListByInstanceId(String instanceId) {
        // 1. 基于流程实例 id 查询所有历史任务
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .list();

        // 2. 判断为空返回空集合
        if (CollectionUtils.isEmpty(taskInstances)) {
            return Collections.emptyList();
        }

        // 3. 将 Activiti 的任务模型对象转换为自己封装的对象
        // 4. 返回集合

        /*return taskInstances.stream()
                .map(t -> {
                    HistoryTask task = new HistoryTask();
                    task.setDuration(DateUtils.getDatePoor(t.getDurationInMillis()));
                    task.setEndTime(t.getEndTime());
                    task.setStartTime(t.getStartTime());
                    task.setName(t.getName());
                    // 查询批注
                    List<Comment> comments = taskService.getTaskComments(t.getId());
                    if (!CollectionUtils.isEmpty(comments)) {
                        task.setComment(comments.get(0).getFullMessage());
                    }

                    return task;
                })
                .collect(Collectors.toList());*/
        List<HistoryTask> tasks = new ArrayList<>();
        for (HistoricTaskInstance t : taskInstances) {
            HistoryTask task = new HistoryTask();
            // 设置耗时
            if (t.getDurationInMillis() != null) {
                task.setDuration(DateUtils.getDatePoor(t.getDurationInMillis()));
            }
            task.setEndTime(t.getEndTime());
            task.setStartTime(t.getStartTime());
            task.setName(t.getName());

            // 如果有删除理由，说明该任务是被用户删除的，肯定就没有任务批注
            String reason = t.getDeleteReason();
            if (StringUtils.isNotEmpty(reason)) {
                task.setComment(reason);
            } else {
                // 查询批注：任务服务进行查询
                List<Comment> comments = taskService.getTaskComments(t.getId());
                if (!CollectionUtils.isEmpty(comments)) {
                    task.setComment(comments.get(0).getFullMessage());
                }
            }

            // 将创建的任务添加到需要返回的集合中
            tasks.add(task);
        }

        return tasks;
    }

    @Override
    public InputStream getProcessingImageByInstanceId(String instanceId) {
        // 1. 基于流程实例 id 查询流程实例对象
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        Assert.notNull(processInstance, "参数错误，流程实例不存在");

        // 2. 基于流程定义 id 查询 `BpmnModel` 对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        // 3. 查询当前流程实例高亮活动 id 集合
        /*List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId)
                .activityType("userTask")
                .unfinished()
                .list();

        List<String> actIds = new ArrayList<>();
        for (HistoricActivityInstance act : activityInstances) {
            actIds.add(act.getActivityId());
        }*/
        List<String> activeActivityIds = null;
        // 判断流程是否没有结束
        if (processInstance.getEndTime() == null) {
            activeActivityIds = runtimeService.getActiveActivityIds(instanceId);
        } else {
            activeActivityIds = Collections.emptyList();
        }

        // 4. 生成图片（BpmnModel、高亮的活动id集合、字体）
        return new DefaultProcessDiagramGenerator()
                .generateDiagram(bpmnModel, activeActivityIds, Collections.emptyList(), "宋体", "宋体", "宋体");
    }
}
