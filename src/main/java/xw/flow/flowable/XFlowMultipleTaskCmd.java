package xw.flow.flowable;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

public class XFlowMultipleTaskCmd implements Command<TaskEntity> {
    protected final String NUMBER_OF_INSTANCES = "nrOfInstances";
    protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    private String taskId;
    private String addUser;

    public XFlowMultipleTaskCmd(String taskId, String taskAssign) {
        this.taskId = taskId;
        this.addUser = taskAssign;
    }

    @Override
    public TaskEntity execute(CommandContext commandContext) {
    	ProcessEngineConfigurationImpl engineConfigurationImpl = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        TaskEntityManager taskEntityManager = engineConfigurationImpl.getTaskServiceConfiguration().getTaskEntityManager();
        ExecutionEntityManager execEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        //根据任务id获取任务实例
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //根据执行实例ID获取当前执行实例
        ExecutionEntity multipleEntity = execEntityManager.findById(taskEntity.getExecutionId());
        // 获取流程执行实例（即当前执行实例的父实例）
        ExecutionEntity parentEntity = multipleEntity.getParent();
        //判断当前执行实例的节点是否是多实例节点
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(multipleEntity.getProcessDefinitionId());
        Activity activityElement = (Activity) bpmnModel.getFlowElement(multipleEntity.getCurrentActivityId());
        MultiInstanceLoopCharacteristics loopCharacts = activityElement.getLoopCharacteristics();
        if (loopCharacts == null) {
            throw new FlowableException("此节点不是多实例节点");
        }
        //判断是否是并行多实例
        if (loopCharacts.isSequential()) {
            throw new FlowableException("此节点为串行节点");
        }
        //创建新的子实例
        ExecutionEntity childExecution = execEntityManager.createChildExecution(parentEntity);
        //获取并为新的执行实例设置当前活动节点
        UserTask currFlowElement = (UserTask) multipleEntity.getCurrentFlowElement();
        //设置处理人
        currFlowElement.setAssignee(addUser);
        childExecution.setCurrentFlowElement(currFlowElement);
        //获取设置变量
        Integer nrOfInstances = (Integer) parentEntity.getVariableLocal(NUMBER_OF_INSTANCES);
        Integer nrOfActiveInstances = (Integer) parentEntity.getVariableLocal(NUMBER_OF_ACTIVE_INSTANCES);
        parentEntity.setVariableLocal(NUMBER_OF_INSTANCES, nrOfInstances + 1);
        parentEntity.setVariableLocal(NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances + 1);
        //通知活动开始
        CommandContextUtil.getActivityInstanceEntityManager(commandContext).recordActivityStart(childExecution);
        //获取处理行为类
        ParallelMultiInstanceBehavior prallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) activityElement.getBehavior();
        AbstractBpmnActivityBehavior innerActivityBehavior = prallelMultiInstanceBehavior.getInnerActivityBehavior();
        //执行
        innerActivityBehavior.execute(childExecution);
        return null;
    }
}
