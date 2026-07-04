package xw.flow.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.XJsonType;

public class FlowUser extends XJsonType<FlowUser> {
    private static final long serialVersionUID = 1L;
    private String name;
    private String display;

    public static FlowUser newInstance(JsonNode node) {
        FlowUser user = new FlowUser();
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
