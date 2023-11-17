package cn.ms.car.flow.service;

import cn.ms.car.audit.domain.vo.HandleAuditParam;
import cn.ms.car.audit.domain.vo.HistoryTask;
import cn.ms.car.flow.domain.BusBpmnInfo;
import org.activiti.bpmn.model.BpmnModel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 工作流程处理服务
 */
public interface IProcessService {

    /**
     * 部署流程定义
     *
     * @param bpmnFileName   流程文件名称
     * @param bpmnFileStream 流程文件输入流对象
     * @return 部署结果
     */
    BusBpmnInfo deploy(String bpmnFileName, InputStream bpmnFileStream);

    /**
     * 获取流程定义资源文件
     *
     * @param processDefinitionKey 流程定义 key
     * @param version              流程定义版本
     * @param type                 资源文件类型（xml/image）
     * @return 资源流对象
     */
    InputStream getResourceAsStream(String processDefinitionKey, Long version, String type);

    void deleteDefinition(String processDefinitionKey, Long version);

    /**
     * 获取 bpmn 模型对象
     *
     * @param definitionKey 流程定义 key
     * @param version       流程定义版本
     * @return 模型对象
     */
    BpmnModel getBpmnModel(String definitionKey, int version);

    /**
     * 开始流程实例
     *
     * @param definitionKey 流程定义 key
     * @param businessKey   业务标识
     * @param variables     流程变量
     * @return 流程实例 id
     */
    String startProcessInstance(String definitionKey, String businessKey, Map<String, Object> variables);

    /**
     * 删除流程实例
     *
     * @param instanceId 流程实例 id
     * @param reason     删除理由
     */
    void deleteProcessInstance(String instanceId, String reason);

    /**
     * 查询业务标识列表
     *
     * @param processDefinitionKey 流程定义 key
     * @param userId               用户 id
     * @return 业务标识列表
     */
    List<String> selectBusinessKeyList(String processDefinitionKey, Long userId);

    /**
     * 处理任务
     *
     * @param instanceId 流程实例 id
     * @param userId     审核人 id
     * @param param      审核参数
     * @return 是否审核结束
     */
    boolean handleTaskByInstanceIdAndAssignee(String instanceId, Long userId, HandleAuditParam param);

    /**
     * 查询业务标识列表
     *
     * @param processDefinitionKey 流程定义 key
     * @param userId               负责人 id
     * @param type                 类型（TODO/DONE）
     * @return 业务标识列表
     */
    List<String> selectBusinessKeyList(String processDefinitionKey, Long userId, String type);

    /**
     * 查询历史任务集合
     *
     * @param instanceId 流程实例 id
     * @return
     */
    List<HistoryTask> selectHistoryTaskListByInstanceId(String instanceId);

    /**
     * 获取处理中的图片
     *
     * @param instanceId 流程实例 id
     * @return 生成的图片流数据
     */
    InputStream getProcessingImageByInstanceId(String instanceId);
}
