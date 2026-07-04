package xw.flow.flowable.resolver;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.scripting.Resolver;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.variable.api.delegate.VariableScope;

import xw.flow.bean.FlowVariable;
import xw.flow.entity.XWorkActivity;
import xw.flow.entity.XWorkInstance;

/**
 * ScriptTask通过XFlowContextVarResolver获取XFlowContext中的变量：XFlow的变量实例存放在XWorkInstance & XWorkActivity中；
 * 1. 从当前VariableScope对象的TransientVariableLocal中获取变量记录, 如果有：直接返回;
 * 2. 如果VariableScope对象是ExecutionEntity对象, 从对应的XWorkActivity中获取FlowVariable, 如果有: 添加进TransientVariableLocal中, 返回结果;
 * 3. 根据XWorkActivity获取对应的XWorkInstance对象, 获取全局FlowVariable, 如果有: 添加进TransientVariableLocal中, 返回结果
 *
 * @author hujin 2023.2.6
 */
public class XFlowContextVarResolver implements Resolver {
    private VariableContainer variableContainer;

    public XFlowContextVarResolver(AbstractEngineConfiguration engineConfiguration, VariableContainer variableContainer) {
        this.variableContainer = variableContainer;
    }

    @Override
    public boolean containsKey(Object key) {
        if (variableContainer == null)
            return false;

        if (variableContainer instanceof VariableScope) {
        	VariableScope variableScope = (VariableScope) variableContainer;
            Object object = variableScope.getFlowVariable(key.toString());
            if (object instanceof FlowVariable)
                return true;
        }

        if (variableContainer instanceof ExecutionEntity) {
            ExecutionEntity execution = (ExecutionEntity) variableContainer;

            XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
            boolean bool = workActivity.hasVariable(key.toString());
            if (bool) {
                return true;
            } else {
                XWorkInstance workInstance = workActivity.getInstance();
                return workInstance.hasVariable(key.toString());
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        if (variableContainer == null || key == null)
            return null;

        if (this.variableContainer instanceof VariableScope) {
        	VariableScope variableScope = (VariableScope) this.variableContainer;
            Object object = variableScope.getFlowVariable(key.toString());
            if (object == null) {
                if (variableContainer instanceof ExecutionEntity) {
                    ExecutionEntity execution = (ExecutionEntity) variableContainer;
                    XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
                    FlowVariable _variable = workActivity.getFlowContext().getVariable(key.toString());
                    variableScope.setFlowVariable(key.toString(), _variable);
                    if (_variable != null)
                        return _variable.getValue();

                    XWorkInstance workInstance = workActivity.getInstance();
                    FlowVariable $variable = workInstance.getFlowContext().getVariable(key.toString());
                    variableScope.setFlowVariable(key.toString(), $variable);
                    if ($variable != null)
                        return $variable.getValue();
                }
            } else if (object instanceof FlowVariable) {
                FlowVariable variable = (FlowVariable) object;
                return variable.getValue();
            }

            return object;
        } else {
        	return this.variableContainer.getVariable(key.toString());
        }
    }
}
