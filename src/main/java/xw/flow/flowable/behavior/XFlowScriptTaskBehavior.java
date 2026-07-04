package xw.flow.flowable.behavior;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.scripting.ScriptEngineRequest;
import org.flowable.common.engine.impl.scripting.ScriptingEngines;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.ScriptTaskActivityBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flame.orm.PersistenceHelper;

import xw.flow.XFlowExecutionHelper;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.XWorkActivity;

/**
 * ScriptTask执行完成时，同步修改对应XWorkActivity的状态和Task返回的路由
 */
public class XFlowScriptTaskBehavior extends ScriptTaskActivityBehavior {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(XFlowScriptTaskBehavior.class);

    public XFlowScriptTaskBehavior(String scriptTaskId, String script, String language, String resultVariable, String skipExpression, boolean storeScriptVariables) {
        super(scriptTaskId, script, language, resultVariable, skipExpression, storeScriptVariables);
    }

    protected void executeScript(DelegateExecution execution) {
        ScriptEngineRequest.Builder builder = ScriptEngineRequest.builder().script(script) //
                .traceEnhancer(trace -> trace.addTraceTag("type", "scriptTask")).language(language).scopeContainer(execution);
        builder = storeScriptVariables ? builder.storeScriptVariables() : builder;
        ScriptingEngines scriptingEngines = CommandContextUtil.getProcessEngineConfiguration().getScriptingEngines();
        Object result = scriptingEngines.evaluate(builder.build()).getResult();

        /**
         * ScriptTask执行完成时，同步修改对应XWorkActivity的状态和Task返回的路由
         */
        XWorkActivity workActivity = XFlowExecutionHelper.execution().getCurrentXWorkActivity((ExecutionEntity) execution);
        if (null != result) {
            if ("juel".equalsIgnoreCase(language) && (result instanceof String) && script.equals(result.toString())) {
                throw new FlowableException("Error evaluating juel script: \"" + script + "\" for " + execution);
            }
            workActivity.setRoutes(result.toString());
        }
        workActivity.setStatus(FlowStatus.COMPLETED);
        workActivity = PersistenceHelper.service().save(workActivity);
        LOGGER.debug("XWorkActivity:{}", workActivity);

        if (resultVariable != null) {
            execution.setVariable(resultVariable, result);
        }
    }
}
