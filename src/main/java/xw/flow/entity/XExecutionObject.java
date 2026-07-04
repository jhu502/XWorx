package xw.flow.entity;

import com.flame.orm.XObject;
import jakarta.persistence.*;
import xw.flow.constants.FlowStatus;

@MappedSuperclass
public class XExecutionObject extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "activityId")
    private String activityId = "";
    @Basic
    @Column(name = "executionId")
    private String executionId = "";
    @Basic
    @Column(name = "actInstId")
    private String actInstId = "";
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 100)
    private FlowStatus status = FlowStatus.OPEN;
    @ManyToOne(targetEntity = XWorkInstance.class)
    @JoinColumn(name = "instanceId", foreignKey = @ForeignKey(name = "FLOWINSTANCE_ID_FK"))
    private XWorkInstance instance;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getActInstId() {
        return actInstId;
    }

    public void setActInstId(String actInstId) {
        this.actInstId = actInstId;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public XWorkInstance getInstance() {
        return instance;
    }

    public void setInstance(XWorkInstance instance) {
        this.instance = instance;
    }
}
