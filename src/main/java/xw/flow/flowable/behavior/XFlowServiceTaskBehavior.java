package xw.flow.flowable.behavior;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.ServiceTaskJavaDelegateActivityBehavior;
import org.flowable.engine.impl.delegate.invocation.JavaDelegateInvocation;
import org.flowable.engine.impl.util.CommandContextUtil;

public class XFlowServiceTaskBehavior extends ServiceTaskJavaDelegateActivityBehavior {
	private static final long serialVersionUID = 1L;

	public void execute(DelegateExecution execution) {
        CommandContextUtil.getProcessEngineConfiguration().getDelegateInterceptor().handleInvocation(new JavaDelegateInvocation(javaDelegate, execution));
        leave(execution);
    }
}
