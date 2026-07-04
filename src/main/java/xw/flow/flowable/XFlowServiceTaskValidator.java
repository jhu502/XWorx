package xw.flow.flowable;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.impl.ServiceTaskValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 为ServiceTask新增加一种类型：thing, 默认得类型校验只支持: mail、mule、shell、camel、dmn
 */
public class XFlowServiceTaskValidator extends ServiceTaskValidator {
    protected void verifyType(Process process, ServiceTask serviceTask, List<ValidationError> errors) {
        if (StringUtils.isNotEmpty(serviceTask.getType())) {
            System.out.println("-----------XS:" + serviceTask.getType());
            if (!serviceTask.getType().equalsIgnoreCase("thing")) {
                super.verifyType(process, serviceTask, errors);
            }
        }
    }
}
