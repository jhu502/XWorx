package xw.flow.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.FlameUtils;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.FlowVariable;
import xw.flow.constants.FlowNodeType;
import xw.flow.entity.*;

import java.util.*;

/**
 * 页面流程模板编辑器在保存当前流程时，当前流程数据都会被提交到这个Processor
 */
public class SaveProcessDefinitionProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();
        String content = commandBean.getTextParameter("flow_content");
        if (FlameUtils.isBlank(content))
            throw new XException("流程图数据为空.");

        XFlowDefinition definition = (XFlowDefinition) primary;
        if (definition == null) {
            String name = commandBean.getTextParameter("name");
            if (FlameUtils.isBlank(name))
                throw new XException("流程名称是空.");

            List<XFlowDefinition> list = XFlowRepositoryHelper.repository().findDefinitionByName(name);
            if (list == null || list.isEmpty()) {
                definition = XFlowDefinition.newXFlowDefinition(UUID.randomUUID().toString(), name);
                definition.setLatest(true);
                definition = PersistenceHelper.service().save(definition);
            } else {
                definition = list.get(0);
            }
        }
        Map<String, XFlowNode> nodeMap = new HashMap<>();
        JsonNode flowJson = JsonUtils.convertJsonNode(content);
        if (JsonNodeType.OBJECT.equals(flowJson.getNodeType())) {
            ObjectNode objectNode = (ObjectNode) flowJson;
            if (objectNode.has("name")) {
                definition.setName(objectNode.get("name").asText());
            }
            if (objectNode.has("instructions")) {
                definition.setInstructions(objectNode.get("instructions").asText());
            }
            if (objectNode.has("variables")) {
                ArrayNode variables = (ArrayNode) objectNode.get("variables");
                if (!variables.isEmpty()) {
                    definition.getVariables().clear();
                    Iterator<JsonNode> vars = variables.elements();
                    while (vars.hasNext()) {
                        JsonNode jsonVar = vars.next();
                        FlowVariable flowVar = FlowVariable.newInstance(jsonVar);
                        definition.addVariable(flowVar);
                    }
                }
            }
            if (objectNode.has("nodes")) {
                ArrayNode nodeArray = (ArrayNode) objectNode.get("nodes");
                Iterator<JsonNode> nodes = nodeArray.elements();
                while (nodes.hasNext()) {
                    JsonNode jsonNode = nodes.next();
                    String xtype = jsonNode.get("xtype").asText();
                    FlowNodeType nodeType = FlowNodeType.valueOf(xtype);
                    if (FlowNodeType.startEvent.equals(nodeType) || FlowNodeType.endEvent.equals(nodeType) || FlowNodeType.groundEvent.equals(nodeType)) {
                        XFlowEvent flowEvent = XFlowDefinitionHelper.definition().createXFlowEvent(jsonNode, definition);
                        nodeMap.put(flowEvent.getNodeId(), flowEvent);
                    } else if (FlowNodeType.userTask.equals(nodeType)) {
                        XFlowUserTask flowTask = XFlowDefinitionHelper.definition().createXFlowUserTask(jsonNode, definition);
                        nodeMap.put(flowTask.getNodeId(), flowTask);
                    } else if (FlowNodeType.scriptTask.equals(nodeType)) {
                        XFlowScriptTask flowTask = XFlowDefinitionHelper.definition().createXFlowScriptTask(jsonNode, definition);
                        nodeMap.put(flowTask.getNodeId(), flowTask);
                    } else if (FlowNodeType.serviceTask.equals(nodeType)) {
                        XFlowServiceTask flowTask = XFlowDefinitionHelper.definition().createXFlowServiceTask(jsonNode, definition);
                        nodeMap.put(flowTask.getNodeId(), flowTask);
                    } else if (FlowNodeType.manualTask.equals(nodeType) || FlowNodeType.sendTask.equals(nodeType) || FlowNodeType.receiveTask.equals(nodeType)) {
                        XFlowUserTask flowTask = XFlowDefinitionHelper.definition().createXFlowUserTask(jsonNode, definition);
                        nodeMap.put(flowTask.getNodeId(), flowTask);
                    } else if (FlowNodeType.parallelGateway.equals(nodeType) || FlowNodeType.complexGateway.equals(nodeType) ||
                            FlowNodeType.exclusiveGateway.equals(nodeType) || FlowNodeType.inclusiveGateway.equals(nodeType)) {
                        XFlowGateway gateway = XFlowDefinitionHelper.definition().createXFlowGateway(jsonNode, definition);
                        nodeMap.put(gateway.getNodeId(), gateway);
                    } else if (FlowNodeType.andGateway.equals(nodeType) || FlowNodeType.orGateway.equals(nodeType)) {
                        XFlowGateway gateway = XFlowDefinitionHelper.definition().createXFlowGateway(jsonNode, definition);
                        nodeMap.put(gateway.getNodeId(), gateway);
                    } else if (FlowNodeType.timeRobot.equals(nodeType)) {
                        XFlowTimer flowTimer = XFlowDefinitionHelper.definition().createXFlowTimer(jsonNode, definition);
                        nodeMap.put(flowTimer.getNodeId(), flowTimer);
                    } else if (FlowNodeType.thingTask.equals(nodeType)) {
                        XFlowThing flowThing = XFlowDefinitionHelper.definition().createXFlowThing(jsonNode, definition);
                        nodeMap.put(flowThing.getNodeId(), flowThing);
                    } else if (FlowNodeType.timeRobot.equals(nodeType)) {
                        XFlowTimer flowTimer = XFlowDefinitionHelper.definition().createXFlowTimer(jsonNode, definition);
                        nodeMap.put(flowTimer.getNodeId(), flowTimer);
                    }
                }
            }

            List<XFlowEdge> deleteEdges = new ArrayList<>();
            if (objectNode.has("edges")) {
                ArrayNode edgeArray = (ArrayNode) objectNode.get("edges");
                Map<String, XFlowEdge> edgeMap = new HashMap<>();
                Iterator<JsonNode> edges = edgeArray.elements();
                while (edges.hasNext()) {
                    JsonNode jsonNode = edges.next();
                    String xtype = jsonNode.get("xtype").asText();
                    FlowNodeType nodeType = FlowNodeType.valueOf(xtype);
                    if (FlowNodeType.flowEdge.equals(nodeType)) {
                        XFlowEdge flowEdge = XFlowDefinitionHelper.definition().createXFlowEdge(jsonNode, definition, nodeMap);
                        if (flowEdge != null) {
                            edgeMap.put(flowEdge.getEdgeId(), flowEdge);
                        }
                    }
                }
                List<XFlowEdge> edgeList = XFlowRepositoryHelper.repository().findXFlowEdge(definition);
                for (XFlowEdge flowEdge : edgeList) {
                    if (!edgeMap.containsKey(flowEdge.getEdgeId())) {
                        deleteEdges.add(flowEdge);
                    }
                }
            }

            List<XFlowNode> deleteNodes = new ArrayList<>();
            for (XFlowEvent event : XFlowRepositoryHelper.repository().findXFlowEvent(definition)) {
                if (!nodeMap.containsKey(event.getNodeId())) {
                    deleteNodes.add(event);
                }
            }
            for (XFlowUserTask task : XFlowRepositoryHelper.repository().findXFlowUserTask(definition)) {
                if (!nodeMap.containsKey(task.getNodeId())) {
                    deleteNodes.add(task);
                }
            }
            for (XFlowScriptTask task : XFlowRepositoryHelper.repository().findXFlowScriptTask(definition)) {
                if (!nodeMap.containsKey(task.getNodeId())) {
                    deleteNodes.add(task);
                }
            }
            for (XFlowServiceTask task : XFlowRepositoryHelper.repository().findXFlowServiceTask(definition)) {
                if (!nodeMap.containsKey(task.getNodeId())) {
                    deleteNodes.add(task);
                }
            }
            for (XFlowGateway gateway : XFlowRepositoryHelper.repository().findXFlowGateway(definition)) {
                if (!nodeMap.containsKey(gateway.getNodeId())) {
                    deleteNodes.add(gateway);
                }
            }
            for (XFlowTimer timer : XFlowRepositoryHelper.repository().findXFlowTimer(definition)) {
                if (!nodeMap.containsKey(timer.getNodeId())) {
                    deleteNodes.add(timer);
                }
            }
            for (XFlowThing thing : XFlowRepositoryHelper.repository().findXFlowThing(definition)) {
                if (!nodeMap.containsKey(thing.getNodeId())) {
                    deleteNodes.add(thing);
                }
            }
            if (deleteEdges.size() > 0) {
                PersistenceHelper.service().remove(deleteEdges);
            }
            if (deleteNodes.size() > 0) {
                PersistenceHelper.service().remove(deleteNodes);
            }

            definition = PersistenceHelper.service().save(definition);
        }
        /**
         * 收集OR Gateway的前置实体节点, 因为流程流经OR Gateway节点后, 前面Open的前置节点需要关闭掉
         */
        Map<String, Set<String>> orGatewayMap = XFlowDefinitionHelper.definition().getPreviousNode(definition);
        List<XFlowGateway> gatewayList = XFlowRepositoryHelper.repository().findXFlowGateway(definition);
        for (XFlowGateway gateway : gatewayList) {
            Set<String> prevSet = orGatewayMap.get(gateway.getNodeId());
            if (prevSet != null && !prevSet.isEmpty()) {
                gateway.setArguments(new ArrayList<>(prevSet));
                PersistenceHelper.service().save(gateway);
            }
        }

        formResult.setData(definition);

        return formResult;
    }
}
