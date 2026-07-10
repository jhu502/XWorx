package xw.flow.bean;

import xw.flow.entity.XFlowUserTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTaskVO extends FlowNodeVO {
    private String taskForm;
    private String necessity;
    private List<Map<String, Object>> variables = new ArrayList<>();
    private List<Map<String, Object>> participants = new ArrayList<>();
    private List<Map<String, Object>> routeEvents = new ArrayList<>();

    public static UserTaskVO newInstance(XFlowUserTask flowTask, boolean movable) {
        UserTaskVO taskBean = new UserTaskVO(flowTask);
        taskBean.setMovable(movable);
        taskBean.setTaskForm(flowTask.getTaskForm());
        taskBean.setNecessity(flowTask.getNecessity());
        for (FlowRole role: flowTask.getRoles()) {
            taskBean.addRole(role);
        }
        for (FlowGroup group : flowTask.getGroups()) {
            taskBean.addGroup(group);
        }
        for (FlowUser user : flowTask.getUsers()) {
            taskBean.addUser(user);
        }
        for (FlowEvent event : flowTask.getEvents()) {
            taskBean.addEvent(event);
        }
        for (FlowRoute route : flowTask.getRoutes()) {
            taskBean.addRoute(route);
        }
        for (FlowVariable var : flowTask.getVariables()) {
            taskBean.addVariables(var);
        }
        return taskBean;
    }

    public UserTaskVO() {}

    public UserTaskVO(XFlowUserTask flowTask) {
        super(flowTask);
    }

    public String getTaskForm() {
        return taskForm;
    }

    public void setTaskForm(String taskForm) {
        this.taskForm = taskForm;
    }

    public String getNecessity() {
        return necessity;
    }

    public void setNecessity(String necessity) {
        this.necessity = necessity;
    }

    public List<Map<String, Object>> getVariables() {
        return variables;
    }

    public void addVariables(FlowVariable variable) {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("name", variable.getName());
        varMap.put("display", variable.getDisplay());
        varMap.put("value", variable.getValue());
        varMap.put("type", variable.getType());
        this.variables.add(varMap);
    }

    public List<Map<String, Object>> getParticipants() {
        return participants;
    }

    public void addUser(FlowUser user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("type", "user");
        userMap.put("value", user.getName());
        userMap.put("display", user.getDisplay());
        this.participants.add(userMap);
    }

    public void addRole(FlowRole role) {
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("type", "role");
        roleMap.put("value", role.getName());
        roleMap.put("display", role.getDisplay());
        this.participants.add(roleMap);
    }

    public void addGroup(FlowGroup group) {
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("type", "group");
        groupMap.put("value", group.getName());
        groupMap.put("display", group.getDisplay());
        this.participants.add(groupMap);
    }

    public List<Map<String, Object>> getRouteEvents() {
        return routeEvents;
    }

    public void addRoute(FlowRoute route) {
        Map<String, Object> routeMap = new HashMap<>();
        routeMap.put("type", "route");
        routeMap.put("value", route.getName());
        routeMap.put("expression", route.getExpression());
        this.routeEvents.add(routeMap);
    }

    public void addEvent(FlowEvent event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("type", "event");
        eventMap.put("value", event.getName());
        eventMap.put("expression", event.getExpression());
        this.routeEvents.add(eventMap);
    }
}
