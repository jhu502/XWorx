package xw.flow.flowable.resolver;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.scripting.Resolver;
import org.flowable.common.engine.impl.scripting.ResolverFactory;

/**
 * 注册XFlowContextVarResolver到ScriptingEngines中, ScriptTask通过XFlowContextVarResolver获取XFlowContext中的变量
 * @author hujin
 */
public class XFlowScriptResolverFactory implements ResolverFactory {
    @Override
    public Resolver createResolver(AbstractEngineConfiguration engineConfiguration, VariableContainer scopeContainer, VariableContainer inputVariableContainer) {
        if (scopeContainer != null) {
            return new XFlowContextVarResolver(engineConfiguration, scopeContainer);
        }
        return null;
    }
}
