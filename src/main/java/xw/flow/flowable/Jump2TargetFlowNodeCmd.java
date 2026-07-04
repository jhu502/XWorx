package xw.flow.flowable;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

public class Jump2TargetFlowNodeCmd implements Command<ExecutionEntity> {
    private String curTaskId;
    private String targetFlowNodeId;

    public Jump2TargetFlowNodeCmd(String curTaskId, String targetFlowNodeId) {
        this.curTaskId = curTaskId;
        this.targetFlowNodeId = targetFlowNodeId;
    }
    @Override
    public ExecutionEntity execute(CommandContext commandContext) {
        System.out.println("跳转到目标流程节点：" + targetFlowNodeId);
		ProcessEngineConfigurationImpl engineConfigurationImpl = CommandContextUtil.getProcessEngineConfiguration(commandContext);
		ExecutionEntityManager executionEntityManager = engineConfigurationImpl.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = engineConfigurationImpl.getTaskServiceConfiguration().getTaskEntityManager();
        // 获取当前任务的来源任务及来源节点信息
        TaskEntity taskEntity = taskEntityManager.findById(curTaskId);
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        // 删除当前节点
        taskEntityManager.delete(taskEntity, true);
        // 获取要跳转的目标节点
        FlowElement targetFlowElement = process.getFlowElement(targetFlowNodeId);
        executionEntity.setCurrentFlowElement(targetFlowElement);
        
        Context.getAgenda().planContinueProcessInCompensation(executionEntity);

        return executionEntity;
    }
}
