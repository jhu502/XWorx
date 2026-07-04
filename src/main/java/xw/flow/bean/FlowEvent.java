package xw.flow.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.XJsonType;

import xw.flow.constants.FlowConstant;

public class FlowEvent extends XJsonType<FlowEvent> {
    private static final long serialVersionUID = 1L;
    private String name;
    private String expression;

    public static FlowEvent newInstance(JsonNode node) {
        FlowEvent event = new FlowEvent();
        if (node.has(FlowConstant.VALUE)) {
            event.setName(node.get(FlowConstant.VALUE).asText());
        }
        if (node.has(FlowConstant.EXPRESSION)) {
            event.setExpression(node.get(FlowConstant.EXPRESSION).asText());
        }

        return event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
