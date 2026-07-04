package xw.flow.bean;

import xw.flow.entity.XFlowNode;

import java.util.HashMap;
import java.util.Map;

public class FlowNodeVO {
    private String id;
    private String shape;
    private String xtype;
    private String label;
    private Boolean visible = true;
    private double x;
    private double y;
    private Map<String, Object> attrs = new HashMap<>();
    private Map<String, Object> data = new HashMap<>();

    public FlowNodeVO() {}

    public <T extends XFlowNode> FlowNodeVO(T node) {
        this.setId(node.getNodeId());
        this.setShape("x-node");
        this.setXType(node.getNodeType().toString());
        this.setLabel(node.getName());
        this.setX(node.getAxisX());
        this.setY(node.getAxisY());
        this.addAttr("image", Map.of("xlink:href", node.getImage()));
        this.addAttr("label", Map.of("text", node.getName()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getXType() {
        return xtype;
    }

    public void setXType(String xtype) {
        this.xtype = xtype;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void addAttr(String key, Object value) {
        this.attrs.put(key, value);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    /**
     * 设置节点可以拖动
     * @param bool
     */
    public void setMovable(boolean bool) {
        this.addData("nodeMovable", bool);
    }
}
