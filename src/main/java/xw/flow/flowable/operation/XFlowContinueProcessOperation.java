package xw.flow.flowable.operation;

import java.util.List;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.ReceiveTask;
import org.flowable.bpmn.model.ScriptTask;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.agenda.ContinueProcessOperation;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.flowable.engine.impl.history.HistoryManager;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.interceptor.MigrationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.bean.FlowVariable;
import xw.flow.constants.FlowContext;
import xw.flow.entity.XWorkActivity;
import xw.flow.entity.XWorkInstance;
import xw.flow.entity.XWorkTimer;

/**
 * ContinueProcessOperation是Flowable引擎内部的"流程推进器"，由引擎自动触发和管理，用于在流程节点完成后推动流程继续执行；
 * - 根据BPMN中为序列流设置条件，ContinueProcessOperation委托TakeOutgoingSequenceFlowsOperation选择符合条件的路径；
 * - TakeOutgoingSequenceFlowsOperation专门用来处理与路由/路径相关的逻辑；
 */
public class XFlowContinueProcessOperation extends ContinueProcessOperation {
	private static Logger LOGGER = LoggerFactory.getLogger(XFlowContinueProcessOperation.class);

	public XFlowContinueProcessOperation(CommandContext commandContext, ExecutionEntity execution, boolean forceSynchronousOperation, boolean inCompensation, MigrationContext migrationContext) {
		super(commandContext, execution, forceSynchronousOperation, inCompensation, migrationContext);
	}

	public XFlowContinueProcessOperation(CommandContext commandContext, ExecutionEntity execution) {
		super(commandContext, execution);
	}

    /**
     * 在节点完成后执行的，用于推动流程从当前节点流转到下一个节点，属于“节点完成后”的流程推进逻辑，增加如下处理：
     * -
     * @param flowNode
     */
	@Override
	protected void executeSynchronous(FlowNode flowNode) {
		CommandContextUtil.getActivityInstanceEntityManager(commandContext).recordActivityStart(execution);

		/**
		 * 如果当前FlowNode是Task, 则需要建立XWorkActivity记录:
		 * - XWorkActivity用来记录ExecutionEntity的路由/状态;
		 * - XWorkActivity使用FlowContext存放局部变量;
		 */
		HistoryManager historyManager = CommandContextUtil.getHistoryManager(commandContext);
		if (flowNode instanceof UserTask || flowNode instanceof ScriptTask) {
			HistoricActivityInstance hisActivity = historyManager.findHistoricActivityInstance(execution, false);
			if (hisActivity == null)
				throw new XException("Please enable HistoryLevel for ACTIVITY Level.");

			XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(execution);
			if (workInstance == null)
				throw new XException("Related XWorkInstance was not found.");

			XWorkActivity workActivity = XWorkActivity.newInstance(workInstance, hisActivity);
			if (flowNode instanceof UserTask) {
				UserTask userTask = (UserTask) flowNode;
				String necessity = XFlowDefinitionHelper.getNecessity(userTask);
				if (FlameUtils.isNotBlank(necessity)) {
					workActivity.setNecessity(necessity);
				}
				if (FlameUtils.isNotBlank(userTask.getFormKey())) {
					workActivity.setTaskForm(userTask.getFormKey());
				}
				List<FlowVariable> variables = XFlowDefinitionHelper.getVariables(userTask);
				FlowContext flowContext = workActivity.getFlowContext();
				for (FlowVariable variable : variables) {
					flowContext.addVariable(variable);
				}
			}
			workActivity = PersistenceHelper.service().save(workActivity);
			/**
			 * 将XWorkActivity对象分配给ExecutionEntity的externalObject属性
			 */
            execution.setExternalObject(workActivity);
		} else if (flowNode instanceof ReceiveTask) {
			HistoricActivityInstance hisActivity = historyManager.findHistoricActivityInstance(execution, false);
			if (hisActivity == null)
				throw new XException("Please enable HistoryLevel for ACTIVITY Level.");

			XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(execution);
			if (workInstance == null)
				throw new XException("Related XWorkInstance was not found.");

			XWorkTimer workTimer = XWorkTimer.newInstance(workInstance, hisActivity);
            workTimer = PersistenceHelper.service().save(workTimer);
            /**
             * 将XWorkTimer对象分配给ExecutionEntity的externalObject属性
             */
            execution.setExternalObject(workTimer);
		}
		
        // Execution listener: event 'start'
        if (CollectionUtil.isNotEmpty(flowNode.getExecutionListeners())) {
            try {
                executeExecutionListeners(flowNode, ExecutionListener.EVENTNAME_START);
            } catch (BpmnError bpmnError) {
                ErrorPropagation.propagateError(bpmnError, execution);
                return;
            }
        }

        // Create any boundary events, sub process boundary events will be created from the activity behavior
        List<ExecutionEntity> boundaryEventExecutions = null;
        List<BoundaryEvent> boundaryEvents = null;
        if (!inCompensation && flowNode instanceof Activity) { // Only activities can have boundary events
            boundaryEvents = ((Activity) flowNode).getBoundaryEvents();
            if (CollectionUtil.isNotEmpty(boundaryEvents)) {
                boundaryEventExecutions = createBoundaryEvents(boundaryEvents, execution);
            }
        }

        // Execute actual behavior
        ActivityBehavior activityBehavior = (ActivityBehavior) flowNode.getBehavior();

        if (activityBehavior != null) {
            executeActivityBehavior(activityBehavior, flowNode);
            executeBoundaryEvents(boundaryEvents, boundaryEventExecutions);
        } else {
            executeBoundaryEvents(boundaryEvents, boundaryEventExecutions);
            LOGGER.debug("No activityBehavior on activity '{}' with execution {}", flowNode.getId(), execution.getId());
            CommandContextUtil.getAgenda().planTakeOutgoingSequenceFlowsOperation(execution, true);
        }
	}
}
