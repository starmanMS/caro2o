package cn.ms.car.audit.utils;

import cn.ms.car.common.utils.StringUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;

import java.math.BigDecimal;
import java.util.Collection;

public class ActivitiUtils {

    public static BigDecimal getDiscountAmount(BpmnModel bpmnModel, String definitionKey) {
        // 获取对应的 process
        Process process = bpmnModel.getProcessById(definitionKey);
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
                    return new BigDecimal(split[split.length - 1].replace("}", ""));
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
