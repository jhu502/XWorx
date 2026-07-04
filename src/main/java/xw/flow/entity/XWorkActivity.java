package xw.flow.entity;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

import org.flowable.engine.history.HistoricActivityInstance;

import com.flame.annotations.XDefinition;
import com.flame.orm.JsonObjectConverter;
import com.flame.orm.XConstant;
import com.flame.thing.IThingModel;
import com.thing.common.DefaultThing;
import com.thing.entity.XThingModel;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import xw.flow.constants.FlowContext;

@Entity
@Table(name = "XWorkActivity")
@XDefinition(name = "XWorkActivity", config = DefaultThing.class, icon = "images/flow/activity.png", description = "XWorkActivity", display = "Work Activity", en_US = "Work Activity", zh_CN = "流程活动")
public class XWorkActivity extends XExecutionObject {
	@Serial
    private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "name")
	private String name = "";
	@Basic
	@Column(name = "necessity")
	private String necessity = "";
	@Basic
	@Column(name = "routes")
	private String routes = "";
	@Basic
	@Column(name = "taskForm")
	private String taskForm = "";
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonObjectConverter.class)
	@Column(name = "flowContext", columnDefinition = XConstant.JSONB)
	private FlowContext flowContext = new FlowContext();

	public static XWorkActivity newInstance(XWorkInstance instance, HistoricActivityInstance history) {
		XWorkActivity activity = new XWorkActivity();
		activity.setName(history.getActivityName());
		activity.setInstance(instance);
		activity.setActInstId(history.getId());
		activity.setActivityId(history.getActivityId());
		activity.setExecutionId(history.getExecutionId());

		return activity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNecessity() {
		return necessity;
	}

	public void setNecessity(String necessity) {
		this.necessity = necessity;
	}

	public String getRoutes() {
		return routes;
	}

	public Set<String> getRouteSet() {
		Set<String> routeSet = new HashSet<>();
		if (this.routes != null && !this.routes.trim().isEmpty()) {
			for (String route : this.routes.split(",")) {
				String _route = route.trim();
				if (_route.isEmpty() || "?".equals(_route))
					continue;
				routeSet.add(_route);
			}
		}

		return routeSet;
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	public String getTaskForm() {
		return taskForm;
	}

	public void setTaskForm(String taskForm) {
		this.taskForm = taskForm;
	}

	public FlowContext getFlowContext() {
		return flowContext;
	}

	public boolean hasVariable(String name) {
		return flowContext.getVariable(name) != null;
	}

	public void setFlowContext(FlowContext flowContext) {
		this.flowContext = flowContext;
	}
}
