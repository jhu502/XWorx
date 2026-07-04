package xw.flow.bean;

import xw.flow.entity.XFlowScriptTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptTaskVO extends FlowNodeVO {
    private String language = "";
    private String expression = "";
    private List<Map<String, Object>> routevents = new ArrayList<>();

    public static ScriptTaskVO newInstance(XFlowScriptTask flowTask, boolean movable) {
        ScriptTaskVO taskBean = new ScriptTaskVO(flowTask);
        taskBean.setMovable(movable);
        taskBean.setLanguage(flowTask.getLanguage().name());
        taskBean.setExpression(flowTask.getExpression());
        
        for (FlowRoute route : flowTask.getRoutes()) {
            taskBean.addRoute(route);
        }
        return taskBean;
    }

    public ScriptTaskVO(XFlowScriptTask flowTask) {
        super(flowTask);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<Map<String, Object>> getRoutevents() {
        return routevents;
    }

    public void addRoute(FlowRoute route) {
        Map<String, Object> routeMap = new HashMap<>();
        routeMap.put("type", "route");
        routeMap.put("value", route.getName());
        routeMap.put("expression", route.getExpression());
        this.routevents.add(routeMap);
    }
}
