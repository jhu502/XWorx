package xw.flow.flowable.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.ScriptTask;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.logging.LoggingSessionConstants;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.agenda.TakeOutgoingSequenceFlowsOperation;
import org.flowable.engine.impl.bpmn.helper.SkipExpressionUtil;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.BpmnLoggingSessionUtil;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.condition.ConditionUtil;
import org.flowable.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.flame.orm.PersistenceHelper;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.bean.FlowVariable;
import xw.flow.entity.XWorkActivity;
import xw.flow.entity.XWorkInstance;

/**
 * TakeOutgoingSequenceFlowsOperation时决定流程流向的核心类, 用于获取流程节点的所有outgoing顺序流(流出箭头):
 * - 基于UserTask路由(Routes)选择+SequenceFlow路由, 决定流程的流向;
 * - 基于ScriptTask的XFlowContext.FLOW_RETURN变量值+SequenceFlow路由, 决定流程的流向;
 * - 将TransientVariablesLocal中的FlowVariable写入进XWorkActivity和XWorkInstance中;
 *
 * XFlowContext.FLOW_RETURN变量值来源于ScriptTask表达式的执行返回;
 * - Gateway后的流向由:XFlowExclusiveGatewayBehavior来决定;
 */
public class XFlowTakeOutgoingOperation extends TakeOutgoingSequenceFlowsOperation {
	private static final Logger logger = LoggerFactory.getLogger(TakeOutgoingSequenceFlowsOperation.class);

	public XFlowTakeOutgoingOperation(CommandContext commandContext, ExecutionEntity executionEntity, boolean evaluateConditions, boolean forcedSynchronous) {
		super(commandContext, executionEntity, evaluateConditions, forcedSynchronous);
	}

	@Override
	protected void leaveFlowNode(FlowNode flowNode) {
		logger.debug("Leaving flow node {} with id '{}' by following it's {} outgoing sequenceflow", flowNode.getClass(), flowNode.getId(), flowNode.getOutgoingFlows().size());
		// Get default sequence flow (if set)
		String defaultSequenceFlowId = null;
		if (flowNode instanceof Activity) {
			((Activity) flowNode).getId();
			defaultSequenceFlowId = ((Activity) flowNode).getDefaultFlow();
		} else if (flowNode instanceof Gateway) {
			defaultSequenceFlowId = ((Gateway) flowNode).getDefaultFlow();
		}

		/** 流程的流向由outgoingSequenceFlows变量中存放的SequenceFlow决定 */
		List<SequenceFlow> outgoingSequenceFlows = new ArrayList<SequenceFlow>();
		for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
			/** 若SequenceFlow上包含有Route信息, 则需要判断XWorkTask的投票结果中是否包含该路由 */
			List<String> routeList = XFlowDefinitionHelper.getRoutes(sequenceFlow);
			if (routeList.isEmpty()) {
				/** 如果XWorkTask的投票结果中不存在路由信息，则执行Flowable自身的规则 */
				String skipExpression = sequenceFlow.getSkipExpression();
				if (!SkipExpressionUtil.isSkipExpressionEnabled(skipExpression, sequenceFlow.getId(), execution, this.commandContext)) {
					if (evaluateConditions) {
						if (ConditionUtil.hasTrueCondition(sequenceFlow, execution) && !sequenceFlow.getId().equals(defaultSequenceFlowId)) {
							outgoingSequenceFlows.add(sequenceFlow);
						}
					} else {
						outgoingSequenceFlows.add(sequenceFlow);
					}
				} else if (flowNode.getOutgoingFlows().size() == 1 || SkipExpressionUtil.shouldSkipFlowElement(skipExpression, sequenceFlow.getId(), execution, commandContext)) {
					// The 'skip' for a sequence flow means that we skip the condition, not the sequence flow.
					outgoingSequenceFlows.add(sequenceFlow);
				}
			} else {
				if (flowNode instanceof UserTask) {
					XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
					/** 对XWorkActivity选择的路由和SequenceFlow上定义的路由求交集, 如果交集不为空, 则当前SequenceFlow命中; */
					Collection<?> routeInterSet = CollectionUtils.intersection(workActivity.getRouteSet(), routeList);
					if (!routeInterSet.isEmpty()) {
						outgoingSequenceFlows.add(sequenceFlow);
					}
				} else if (flowNode instanceof ScriptTask) {
					/** ScriptTask支持Route选择: ScriptTask的Expression的执行返回值如果与当前SequenceFlow的Route一致, OutGoing将包含SequenceFlow */
					XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
					Collection<?> routeInterSet = CollectionUtils.intersection(workActivity.getRouteSet(), routeList);
					if (!routeInterSet.isEmpty()) {
						outgoingSequenceFlows.add(sequenceFlow);
					}
				} else {
					outgoingSequenceFlows.add(sequenceFlow);
				}
			}
		}

