package xw.flow.flowable.behavior;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.SkipExpressionUtil;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.condition.ConditionUtil;
import org.flowable.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.constants.FlowConstant;
import xw.flow.entity.XFlowDefinition;

/**
 * XFlowExclusiveGatewayBehavior主要用来实现OrGateway的功能：(OrGateway是用ExclusiveGateway实现的, 扩展元素x-config的type元素值为OR)
 * - ExclusiveGateway默认只会激活一个SequenceFlow, 在OrGateway则需要能够同时激活所有的下游SequenceFlow;
 * - 流程流经ExclusiveGateway节点时, ExclusiveGateway节点之前的Open的节点不会自动Close, 流程流经OrGateway节点后需要终止前面Open的节点;
 */
public class XFlowExclusiveGatewayBehavior extends ExclusiveGatewayActivityBehavior {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = LoggerFactory.getLogger(XFlowExclusiveGatewayBehavior.class);

	@Override
	public void leave(DelegateExecution execution) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Leaving exclusive gateway '{}'", execution.getCurrentActivityId());
		}
		String instanceId = execution.getProcessInstanceId();
		String definitionId = execution.getProcessDefinitionId();

		ExclusiveGateway exclusiveGateway = (ExclusiveGateway) execution.getCurrentFlowElement();
		String configType = XFlowDefinitionHelper.getNodeConfig(exclusiveGateway, FlowConstant.TYPE);

		if (!"andGateway".equalsIgnoreCase(configType)) {
			CommandContext commandContext = CommandContextUtil.getCommandContext();
			ProcessEngineConfigurationImpl engineConfigurationImpl = CommandContextUtil.getProcessEngineConfiguration(commandContext);

			if (engineConfigurationImpl != null) {
				FlowableEventDispatcher eventDispatcher = engineConfigurationImpl.getEventDispatcher();
				if (eventDispatcher != null && eventDispatcher.isEnabled()) {
					eventDispatcher.dispatchEvent(FlowableEventBuilder.createActivityEvent(FlowableEngineEventType.ACTIVITY_COMPLETED, exclusiveGateway.getId(), exclusiveGateway.getName(),
							execution.getId(), execution.getProcessInstanceId(), execution.getProcessDefinitionId(), exclusiveGateway), engineConfigurationImpl.getEngineCfgKey());
				}
			}

			SequenceFlow defaultSequenceFlow = null;
			String defaultSequenceFlowId = exclusiveGateway.getDefaultFlow();
			List<SequenceFlow> outgoingSequenceFlows = new ArrayList<SequenceFlow>();

			// Determine sequence flow to take
			Iterator<SequenceFlow> sequenceFlowIterator = exclusiveGateway.getOutgoingFlows().iterator();
			while (sequenceFlowIterator.hasNext()) {
				SequenceFlow sequenceFlow = sequenceFlowIterator.next();

				String skipExpressionString = sequenceFlow.getSkipExpression();
				if (!SkipExpressionUtil.isSkipExpressionEnabled(skipExpressionString, sequenceFlow.getId(), execution, commandContext)) {
					boolean conditionEvaluatesToTrue = ConditionUtil.hasTrueCondition(sequenceFlow, execution);
					if (conditionEvaluatesToTrue && (defaultSequenceFlowId == null || !defaultSequenceFlowId.equals(sequenceFlow.getId()))) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Sequence flow '{}' selected as outgoing sequence flow.", sequenceFlow.getId());
						}
						outgoingSequenceFlows.add(sequenceFlow);
					}

				} else if (SkipExpressionUtil.shouldSkipFlowElement(skipExpressionString, sequenceFlow.getId(), execution, Context.getCommandContext())) {
					outgoingSequenceFlows.add(sequenceFlow);
				}

				// Already store it, if we would need it later. Saves one for loop.
				if (defaultSequenceFlowId != null && defaultSequenceFlowId.equals(sequenceFlow.getId())) {
					defaultSequenceFlow = sequenceFlow;
				}
			}

			// Leave the gateway
			if (!outgoingSequenceFlows.isEmpty() && engineConfigurationImpl != null) {
				// Leave, and reuse the incoming sequence flow, make executions for all the others (if applicable)
				ExecutionEntityManager executionEntityManager = engineConfigurationImpl.getExecutionEntityManager();
				RuntimeService runtimeService = engineConfigurationImpl.getRuntimeService();
				List<ExecutionEntity> outgoingExecutions = new ArrayList<ExecutionEntity>(exclusiveGateway.getOutgoingFlows().size());

				boolean bool = true;
				for (SequenceFlow outgoingSeqFlow : outgoingSequenceFlows) {
					FlowElement nextElement = outgoingSeqFlow.getTargetFlowElement();
					List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(instanceId).activityId(nextElement.getId()).list();
					/**
					 * XFlow Flowable引入了驳回功能, 当UserTask驳回后再次提交时, 有可能前次驳回“正在运行”的节点被再次进入, 导致重复进入的问题；
					 * - 检查SequenceFlow的下游节点是否是“正在运行”的状态, 若是“正在运行”状态, OutGoing不包含此SequenceFlow;
					 * - executions如果不是空, 说明下游的节点是Running状态
					 */
					if (executions.isEmpty()) {
						if (bool) { // Reuse existing one, true:原有的Execution还未重用过
							execution.setCurrentFlowElement(outgoingSeqFlow);
							execution.setActive(true);
							outgoingExecutions.add((ExecutionEntity) execution);
							bool = false;
						} else {
							/** 如果存在多个下游分支, 并且存在的ExecutionEntity已经重用过, 则创建新的ExecutionEntity */
							ExecutionEntity parent = execution.getParentId() != null ? (ExecutionEntity) execution.getParent() : (ExecutionEntity) execution;
							ExecutionEntity outgoingExecutionEntity = executionEntityManager.createChildExecution(parent);
							outgoingExecutionEntity.setCurrentFlowElement(outgoingSeqFlow);
							executionEntityManager.insert(outgoingExecutionEntity);
							outgoingExecutions.add(outgoingExecutionEntity);
						}
					}
				}
				/** 流程流经OrGateway节点后, OrGateway前面Open的节点需要被终止 */
				XFlowDefinition definition = XFlowExecutionHelper.execution().getXFlowDefinition(definitionId);
				XFlowExecutionHelper.execution().terminatePrevTaskEntity(definition, exclusiveGateway, instanceId);

				// Leave (only done when all executions have been made, since some queries depend on this)
				for (ExecutionEntity outgoingExecution : outgoingExecutions) {
					Context.getAgenda().planContinueProcessOperation(outgoingExecution);
				}
			} else {
				/**
				 * 默认如果无下游节点就执行EndExecutionOperation, 此规则被禁用
				 */
				if (defaultSequenceFlow != null) {
					//	execution.setCurrentFlowElement(defaultSequenceFlow);
				} else {
					// No sequence flow could be found, not even a default one
					throw new FlowableException("No outgoing sequence flow of the exclusive gateway '" + exclusiveGateway.getId() + "' could be selected for continuing " + execution);
				}
			}
		} else {
			super.leave(execution);
		}
	}
}
