package xw.flow.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.XJsonType;

public class FlowGroup extends XJsonType<FlowGroup> {
    private static final long serialVersionUID = 1L;
    private String name;
    private String display;

    public static FlowGroup newInstance(JsonNode node) {
        FlowGroup user = new FlowGroup();
        if (node.has("value")) {
            user.setName(node.get("value").asText());
        }
        if (node.has("display")) {
            user.setDisplay(node.get("display").asText());
        }

        return user;
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
}
