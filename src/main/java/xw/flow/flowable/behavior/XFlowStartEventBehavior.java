package xw.flow.flowable.behavior;

import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;
import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.bean.FlowVariable;
import xw.flow.entity.XWorkInstance;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;

/**
 * 在StartEvent中, 用来初始化XWorkInstance的环境：
 * - 从ProcessDefinition中获取全局变量;
 * -
 */
public class XFlowStartEventBehavior extends NoneStartEventActivityBehavior {
	private static final long serialVersionUID = 1L;

	public void execute(DelegateExecution execution) {
        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(executionEntity);
        if (workInstance == null)
            throw new XException("Related XWorkInstance was not found.");

        ProcessInstance processInstance = executionEntity.getProcessInstance();

        Process process = ProcessDefinitionUtil.getProcess(processInstance.getProcessDefinitionId());
        List<FlowVariable> flowVariables = XFlowDefinitionHelper.getVariables(process);
        for (FlowVariable variable : flowVariables) {
            workInstance.getFlowContext().addVariable(variable);
        }
        workInstance.setProcessInstance(processInstance);
        PersistenceHelper.service().save(workInstance);
        super.leave(executionEntity);
    }
}
