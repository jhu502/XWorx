package xw.flow;

import xw.auths.XGroupHelper;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;
import com.flame.config.basic.BasicConfiguration;
import com.flame.util.FlameUtils;
import xw.flow.bean.FlowRole;
import xw.flow.bean.FlowVariable;
import xw.flow.bean.TimeRobotVO;
import xw.flow.constants.FlowConstant;
import xw.flow.service.XFlowDefinitionService;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;

import java.util.ArrayList;
import java.util.List;

public class XFlowDefinitionHelper {
    private static XFlowDefinitionService definition;

    public static XFlowDefinitionService definition() {
        if (definition == null) {
            definition = BasicConfiguration.getBean(XFlowDefinitionService.class);
        }

        return definition;
    }

    public static List<XUser> getUsers(UserTask userTask) {
        List<XUser> result = new ArrayList<>();
        for (ExtensionElement children : userTask.getExtensionElements().get(FlowConstant.X_ACTOR)) {
            List<ExtensionElement> userList = children.getChildElements().get(FlowConstant.USER);
            if (userList == null || userList.isEmpty())
                return result;

            for (ExtensionElement element : userList) {
                String username = element.getAttributeValue(null, "name");
                if (FlameUtils.isBlank(username))
                    continue;
                XUser xuser = XGroupHelper.repository().findByNameIgnoreCase(username);
                if (xuser == null)
                    continue;
                result.add(xuser);
            }
        }
        return result;
    }

    public static List<XGroup> getGroups(UserTask userTask) {
        List<XGroup> result = new ArrayList<>();
        for (ExtensionElement children : userTask.getExtensionElements().get(FlowConstant.X_ACTOR)) {
            List<ExtensionElement> groupList = children.getChildElements().get(FlowConstant.GROUP);
            if (groupList == null || groupList.isEmpty())
                return result;

            for (ExtensionElement element : groupList) {
                String name = element.getAttributeValue(null, FlowConstant.NAME);
                if (FlameUtils.isBlank(name))
                    continue;
                List<XGroup> groups = XGroupHelper.repository().getXGroupByName(name);
                if (!groups.isEmpty()) {
                    result.addAll(groups);
                }
            }
        }

        return result;
    }

