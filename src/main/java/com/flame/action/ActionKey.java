package com.flame.action;

import java.io.Serializable;

public class ActionKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String BUILDER$ = "builder$";
    private String type;
    private String name;

    public static ActionKey newActionKey(String key) {
        ActionKey actionKey = new ActionKey();
        if (key == null)
            return actionKey;

        String[] ps = key.split(":");
        actionKey.type = ps[0];
        if (ps.length < 2)
            return actionKey;

        actionKey.name = ps[1];


        return actionKey;
    }

    public static ActionKey newActionKey(String name, Class<?> clazz) {
        ActionKey actionKey = new ActionKey();
        actionKey.setName(name);
        actionKey.setType(clazz.getSimpleName());

        return actionKey;
    }

    public static ActionKey newActionKey(String name, String type) {
        ActionKey actionKey = new ActionKey();
        actionKey.setName(name);
        actionKey.setType(type);

        return actionKey;
    }

    public String getOriginType() {
        return this.type;
    }

    public String getType() {
        if (this.fromBuilder()) {
            return this.type.substring(8);
        } else {
            return type;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean fromBuilder() {
        if (this.type == null)
            return false;

        if (this.type.startsWith(BUILDER$))
            return true;
        else
            return false;
    }

    public IActionItem getActionItem() {
        return XActionHelper.manager().getActionItem(this.getName(), this.getType());
    }

    public IActionModel getIActionModel() {
        return null; //XActionHelper.service().getActionModel(this.getName(), this.getType());
    }

    public String toString() {
        return type + ":" + name;
    }
}
