package xw.flow.flowable.behavior;

import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.scripting.ScriptingEngines;
import org.flowable.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.ScriptTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

/**
 * DefaultActivityBehaviorFactory是ActivityBehavior的注册工厂类, XFlowActivityBehaviorFactory是XFlow模块注册自定义Behavior的工厂类：
 * 1. 注册XFlowUserTaskBehavior以实现多TaskEntity会签的功能;
 * 2. 注册XFlowExclusiveGatewayBehavior以实现OrGateway功能;
 * 3. 注册XFlowScriptTaskBehavior以实现ScriptTask与XWorkActivity的状态/路由同步功能;
 * 4. 注册XFlowTimerRobotBehavior以实现时间自动机的功能;
 */
public class XFlowActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
	@Override
	public XFlowStartEventBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
		return new XFlowStartEventBehavior();
	}

	@Override
	public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
		return new XFlowUserTaskBehavior(userTask);
	}

	@Override
	public ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway) {
		return new XFlowExclusiveGatewayBehavior();
	}

	@Override
	public ScriptTaskActivityBehavior createScriptTaskActivityBehavior(ScriptTask scriptTask) {
		String language = scriptTask.getScriptFormat();
		if (language == null) {
			language = ScriptingEngines.DEFAULT_SCRIPTING_LANGUAGE;
		}

		return new XFlowScriptTaskBehavior(scriptTask.getId(), scriptTask.getScript(), language, scriptTask.getResultVariable(), scriptTask.getSkipExpression(), scriptTask.isAutoStoreVariables());
	}

	public ReceiveTaskActivityBehavior createReceiveTaskActivityBehavior(ReceiveTask receiveTask) {
	    XFlowTimerRobotBehavior timerRobotBehavior = new XFlowTimerRobotBehavior(receiveTask.getId(), receiveTask.getSkipExpression());
	    timerRobotBehavior.setReceiveTask(receiveTask);
		return timerRobotBehavior;
	}
}
