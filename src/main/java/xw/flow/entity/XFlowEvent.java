package xw.flow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XFlowEvent", uniqueConstraints = {})
public class XFlowEvent extends XFlowNode {
    private static final long serialVersionUID = 1L;

    public static XFlowEvent newInstance(String nodeId, XFlowDefinition definition) {
        XFlowEvent flowEvent = new XFlowEvent();
        flowEvent.setXFlowDefinition(definition);
        flowEvent.setNodeId(nodeId);

        return flowEvent;
    }
}