    public static List<FlowRole> getRoles(UserTask userTask) {
        List<FlowRole> result = new ArrayList<>();
        for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.X_ACTOR)) {
            List<ExtensionElement> roleList = element.getChildElements().get(FlowConstant.ROLE);
            if (roleList == null || roleList.isEmpty())
                return result;

            for (ExtensionElement child : roleList) {
                FlowRole role = new FlowRole();
                role.setName(child.getName());
                role.setDisplay(child.getAttributeValue(null, FlowConstant.ROLE));
                result.add(role);
            }
        }

        return result;
    }

    public static List<String> getRoutes(SequenceFlow seqFlow) {
        List<String> result = new ArrayList<>();
        for (ExtensionElement element : seqFlow.getExtensionElements().get(FlowConstant.X_ROUTE)) {
            List<ExtensionElement> routeList = element.getChildElements().get(FlowConstant.ROUTE);
            if (routeList == null || routeList.isEmpty())
                return result;

            for (ExtensionElement child : routeList) {
                String name = child.getAttributeValue(null, FlowConstant.NAME);
                if (FlameUtils.isNotBlank(name) && !"?".equals(name)) {
                    result.add(name);
                }
            }
        }

        return result;
    }

    public static List<String> getRoutes(UserTask userTask) {
        List<String> result = new ArrayList<>();
        for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.X_ROUTE)) {
            List<ExtensionElement> routeList = element.getChildElements().get(FlowConstant.ROUTE);
            if (routeList == null || routeList.isEmpty())
                return result;

            for (ExtensionElement child : routeList) {
                String name = child.getAttributeValue(null, FlowConstant.NAME);
                if (FlameUtils.isNotBlank(name) && !"?".equals(name)) {
                    result.add(name);
                }
            }
        }

        return result;
    }

    public static List<FlowVariable> getVariables(UserTask userTask) {
        List<FlowVariable> result = new ArrayList<>();
        for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.X_VARIABLE)) {
            List<ExtensionElement> variables = element.getChildElements().get(FlowConstant.VARIABLE);
            if (variables == null || variables.isEmpty())
                return result;

            for (ExtensionElement variable : variables) {
                String name = variable.getAttributeValue(null, FlowConstant.NAME);
                String display = variable.getAttributeValue(null, FlowConstant.DISPLAY);
                String type = variable.getAttributeValue(null, FlowConstant.TYPE);
                String value = variable.getAttributeValue(null, FlowConstant.VALUE);
                if (FlameUtils.isNotBlank(name)) {
                    FlowVariable flowVariable = FlowVariable.newInstance(name);
                    flowVariable.setDisplay(display);
                    flowVariable.setType(type);

                    result.add(flowVariable);
                }
            }
        }
        return result;
    }

    public static List<FlowVariable> getVariables(Process process) {
        List<FlowVariable> result = new ArrayList<>();
        for (ExtensionElement element : process.getExtensionElements().get(FlowConstant.X_VARIABLE)) {
            List<ExtensionElement> variables = element.getChildElements().get(FlowConstant.VARIABLE);
            if (variables == null || variables.isEmpty())
                return result;

            for (ExtensionElement variable : variables) {
                String name = variable.getAttributeValue(null, FlowConstant.NAME);
                String display = variable.getAttributeValue(null, FlowConstant.DISPLAY);
                String type = variable.getAttributeValue(null, FlowConstant.TYPE);
                String value = variable.getAttributeValue(null, FlowConstant.VALUE);
                if (FlameUtils.isNotBlank(name)) {
                    FlowVariable flowvar = FlowVariable.newInstance(name);
                    flowvar.setDisplay(display);
                    flowvar.setType(type);
                    
                    if (flowvar.isType(String.class)) {
                        flowvar.setValue(value);
                    } else if (flowvar.isType(Integer.class)) {
                        if (FlameUtils.isInteger(value)) {
                            flowvar.setValue(Integer.parseInt(value));
                        }
                    } else if (flowvar.isType(Long.class)) {
                        if (FlameUtils.isNumeric(value)) {
                            flowvar.setValue(Long.parseLong(value));
                        }
                    } else if (flowvar.isType(Float.class)) {
                        if (FlameUtils.isNumeric(value)) {
                            flowvar.setValue(Float.parseFloat(value));
                        }
                    } else if (flowvar.isType(Double.class)) {
                        if (FlameUtils.isNumeric(value)) {
                            flowvar.setValue(Double.parseDouble(value));
                        }
                    } else if (flowvar.isType(Boolean.class)) {
                        flowvar.setValue(Boolean.parseBoolean(value));
                    } else {
                        flowvar.setValue(value);
                    }
                    result.add(flowvar);
                }
            }
        }
        return result;
    }

    public static String getNecessity(UserTask userTask) {
        for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.X_ACTOR)) {
            List<ExtensionAttribute> attrList = element.getAttributes().get(FlowConstant.NECESSITY);
            if (attrList != null && !attrList.isEmpty()) {
                for (ExtensionAttribute attribute : attrList) {
                    if (FlowConstant.NECESSITY.equals(attribute.getName())) {
                        return attribute.getValue();
                    }
                }
            }
        }
        return "ALL";
    }

    public static TimeRobotVO getTimeRobot(ReceiveTask receiveTask) {
        TimeRobotVO robotVO = new TimeRobotVO();

        robotVO.setId(receiveTask.getId());
        for (ExtensionElement element : receiveTask.getExtensionElements().get(FlowConstant.X_TIMER)) {
            for (ExtensionElement timer : element.getChildElements().get(FlowConstant.TIMER)) {
                String year = timer.getAttributeValue(null, FlowConstant.YEAR);
                year = year == null ? "0" : year;
                robotVO.setYears(Integer.parseInt(year));
                String month = timer.getAttributeValue(null, FlowConstant.MONTH);
                month = month == null ? "0" : month;
                robotVO.setMonths(Integer.parseInt(month));
                String day = timer.getAttributeValue(null, FlowConstant.DAY);
                day = day == null ? "0" : day;
                robotVO.setDays(Integer.parseInt(day));
                String hour = timer.getAttributeValue(null, FlowConstant.HOUR);
                hour = hour == null ? "0" : hour;
                robotVO.setHours(Integer.parseInt(hour));
                String minute = timer.getAttributeValue(null, FlowConstant.MINUTE);
                minute = minute == null ? "0" : minute;
                robotVO.setMinutes(Integer.parseInt(minute));
                String second = timer.getAttributeValue(null, FlowConstant.SECOND);
                second = second == null ? "0" : second;
                robotVO.setSeconds(Integer.parseInt(second));
            }
        }

        return robotVO;
    }

    public static String getNodeConfig(FlowNode node, String el) {
        if (node == null || FlameUtils.isBlank(el))
            return "";

        if (node.getExtensionElements().containsKey(FlowConstant.X_CONFIG)) {
            for (ExtensionElement element : node.getExtensionElements().get(FlowConstant.X_CONFIG)) {
                List<ExtensionElement> elList = element.getChildElements().get(el);
                if (elList == null || elList.isEmpty())
                    return "";

                return elList.get(0).getAttributeValue(null, FlowConstant.VALUE);
            }
        }

        return "";
    }
}
