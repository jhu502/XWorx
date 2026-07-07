package xw.flow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ComplexGateway;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Event;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.InclusiveGateway;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ReceiveTask;
import org.flowable.bpmn.model.ScriptTask;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.Task;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

import xw.flow.IFlowActor;
import xw.flow.IFlowEvent;
import xw.flow.IFlowRoute;
import xw.flow.IFlowTimer;
import xw.flow.IFlowVariable;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.FlowEvent;
import xw.flow.bean.FlowGroup;
import xw.flow.bean.FlowRole;
import xw.flow.bean.FlowRoute;
import xw.flow.bean.FlowUser;
import xw.flow.bean.FlowVariable;
import xw.flow.constants.FlowConstant;
import xw.flow.constants.FlowContext;
import xw.flow.constants.FlowLanguage;
import xw.flow.constants.FlowNodeType;
import xw.flow.constants.FlowRouteType;
import xw.flow.entity.XFlowDefinition;
import xw.flow.entity.XFlowEdge;
import xw.flow.entity.XFlowEvent;
import xw.flow.entity.XFlowGateway;
import xw.flow.entity.XFlowNode;
import xw.flow.entity.XFlowScriptTask;
import xw.flow.entity.XFlowServiceTask;
import xw.flow.entity.XFlowThing;
import xw.flow.entity.XFlowTimer;
import xw.flow.entity.XFlowUserTask;
import xw.flow.flowable.XFlowThingTaskDelegate;
import xw.flow.repos.XFlowRepository;

/**
 * 流程定义服务 —— 负责 XFlow 流程定义的部署、BPMN 模型生成、流程节点/边的创建与管理，以及从 BPMN 扩展元素中解析路由与事件。
 *
 * <p>该服务是 XFlow 设计态的核心服务，承担以下主要职责：</p>
 * <ul>
 *   <li><b>流程部署：</b>将 XFlowDefinition 转换为 BPMN 2.0 兼容的 BpmnModel，并通过 Flowable RepositoryService 部署到引擎</li>
 *   <li><b>BPMN 模型生成：</b>遍历 XFlowDefinition 中的所有节点（UserTask / ScriptTask / ServiceTask / Event / Gateway / Timer / Thing）
 *       和边（Edge），逐一转换为 Flowable BPMN 模型元素，并注入 XFlow 自定义扩展属性（X-TYPE / X-ROUTE / X-EVENT / X-ACTOR / X-TIMER / X-VARIABLE）</li>
 *   <li><b>节点管理：</b>提供各种节点类型（Event / UserTask / ScriptTask / ServiceTask / ManualTask / Thing / Timer / Gateway / Edge）的创建、更新方法，
 *       支持从 JSON（前端画布数据）反序列化并持久化</li>
 *   <li><b>流程拓扑分析：</b>计算 OrGateway 的前置节点关系，用于运行时流程分支决策</li>
 *   <li><b>BPMN 元素反查：</b>从已部署的 UserTask 扩展元素中解析回 XFlow 的 Route / Event 业务对象</li>
 * </ul>
 *
 * <p><b>BPMN 扩展元素说明：</b>XFlow 在标准 BPMN 2.0 基础上通过自定义命名空间（{@code xflow}）注入业务元数据：</p>
 * <table border="1">
 *   <tr><th>扩展元素</th><th>用途</th><th>载体节点</th></tr>
 *   <tr><td>{@code X-TYPE}</td><td>标记 XFlow 节点类型（如 userTask / scriptTask / exclusiveGateway）</td><td>所有 FlowNode</td></tr>
 *   <tr><td>{@code X-ROUTE}</td><td>存储节点的路由规则（审批通过/驳回等）</td><td>UserTask / ScriptTask / Edge</td></tr>
 *   <tr><td>{@code X-EVENT}</td><td>存储节点的触发事件</td><td>UserTask</td></tr>
 *   <tr><td>{@code X-ACTOR}</td><td>存储办理人配置（用户/组/角色 + 必要性）</td><td>UserTask</td></tr>
 *   <tr><td>{@code X-TIMER}</td><td>存储定时器时间配置（年月日时分秒）</td><td>Timer</td></tr>
 *   <tr><td>{@code X-VARIABLE}</td><td>存储流程变量定义</td><td>XFlowDefinition / UserTask</td></tr>
 * </table>
 */
@Service
public class XFlowDefinitionService extends AbstractXFlowService {
	private final String DEFAULT_TASKFORM = "thymeleaf/xflow/taskform/flowWorkTaskReview";
	private final RepositoryService repositoryService;
	private final XFlowRepository flowRepository;

	/**
	 * 构造注入 RepositoryService 与 XFlowRepository。
	 *
	 * @param repositoryService Flowable 资源库服务，用于流程定义的部署与查询
	 * @param flowRepository    XFlow 自定义 Repository，用于流程节点/边等业务对象的数据库访问
	 */
	@Autowired
	public XFlowDefinitionService(RepositoryService repositoryService, XFlowRepository flowRepository) {
		this.repositoryService = repositoryService;
		this.flowRepository = flowRepository;
	}

	// ============================================================
	//   流程部署与 BPMN 模型生成
	// ============================================================

	/**
	 * 将 XFlowDefinition 转换为 BPMN 模型并部署到 Flowable 引擎。
	 *
	 * <p>执行步骤：</p>
	 * <ol>
	 *   <li>创建 DeploymentBuilder，设置部署名称（definition.getName()）和 key（definition.getOid()）</li>
	 *   <li>调用 {@link #generateBpmnModel(XFlowDefinition)} 生成 BpmnModel，以 {@code <oid>.bpmn} 为资源名添加到部署</li>
	 *   <li>执行 deploy() 部署到引擎</li>
	 *   <li>查询部署后的 ProcessDefinition，获取 Flowable 侧的 processDefinitionId</li>
	 *   <li>回写 definition：setDeployed(true) / setProcessDefId(definitionId) 并持久化</li>
	 * </ol>
	 *
	 * @param definition 待部署的 XFlow 流程定义
	 * @return Flowable Deployment 对象，包含部署 ID / 部署时间等元信息
	 */
	@Transactional
	public Deployment deployFlowDefinition(XFlowDefinition definition) {
		DeploymentBuilder deployBuilder = repositoryService.createDeployment().name(definition.getName()).key(definition.getOid());
		Deployment deployment = deployBuilder.addBpmnModel(definition.getOid() + ".bpmn", this.generateBpmnModel(definition)).deploy();
		ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId());
		ProcessDefinition processDefinition = definitionQuery.processDefinitionKey(oid2NCName(definition.getOid())).singleResult();
		definition.setDeployed(true);
		definition.setProcessDefinition(processDefinition);
		definition = PersistenceHelper.service().save(definition);
		LOGGER.info("Flow Definition:%s was deployed successfully.", definition.getName());

