package xw.flow.entity;

import java.sql.Timestamp;

import org.flowable.task.api.Task;

import com.flame.annotations.XDefinition;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IThingModel;
import com.flame.util.FlameUtils;
import com.thing.common.DefaultThing;
import com.thing.entity.XThingModel;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import xw.auths.entity.XUser;
import xw.flow.constants.FlowStatus;

@Entity
@Table(name = "XWorkTask", uniqueConstraints = {})
@XDefinition(name = "XWorkTask", config = DefaultThing.class, icon = "images/flow/workitem.png", description = "WorkTask", display = "WorkTask", en_US = "WorkTask", zh_CN = "工作任务")
public class XWorkTask extends XObject {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "taskId", nullable = false)
	private String taskId = "";
	@Basic
	@Column(name = "name", nullable = false)
	private String name = "";
	@Basic
	@Column(name = "routes")
	private String routes = "";
	@Basic
	@Column(name = "remarks")
	private String remarks = "";
	@Basic
	@Column(name = "completedBy")
	private String completedBy = "";
	@Basic
	@Column(name = "completedOn")
	private Timestamp completedOn;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 100)
	private FlowStatus status = FlowStatus.OPEN;
	@ManyToOne
	@JoinColumn(name = "assignee", foreignKey = @ForeignKey(name = "ASSIGNEE_ID_FK"))
	private XUser assignee;
	@ManyToOne(targetEntity = XWorkActivity.class)
	@JoinColumn(name = "activityId", foreignKey = @ForeignKey(name = "WORKACTIVITY_ID_FK"))
	private XWorkActivity activity;
	@ManyToOne(targetEntity = XWorkInstance.class)
	@JoinColumn(name = "instanceId", foreignKey = @ForeignKey(name = "FLOWINSTANCE_ID_FK"))
	private XWorkInstance instance;

	public static XWorkTask newInstance(XWorkInstance instance, XWorkActivity activity, Task entity, XUser user) {
		XWorkTask workItem = new XWorkTask();
		workItem.setInstance(instance);
		workItem.setActivity(activity);
		workItem.setName(entity.getName());
		workItem.setTaskId(entity.getId());
		workItem.setAssignee(user);

		return workItem;
	}

	public XWorkInstance getInstance() {
		return instance;
	}

	public void setInstance(XWorkInstance instance) {
		this.instance = instance;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoutes() {
		return routes;
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public FlowStatus getStatus() {
		return status;
	}

	public void setStatus(FlowStatus status) {
		this.status = status;
	}

	public boolean isOpenStatus() {
		return FlowStatus.OPEN.equals(this.getStatus());
	}

	public XUser getAssignee() {
		return assignee;
	}

	public void setAssignee(XUser assignee) {
		this.assignee = assignee;
	}

	public String getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}

	public Timestamp getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Timestamp completedOn) {
		this.completedOn = completedOn;
	}

	public XWorkActivity getActivity() {
		return activity;
	}

	public void setActivity(XWorkActivity activity) {
		this.activity = activity;
	}
}
