package xw.flow.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.XJsonType;

public class FlowRoute extends XJsonType<FlowRoute> {
    private static final long serialVersionUID = 1L;
    private String name;
    private String expression;

    public static FlowRoute newInstance(JsonNode node) {
        FlowRoute route = new FlowRoute();
        if (node.has("value")) {
            route.setName(node.get("value").asText());
        }
        if (node.has("expression")) {
            route.setExpression(node.get("expression").asText());
        }

        return route;
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
