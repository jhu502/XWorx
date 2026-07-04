package xw.flow.entity;

import java.util.ArrayList;
import java.util.List;

import com.flame.orm.XConstant;
import com.flame.orm.JsonArrayConverter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import xw.flow.IFlowActor;
import xw.flow.IFlowEvent;
import xw.flow.IFlowRoute;
import xw.flow.IFlowVariable;
import xw.flow.bean.FlowEvent;
import xw.flow.bean.FlowGroup;
import xw.flow.bean.FlowRole;
import xw.flow.bean.FlowRoute;
import xw.flow.bean.FlowUser;
import xw.flow.bean.FlowVariable;
import xw.flow.constants.FlowConstant;
import xw.flow.constants.FlowNodeType;

@Entity
@Table(name = "XFlowUserTask", uniqueConstraints = {})
public class XFlowUserTask extends XFlowNode implements IFlowRoute, IFlowEvent, IFlowActor, IFlowVariable {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "taskForm")
	private String taskForm = "thymeleaf/xflow/taskform/workitemReviewData.html";
	@Basic
	@Column(name = "necessity")
	private String necessity = "ANY";
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "variables", columnDefinition = XConstant.JSONB)
	private List<FlowVariable> variables = new ArrayList<>();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "routes", columnDefinition = XConstant.JSONB)
	private List<FlowRoute> routes = new ArrayList<>();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "events", columnDefinition = XConstant.JSONB)
	private List<FlowEvent> events = new ArrayList<>();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "users", columnDefinition = XConstant.JSONB)
	private List<FlowUser> users = new ArrayList<>();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "groups", columnDefinition = XConstant.JSONB)
	private List<FlowGroup> groups = new ArrayList<>();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "roles", columnDefinition = XConstant.JSONB)
	private List<FlowRole> roles = new ArrayList<>();

	public static XFlowUserTask newInstance(String nodeId, XFlowDefinition definition) {
		XFlowUserTask flowtask = new XFlowUserTask();
		flowtask.setNodeType(FlowNodeType.userTask);
		flowtask.setXFlowDefinition(definition);
		flowtask.setNecessity(FlowConstant.ANY);
		flowtask.setNodeId(nodeId);

		return flowtask;
	}

	public String getTaskForm() {
		return taskForm;
	}

	public void setTaskForm(String taskForm) {
		this.taskForm = taskForm;
	}

	public String getNecessity() {
		return necessity;
	}

	public void setNecessity(String necessity) {
		this.necessity = necessity;
	}

	public List<FlowVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<FlowVariable> variables) {
		this.variables = variables;
	}

	public void addVariable(FlowVariable variable) {
		this.variables.add(variable);
	}

	public List<FlowRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<FlowRoute> routes) {
		this.routes = routes;
	}

	public void addRoute(FlowRoute route) {
		this.routes.add(route);
	}

	public List<FlowEvent> getEvents() {
		return events;
	}

	public void setEvents(List<FlowEvent> events) {
		this.events = events;
	}

	public void addEvent(FlowEvent event) {
		this.events.add(event);
	}

	public List<FlowUser> getUsers() {
		return users;
	}

	public void setUsers(List<FlowUser> users) {
		this.users = users;
	}

	public void addUser(FlowUser user) {
		this.users.add(user);
	}

	public List<FlowGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<FlowGroup> groups) {
		this.groups = groups;
	}

	public void addGroup(FlowGroup group) {
		this.groups.add(group);
	}

	public List<FlowRole> getRoles() {
		return roles;
	}

	public void setRoles(List<FlowRole> roles) {
		this.roles = roles;
	}

	public void addRole(FlowRole role) {
		this.roles.add(role);
	}
}
