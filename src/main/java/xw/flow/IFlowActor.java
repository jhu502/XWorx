package xw.flow;

import xw.flow.bean.FlowGroup;
import xw.flow.bean.FlowRole;
import xw.flow.bean.FlowRoute;
import xw.flow.bean.FlowUser;

import java.util.List;

public interface IFlowActor {
    String getNecessity();

    List<FlowRoute> getRoutes();

    List<FlowUser> getUsers();

    List<FlowGroup> getGroups();

    List<FlowRole> getRoles();
}
