package xw.flow.bean;

import com.flame.orm.XJsonType;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.XConstant;

public class FlowVariable extends XJsonType<FlowVariable> {
    private static final long serialVersionUID = 1L;
    private String name;
    private String display;
    private String type;
    private Object value;

    public static FlowVariable newInstance(JsonNode node) {
        if (node.has(XConstant.NAME)) {
            FlowVariable variable = FlowVariable.newInstance(node.get(XConstant.NAME).asText());
            if (node.has(XConstant.DISPLAY)) {
                variable.setDisplay(node.get(XConstant.DISPLAY).asText());
            }
            if (node.has(XConstant.TYPE)) {
                String type = node.get(XConstant.TYPE).asText();
                variable.setType(type);
            }
            if (node.has(XConstant.VALUE)) {
                variable.setValue(node.get(XConstant.VALUE).asText());
            }

            return variable;
        } else {
            return null;
        }
    }

    public static FlowVariable newInstance(String name) {
        FlowVariable variable = new FlowVariable();
        variable.setName(name);

        return variable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public boolean isType(Class<?> clazz) {
    	if (clazz == null) {
    		return false;
    	}
    	
    	return StringUtils.equals(this.type, clazz.getName());
    }
}
