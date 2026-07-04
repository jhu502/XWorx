package xw.flow.constants;

import java.util.HashMap;
import java.util.Map;

import com.flame.orm.XJsonType;

import xw.flow.bean.FlowVariable;

public class FlowContext extends XJsonType<FlowContext> {
    private static final long serialVersionUID = 1L;
    /**
     * Flowable中, 每个变量都会在act_ru_variable,act_hi_varinst表中生成一条记录; 为了减少变量的数量, 所有的变量
     * 都统一记录在名称为XFlowContext的对象中, XFlowContext被序列化成json后存放在act_ru_variable,act_hi_varinst表中;
     */
    public static String FLOW_CONTEXT = "XFlowContext";
    /**
     * 名称为:return的变量, 用来记录ScriptTask的返回值;
     */
    public static String FLOW_RETURN = "return";
    private Map<String, FlowVariable> variables = new HashMap<>();
    private boolean sendMail = false;

    public static FlowContext newXFlowContext() {
        return new FlowContext();
    }

    public Map<String, FlowVariable> getVariables() {
        return variables;
    }

    public FlowVariable getVariable(String name) {
        return this.variables.get(name);
    }

    public void setVariables(Map<String, FlowVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(FlowVariable variable) {
        this.variables.put(variable.getName(), variable);
    }

    public void addVariable(String name, Object value) {
        FlowVariable variable = new FlowVariable();
        variable.setName(name);
        if (value == null)
            variable.setType(Object.class.getName());
        else
            variable.setType(value.getClass().getName());
        variable.setValue(value);
        this.addVariable(variable);
    }

    public boolean isSendMail() {
        return sendMail;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }
}
