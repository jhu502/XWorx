package xw.flow.flowable.resolver;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.common.engine.impl.javax.el.ELResolver;

import com.flame.util.XException;

/**
 * Flowable默认变量处理机制, 每个节点/变量都被单独作为一行act_ru_variable、act_hi_varint记录进行存放, 虽然这种存储方式方便了查询,
 * 但是这导致act_hi_varint表的数据记录会非常的多;
 * 为了解决Flowable变量表增长速率过快的状况, XFlow中采用XFlowContext对象来集中存储Flowable变量, 以减少act_hi_varint表的增长;
 * 由于以XFlowContext来集中管理变量, 那么Flowable原生解析变量的脚本不能够直接去使用变量, XFlowContextVarELResolver就是为了解决这个问题.
 *
 */
public class XFlowContextVarELResolver extends ELResolver {
    protected VariableContainer variableContainer;

    public XFlowContextVarELResolver(VariableContainer variableContainer) {
        this.variableContainer = variableContainer;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (true) {
            throw new XException("1");
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (true) {
            throw new XException("2");
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (true) {
            throw new XException("3");
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (true) {
            throw new XException("4");
        }
        return false;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (true) {
            throw new XException("6");
        }
        return null;
    }
}