		return deployment;
	}

	/**
	 * 生成 XFlowDefinition 对应的 BPMN 2.0 XML 字符串。
	 *
	 * <p>内部调用 {@link #generateBpmnModel(XFlowDefinition)} 构建 BpmnModel，
	 * 再通过 BpmnXMLConverter 将模型序列化为 XML 字节数组，最终以 UTF-8 字符串返回。
	 * 该方法主要用于调试、预览或导出流程定义的 XML 表示。</p>
	 *
	 * @param definition XFlow 流程定义
	 * @return BPMN 2.0 XML 字符串，若定义不完整则可能生成不完整的 XML
	 */
	public String generateBpmnXML(XFlowDefinition definition) {
		BpmnModel bpmnModel = this.generateBpmnModel(definition);
		BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
		return new String(bpmnXMLConverter.convertToXML(bpmnModel));
	}

	/**
	 * 将 XFlowDefinition 转换为 Flowable BpmnModel 对象（核心转换方法）。
	 *
	 * <p>这是从 XFlow 设计态模型到 Flowable 可执行模型的桥梁，转换过程如下：</p>
	 * <ol>
	 *   <li>创建空的 BpmnModel 并注册 XFlow 自定义命名空间（{@code xflow}）</li>
	 *   <li>创建 Process 主流程对象，ID 为 definition.getOid() 的 NCName 转换，name 为所属主数据的 OID</li>
	 *   <li>为 Process 添加 X-VARIABLE 扩展元素（流程级变量定义）</li>
	 *   <li>依次从数据库查询并转换各类型节点：</li>
	 *   <ul>
	 *     <li>XFlowUserTask → BPMN UserTask</li>
	 *     <li>XFlowScriptTask → BPMN ScriptTask</li>
	 *     <li>XFlowServiceTask → BPMN ServiceTask</li>
	 *     <li>XFlowEvent → BPMN StartEvent / EndEvent</li>
	 *     <li>XFlowGateway → BPMN Gateway（根据子类型映射为 Parallel/Exclusive/Inclusive/Complex）</li>
	 *     <li>XFlowTimer → BPMN ReceiveTask（定时器节点）</li>
	 *     <li>XFlowThing → BPMN ServiceTask（实体节点）</li>
	 *   </ul>
	 *   <li>转换所有 XFlowEdge → BPMN SequenceFlow（连线），其中携带 X-ROUTE 扩展元素</li>
	 * </ol>
	 *
	 * @param definition XFlow 流程定义，为 null 时返回 null
	 * @return 完整的 BpmnModel 对象，包含所有节点和连线
	 */
	public BpmnModel generateBpmnModel(XFlowDefinition definition) {
		if (definition == null)
			return null;

		BpmnModel bpmnModel = new BpmnModel();
		bpmnModel.addNamespace(FlowConstant.NS_PREFIX, FlowConstant.NAMESPACE);
		Process process = new Process();
		process.setId(oid2NCName(definition.getOid()));
		process.setName(definition.getMaster().getOid());
		bpmnModel.addProcess(process);

		process.addExtensionElement(this.buildVariableElement(definition));

		List<XFlowUserTask> userTasks = flowRepository.findXFlowUserTask(definition);
		for (XFlowUserTask node : userTasks) {
			FlowNode flowNode = this.convertTask2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowScriptTask> scriptTasks = flowRepository.findXFlowScriptTask(definition);
		for (XFlowScriptTask node : scriptTasks) {
			FlowNode flowNode = this.convertTask2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowServiceTask> serviceTasks = flowRepository.findXFlowServiceTask(definition);
		for (XFlowServiceTask node : serviceTasks) {
			FlowNode flowNode = this.convertTask2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowEvent> events = flowRepository.findXFlowEvent(definition);
		for (XFlowEvent node : events) {
			FlowNode flowNode = this.convertEvent2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowGateway> gateways = flowRepository.findXFlowGateway(definition);
		for (XFlowGateway node : gateways) {
			FlowNode flowNode = this.convertGateway2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowTimer> timers = flowRepository.findXFlowTimer(definition);
		for (XFlowTimer node : timers) {
			FlowNode flowNode = this.convertTimer2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowThing> things = flowRepository.findXFlowThing(definition);
		for (XFlowThing node : things) {
			FlowNode flowNode = this.convertThing2Bpmn(node);
			process.addFlowElement(flowNode);
		}
		List<XFlowEdge> edges = flowRepository.findXFlowEdge(definition);
		for (XFlowEdge edge : edges) {
			SequenceFlow sequenceFlow = this.convertEdge2Bpmn(edge);
			process.addFlowElement(sequenceFlow);
		}

		return bpmnModel;
	}

	// ============================================================
	//   BPMN 扩展元素构造（私有方法）
	// ============================================================

	/**
	 * 为 XFlowNode 构造 X-TYPE 扩展元素，用于在 BPMN 模型中标记 XFlow 节点类型。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-CONFIG>
	 *   <xflow:TYPE value="userTask" />
	 * </xflow:X-CONFIG>
	 * }</pre>
	 * <p>运行时，XFlow 通过解析该扩展元素还原节点的业务类型。</p>
	 *
	 * @param flowNode XFlow 流程节点
	 * @return 包含 X-TYPE 子元素的 ExtensionElement
	 */
	private ExtensionElement buildTypeElement(XFlowNode flowNode) {
		ExtensionElement typeElement = new ExtensionElement();
		typeElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		typeElement.setNamespace(FlowConstant.NAMESPACE);
		typeElement.setName(FlowConstant.CONFIG);

		ExtensionElement typeItem = new ExtensionElement();
		typeItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
		typeItem.setNamespace(FlowConstant.NAMESPACE);
		typeItem.setName(FlowConstant.TYPE);
		ExtensionAttribute name = new ExtensionAttribute(FlowConstant.VALUE);
		name.setValue(flowNode.getNodeType().name());
		typeItem.addAttribute(name);
		typeElement.addChildElement(typeItem);

		return typeElement;
	}

	/**
	 * 为支持路由的节点（UserTask / ScriptTask 等）构造 X-ROUTE 扩展元素。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-ROUTE type="Exclusive">
	 *   <xflow:ROUTE name="同意" language="juel">${approveResult == 'agree'}</xflow:ROUTE>
	 *   <xflow:ROUTE name="驳回" language="juel">${approveResult == 'reject'}</xflow:ROUTE>
	 * </xflow:X-ROUTE>
	 * }</pre>
	 * <p>路由类型固定为 {@code Exclusive}（排他路由），每个子 ROUTE 元素包含 name（路由名称）和可选的 JUEL 表达式。</p>
	 *
	 * @param flowRoute 实现 IFlowRoute 接口的节点（如 XFlowUserTask）
	 * @return 包含路由配置的 ExtensionElement
	 */
	private ExtensionElement buildRouteElement(IFlowRoute flowRoute) {
		ExtensionElement routeElement = new ExtensionElement();
		routeElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		routeElement.setNamespace(FlowConstant.NAMESPACE);
		routeElement.setName(FlowConstant.ROUTE);
		ExtensionAttribute routeType = new ExtensionAttribute(FlowConstant.TYPE);
		routeType.setValue(FlowRouteType.Exclusive.toString());
		routeElement.addAttribute(routeType);

		// 为X-ROUTE 扩展元素添加子元素: ROUTE name="Approved"
		for (FlowRoute route : flowRoute.getRoutes()) {
			ExtensionElement routeItem = new ExtensionElement();
			routeItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			routeItem.setNamespace(FlowConstant.NAMESPACE);
			routeItem.setName(FlowConstant.ROUTE);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(route.getName());
			routeItem.addAttribute(name);
			ExtensionAttribute language = new ExtensionAttribute(FlowConstant.LANGUAGE);
			language.setValue(FlowConstant.JUEL);
			routeItem.addAttribute(language);
			if (!FlameUtils.isBlank(route.getExpression())) {
				routeItem.setElementText(route.getExpression());
			}
			routeElement.addChildElement(routeItem);
		}

		return routeElement;
	}

	/**
	 * 为支持事件的节点（UserTask 等）构造 X-EVENT 扩展元素。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-EVENT>
	 *   <xflow:EVENT name="onSubmit" language="juel">${triggerSubmit == true}</xflow:EVENT>
	 * </xflow:X-EVENT>
	 * }</pre>
	 *
	 * @param flowEvent 实现 IFlowEvent 接口的节点
	 * @return 包含事件配置的 ExtensionElement
	 */
	private ExtensionElement buildEventElement(IFlowEvent flowEvent) {
		ExtensionElement eventElement = new ExtensionElement();
		eventElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		eventElement.setNamespace(FlowConstant.NAMESPACE);
		eventElement.setName(FlowConstant.EVENT);

		// 为X-ROUTE 扩展元素添加子元素: ROUTE name="Approved"
		for (FlowEvent event : flowEvent.getEvents()) {
			ExtensionElement eventItem = new ExtensionElement();
			eventItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			eventItem.setNamespace(FlowConstant.NAMESPACE);
			eventItem.setName(FlowConstant.EVENT);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(event.getName());
			eventItem.addAttribute(name);
			ExtensionAttribute language = new ExtensionAttribute(FlowConstant.LANGUAGE);
			language.setValue(FlowConstant.JUEL);
			eventItem.addAttribute(language);
			if (!FlameUtils.isBlank(event.getExpression())) {
				eventItem.setElementText(event.getExpression());
			}
			eventElement.addChildElement(eventItem);
		}

		return eventElement;
	}

	/**
	 * 为支持办理人配置的节点（UserTask 等）构造 X-ACTOR 扩展元素。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-ACTOR necessity="ALL">
	 *   <xflow:USER name="jhu" display="John Huang" />
	 *   <xflow:GROUP name="managers" display="Managers" />
	 *   <xflow:ROLE name="approver" display="Approver" />
	 * </xflow:X-ACTOR>
	 * }</pre>
	 *
	 * <p>{@code necessity} 属性控制办理模式：</p>
	 * <ul>
	 *   <li>{@code ANY}：任意一个办理人完成即可推进流程</li>
	 *   <li>{@code ALL}：所有办理人都完成后才推进流程</li>
	 * </ul>
	 *
	 * <p>办理人来源支持三种类型：USER（指定用户）、GROUP（用户组）、ROLE（角色），
	 * 每种类型包含 name（标识）和 display（显示名）两个属性。</p>
	 *
	 * @param flowActor 实现 IFlowActor 接口的节点
	 * @return 包含办理人配置的 ExtensionElement
	 */
	private ExtensionElement buildActorElement(IFlowActor flowActor) {
		ExtensionElement actorElement = new ExtensionElement();
		actorElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		actorElement.setNamespace(FlowConstant.NAMESPACE);
		actorElement.setName(FlowConstant.ACTOR);
		ExtensionAttribute necessity = new ExtensionAttribute(FlowConstant.NECESSITY);
		necessity.setValue(flowActor.getNecessity());
		actorElement.addAttribute(necessity);

		// 为X-ACTOR 扩展元素添加子元素: USER name="guest"
		for (FlowUser user : flowActor.getUsers()) {
			ExtensionElement userItem = new ExtensionElement();
			userItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			userItem.setNamespace(FlowConstant.NAMESPACE);
			userItem.setName(FlowConstant.USER);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(user.getName());
			userItem.addAttribute(name);
			ExtensionAttribute display = new ExtensionAttribute(FlowConstant.DISPLAY);
			display.setValue(user.getDisplay());
			userItem.addAttribute(display);
			actorElement.addChildElement(userItem);
		}

		// 为X-ACTOR 扩展元素添加子元素: GROUP name="ResourceGroup"
		for (FlowGroup group : flowActor.getGroups()) {
			ExtensionElement groupItem = new ExtensionElement();
			groupItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			groupItem.setNamespace(FlowConstant.NAMESPACE);
			groupItem.setName(FlowConstant.GROUP);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(group.getName());
			groupItem.addAttribute(name);
			ExtensionAttribute display = new ExtensionAttribute(FlowConstant.DISPLAY);
			display.setValue(group.getDisplay());
			groupItem.addAttribute(display);
			actorElement.addChildElement(groupItem);
		}

		// 为X-ACTOR 扩展元素添加子元素: USER name="guest"
		for (FlowRole role : flowActor.getRoles()) {
			ExtensionElement roleItem = new ExtensionElement();
			roleItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			roleItem.setNamespace(FlowConstant.NAMESPACE);
			roleItem.setName(FlowConstant.ROLE);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(role.getName());
			roleItem.addAttribute(name);
			ExtensionAttribute display = new ExtensionAttribute(FlowConstant.DISPLAY);
			display.setValue(role.getDisplay());
			roleItem.addAttribute(display);
			actorElement.addChildElement(roleItem);
		}

		return actorElement;
	}

	/**
	 * 为定时器节点构造 X-TIMER 扩展元素。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-TIMER>
	 *   <xflow:TIMER year="0" month="0" day="3" hour="12" minute="0" second="0" />
	 * </xflow:X-TIMER>
	 * }</pre>
	 *
	 * <p>时间字段支持年月日时分秒六个维度，未设置或为 null 的值默认为 0。</p>
	 *
	 * @param flowTimer 实现 IFlowTimer 接口的节点
	 * @return 包含定时器配置的 ExtensionElement
	 */
	private ExtensionElement buildTimerElement(IFlowTimer flowTimer) {
		ExtensionElement timerElement = new ExtensionElement();
		timerElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		timerElement.setNamespace(FlowConstant.NAMESPACE);
		timerElement.setName(FlowConstant.TIMER);

		ExtensionElement timerItem = new ExtensionElement();
		timerItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
		timerItem.setNamespace(FlowConstant.NAMESPACE);
		timerItem.setName(FlowConstant.TIMER);
		ExtensionAttribute year = new ExtensionAttribute(FlowConstant.YEAR);
		year.setValue(String.valueOf(flowTimer.getYears() == null ? 0 : flowTimer.getYears()));
		timerItem.addAttribute(year);
		ExtensionAttribute month = new ExtensionAttribute(FlowConstant.MONTH);
		month.setValue(String.valueOf(flowTimer.getMonths() == null ? 0 : flowTimer.getMonths()));
		timerItem.addAttribute(month);
		ExtensionAttribute day = new ExtensionAttribute(FlowConstant.DAY);
		day.setValue(String.valueOf(flowTimer.getDays() == null ? 0 : flowTimer.getDays()));
		timerItem.addAttribute(day);
		ExtensionAttribute hour = new ExtensionAttribute(FlowConstant.HOUR);
		hour.setValue(String.valueOf(flowTimer.getHours() == null ? 0 : flowTimer.getHours()));
		timerItem.addAttribute(hour);
		ExtensionAttribute minute = new ExtensionAttribute(FlowConstant.MINUTE);
		minute.setValue(String.valueOf(flowTimer.getMinutes() == null ? 0 : flowTimer.getMinutes()));
		timerItem.addAttribute(minute);
		ExtensionAttribute second = new ExtensionAttribute(FlowConstant.SECOND);
		second.setValue(String.valueOf(flowTimer.getSeconds() == null ? 0 : flowTimer.getSeconds()));
		timerItem.addAttribute(second);

		timerElement.addChildElement(timerItem);

		return timerElement;
	}

	/**
	 * 为流程定义或用户任务构造 X-VARIABLE 扩展元素。
	 *
	 * <p>生成的 XML 结构：</p>
	 * <pre>{@code
	 * <xflow:X-VARIABLE>
	 *   <xflow:VARIABLE name="approveResult" type="String" display="审批结果" value="" />
	 *   <xflow:VARIABLE name="amount" type="Double" display="审批金额" value="0.0" />
	 * </xflow:X-VARIABLE>
	 * }</pre>
	 *
	 * <p>流程变量可以在流程级别（Process）或节点级别（UserTask）上定义，
	 * 每个变量包含 name / type / display / value 四个属性。</p>
	 *
	 * @param flowVariable 实现 IFlowVariable 接口的对象（XFlowDefinition 或 XFlowUserTask）
	 * @return 包含变量定义的 ExtensionElement
	 */
	private ExtensionElement buildVariableElement(IFlowVariable flowVariable) {
		ExtensionElement variableElement = new ExtensionElement();
		variableElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		variableElement.setNamespace(FlowConstant.NAMESPACE);
		variableElement.setName(FlowConstant.VARIABLE);

		// 为X-ROUTE 扩展元素添加子元素: ROUTE name="Approved"
		for (FlowVariable variable : flowVariable.getVariables()) {
			ExtensionElement variableItem = new ExtensionElement();
			variableItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
			variableItem.setNamespace(FlowConstant.NAMESPACE);
			variableItem.setName(FlowConstant.VARIABLE);
			ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
			name.setValue(variable.getName());
			variableItem.addAttribute(name);
			ExtensionAttribute type = new ExtensionAttribute(FlowConstant.TYPE);
			type.setValue(variable.getType());
			variableItem.addAttribute(type);
			ExtensionAttribute display = new ExtensionAttribute(FlowConstant.DISPLAY);
			display.setValue(variable.getDisplay());
			variableItem.addAttribute(display);
			ExtensionAttribute value = new ExtensionAttribute(FlowConstant.VALUE);
			value.setValue(variable.getValue() == null ? "" : variable.getValue().toString());
			variableItem.addAttribute(value);
			variableElement.addChildElement(variableItem);
		}

		return variableElement;
	}

	// ============================================================
	//   XFlow 节点 → BPMN 模型转换（私有方法）
	// ============================================================

	/**
	 * 将 XFlowUserTask 转换为 BPMN UserTask 模型元素。
	 *
	 * <p>转换过程：</p>
	 * <ol>
	 *   <li>创建 UserTask 并设置 ID（nodeId）/ Name</li>
	 *   <li>设置表单 Key（formKey）：若节点配置了 taskForm 则使用配置值，否则使用默认表单 {@code flowWorkTaskReview}</li>
	 *   <li>依次添加扩展元素：X-TYPE → X-VARIABLE → X-ROUTE → X-EVENT → X-ACTOR</li>
	 * </ol>
	 *
	 * @param node XFlow 用户任务节点
	 * @return Flowable UserTask 模型元素
	 */
	private UserTask convertTask2Bpmn(XFlowUserTask node) {
		UserTask task = new UserTask();
		task.setId(node.getNodeId());
		task.setName(node.getName());
		if (FlameUtils.isBlank(node.getTaskForm())) {
			task.setFormKey(DEFAULT_TASKFORM);
		} else {
			task.setFormKey(node.getTaskForm());
		}
		task.addExtensionElement(this.buildTypeElement(node));
		// 为UserTask添加 X-VARIABLE 扩展元素 */
		task.addExtensionElement(this.buildVariableElement(node));
		// 为UserTask添加 X-ROUTE 扩展元素 */
		task.addExtensionElement(this.buildRouteElement(node));
		// 为UserTask添加 X-EVENT 扩展元素 */
		task.addExtensionElement(this.buildEventElement(node));
		// 为UserTask添加 X-ACTOR 扩展元素 */
		task.addExtensionElement(this.buildActorElement(node));

		return task;
	}

	/**
	 * 将 XFlowScriptTask 转换为 BPMN ScriptTask 模型元素。
	 *
	 * <p>脚本任务允许在流程中执行内联脚本逻辑（Groovy 等），转换要点：</p>
	 * <ul>
	 *   <li>scriptFormat：取自节点的 language 属性（如 groovy）</li>
	 *   <li>script：节点的 expression 表达式内容</li>
	 *   <li>resultVariable：固定为 {@code FLOW_RETURN}，用于在流程上下文中传递脚本返回值</li>
	 *   <li>扩展元素：X-TYPE + X-ROUTE</li>
	 * </ul>
	 *
	 * @param node XFlow 脚本任务节点
	 * @return Flowable ScriptTask 模型元素
	 */
	private ScriptTask convertTask2Bpmn(XFlowScriptTask node) {
		ScriptTask task = new ScriptTask();
		task.setId(node.getNodeId());
		task.setName(node.getName());
		task.setScriptFormat(node.getLanguage().name());
		task.setScript(node.getExpression());
		task.setResultVariable(FlowContext.FLOW_RETURN);
		task.addExtensionElement(this.buildTypeElement(node));
		// 为UserTask添加 X-ROUTE 扩展元素 */
		task.addExtensionElement(this.buildRouteElement(node));

		return task;
	}

	/**
	 * 将 XFlowServiceTask 转换为 BPMN ServiceTask 模型元素。
	 *
	 * <p>服务任务用于调用外部 Java 服务或 HTTP 接口，通过 implementation 和 implementationType 指定调用方式。
	 * 与 UserTask 不同，ServiceTask 不需要路由和办理人配置，仅携带 X-TYPE 扩展元素。</p>
	 *
	 * @param node XFlow 服务任务节点
	 * @return Flowable ServiceTask 模型元素
	 */
	private ServiceTask convertTask2Bpmn(XFlowServiceTask node) {
		ServiceTask task = new ServiceTask();
		task.setId(node.getNodeId());
		task.setName(node.getName());
		task.setImplementation(node.getImplementation());
		task.setImplementationType(node.getImplementedType());
		task.addExtensionElement(this.buildTypeElement(node));

		return task;
	}

	/**
	 * 将 XFlowEvent 转换为 BPMN Event 模型元素。
	 *
	 * <p>根据 nodeType 映射到不同的 Flowable Event 类型：</p>
	 * <ul>
	 *   <li><b>startEvent：</b>创建 StartEvent，配置 NoneStartEvent 行为（空开始事件，不等待任何信号）</li>
	 *   <li><b>endEvent：</b>创建 EndEvent，流程到达时正常终止</li>
	 *   <li><b>groundEvent：</b>创建 EndEvent（终止结束事件），流程到达时强制终止当前分支</li>
	 * </ul>
	 * <p>所有事件节点均添加 X-TYPE 扩展元素。StartEvent 的 ActivityBehavior
	 * 由全局注册的 {@code XFlowActivityBehaviorFactory} 在执行阶段自动创建，部署时无需显式设置。</p>
	 *
	 * @param node XFlow 事件节点
	 * @return Flowable Event 模型元素（StartEvent 或 EndEvent）
	 */
	private Event convertEvent2Bpmn(XFlowEvent node) {
		Event event = null;
		switch (node.getNodeType()) {
		case startEvent:
			event = new StartEvent();
			event.setId(node.getNodeId());
			event.setName(node.getName());
			event.addExtensionElement(this.buildTypeElement(node));
			break;
		case endEvent:
			event = new EndEvent();
			event.setId(node.getNodeId());
			event.setName(node.getName());
			event.addExtensionElement(this.buildTypeElement(node));
			break;
		case groundEvent:
			event = new EndEvent();
			event.setId(node.getNodeId());
			event.setName(node.getName());
			event.addExtensionElement(this.buildTypeElement(node));
			break;
		default:
			break;
		}
		if (event != null) {
			List<FlowableListener> listeners = event.getExecutionListeners();
			if (listeners == null) {
				listeners = new ArrayList<>();

			}
		}

		return event;
	}

	/**
	 * 将 XFlowTimer 转换为 BPMN ReceiveTask 模型元素。
	 *
	 * <p>XFlow 使用 ReceiveTask 承载定时器节点，通过 X-TIMER 扩展元素携带时间配置（年月日时分秒），
	 * 运行时由引擎解析并执行定时等待逻辑。</p>
	 *
	 * @param node XFlow 定时器节点
	 * @return Flowable ReceiveTask 模型元素
	 */
	private ReceiveTask convertTimer2Bpmn(XFlowTimer node) {
		ReceiveTask task = new ReceiveTask();
		task.setId(node.getNodeId());
		task.setName(node.getName());
		task.addExtensionElement(this.buildTypeElement(node));
		// 为ReceiveTask添加 X-TIMER 扩展元素*/
		task.addExtensionElement(this.buildTimerElement(node));

		return task;
	}

	/**
	 * 将 XFlowGateway 转换为 BPMN Gateway 模型元素。
	 *
	 * <p>映射关系：</p>
	 * <table border="1">
	 *   <tr><th>XFlow nodeType</th><th>Flowable Gateway 类型</th><th>说明</th></tr>
	 *   <tr><td>parallelGateway</td><td>ParallelGateway</td><td>并行网关 —— 所有分支同时执行</td></tr>
	 *   <tr><td>exclusiveGateway</td><td>ExclusiveGateway</td><td>排他网关 —— 仅选择一个分支</td></tr>
	 *   <tr><td>inclusiveGateway</td><td>InclusiveGateway</td><td>包容网关 —— 选择一个或多个分支</td></tr>
	 *   <tr><td>complexGateway</td><td>ComplexGateway</td><td>复杂网关 —— 自定义分支决策</td></tr>
	 *   <tr><td>andGateway</td><td>ParallelGateway</td><td>AND 网关 —— 与并行网关行为相同</td></tr>
	 *   <tr><td>orGateway</td><td>ExclusiveGateway</td><td>OR 网关 —— 与排他网关行为相同（XFlow 自定义语义）</td></tr>
	 * </table>
	 * <p>所有网关节点均添加 X-TYPE 扩展元素，并初始化 ExecutionListener 列表。</p>
	 *
	 * @param node XFlow 网关节点
	 * @return Flowable Gateway 模型元素，未知类型返回 null
	 */
	private Gateway convertGateway2Bpmn(XFlowGateway node) {
		Gateway gateway = null;
		switch (node.getNodeType()) {
		case parallelGateway:
			gateway = new ParallelGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		case exclusiveGateway:
			gateway = new ExclusiveGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		case inclusiveGateway:
			gateway = new InclusiveGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		case complexGateway:
			gateway = new ComplexGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		case andGateway:
			gateway = new ParallelGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		case orGateway:
			gateway = new ExclusiveGateway();
			gateway.setId(node.getNodeId());
			gateway.setName(node.getName());
			gateway.addExtensionElement(this.buildTypeElement(node));
			break;
		default:
			break;
		}
		if (gateway != null) {
			List<FlowableListener> listeners = gateway.getExecutionListeners();
			if (listeners == null) {
				listeners = new ArrayList<>();
			}
		}

		return gateway;
	}

	/**
	 * 将 XFlowThing 转换为 BPMN ServiceTask 模型元素。
	 *
	 * <p>XFlowThing 是 XFlow 中表示"实体操作"的特殊节点类型，在 BPMN 层映射为 ServiceTask：</p>
	 * <ul>
	 *   <li>type 设为 {@code THING} 用于运行时识别</li>
	 *   <li>implementationType 固定为 {@code CLASS}（Java 类委托）</li>
	 *   <li>implementation 固定为 XFlowThingTaskDelegate 的全限定类名</li>
	 *   <li>添加 X-TYPE 扩展元素</li>
	 * </ul>
	 * <p>目前仅支持 {@code thingTask} 类型，其他类型返回 null。</p>
	 *
	 * @param node XFlow 实体节点
	 * @return Flowable ServiceTask 模型元素，不支持的节点类型返回 null
	 */
	private Task convertThing2Bpmn(XFlowThing node) {
		ServiceTask thingTask = null;
		switch (node.getNodeType()) {
		case thingTask:
			thingTask = new ServiceTask();
			thingTask.setId(node.getNodeId());
			thingTask.setName(node.getName());
			thingTask.setType(FlowConstant.THING);
			thingTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
			thingTask.setImplementation(XFlowThingTaskDelegate.class.getCanonicalName());
			thingTask.addExtensionElement(this.buildTypeElement(node));
			return thingTask;
		default:
			return null;
		}
	}

	/**
	 * 将 XFlowEdge 转换为 BPMN SequenceFlow 模型元素。
	 *
	 * <p>连线（边）表示流程中两个节点之间的有向连接：</p>
	 * <ul>
	 *   <li>ID：edgeId</li>
	 *   <li>sourceRef：源节点 ID（边的起点）</li>
	 *   <li>targetRef：目标节点 ID（边的终点）</li>
	 * </ul>
	 * <p>连线上携带 X-ROUTE 扩展元素，其子 ROUTE 元素的 name 属性
	 * 对应源节点中定义的审批路由名称，运行时引擎根据完成工作项时选择的路由匹配对应的连线来推进流程。</p>
	 *
	 * @param edge XFlow 边（连线）对象
	 * @return Flowable SequenceFlow 模型元素
	 */
	private SequenceFlow convertEdge2Bpmn(XFlowEdge edge) {
		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId(edge.getEdgeId());
		sequenceFlow.setSourceRef(edge.getSource());
		sequenceFlow.setTargetRef(edge.getTarget());

		ExtensionElement routeElement = new ExtensionElement();
		routeElement.setNamespacePrefix(FlowConstant.NS_PREFIX);
		routeElement.setNamespace(FlowConstant.NAMESPACE);
		routeElement.setName(FlowConstant.ROUTE);

		String routes = edge.getRoutes();
		if (FlameUtils.isNotBlank(routes)) {
			// 为X-ROUTE 扩展元素添加子元素: ROUTE name="Approved"
			for (String route : routes.split(",")) {
				ExtensionElement routeItem = new ExtensionElement();
				routeItem.setNamespacePrefix(FlowConstant.NS_PREFIX);
				routeItem.setNamespace(FlowConstant.NAMESPACE);
				routeItem.setName(FlowConstant.ROUTE);
				ExtensionAttribute name = new ExtensionAttribute(FlowConstant.NAME);
				name.setValue(route);
				routeItem.addAttribute(name);
				routeElement.addChildElement(routeItem);
			}
		}
		sequenceFlow.addExtensionElement(routeElement);

		return sequenceFlow;
	}

	// ============================================================
	//   流程节点 CRUD（从 JSON 创建/更新）
	// ============================================================

	/**
	 * 根据 JSON 数据创建或更新 XFlowEvent 事件节点（startEvent / endEvent / groundEvent）。
	 *
	 * <p>处理逻辑：</p>
	 * <ol>
	 *   <li>从 JSON 中提取 nodeId / xtype / label / position</li>
	 *   <li>按 nodeId 在数据库中查找已有节点：存在则更新，不存在则新建</li>
	 *   <li>设置名称、类型、坐标信息并持久化</li>
	 * </ol>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据，包含 id / xtype / data / label / position 等字段
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowEvent 实例
	 */
	public XFlowEvent createXFlowEvent(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		JsonNode data = jsonNode.get(FlowConstant.DATA);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(data.toPrettyString());
		}
		XFlowEvent flowEvent = null;
		List<XFlowEvent> thingList = XFlowRepositoryHelper.repository().findXFlowEventById(definition, nodeId);
		if (thingList.isEmpty()) {
			flowEvent = XFlowEvent.newInstance(nodeId, definition);
		} else {
			flowEvent = thingList.get(0);
		}
		flowEvent.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowEvent.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowEvent.setAxisX(position.get(FlowConstant.X).asLong());
			flowEvent.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		flowEvent = PersistenceHelper.service().save(flowEvent);
		return flowEvent;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowUserTask 用户任务节点。
	 *
	 * <p>这是最复杂的节点创建方法，因为 UserTask 包含丰富的业务配置：</p>
	 * <ul>
	 *   <li><b>基础属性：</b>名称、类型、坐标、说明（instructions）</li>
	 *   <li><b>必要性 necessity：</b>ANY（任意办理人完成即可推进）/ ALL（所有办理人完成才推进）</li>
	 *   <li><b>办理人 participants：</b>USER（用户）/ GROUP（组）/ ROLE（角色），空数组表示未编辑则保留原数据</li>
	 *   <li><b>路由与事件 route_events：</b>ROUTE（审批通过/驳回等方向）/ EVENT（节点事件），空数组表示未编辑则保留原数据</li>
	 *   <li><b>流程变量 variables：</b>节点级别的流程变量定义</li>
	 * </ul>
	 *
	 * <p>若 JSON 中某数组字段为空（前端未编辑），则不清除已有数据，避免保存时丢失历史配置。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowUserTask 实例
	 */
	public XFlowUserTask createXFlowUserTask(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowUserTask flowTask = null;
		List<XFlowUserTask> userTasks = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, nodeId);
		if (userTasks.isEmpty()) {
			flowTask = XFlowUserTask.newInstance(nodeId, definition);
		} else {
			flowTask = userTasks.get(0);
		}
		flowTask.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowTask.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.INSTRUCTIONS))
			flowTask.setInstructions(jsonNode.get(FlowConstant.INSTRUCTIONS).asText());
		if (jsonNode.has(FlowConstant.NECESSITY))
			flowTask.setNecessity(jsonNode.get(FlowConstant.NECESSITY).asText());
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowTask.setAxisX(position.get(FlowConstant.X).asLong());
			flowTask.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		if (jsonNode.has(FlowConstant.PARTICIPANTS)) {
			ArrayNode arrayNode = (ArrayNode) jsonNode.get(FlowConstant.PARTICIPANTS);
			/**
			 * 如果arrayNode是空, 说明在UI上没有去编辑过Participant, 因此就不需要去处理
			 */
			if (!arrayNode.isEmpty()) {
				flowTask.getUsers().clear();
				flowTask.getRoles().clear();
				flowTask.getGroups().clear();
				Iterator<JsonNode> iterator = arrayNode.elements();
				while (iterator.hasNext()) {
					JsonNode node = iterator.next();
					if (FlowConstant.USER.equals(node.get(FlowConstant.TYPE).asText())) {
						flowTask.addUser(FlowUser.newInstance(node));
					} else if (FlowConstant.GROUP.equals(node.get(FlowConstant.TYPE).asText())) {
						flowTask.addGroup(FlowGroup.newInstance(node));
					} else if (FlowConstant.ROLE.equals(node.get(FlowConstant.TYPE).asText())) {
						flowTask.addRole(FlowRole.newInstance(node));
					}
				}
			}
		}
		if (jsonNode.has(FlowConstant.ROUTE_EVENTS)) {
			ArrayNode arrayNode = (ArrayNode) jsonNode.get(FlowConstant.ROUTE_EVENTS);
			/**
			 * 如果arrayNode是空, 说明在UI上没有去编辑Route、Event, 因此就不需要去处理
			 */
			if (!arrayNode.isEmpty()) {
				flowTask.getRoutes().clear();
				flowTask.getEvents().clear();
				Iterator<JsonNode> iterator = arrayNode.elements();
				while (iterator.hasNext()) {
					JsonNode node = iterator.next();
					if (!node.has(FlowConstant.TYPE))
						continue;

					String type = node.get(FlowConstant.TYPE).asText();
					if (FlowConstant.EVENT.equals(type)) {
						flowTask.addEvent(FlowEvent.newInstance(node));
					} else if (FlowConstant.ROUTE.equals(type)) {
						flowTask.addRoute(FlowRoute.newInstance(node));
					}
				}
			}
		}
		if (jsonNode.has(FlowConstant.VARIABLES)) {
			ArrayNode arrayNode = (ArrayNode) jsonNode.get(FlowConstant.VARIABLES);
			Iterator<JsonNode> iterator = arrayNode.elements();
			if (iterator.hasNext())
				flowTask.getVariables().clear();
			while (iterator.hasNext()) {
				JsonNode node = iterator.next();
				flowTask.addVariable(FlowVariable.newInstance(node));
			}
		}
		flowTask = PersistenceHelper.service().save(flowTask);
		return flowTask;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowScriptTask 脚本任务节点。
	 *
	 * <p>脚本任务包含：名称、类型、坐标、脚本语言（固定为 groovy）、脚本表达式（expression）、
	 * 说明（instructions）以及路由配置（routes）。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowScriptTask 实例
	 */
	public XFlowScriptTask createXFlowScriptTask(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowScriptTask flowTask = null;
		List<XFlowScriptTask> scriptTasks = XFlowRepositoryHelper.repository().findXFlowScriptTaskById(definition, nodeId);
		if (scriptTasks.isEmpty()) {
			flowTask = XFlowScriptTask.newInstance(nodeId, definition);
		} else {
			flowTask = scriptTasks.get(0);
		}
		flowTask.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowTask.setNodeType(FlowNodeType.valueOf(xtype));
		flowTask.setLanguage(FlowLanguage.groovy);
		if (jsonNode.has(FlowConstant.EXPRESSION))
			flowTask.setExpression(jsonNode.get(FlowConstant.EXPRESSION).asText());
		if (jsonNode.has(FlowConstant.INSTRUCTIONS))
			flowTask.setInstructions(jsonNode.get(FlowConstant.INSTRUCTIONS).asText());
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowTask.setAxisX(position.get(FlowConstant.X).asLong());
			flowTask.setAxisY(position.get(FlowConstant.Y).asLong());
		}

		if (jsonNode.has(FlowConstant.ROUTE_EVENTS)) {
			ArrayNode arrayNode = (ArrayNode) jsonNode.get(FlowConstant.ROUTE_EVENTS);
			/**
			 * 如果arrayNode是空, 说明在UI上没有去编辑Route、Event, 因此就不需要去处理
			 */
			if (!arrayNode.isEmpty()) {
				flowTask.getRoutes().clear();
				Iterator<JsonNode> iterator = arrayNode.elements();
				while (iterator.hasNext()) {
					JsonNode node = iterator.next();
					if (!node.has(FlowConstant.TYPE))
						continue;

					String type = node.get(FlowConstant.TYPE).asText();
					if (FlowConstant.ROUTE.equals(type)) {
						flowTask.addRoute(FlowRoute.newInstance(node));
					}
				}
			}
		}
		flowTask = PersistenceHelper.service().save(flowTask);
		return flowTask;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowServiceTask 服务任务节点。
	 *
	 * <p>服务任务用于调用外部 Java 服务或 HTTP 接口，包含：名称、类型、坐标、说明、
	 * implementation（实现类/URL）以及 implementedType（实现方式：class / delegateExpression 等）。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowServiceTask 实例
	 */
	public XFlowServiceTask createXFlowServiceTask(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowServiceTask flowTask = null;
		List<XFlowServiceTask> serviceTasks = XFlowRepositoryHelper.repository().findXFlowServiceTaskById(definition, nodeId);
		if (serviceTasks.isEmpty()) {
			flowTask = XFlowServiceTask.newInstance(nodeId, definition);
		} else {
			flowTask = serviceTasks.get(0);
		}
		flowTask.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowTask.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.INSTRUCTIONS))
			flowTask.setInstructions(jsonNode.get(FlowConstant.INSTRUCTIONS).asText());
		if (jsonNode.has(FlowConstant.IMPLEMENTATION))
			flowTask.setImplementation(jsonNode.get(FlowConstant.IMPLEMENTATION).asText());
		if (jsonNode.has(FlowConstant.IMPLEMENTED_TYPE))
			flowTask.setImplementedType(jsonNode.get(FlowConstant.IMPLEMENTED_TYPE).asText());
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowTask.setAxisX(position.get(FlowConstant.X).asLong());
			flowTask.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		flowTask = PersistenceHelper.service().save(flowTask);
		return flowTask;
	}

	/**
	 * 根据 JSON 数据创建或更新手动任务节点（实际存储为 XFlowUserTask）。
	 *
	 * <p>手动任务是用户任务的简化版本，仅包含名称、类型、坐标等基本信息，
	 * 不包含办理人/路由/事件/变量等复杂配置。在 BPMN 模型生成时与普通 UserTask 走相同的转换逻辑。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowUserTask 实例
	 */
	public XFlowUserTask createXFlowManualTask(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowUserTask flowTask = null;
		List<XFlowUserTask> taskList = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, nodeId);
		if (taskList.isEmpty()) {
			flowTask = XFlowUserTask.newInstance(nodeId, definition);
		} else {
			flowTask = taskList.get(0);
		}
		flowTask.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowTask.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowTask.setAxisX(position.get(FlowConstant.X).asLong());
			flowTask.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		flowTask = PersistenceHelper.service().save(flowTask);

		return flowTask;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowThing 实体节点。
	 *
	 * <p>XFlowThing 表示流程中的"实体操作"节点（如创建/更新某个业务对象），
	 * 目前仅包含名称、类型和坐标信息，运行时由 XFlowThingTaskDelegate 执行具体逻辑。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowThing 实例
	 */
	public XFlowThing createXFlowThing(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowThing flowThing = null;
		List<XFlowThing> thingList = XFlowRepositoryHelper.repository().findXFlowThingById(definition, nodeId);
		if (thingList.isEmpty()) {
			flowThing = XFlowThing.newInstance(nodeId, definition);
		} else {
			flowThing = thingList.get(0);
		}
		flowThing.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowThing.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowThing.setAxisX(position.get(FlowConstant.X).asLong());
			flowThing.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		flowThing = PersistenceHelper.service().save(flowThing);
		return flowThing;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowTimer 定时器节点。
	 *
	 * <p>定时器节点用于在流程中引入等待/延迟逻辑，支持年月日时分秒六个时间维度。
	 * 节点配置说明（instructions）可用于向用户解释定时器的用途。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据，包含 years / months / days / hours / minutes / seconds 等时间字段
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowTimer 实例
	 */
	public XFlowTimer createXFlowTimer(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowTimer flowTimer = null;
		List<XFlowTimer> timerList = XFlowRepositoryHelper.repository().findXFlowTimerById(definition, nodeId);
		if (timerList.isEmpty()) {
			flowTimer = XFlowTimer.newInstance(nodeId, definition);
		} else {
			flowTimer = timerList.get(0);
		}
		flowTimer.setName(jsonNode.get(FlowConstant.LABEL).asText());
		flowTimer.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.INSTRUCTIONS))
			flowTimer.setInstructions(jsonNode.get(FlowConstant.INSTRUCTIONS).asText());
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			flowTimer.setAxisX(position.get(FlowConstant.X).asLong());
			flowTimer.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		if (jsonNode.has(FlowConstant.YEARS))
			flowTimer.setYears(jsonNode.get(FlowConstant.YEARS).asInt());
		if (jsonNode.has(FlowConstant.MONTHS))
			flowTimer.setMonths(jsonNode.get(FlowConstant.MONTHS).asInt());
		if (jsonNode.has(FlowConstant.DAYS))
			flowTimer.setDays(jsonNode.get(FlowConstant.DAYS).asInt());
		if (jsonNode.has(FlowConstant.HOURS))
			flowTimer.setHours(jsonNode.get(FlowConstant.HOURS).asInt());
		if (jsonNode.has(FlowConstant.MINUTES))
			flowTimer.setMinutes(jsonNode.get(FlowConstant.MINUTES).asInt());
		if (jsonNode.has(FlowConstant.SECONDS))
			flowTimer.setSeconds(jsonNode.get(FlowConstant.SECONDS).asInt());
		flowTimer = PersistenceHelper.service().save(flowTimer);
		return flowTimer;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowGateway 网关节点。
	 *
	 * <p>网关类型由 xtype 字段决定，支持：parallelGateway / exclusiveGateway / inclusiveGateway /
	 * complexGateway / andGateway / orGateway。网关仅包含名称、类型和坐标信息，
	 * 分支决策逻辑由连线的 X-ROUTE 扩展元素承载。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据
	 * @param definition 所属的 XFlow 流程定义
	 * @return 创建或更新后的 XFlowGateway 实例
	 */
	public XFlowGateway createXFlowGateway(JsonNode jsonNode, XFlowDefinition definition) {
		String nodeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		XFlowGateway gateway = null;
		List<XFlowGateway> taskList = XFlowRepositoryHelper.repository().findXFlowGatewayById(definition, nodeId);
		if (taskList.isEmpty()) {
			gateway = XFlowGateway.newInstance(nodeId, definition);
		} else {
			gateway = taskList.get(0);
		}
		gateway.setName(jsonNode.get(FlowConstant.LABEL).asText());
		gateway.setNodeType(FlowNodeType.valueOf(xtype));
		if (jsonNode.has(FlowConstant.POSITION)) {
			JsonNode position = jsonNode.get(FlowConstant.POSITION);
			gateway.setAxisX(position.get(FlowConstant.X).asLong());
			gateway.setAxisY(position.get(FlowConstant.Y).asLong());
		}
		gateway = PersistenceHelper.service().save(gateway);
		return gateway;
	}

	/**
	 * 根据 JSON 数据创建或更新 XFlowEdge 连线（边）。
	 *
	 * <p>连线表示流程中两个节点之间的有向连接，关键属性：</p>
	 * <ul>
	 *   <li><b>source / target：</b>源节点 ID 和目标节点 ID，若两者相同（自环）则返回 null</li>
	 *   <li><b>routes：</b>该连线对应的路由名称（如 "同意,驳回"），若 JSON 中未提供则默认为 "?"（通配路由）</li>
	 *   <li><b>srcType / tgtType：</b>根据 nodeMap 自动计算源节点和目标节点的 FlowNodeType</li>
	 * </ul>
	 *
	 * <p>查找策略：先按 edgeId 查找已有边，若不存在则按 source+target 组合查找（避免重复创建），
	 * 都找不到则新建。</p>
	 *
	 * @param jsonNode   前端画布传递的 JSON 数据，包含 id / source / target / routes 等字段
	 * @param definition 所属的 XFlow 流程定义
	 * @param nodeMap    当前流程定义中所有节点的映射（ID → XFlowNode），用于计算 srcType / tgtType
	 * @return 创建或更新后的 XFlowEdge 实例，source 等于 target 时返回 null
	 */
	public XFlowEdge createXFlowEdge(JsonNode jsonNode, XFlowDefinition definition, Map<String, XFlowNode> nodeMap) {
		String edgeId = jsonNode.get(FlowConstant.ID).asText();
		String xtype = jsonNode.get(FlowConstant.XTYPE).asText();
		String source = jsonNode.get(FlowConstant.SOURCE).asText();
		String target = jsonNode.get(FlowConstant.TARGET).asText();
		if (source.equals(target))
			return null;

		String routes = jsonNode.has(FlowConstant.ROUTES) ? jsonNode.get(FlowConstant.ROUTES).asText() : "?";
		XFlowEdge flowEdge = null;
		List<XFlowEdge> edgeList = XFlowRepositoryHelper.repository().findXFlowEdgeById(definition, edgeId);
		if (edgeList.isEmpty()) {
			edgeList = XFlowRepositoryHelper.repository().findXFlowEdgeById(source, target);
		}
		if (edgeList.isEmpty()) {
			flowEdge = XFlowEdge.newInstance(edgeId, definition);
		} else {
			flowEdge = edgeList.get(0);
		}
		flowEdge.setNodeType(FlowNodeType.valueOf(xtype));
		flowEdge.setRoutes(FlameUtils.isBlank(routes) ? "?" : routes);
		XFlowNode srcNode = nodeMap.get(source);
		flowEdge.setSource(source);
		flowEdge.setSrcType(srcNode.getNodeType());
		XFlowNode tgtNode = nodeMap.get(target);
		flowEdge.setTarget(target);
		flowEdge.setTgtType(tgtNode.getNodeType());
		flowEdge = PersistenceHelper.service().save(flowEdge);

		return flowEdge;
	}

	// ============================================================
	//   从 BPMN 模型反解析 XFlow 业务对象
	// ============================================================

	/**
	 * 从 BPMN UserTask 扩展元素中解析流程路由列表（FlowRoute）。
	 *
	 * <p>遍历 UserTask 的 X-ROUTE 扩展元素，提取每一个 ROUTE 子元素的 name 属性和文本内容（JUEL 表达式），
	 * 封装为 FlowRoute 列表返回。这些路由信息用于运行时审批页面的操作按钮渲染和流程方向决策。</p>
	 *
	 * @param userTask Flowable BPMN UserTask 模型元素
	 * @return FlowRoute 列表（name + expression），若无路由配置则返回空列表
	 */
	public List<FlowRoute> getFlowRoute(UserTask userTask) {
		List<FlowRoute> result = new ArrayList<>();
		for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.ROUTE)) {
			List<ExtensionElement> routeList = element.getChildElements().get(FlowConstant.ROUTE);
			if (routeList == null || routeList.isEmpty())
				return result;

			for (ExtensionElement child : routeList) {
				FlowRoute route = new FlowRoute();
				route.setName(child.getAttributeValue(null, FlowConstant.NAME));
				route.setExpression(child.getElementText());
				result.add(route);
			}
		}

		return result;
	}

	/**
	 * 从 BPMN UserTask 扩展元素中解析流程事件列表（FlowEvent）。
	 *
	 * <p>遍历 UserTask 的 X-EVENT 扩展元素，提取每一个 EVENT 子元素的 name 属性和文本内容（JUEL 表达式），
	 * 封装为 FlowEvent 列表返回。与 FlowRoute 的解析逻辑类似，但用于事件触发场景。</p>
	 *
	 * @param userTask Flowable BPMN UserTask 模型元素
	 * @return FlowEvent 列表（name + expression），若无事件配置则返回空列表
	 */
	public List<FlowEvent> getFlowEvent(UserTask userTask) {
		List<FlowEvent> result = new ArrayList<>();
		for (ExtensionElement element : userTask.getExtensionElements().get(FlowConstant.EVENT)) {
			List<ExtensionElement> eventList = element.getChildElements().get(FlowConstant.EVENT);
			if (eventList == null || eventList.isEmpty())
				return result;

			for (ExtensionElement child : eventList) {
				FlowEvent event = new FlowEvent();
				event.setName(child.getAttributeValue(null, FlowConstant.NAME));
				event.setExpression(child.getElementText());
				result.add(event);
			}
		}

		return result;
	}

	// ============================================================
	//   流程节点查询与拓扑分析
	// ============================================================

	/**
	 * 根据 nodeId 查找 XFlowDefinition 中任意类型的流程节点。
	 *
	 * <p>按优先级依次在各节点表中查找：</p>
	 * <ol>
	 *   <li>XFlowEvent（事件）</li>
	 *   <li>XFlowUserTask（用户任务）</li>
	 *   <li>XFlowScriptTask（脚本任务）</li>
	 *   <li>XFlowServiceTask（服务任务）</li>
	 *   <li>XFlowGateway（网关）</li>
	 *   <li>XFlowTimer（定时器）</li>
	 *   <li>XFlowThing（实体节点）</li>
	 * </ol>
	 * <p>找到第一个匹配即返回，若所有类型都不匹配则返回 null。</p>
	 *
	 * @param definition 流程定义
	 * @param nodeId     节点 ID（BPMN activityId 或 XFlowNode nodeId）
	 * @return 匹配的 XFlowNode 子类实例，找不到则返回 null
	 */
	public XFlowNode findXFlowNodeById(XFlowDefinition definition, String nodeId) {
		List<XFlowEvent> events = XFlowRepositoryHelper.repository().findXFlowEventById(definition, nodeId);
		if (!events.isEmpty())
			return events.get(0);
		List<XFlowUserTask> userTasks = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, nodeId);
		if (!userTasks.isEmpty())
			return userTasks.get(0);
		List<XFlowScriptTask> scriptTasks = XFlowRepositoryHelper.repository().findXFlowScriptTaskById(definition, nodeId);
		if (!scriptTasks.isEmpty())
			return scriptTasks.get(0);
		List<XFlowServiceTask> serviceTasks = XFlowRepositoryHelper.repository().findXFlowServiceTaskById(definition, nodeId);
		if (!serviceTasks.isEmpty())
			return serviceTasks.get(0);
		List<XFlowGateway> gateways = XFlowRepositoryHelper.repository().findXFlowGatewayById(definition, nodeId);
		if (!gateways.isEmpty())
			return gateways.get(0);
		List<XFlowTimer> timers = XFlowRepositoryHelper.repository().findXFlowTimerById(definition, nodeId);
		if (!timers.isEmpty())
			return timers.get(0);
		List<XFlowThing> things = XFlowRepositoryHelper.repository().findXFlowThingById(definition, nodeId);
		if (!things.isEmpty())
			return things.get(0);

		return null;
	}

	/**
	 * 计算流程定义中所有 OrGateway 与其前置普通节点（非 Start/And/Or 节点）的映射关系。
	 *
	 * <p>除了 And 节点和阈值节点，其他所有节点都具有 OR 节点的属性。当流程流经非 AND 网关节点时，
	 * 需要找出从"分支节点到当前节点"路径上所有正在运行的节点并终止。该方法就是用于计算这个映射关系。</p>
	 *
	 * <p>算法步骤：</p>
	 * <ol>
	 *   <li>遍历所有 XFlowEdge，收集 Start 节点的 ID（sourceSet）以及所有 Start/And/Or 节点的 ID（allSaoSet）</li>
	 *   <li>找出所有前置节点数量 > 1 的 OrGateway（即真正的分支汇聚点），作为目标节点（targetOrSet）</li>
	 *   <li>通过递归 DFS 遍历边集合，找出从每个 Start 节点到每个 Target OrGateway 的所有路径</li>
	 *   <li>对每条路径，从倒数第二个节点向前回溯，取出所有不在 allSaoSet 中的节点（即普通任务节点），
	 *       这些节点就是该 OrGateway 的前置节点</li>
	 * </ol>
	 *
	 * <p><b>用途：</b>运行时流程推进到 OrGateway 时，根据此映射关系找出需要被终止的过期任务。</p>
	 *
	 * @param definition XFlow 流程定义，不能为 null
	 * @return key 为 OrGateway 的 nodeId，value 为其前置普通节点的 nodeId 集合
	 * @throws XException 当 definition 为 null 时抛出
	 */
	public Map<String, Set<String>> getPreviousNode(XFlowDefinition definition) {
		if (definition == null)
			throw new XException("Parameter definition is null.");

		/**
		 * 存放OR节点的直接前置节点的UUID
		 */
		Map<String, Set<String>> orGatewayMap = new HashMap<>();
		List<XFlowEdge> edgeList = XFlowRepositoryHelper.repository().findXFlowEdge(definition);
		Set<String> sourceSet = new HashSet<>();
		Set<String> allSaoSet = new HashSet<>(); //收集流程中所有的Start/And/Or节点ID
		for (XFlowEdge edge : edgeList) {
			if (FlowNodeType.startEvent.equals(edge.getSrcType())) {
				sourceSet.add(edge.getSource());
			}
			if (FlowNodeType.startEvent.equals(edge.getSrcType()) || FlowNodeType.orGateway.equals(edge.getSrcType()) || FlowNodeType.andGateway.equals(edge.getSrcType())) {
				allSaoSet.add(edge.getSource());
			}
			if (FlowNodeType.startEvent.equals(edge.getTgtType()) || FlowNodeType.orGateway.equals(edge.getTgtType()) || FlowNodeType.andGateway.equals(edge.getTgtType())) {
				allSaoSet.add(edge.getTarget());
			}
			if (!FlowNodeType.orGateway.equals(edge.getTgtType()))
				continue;

			Set<String> prevNodeIdSet = orGatewayMap.get(edge.getTarget());
			if (prevNodeIdSet == null) {
				prevNodeIdSet = new HashSet<>();
				orGatewayMap.put(edge.getTarget(), prevNodeIdSet);
			}
			prevNodeIdSet.add(edge.getSource());
		}
		/**
		 * 过滤掉前置节点数量为1的OR Gateway
		 */
		Set<String> targetOrSet = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : orGatewayMap.entrySet()) {
			if (entry.getValue().size() > 1) {
				targetOrSet.add(entry.getKey());
			}
		}

		List<List<String>> paths = new ArrayList<>();
		for (String source : sourceSet) {
			for (String target : targetOrSet) {
				if (!source.equals(target)) {
					recurseNodePath(edgeList, source, target, paths);
				}
			}
		}

		Map<String, Set<String>> ORGatewayMap = new HashMap<>();
		for (List<String> path : paths) {
			int size = path.size();
			if (size < 2)
				continue; //路径中至少包含两个节点(Source, Target)

			String key = path.get(size - 1); //最后一个节点是ORGateway节点
			Set<String> nodeSet = ORGatewayMap.get(key);
			if (nodeSet == null) {
				nodeSet = new HashSet<>();
				ORGatewayMap.put(key, nodeSet);
			}
			for (int i = size - 2; i >= 0; i--) {
				String nodeId = path.get(i);
				if (allSaoSet.contains(nodeId))
					break;
				nodeSet.add(nodeId);
			}

		}
		return ORGatewayMap;
	}

	// ============================================================
	//   流程拓扑图遍历（私有方法）
	// ============================================================

	/**
	 * 递归遍历边集合，查找从 source 到 target 的所有路径（入口方法，无需初始路径列表）。
	 *
	 * <p>该重载版本用于首次调用：创建空路径列表并调用去重方法，然后遍历所有以 source 为起点的边，
	 * 若边直接连接到 target 则形成完整路径；否则将 source 加入路径并沿 target 方向继续递归。</p>
	 *
	 * <p><b>注意：</b>该方法创建的 path 局部变量在首次 cleanDuplicateNode 调用后会被
	 * 递归版本中的 path 引用覆盖，实际路径收集在递归版本中完成。</p>
	 *
	 * @param edgeList 流程中所有边的列表
	 * @param source   起始节点 ID
	 * @param target   目标节点 ID
	 * @param result   输出参数，所有找到的路径（每条路径是一个节点 ID 列表）会被添加到此列表中
	 */
	private void recurseNodePath(List<XFlowEdge> edgeList, String source, String target, List<List<String>> result) {
		List<String> path = new ArrayList<>();
		path = cleanDuplicateNode(path);
		for (int i = 0; i < edgeList.size(); i++) {
			XFlowEdge edge = edgeList.get(i);
			if (edge.getSource().equals(source)) {
				//如果相等则找到路径
				if (edge.getTarget().equals(target)) {
					path.add(edge.getSource());
					path.add(edge.getTarget());
					List<String> pathList = cleanDuplicateNode(path);
					result.add(pathList);
					path.clear();
					return;
				}
				path.add(edge.getSource());
				recurseNodePath(edgeList, edge.getTarget(), target, path, result);
			}
		}
		return;
	}

	/**
	 * 递归遍历边集合，查找从 source 到 target 的所有路径（递归版本，保持路径上下文）。
	 *
	 * <p>该重载版本携带当前已走过的 path，防止重复访问同一节点（通过 {@code path.contains(source)} 检测环）。</p>
	 *
	 * <p>递归逻辑：</p>
	 * <ol>
	 *   <li>若 path 已包含 source（环检测），直接返回，避免无限递归</li>
	 *   <li>对 path 执行去重清理</li>
	 *   <li>遍历所有边，找到以 source 为起点的边：
	 *     <ul>
	 *       <li>若边的 target 等于目标节点：形成完整路径，加入 result，清空 path 后返回</li>
	 *       <li>否则：将 source 添加到 path，以边的 target 为新的 source 继续递归</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * @param edgeList 流程中所有边的列表
	 * @param source   当前起始节点 ID
	 * @param target   目标节点 ID
	 * @param path     当前已走过的节点 ID 列表（保持递归上下文）
	 * @param result   输出参数，所有找到的路径会被添加到此列表中
	 */
	private void recurseNodePath(List<XFlowEdge> edgeList, String source, String target, List<String> path, List<List<String>> result) {
		if (path == null)
			path = new ArrayList<>();
		if (path.contains(source))
			return;

		path = cleanDuplicateNode(path);
		for (int i = 0; i < edgeList.size(); i++) {
			XFlowEdge edge = edgeList.get(i);
			if (edge.getSource().equals(source)) {
				//如果相等则找到路径
				if (edge.getTarget().equals(target)) {
					path.add(edge.getSource());
					path.add(edge.getTarget());
					List<String> pathList = cleanDuplicateNode(path);
					result.add(pathList);
					path.clear();
					return;
				}
				path.add(edge.getSource());
				recurseNodePath(edgeList, edge.getTarget(), target, path, result);
			}
		}
		return;
	}

	/**
	 * 去除路径列表中的重复节点（保持首次出现的顺序）。
	 *
	 * <p>在递归路径探索过程中，同一个节点可能被重复添加（例如回溯时），
	 * 该方法用于清理重复条目，确保路径中每个节点只出现一次，方便后续的路径分析和展示。</p>
	 *
	 * @param path 可能包含重复节点的路径列表
	 * @return 去重后的新列表，保持首次出现的顺序
	 */
	private List<String> cleanDuplicateNode(List<String> path) {
		List<String> result = new ArrayList<>();
		for (String node : path) {
			if (!result.contains(node)) {
				result.add(node);
			}
		}
		return result;
	}
}
