package xw.flow.bean;

import xw.flow.entity.XFlowServiceTask;

public class ServiceTaskVO extends FlowNodeVO {
    private String implementedType = "";
    private String implementation = "";

    public static ServiceTaskVO newInstance(XFlowServiceTask flowTask, boolean movable) {
        ServiceTaskVO taskBean = new ServiceTaskVO(flowTask);
        taskBean.setMovable(movable);
        taskBean.setImplementedType(flowTask.getImplementedType());
        taskBean.setImplementation(flowTask.getImplementation());
        return taskBean;
    }

    public ServiceTaskVO() {}

    public ServiceTaskVO(XFlowServiceTask flowTask) {
        super(flowTask);
    }

    public String getImplementedType() {
        return implementedType;
    }

    public void setImplementedType(String implementedType) {
        this.implementedType = implementedType;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }
}
