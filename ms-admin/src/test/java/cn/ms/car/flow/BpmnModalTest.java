package cn.ms.car.flow;

import cn.ms.car.common.utils.StringUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

@SpringBootTest
public class BpmnModalTest {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testGetBpmnModal() {
        // 基于 key 和版本号获取流程定义对象
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("car_package_audit")
                .processDefinitionVersion(1)
                .singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(definition.getId());
        // 获取对应的 process
        Process process = bpmnModel.getProcessById(definition.getKey());
        Collection<FlowElement> elements = process.getFlowElements();
        for (FlowElement element : elements) {
            // 如果当前是箭头类型
            if (element instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow) element;
                // 获取箭头中的条件表达式
                String conditionExpression = flow.getConditionExpression();
                // 判断条件不为空，且以 ${discountPrice 开头
                if (StringUtils.isNotEmpty(conditionExpression) && conditionExpression.startsWith("${discountAmount")) {
                    String[] split = conditionExpression.split(" |>=");
                    new BigDecimal(split[split.length - 1].replace("}", ""));
                }
            }
        }

        System.out.println(bpmnModel);
    }
}