		// Check if there is a default sequence flow
		if (evaluateConditions) {
			if (outgoingSequenceFlows.size() == 0 && defaultSequenceFlowId != null) { // The elements that set this to false also have no support for default sequence flow
				for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
					if (defaultSequenceFlowId.equals(sequenceFlow.getId())) {
						outgoingSequenceFlows.add(sequenceFlow);
						break;
					}
				}
			}
		}

		// No outgoing found. Ending the execution
		if (outgoingSequenceFlows.isEmpty()) {
			this.persistExecutionVariable(execution);
			/** 如果当前flowNode是EndEvent, 则执行EndExecutionOperation */
			if (flowNode instanceof EndEvent) {
				agenda.planEndExecutionOperation(execution);
			} else {
				throw new FlowableException("No outgoing sequence flow of element '" + flowNode.getId() + "' could be selected for continuing the process");
			}
		} else {
			// Leave, and reuse the incoming sequence flow, make executions for all the others (if applicable)
			ProcessEngineConfigurationImpl engineConfigurationImpl = CommandContextUtil.getProcessEngineConfiguration(commandContext);
			ExecutionEntityManager executionEntityManager = engineConfigurationImpl.getExecutionEntityManager();
			RuntimeService runtimeService = engineConfigurationImpl.getRuntimeService();
			List<ExecutionEntity> outgoingExecutions = new ArrayList<>(flowNode.getOutgoingFlows().size());

			boolean bool = true;
			for (SequenceFlow outgoingSequenceFlow : outgoingSequenceFlows) {
				FlowElement nextElement = outgoingSequenceFlow.getTargetFlowElement();
				List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(execution.getProcessInstanceId()).activityId(nextElement.getId()).list();
				/**
				 * XFlow引入了驳回功能, 当UserTask驳回后再次提交时, 有可能前次驳回“正在运行”的节点被再次进入, 导致重复进入的问题；
				 * - 检查SequenceFlow的下游节点是否是“正在运行”的状态, 若是“正在运行”状态, OutGoing不包含此SequenceFlow;
				 * - executions如果不是空, 说明下游的节点是Running状态
				 */
				if (executions.isEmpty()) {
					/**
					 * Flowable在流向下一个节点时, 下一个节点的Execution会优先重用历史的Execution, 在数据库中的表现就是：
					 * - 同一个Flowable流程的大部分Task/Event/Gateway节点的Execution的id都是一样的;
					 */
					if (bool) { // Reuse existing one, true:原有的Execution还未重用过
						execution.setCurrentFlowElement(outgoingSequenceFlow);
						execution.setActive(false);
						outgoingExecutions.add(execution);
						bool = false;
					} else {
						/**
						 * 如果存在多个下游分支, 并且存在的ExecutionEntity已经重用过, 则创建新的ExecutionEntity
						 */
						ExecutionEntity parent = execution.getParentId() != null ? execution.getParent() : execution;
						ExecutionEntity outgoingExecutionEntity = executionEntityManager.createChildExecution(parent);
						outgoingExecutionEntity.setActive(false);
						outgoingExecutionEntity.setCurrentFlowElement(outgoingSequenceFlow);
						executionEntityManager.insert(outgoingExecutionEntity);
						outgoingExecutions.add(outgoingExecutionEntity);
					}
				}
			}

			/**
			 * 将执行的变量值持久保存到XWorkActivity和XWorkInstance对象中
			 */
			this.persistExecutionVariable(execution);
			
            // Leave (only done when all executions have been made, since some queries depend on this)
            for (ExecutionEntity outgoingExecution : outgoingExecutions) {
                agenda.planContinueProcessOperation(outgoingExecution);
                if (engineConfigurationImpl.isLoggingSessionEnabled()) {
                    BpmnLoggingSessionUtil.addSequenceFlowLoggingData(LoggingSessionConstants.TYPE_SEQUENCE_FLOW_TAKE, outgoingExecution);
                }
            }
		}
	}

	/**
	 * 修改XWorkActivity的状态为Closed, 并且将TransientVariablesLocal中的FlowVariable写入进XWorkActivity或XWorkInstance的flowContext字段中
	 */
	@Transactional
	public void persistExecutionVariable(ExecutionEntity execution) {
		if (execution == null)
			return;

		try {
			XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
			if (workActivity == null)
				return;

			XWorkInstance workInstance = workActivity.getInstance();
			for (Map.Entry<String, Object> entry : execution.getFlowVariables().entrySet()) {
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof FlowVariable) {
					FlowVariable variable = (FlowVariable) value;
					FlowVariable actVar = workActivity.getFlowContext().getVariable(name);
					if (actVar != null) {
						actVar.setValue(variable.getValue());
					}
					FlowVariable instVar = workInstance.getFlowContext().getVariable(name);
					if (instVar != null) {
						instVar.setValue(variable.getValue());
					}
				}
			}
			/** 关闭Execution相关的XWorkActivity */
			PersistenceHelper.service().save(workActivity);
			PersistenceHelper.service().save(workInstance);
		} finally {
			execution.setExternalObject(null);
		}
	}
}
