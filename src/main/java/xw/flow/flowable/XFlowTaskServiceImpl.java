package xw.flow.flowable;

import java.util.Map;

import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

public class XFlowTaskServiceImpl extends TaskServiceImpl {

	public XFlowTaskServiceImpl(ProcessEngineConfigurationImpl engineConfiguration) {
		super(engineConfiguration);
	}

	public void complete(String taskId) {
		super.complete(taskId);
	}

	public void complete(String taskId, Map<String, Object> variables) {
		super.complete(taskId, variables);
	}

	@Override
	public void complete(String taskId, Map<String, Object> variables, Map<String, Object> transientVariables) {
		super.complete(taskId, variables, transientVariables);
	}

	public void complete(String taskId, Map<String, Object> variables, boolean localScope) {
		super.complete(taskId, variables, localScope);
	}
}
