package xw.flow.flowable.operation;

import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.FlowableEngineAgendaFactory;
import org.flowable.engine.impl.agenda.DefaultFlowableEngineAgenda;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

/**
 * FlowableEngineAgenda是Operation的注册类型, Operation用来控制Flowable流程节点的执行;
 * 若要自定义新的Operation去操作流程节点的执行, 就需要把自定义的Operation在ActivitiEngineAgenda中实现, 然后通过FlowableEngineAgendaFactory把Agenda注册进ProcessEngineConfiguration中;
 * XFlowFlowableEngineAgenda就是为了注册XFlowTakeOutgoingSequenceFlowsOperation, 以实现通过路由的方式控制流程的执行流向;
 */
public class XFlowableEngineAgenda extends DefaultFlowableEngineAgenda {
    public static class XFlowableEngineAgendaFactory implements FlowableEngineAgendaFactory {
        @Override
        public FlowableEngineAgenda createAgenda(CommandContext commandContext) {
            return new XFlowableEngineAgenda(commandContext);
        }
    }

    public XFlowableEngineAgenda(CommandContext commandContext) {
        super(commandContext);
    }

    @Override
    public void planContinueProcessOperation(ExecutionEntity execution) {
        planOperation(new XFlowContinueProcessOperation(commandContext, execution));
    }

    /**
     * BpmnActivityBehavior在执行performOutgoingBehavior()时, 会调用下面代码, 最终会执行此方法以驱动流程流向下一个节点
     * - CommandContextUtil.getAgenda().planTakeOutgoingSequenceFlowsOperation(execution, true);
     * @param execution
     * @param evaluateConditions
     */
    @Override
    public void planTakeOutgoingSequenceFlowsOperation(ExecutionEntity execution, boolean evaluateConditions) {
        planOperation(new XFlowTakeOutgoingOperation(commandContext, execution, evaluateConditions, false));
    }

    @Override
    public void planEndExecutionOperation(ExecutionEntity execution) {
        planOperation(new XFlowEndExecutionOperation(commandContext, execution));
    }
}
