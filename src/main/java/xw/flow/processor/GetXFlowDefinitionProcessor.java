package xw.flow.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.*;
import xw.flow.entity.*;

import java.util.List;

public class GetXFlowDefinitionProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XFlowDefinition definition = (XFlowDefinition) commandBean.getPrimaryObj();

        FlowDataBean flowDataBean = new FlowDataBean();
        flowDataBean.setNumber(definition.getNumber());
        flowDataBean.setName(definition.getName());
        flowDataBean.setInstructions(definition.getInstructions());
        for (FlowVariable variable : definition.getVariables()) {
            flowDataBean.addVariables(variable);
        }

        List<XFlowEvent> eventList = XFlowRepositoryHelper.repository().findXFlowEvent(definition);
        for (XFlowEvent task : eventList) {
            EventNodeVO nodeBean = EventNodeVO.newInstance(task, true);
            flowDataBean.getNodes().add(nodeBean);
        }
        List<XFlowUserTask> userTasks = XFlowRepositoryHelper.repository().findXFlowUserTask(definition);
        for (XFlowUserTask task : userTasks) {
            UserTaskVO userTaskBean = UserTaskVO.newInstance(task, true);
            flowDataBean.getNodes().add(userTaskBean);
        }
        List<XFlowScriptTask> scriptTasks = XFlowRepositoryHelper.repository().findXFlowScriptTask(definition);
        for (XFlowScriptTask task : scriptTasks) {
            ScriptTaskVO scriptTaskVO = ScriptTaskVO.newInstance(task, true);
            flowDataBean.getNodes().add(scriptTaskVO);
        }
        List<XFlowServiceTask> serviceTasks = XFlowRepositoryHelper.repository().findXFlowServiceTask(definition);
        for (XFlowServiceTask task : serviceTasks) {
            ServiceTaskVO serviceTaskVO = ServiceTaskVO.newInstance(task, true);

            flowDataBean.getNodes().add(serviceTaskVO);
        }
        List<XFlowGateway> gateList = XFlowRepositoryHelper.repository().findXFlowGateway(definition);
        for (XFlowGateway gateway : gateList) {
            GatewayVO gatewayBean = GatewayVO.newInstance(gateway, true);
            flowDataBean.getNodes().add(gatewayBean);
        }
        List<XFlowTimer> timerList = XFlowRepositoryHelper.repository().findXFlowTimer(definition);
        for (XFlowTimer thing : timerList) {
            TimeRobotVO timerBean = TimeRobotVO.newInstance(thing, true);
            flowDataBean.getNodes().add(timerBean);
        }
        List<XFlowThing> thingList = XFlowRepositoryHelper.repository().findXFlowThing(definition);
        for (XFlowThing thing : thingList) {
            ThingNodeVO thingBean = ThingNodeVO.newInstance(thing, true);
            flowDataBean.getNodes().add(thingBean);
        }

        List<XFlowEdge> edgeList = XFlowRepositoryHelper.repository().findXFlowEdge(definition);
        for (XFlowEdge edge : edgeList) {
            FlowEdgeVO edgeBean = new FlowEdgeVO();
            edgeBean.setId(edge.getEdgeId());
            edgeBean.setShape("x-edge");
            edgeBean.setLabel(edge.getRoutes());
            edgeBean.setRoutes(edge.getRoutes());
            edgeBean.setSource(edge.getSource());
            edgeBean.setTarget(edge.getTarget());
            flowDataBean.getEdges().add(edgeBean);
        }

        formResult.setData(flowDataBean);
        return formResult;
    }
}
