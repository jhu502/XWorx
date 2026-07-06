package xw.flow.constants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlowConstant {
    public static final String NAMESPACE = "http://www.xworx.cn/xflow";
    public static final String NS_PREFIX = "xflow";
    public static final String CONFIG = "config";
    public static final String ROUTE = "route";
    public static final String EVENT = "event";
    public static final String ACTOR = "actor";
    public static final String NODE = "node";
    public static final String TIMER = "timer";
    public static final String VARIABLE = "variable";
    public static final String NAME = "name";
    public static final String DATA = "data";
    public static final String DISPLAY = "display";
    public static final String END_EVENT = "endEvent";
    public static final String EXPRESSION = "expression";
    public static final String IMAGE = "image";
    public static final String INSTRUCTIONS = "instructions";
    public static final String IMPLEMENTATION = "implementation";
    public static final String IMPLEMENTED_TYPE = "implementedType";
    public static final String JUEL = "juel";
    public static final String NECESSITY = "necessity";
    public static final String LABEL = "label";
    public static final String LANGUAGE = "language";
    public static final String NODEID = "nodeId";
    public static final String POSITION = "position";
    public static final String PROCESS = "process";
    public static final String PARTICIPANTS = "participants";
    public static final String ROUTES = "routes";
    public static final String ROUTE_EVENTS = "routevents";
    public static final String START_EVENT = "startEvent";
    public static final String TARGET = "target";
    public static final String VARIABLES = "variables";
    public static final String GROUP = "group";
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String YEAR = "year";
    public static final String YEARS = "years";
    public static final String MONTH = "month";
    public static final String MONTHS = "months";
    public static final String DAY = "day";
    public static final String DAYS = "days";
    public static final String HOUR = "hour";
    public static final String HOURS = "hours";
    public static final String MINUTE = "minute";
    public static final String MINUTES = "minutes";
    public static final String SECOND = "second";
    public static final String SECONDS = "seconds";
    public static final String SOURCE = "source";
    public static final String THING = "thing";
    public static final String XTYPE = "xtype";
    public static final String TEXT = "text";
    public static final String ANY = "ANY";
    public static final String ID = "id";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String L_NODE = "l-node"; //节点背景为绿色
    public static final String H_NODE = "h-node"; //节点背景为灰色
    public static final String W_NODE = "w-node"; //节点背景无颜色
    public static final String C_NODE = "c-node"; //节点背景为橙色
    public static final String X_EDGE = "x-edge"; //连接线的样式
    public static final Map<String, String> varTypeMap = new LinkedHashMap<>();

    public static Map<String, String> varTypes() {
        if (varTypeMap.isEmpty()) {
            varTypeMap.put(Boolean.class.getName(), Boolean.class.getName());
            varTypeMap.put(Double.class.getName(), Double.class.getName());
            varTypeMap.put(Integer.class.getName(), Integer.class.getName());
            varTypeMap.put(Long.class.getName(), Long.class.getName());
            varTypeMap.put(String.class.getName(), String.class.getName());
            varTypeMap.put(List.class.getName(), List.class.getName());
            varTypeMap.put(HashMap.class.getName(), HashMap.class.getName());
        }
        return varTypeMap;
    }
}
