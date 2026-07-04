package xw.flow.flowable.operation;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.constants.FlowConstant;
import xw.flow.entity.XWorkInstance;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.agenda.EndExecutionOperation;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

/**
 * 修改EndEvent的默认行为, EndEvent执行结束后,并不会自动终止流程中其他Open状态的流程节点
 */
public class XFlowEndExecutionOperation extends EndExecutionOperation {
    public XFlowEndExecutionOperation(CommandContext commandContext, ExecutionEntity execution) {
        super(commandContext, execution);
    }

    @Override
    protected void handleRegularExecution() {
        EndEvent endEvent = (EndEvent) execution.getCurrentFlowElement();
        String configType = XFlowDefinitionHelper.getNodeConfig(endEvent, FlowConstant.TYPE);
        if ("endEvent".equals(configType)) {
            super.handleRegularExecution();
            XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(execution);
            XFlowExecutionHelper.execution().closeXWorkInstance(workInstance);
        } else if ("groundEvent".equals(configType)) {
            super.handleRegularExecution();
        } else {
            super.handleRegularExecution();
        }
    }
}
