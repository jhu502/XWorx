package xw.flow.flowable;

import java.util.List;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.scripting.Resolver;
import org.flowable.common.engine.impl.scripting.ScriptBindings;
import org.flowable.variable.api.delegate.VariableScope;

import xw.flow.bean.FlowVariable;

/**
 * 用来解决ScriptTask中FlowVariable通过groovy脚本进行赋值; 在groovy脚本中有对定义的变量进行赋值操作, 会调用put(name, value)方法, 如果value的类型
 * 是FlowVariable, 变量值被写入进VariableScope对象的TransientVariableLocal中, XFlowTakeOutgoingOperation时会一次性将FlowVariable变量写入进XWorkActivity和XWorkInstance
 */
public class XFlowScriptBindings extends ScriptBindings {
    public XFlowScriptBindings(List<Resolver> scriptResolvers, VariableContainer scopeContainer, VariableContainer inputVariableContainer) {
        super(scriptResolvers, scopeContainer, inputVariableContainer);
    }

    public XFlowScriptBindings(List<Resolver> scriptResolvers, VariableScope variableScope, VariableContainer inputVariableContainer) {
        super(scriptResolvers, variableScope, inputVariableContainer);
    }

    @Override
    public Object put(String name, Object value) {
    	if (name == null || name.isBlank())
    		return null;
    	
    	if (this.scopeContainer instanceof VariableScope) {
    		VariableScope variableScope = (VariableScope) this.scopeContainer;
            FlowVariable variable = (FlowVariable) variableScope.getFlowVariable(name);
            if (variable != null) {
                variable.setValue(value);
                return variable;
            } else {
                if (storeScriptVariables) {
                    Object oldValue = null;
                    if (!UNSTORED_KEYS.contains(name)) {
                        oldValue = variableScope.getVariable(name);
                        variableScope.setVariable(name, value);
                        return oldValue;
                    }
                }
                return defaultBindings.put(name, value);
            }
    	} else {
    		return super.put(name, value);
    	}
    }
}
