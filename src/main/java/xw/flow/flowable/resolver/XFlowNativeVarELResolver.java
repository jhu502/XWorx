package xw.flow.flowable.resolver;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.common.engine.impl.javax.el.ELResolver;
import org.flowable.common.engine.impl.javax.el.PropertyNotFoundException;
import org.flowable.common.engine.impl.javax.el.PropertyNotWritableException;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.variable.api.delegate.VariableScope;

import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

import xw.flow.entity.XWorkInstance;

/**
 * ELResolver是JUEL的核心类, 用来解析EL表达式中的对象、属性、方法, 如果解析成功需设置resolved为true,
 * context.setPropertyResolved(true);
 * 调用者通过context.isPropertyResolved()的返回值来判断当前ELResolver是否已经解析成功;
 * XFlowVariableELResolver是用来处理pbo 和 self的EL对象:
 * pbo: businessKey的XPersistable对象;
 * self:VariableScope对象;
 */
public class XFlowNativeVarELResolver extends ELResolver {
	protected VariableContainer variableContainer;
	protected Set<String> wrapperSet = Set.of("pbo");

	public XFlowNativeVarELResolver(VariableContainer variableContainer) {
		this.variableContainer = variableContainer;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			if ("pbo".equals(property)) {
				if (variableContainer instanceof ExecutionEntity) {
					ExecutionEntity executionEntity = (ExecutionEntity) variableContainer;
					String businessKey = executionEntity.getBusinessKey();
					if (StringUtils.isEmpty(businessKey)) {
						ExecutionEntity instance = executionEntity.getProcessInstance();
						businessKey = instance.getBusinessKey();
					}
					if (ObjectReference.isOid(businessKey)) {
						context.setPropertyResolved(true);
						XWorkInstance flowInstance = PersistenceHelper.service().find(businessKey);
						return flowInstance.getBusinessRef().getObject();
					}
				}
			} else if ("self".equals(property)) {
				if (variableContainer != null) {
					context.setPropertyResolved(true);
					return variableContainer;
				}
			}
		}
		return null;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base != null) {
			return;
		}
		context.setPropertyResolved(false);
		if (property == null) {
			throw new PropertyNotFoundException("property is null.");
		}
		if ("pbo".equals(property) || "self".equals(property)) {
			throw new PropertyNotWritableException((String) property);
		}
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return XObject.class;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class<?> getType(ELContext context, Object base, Object property) {
		if (base != null) {
			return null;
		}
		if (property == null) {
			throw new PropertyNotFoundException("property is null.");
		}
		if ("pbo".equals(property)) {
			context.setPropertyResolved(true);
			return XObject.class;
		} else if ("self".equals(property)) {
			context.setPropertyResolved(true);
			return VariableScope.class;
		}

		return null;
	}
}
