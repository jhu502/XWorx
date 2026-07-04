package xw.flow.flowable;

import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

import xw.auths.entity.XUser;

public class XFlowAddAssigneeTaskCmd implements Command<TaskEntity> {
    private String actId;
    private XUser xuser;

    public XFlowAddAssigneeTaskCmd(String actId, XUser xuser) {
        this.actId = actId;
        this.xuser = xuser;
    }

    @Override
    public TaskEntity execute(CommandContext commandContext) {
    	ProcessEngineConfigurationImpl engineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        RuntimeService runtimeService = engineConfiguration.getRuntimeService();
        TaskEntityManager taskEntityManager = engineConfiguration.getTaskServiceConfiguration().getTaskEntityManager();

        ExecutionEntity exectionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().activityId(this.actId).singleResult();
        UserTask flowElement = (UserTask) exectionEntity.getCurrentFlowElement(); //获取并为新的执行实例设置当前活动节点

        TaskEntity newTask = taskEntityManager.create();
        newTask.setExecutionId(exectionEntity.getId());
        newTask.setProcessDefinitionId(exectionEntity.getProcessDefinitionId());
        newTask.setProcessInstanceId(exectionEntity.getProcessInstanceId());
        newTask.setTaskDefinitionKey(exectionEntity.getCurrentActivityId());
        newTask.setAssignee(this.xuser.getOid());
        newTask.setName(flowElement.getName());
        taskEntityManager.insert(newTask, true);
        //创建新的子实例
        // ExecutionEntity childExecution = execEntityManager.createChildExecution(parentEntity);
        // childExecution.setCurrentFlowElement(flowElement);
        // historyManager.recordActivityStart(childExecution);

        //ParallelMultiInstanceBehavior prallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) miActivityElement.getBehavior();
        //AbstractBpmnActivityBehavior innerActivityBehavior = prallelMultiInstanceBehavior.getInnerActivityBehavior();
        //innerActivityBehavior.execute(childExecution);

        return newTask;
    }
}
