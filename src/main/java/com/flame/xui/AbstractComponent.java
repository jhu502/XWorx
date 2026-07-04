package com.flame.xui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractComponent implements IComponent {
    private boolean isEasyUI = true;
    private String id = "";
    private String name = "";
    private WidgetMode widgetMode;
    private WidgetType widgetType;
    private List<String> classes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEasyUI() {
        return this.isEasyUI;
    }

    public void setEasyUI(boolean bool) {
        this.isEasyUI = bool;
    }

    public WidgetType getWidgetType() {
        return this.widgetType;
    }

    public void setWidgetType(WidgetType widgetType) {
        this.widgetType = widgetType;
    }

    public WidgetMode getWidgetMode() {
        return widgetMode;
    }

    public void setWidgetMode(WidgetMode widgetMode) {
        this.widgetMode = widgetMode;
    }
    
    public List<String> getClasses() {
        return this.classes;
    }
    
    public String getDomClass() {
        if (this.classes.isEmpty())
            return "";
        
        return StringUtils.join(this.classes, " ");
    }

    public void addDomClass(String domClass) {
        this.classes.add(domClass);
    }
}
