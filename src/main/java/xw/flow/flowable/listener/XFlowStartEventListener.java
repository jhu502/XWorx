package xw.flow.flowable.listener;

import java.util.List;

import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.ProcessInstance;

import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.bean.FlowVariable;
import xw.flow.entity.XWorkInstance;

/**
 * StartEvent 执行监听器 —— 在流程启动时初始化 XWorkInstance 的运行环境。
 *
 * <p>作为 Flowable 标准的 {@link ExecutionListener}（event="start"）挂载在 StartEvent 上，
 * 在流程实例启动后、离开 StartEvent 之前执行。</p>
 *
 * <p>初始化内容：</p>
 * <ol>
 *   <li>通过 businessKey 查找关联的 XWorkInstance</li>
 *   <li>从 BPMN Process 定义中读取全局变量，注入 workInstance.getFlowContext()</li>
 *   <li>将 Flowable ProcessInstance 关联到 workInstance</li>
 *   <li>持久化 workInstance</li>
 * </ol>
 *
 * <p>与 {@link xw.flow.flowable.behavior.XFlowStartEventBehavior} 的逻辑完全一致，
 * 但通过 ExecutionListener 机制触发，不依赖 ActivityBehaviorFactory 覆盖，
 * 确保在 Flowable 8 中稳定运行。</p>
 *
 * @author hujin
 * @see xw.flow.flowable.behavior.XFlowStartEventBehavior
 */
public class XFlowStartEventListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
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
    }
}
