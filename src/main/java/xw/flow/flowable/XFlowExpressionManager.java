package xw.flow.flowable;

import java.util.Map;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.DefaultExpressionManager;
import org.flowable.common.engine.impl.el.FlowableElContext;
import org.flowable.common.engine.impl.javax.el.CompositeELResolver;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.common.engine.impl.javax.el.ELResolver;

import xw.flow.flowable.resolver.XFlowContextVarELResolver;
import xw.flow.flowable.resolver.XFlowNativeVarELResolver;

public class XFlowExpressionManager extends DefaultExpressionManager {
	public XFlowExpressionManager(Map<Object, Object> beans) {
		super(beans);
	}
    
    @Override
    public ELContext getElContext(VariableContainer variableContainer) {
        ELResolver elResolver = getOrCreateStaticElResolver();
        if (elResolver instanceof CompositeELResolver) {
        	CompositeELResolver compositeELResolver = (CompositeELResolver) elResolver;
        	compositeELResolver.add(new XFlowNativeVarELResolver(variableContainer));
        	compositeELResolver.add(new XFlowContextVarELResolver(variableContainer));
        }
        return new FlowableElContext(elResolver, this.functionResolver, this.expressionFactory);
    }
}
