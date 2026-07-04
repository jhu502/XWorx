package xw.flow.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import xw.flow.XFlowExecutionHelper;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.*;
import xw.flow.constants.FlowConstant;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetXWorkInstanceProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XWorkInstance workInstance = (XWorkInstance) commandBean.getPrimaryObj();
        XFlowDefinition definition = workInstance.getDefinition();

        Set<String> runtimeSet = new HashSet<>();
        List<Execution> runtimeList = XFlowExecutionHelper.execution().getExecutionEntity(workInstance);
        for (Execution execution : runtimeList) {
            String activityId = execution.getActivityId();
            if (activityId != null && !activityId.isEmpty())
                runtimeSet.add(execution.getActivityId());
        }
        Set<String> historySet = new HashSet<>();
        List<HistoricActivityInstance> historyList = XFlowExecutionHelper.execution().getHistoricActivity(workInstance);
        for (HistoricActivityInstance activity : historyList) {
            String activityId = activity.getActivityId();
            if (activityId != null && !activityId.isEmpty())
                historySet.add(activity.getActivityId());
        }
        Map<String, XWorkActivity> activityMap = XFlowExecutionHelper.execution().getLatestXWorkActivity(workInstance);

        FlowDataBean flowData = new FlowDataBean();
        List<XFlowUserTask> userTasks = XFlowRepositoryHelper.repository().findXFlowUserTask(definition);
        for (XFlowUserTask task : userTasks) {
            UserTaskVO taskVO = UserTaskVO.newInstance(task, false);
            XWorkActivity activity = activityMap.get(task.getNodeId());
            if (activity == null) {
                if (runtimeSet.contains(task.getNodeId())) {
                    taskVO.setShape(FlowConstant.L_NODE);
                } else if (historySet.contains(task.getNodeId())) {
                    taskVO.setShape(FlowConstant.H_NODE);
                } else {
                    taskVO.setShape(FlowConstant.W_NODE);
                }
            } else {
                if (FlowStatus.OPEN.equals(activity.getStatus())) {
                    taskVO.setShape(FlowConstant.L_NODE);
                } else if (FlowStatus.TERMINATED.equals(activity.getStatus())) {
                    taskVO.setShape(FlowConstant.C_NODE);
                } else if (FlowStatus.COMPLETED.equals(activity.getStatus())) {
                    taskVO.setShape(FlowConstant.H_NODE);
                }  else {
                    taskVO.setShape(FlowConstant.W_NODE);
                }
            }
            flowData.getNodes().add(taskVO);
        }
        List<XFlowScriptTask> scriptTasks = XFlowRepositoryHelper.repository().findXFlowScriptTask(definition);
        for (XFlowScriptTask task : scriptTasks) {
            ScriptTaskVO taskVO = ScriptTaskVO.newInstance(task, false);
            if (runtimeSet.contains(task.getNodeId())) {
                taskVO.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(task.getNodeId())) {
                taskVO.setShape(FlowConstant.H_NODE);
            } else {
                taskVO.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(taskVO);
        }
        List<XFlowServiceTask> serviceTasks = XFlowRepositoryHelper.repository().findXFlowServiceTask(definition);
        for (XFlowServiceTask task : serviceTasks) {
            ServiceTaskVO taskVO = ServiceTaskVO.newInstance(task, false);
            if (runtimeSet.contains(task.getNodeId())) {
                taskVO.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(task.getNodeId())) {
                taskVO.setShape(FlowConstant.H_NODE);
            } else {
                taskVO.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(taskVO);
        }
        List<XFlowEvent> eventList = XFlowRepositoryHelper.repository().findXFlowEvent(definition);
        for (XFlowEvent event : eventList) {
            EventNodeVO nodeBean = EventNodeVO.newInstance(event, false);
            if (runtimeSet.contains(event.getNodeId())) {
                nodeBean.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(event.getNodeId())) {
                nodeBean.setShape(FlowConstant.H_NODE);
            } else {
                nodeBean.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(nodeBean);
        }
        List<XFlowGateway> gateList = XFlowRepositoryHelper.repository().findXFlowGateway(definition);
        for (XFlowGateway gateway : gateList) {
            GatewayVO gatewayBean = GatewayVO.newInstance(gateway, false);
            if (runtimeSet.contains(gateway.getNodeId())) {
                gatewayBean.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(gateway.getNodeId())) {
                gatewayBean.setShape(FlowConstant.H_NODE);
            } else {
                gatewayBean.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(gatewayBean);
        }
        List<XFlowTimer> timerList = XFlowRepositoryHelper.repository().findXFlowTimer(definition);
        for (XFlowTimer timer : timerList) {
            TimeRobotVO timerBean = TimeRobotVO.newInstance(timer, false);
            if (runtimeSet.contains(timer.getNodeId())) {
                timerBean.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(timer.getNodeId())) {
                timerBean.setShape(FlowConstant.H_NODE);
            } else {
                timerBean.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(timerBean);
        }
        List<XFlowThing> thingList = XFlowRepositoryHelper.repository().findXFlowThing(definition);
        for (XFlowThing thing : thingList) {
            ThingNodeVO nodeBean = ThingNodeVO.newInstance(thing, false);
            if (runtimeSet.contains(thing.getNodeId())) {
                nodeBean.setShape(FlowConstant.L_NODE);
            } else if (historySet.contains(thing.getNodeId())) {
                nodeBean.setShape(FlowConstant.H_NODE);
            } else {
                nodeBean.setShape(FlowConstant.W_NODE);
            }
            flowData.getNodes().add(nodeBean);
        }

        List<XFlowEdge> edgeList = XFlowRepositoryHelper.repository().findXFlowEdge(definition);
        for (XFlowEdge edge : edgeList) {
            FlowEdgeVO edgeBean = new FlowEdgeVO();
            edgeBean.setId(edge.getEdgeId());
            edgeBean.setShape(FlowConstant.X_EDGE);
            edgeBean.setLabel(edge.getRoutes());
            edgeBean.setRoutes(edge.getRoutes());
            edgeBean.setSource(edge.getSource());
            edgeBean.setTarget(edge.getTarget());
            flowData.getEdges().add(edgeBean);
        }

        formResult.setData(flowData);

        return formResult;
    }
}
