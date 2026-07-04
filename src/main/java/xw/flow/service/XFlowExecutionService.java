package xw.flow.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.history.HistoryManager;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flame.auths.SessionHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

import xw.auths.entity.XUser;
import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.*;
import xw.flow.flowable.XFlowAddAssigneeTaskCmd;
import xw.flow.repos.XFlowRepository;

/**
 * 流程执行服务 —— 流程实例运行时的核心服务，负责流程实例的启停、工作项与工作活动的管理、任务签收/完成、流程节点路由以及执行实体的终止等操作。
 *
 * <p>该服务是 Flowable 引擎与 XFlow 业务模型之间的桥梁，封装了 RuntimeService / TaskService / HistoryService 的调用，
 * 同时维护 XWorkInstance → XWorkActivity → XWorkItem 三级业务对象与底层 ExecutionEntity / TaskEntity 的一致性。</p>
 *
 * <p>主要职责：</p>
 * <ul>
 *   <li>流程实例的启动、查询、关闭与移除</li>
 *   <li>工作项（XWorkItem）的认领、完成以及路由决策</li>
 *   <li>工作活动（XWorkActivity）的创建、完成、终止</li>
 *   <li>OrGateway 场景下过期执行实体的清理</li>
 *   <li>Flowable 历史数据的查询</li>
 * </ul>
 */
@Service
public class XFlowExecutionService extends AbstractXFlowService {
	private final ProcessEngine processEngine;
	private final RuntimeService runtimeService;
	private final XFlowRepository flowRepository;
	private final ProcessEngineConfigurationImpl engineConfiguration;

	/**
	 * 构造注入 ProcessEngine / RuntimeService / XFlowRepository / ProcessEngineConfigurationImpl。
	 *
	 * @param processEngine       Flowable 流程引擎主入口
	 * @param runtimeService      运行时服务，用于启动、查询、操作流程实例与执行实体
	 * @param flowRepository      流程自定义 Repository，用于 XWorkItem / XWorkActivity 等业务对象的数据库访问
	 * @param engineConfiguration 流程引擎配置实现，提供 CommandExecutor / HistoryManager / TaskEntityManager 等底层组件
	 */
	@Autowired
	public XFlowExecutionService(ProcessEngine processEngine, RuntimeService runtimeService, XFlowRepository flowRepository, ProcessEngineConfigurationImpl engineConfiguration) {
		this.processEngine = processEngine;
		this.runtimeService = runtimeService;
		this.flowRepository = flowRepository;
		this.engineConfiguration = engineConfiguration;
	}

	/**
	 * 获取当前绑定的 Flowable ProcessEngine 实例。
	 *
	 * @return ProcessEngine 实例
	 */
	public ProcessEngine getProcessEngine() {
		return this.processEngine;
	}

	/**
	 * 启动一个新的流程实例。
	 *
	 * <p>执行步骤：</p>
	 * <ol>
	 *   <li>校验 definition / primaryObj / variables 三个参数均不为 null</li>
	 *   <li>根据 XFlowDefinition 与 primaryObj 创建 XWorkInstance 业务对象并持久化</li>
	 *   <li>调用 Flowable RuntimeService 以 definition.getProcessDefId() 启动流程，并将 XWorkInstance 的 OID 作为 businessKey 传入</li>
	 * </ol>
	 *
	 * @param definition    流程定义（XFlow 业务层定义，包含 processDefId 等映射信息）
	 * @param primaryObj 与流程实例关联的主业务对象，其 OID 将作为 Flowable 流程实例的 businessKey
	 * @param variables     启动流程时传入的流程变量
	 * @return 持久化后的 XWorkInstance 实例
	 * @throws XException 当任一参数为 null 时抛出
	 */
	@Transactional
	public XWorkInstance startProcessInstance(XFlowDefinition definition, XObject primaryObj, Map<String, Object> variables) {
		if (definition == null)
			throw new XException("未指定XFlow定义参数");
		if (primaryObj == null)
			throw new XException("未指定Primary业务参数");
		if (variables == null)
			throw new XException("未指定Variable参数");

		XWorkInstance workInstance = XWorkInstance.newInstance(definition, primaryObj);
		workInstance.setName(definition.getName());
		workInstance = PersistenceHelper.service().save(workInstance);
		ProcessInstance instance = this.runtimeService.startProcessInstanceById(definition.getProcessDefId(), workInstance.getOid(), variables);
		LOGGER.info("启动流程实例成功:{}", instance);
		return workInstance;
	}

	/**
	 * 根据 XWorkInstance 查询对应的 Flowable ProcessInstance。
	 *
	 * <p>通过 XWorkInstance 中存储的 processInstId 去 RuntimeService 的 ExecutionQuery 查询，
	 * 返回的是 ProcessInstance 类型（Execution 的子接口），代表一个正在运行的流程实例根执行实体。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象，不能为 null
	 * @return 对应的 Flowable ProcessInstance，若流程已结束则可能返回 null
	 * @throws XException 当 workInstance 为 null 时抛出
	 */
	public ProcessInstance getProcessInstance(XWorkInstance workInstance) {
		if (workInstance == null)
			throw new XException("参数是空.");

		return (ProcessInstance) this.runtimeService.createExecutionQuery().executionId(workInstance.getProcessInstId()).singleResult();
	}

	/**
	 * 查询指定流程实例下所有子执行实体（Execution）。
	 *
	 * <p>通过 parentId 查询，即查询以该流程实例为父的所有 Execution，通常用于获取流程中各个活动节点的执行分支。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象
	 * @return 子执行实体列表，若无子执行则返回空列表
	 */
	public List<Execution> getExecutionEntity(XWorkInstance workInstance) {
		return this.runtimeService.createExecutionQuery().parentId(workInstance.getProcessInstId()).list();
	}

	/**
	 * 为指定活动节点动态添加任务办理人（加签操作）。
	 *
	 * <p>通过 engineConfiguration 获取 CommandExecutor 执行自定义命令 XFlowAddAssigneeTaskCmd，
	 * 在流程运行期间向指定 activityId 对应的节点追加一个办理人，返回新创建的 TaskEntity。</p>
	 *
	 * @param flowInstance 当前的 XWork 流程实例
	 * @param activityId   要添加办理人的 BPMN 活动节点 ID
	 * @param xuser        要添加的办理人用户对象
	 * @return 新创建的 TaskEntity，若命令执行失败则可能为 null
	 */
	public TaskEntity addTaskAssignee(XWorkInstance flowInstance, String activityId, XUser xuser) {
		CommandExecutor commandExecutor = engineConfiguration.getCommandExecutor();
		return commandExecutor.execute(new XFlowAddAssigneeTaskCmd(activityId, xuser));
	}

	/**
	 * 根据流程状态列出当前登录用户的所有工作项。
	 *
	 * <p>从 Session 中获取当前用户，然后调用 XFlowRepository 查询该用户在指定状态下的所有 XWorkItem。
	 * 若 status 为 null，直接返回空列表，避免无效查询。</p>
	 *
	 * @param status 工作项状态（OPEN / COMPLETED / TERMINATED 等），为 null 时返回空列表
	 * @return 当前用户在指定状态下的 XWorkItem 列表，可能为空列表
	 */
	public List<XWorkTask> listOwnedWorkItem(FlowStatus status) {
		if (status == null)
			return new ArrayList<>();

		XUser user = (XUser) SessionHelper.getCurrentUser();
		return flowRepository.findXWorkItem(user, status);
	}

	/**
	 * 根据 Flowable ProcessInstance ID 反查 XFlowDefinition。
	 *
	 * <p>流程实例启动后，businessKey 中存储的是 XWorkInstance 的 OID，
	 * 而 ProcessInstance 的 processDefinitionId 对应的是 Flowable 部署定义 ID。
	 * 该方法通过 DeploymentManager 查找部署定义，再将其 key 中的 "-" 替换为 ":" 还原 XFlow 定义的 OID，最后查询 XFlowDefinition。</p>
	 *
	 * @param processInstanceId Flowable 流程实例 ID
	 * @return 对应的 XFlowDefinition，若找不到则返回 null
	 */
	public XFlowDefinition getXFlowDefinition(String processInstanceId) {
		DeploymentManager deploymentManager = Context.getProcessEngineConfiguration().getDeploymentManager();
		ProcessDefinition definitionEntity = deploymentManager.findDeployedProcessDefinitionById(processInstanceId);
		String defOid = definitionEntity.getKey().replace("-", ":");
		return PersistenceHelper.service().find(defOid);
	}

	/**
	 * 查询指定执行实体在指定状态下的所有工作活动（XWorkActivity）。
	 *
	 * <p>从 ExecutionEntity 的 businessKey 定位 XWorkInstance，再通过 XFlowRepository 查询关联的 XWorkActivity 列表。
	 * 若 execution 为 null 或找不到对应的 XWorkInstance，则返回空列表。</p>
	 *
	 * @param execution Flowable 执行实体，用于推导 XWorkInstance
	 * @param status    工作活动状态过滤条件
	 * @return XWorkActivity 列表，可能为空列表
	 */
	public List<XWorkActivity> getXWorkActivities(ExecutionEntity execution, FlowStatus status) {
		List<XWorkActivity> result = new ArrayList<>();
		if (execution == null)
			return result;

		XWorkInstance workInstance = this.getXWorkInstance(execution);
		if (workInstance == null)
			return result;

		return this.flowRepository.findXWorkActivity(workInstance, execution, status);
	}

	/**
	 * 获取指定 ExecutionEntity 当前所在的工作活动。
	 *
	 * <p>查找策略（按优先级）：</p>
	 * <ol>
	 *   <li><b>缓存命中：</b>首先尝试从 ExecutionEntity 的 ExternalObject 变量中读取已缓存的 XWorkActivity，若命中则直接返回</li>
	 *   <li><b>历史查询：</b>通过 HistoryManager 查找当前执行的 HistoricActivityInstance（非结束状态）</li>
	 *   <li><b>业务匹配：</b>根据 HistoricActivityInstance 的 activityId 查找对应的 XWorkActivity，若存在多条记录则取创建时间最新的一条</li>
	 *   <li><b>缓存写入：</b>将查询结果回写到 ExecutionEntity.setExternalObject()，供后续调用复用</li>
	 * </ol>
	 *
	 * @param execution Flowable 执行实体
	 * @return 当前 XWorkActivity，若找不到则返回 null
	 */
	public XWorkActivity getCurrentXWorkActivity(ExecutionEntity execution) {
		if (execution == null)
			return null;

		// 首先从$$_XWORK_ACTIVITY_$$变量中获取XWorkActivity, 如果不为null, 直接返回;
		XWorkActivity workActivity = (XWorkActivity) execution.getExternalObject();
		if (workActivity != null)
			return workActivity;

		// 从数据库去查询Execution的当前HistoricActivityInstance, 然后根据HistoricActivityInstance去查询XWorkActivity;
		HistoryManager historyManager = this.engineConfiguration.getHistoryManager();
		HistoricActivityInstance hisActivity = historyManager.findHistoricActivityInstance(execution, false);
		if (hisActivity == null)
			return null;

		// 从Execution的BussinessKey中去获取对应的XWorkInstance对象
		XWorkInstance workInstance = this.getXWorkInstance(execution);
		if (workInstance == null)
			return null;

		List<XWorkActivity> workActivities = this.flowRepository.findXWorkActivity(workInstance, hisActivity);

		XWorkActivity resultActivity = null;
		for (XWorkActivity activity : workActivities) {
			if (resultActivity == null || activity.getCreatedStamp().after(resultActivity.getCreatedStamp())) {
				resultActivity = activity;
			}
		}
		if (resultActivity != null) {
			execution.setExternalObject(resultActivity);
		}
		return resultActivity;
	}

	/**
	 * 获取指定流程实例中每个活动节点的最新 XWorkActivity。
	 *
	 * <p>一个活动节点可能被多次执行（如循环、回退场景），该方法按 activityId 分组，
	 * 每组取 createdStamp 最新的一条 XWorkActivity，以 Map 形式返回。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象
	 * @return key 为 BPMN activityId、value 为该节点最新 XWorkActivity 的映射
	 */
	public Map<String, XWorkActivity> getLatestXWorkActivity(XWorkInstance workInstance) {
		Map<String, XWorkActivity> result = new HashMap<>();
		List<XWorkActivity> workActivities = this.flowRepository.findXWorkActivity(workInstance);
		for (XWorkActivity activity : workActivities) {
			XWorkActivity $activity = result.get(activity.getActivityId());
			if ($activity == null || activity.getCreatedStamp().after($activity.getCreatedStamp())) {
				result.put(activity.getActivityId(), activity);
			}
		}

		return result;
	}

	/**
	 * 查询指定流程实例的所有历史活动实例（HistoricActivityInstance）。
	 *
	 * <p>调用 Flowable HistoryService 按 processInstanceId 查询，返回该流程实例从启动到当前的所有活动记录
	 * （包括已完成的、正在运行的和已终止的节点）。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象
	 * @return HistoricActivityInstance 列表，按执行时间排序
	 */
	public List<HistoricActivityInstance> getHistoricActivity(XWorkInstance workInstance) {
		HistoryService historyService = this.processEngine.getHistoryService();
		return historyService.createHistoricActivityInstanceQuery().processInstanceId(workInstance.getProcessInstId()).list();
	}

	/**
	 * 查询指定流程实例中某个活动节点下的所有执行实体（Execution）。
	 *
	 * <p>在多实例（Multi-Instance）或并行网关场景下，同一个 activityId 可能对应多个 Execution，
	 * 本方法返回所有这些并行执行分支。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象
	 * @param activityId   BPMN 活动节点 ID
	 * @return 该活动节点下的 Execution 列表
	 */
	public List<Execution> getExecutionActivity(XWorkInstance workInstance, String activityId) {
		RuntimeService runtimeService = this.processEngine.getRuntimeService();
		return runtimeService.createExecutionQuery().parentId(workInstance.getProcessInstId()).activityId(activityId).list();
	}

	/**
	 * 根据 TaskInfo 获取对应的 BPMN FlowElement（流程元素）。
	 *
	 * <p>该方法通过 TaskInfo 获取 processDefinitionId 和 taskDefinitionKey，
	 * 然后加载对应的 BpmnModel，从主流程中定位到具体的 FlowElement（如 UserTask、ServiceTask 等），
	 * 通常用于获取节点的扩展属性或判断节点类型。</p>
	 *
	 * <p><b>注意：</b>调用方需确保 taskInfo 不为 null，否则会抛出 NullPointerException。</p>
	 *
	 * @param taskInfo Flowable 任务信息（运行中的 Task 或历史 HistoricTaskInstance），不能为 null
	 * @return 对应的 FlowElement，若流程模型中不存在该 taskDefinitionKey 则返回 null
	 */
	public FlowElement getFlowElement(TaskInfo taskInfo) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(taskInfo.getProcessDefinitionId());
		Process process = bpmnModel.getMainProcess();
		return process.getFlowElement(taskInfo.getTaskDefinitionKey());
	}

	/**
	 * 根据 XWorkItem 查询对应的 Flowable TaskInfo。
	 *
	 * <p>查询逻辑：先从运行中的 TaskService 查询（流程未结束时），若查不到则回退到 HistoryService 查询（流程已结束或任务已完成）。
	 * 这样无论任务处于何种状态都能获取到 TaskInfo。</p>
	 *
	 * <p><b>注意：</b>如果 XWorkItem 的 instance 关联为空或 processInstId/taskId 无效，返回值可能为 null。</p>
	 *
	 * @param workitem XWork 工作项业务对象
	 * @return 对应的 TaskInfo（运行中的 Task 或历史 HistoricTaskInstance），若找不到则返回 null
	 */
	public TaskInfo getTaskInfo(XWorkTask workitem) {
		XWorkInstance instance = workitem.getInstance();
		TaskService taskService = this.processEngine.getTaskService();
		TaskInfo taskInfo = taskService.createTaskQuery().processInstanceId(instance.getProcessInstId()).taskId(workitem.getTaskId()).singleResult();
		if (taskInfo == null) {
			HistoryService historyService = this.processEngine.getHistoryService();
			taskInfo = historyService.createHistoricTaskInstanceQuery().taskId(workitem.getTaskId()).singleResult();
		}
		return taskInfo;
	}

	/**
	 * 根据流程实例 ID 和任务 ID 完成任务（便捷重载）。
	 *
	 * <p>先通过 TaskService 查询出对应的 Task，再委托给 {@link #completeTask(TaskInfo, String, String)} 执行实际的完成逻辑。</p>
	 *
	 * @param instance XWork 流程实例业务对象
	 * @param taskId   Flowable 任务 ID
	 * @param routes   路由选择（多个路由以逗号分隔），决定流程走向
	 * @param remarks  办理意见/备注
	 * @return 完成后的 HistoricTaskInstance（历史任务实例），若找不到 Task 则返回 null
	 */
	@Transactional
	public TaskInfo completeTask(XWorkInstance instance, String taskId, String routes, String remarks) {
		TaskService taskService = this.processEngine.getTaskService();
		Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstId()).taskId(taskId).singleResult();
		return this.completeTask(task, routes, remarks);
	}

	/**
	 * 完成指定任务（核心完成任务方法）。
	 *
	 * <p>执行步骤：</p>
	 * <ol>
	 *   <li>若 taskInfo 为 null，直接返回 null（防御性处理）</li>
	 *   <li>查找该 Task 关联的所有 XWorkItem，逐一更新：设置 routes / 状态为 COMPLETED / 备注 / 完成人与完成时间，并持久化</li>
	 *   <li>调用 TaskService.claim() 将任务签收给当前用户（确保 claim 权限）</li>
	 *   <li>调用 TaskService.complete() 完成 Flowable 任务，触发流程流转</li>
	 *   <li>从 HistoryService 查询刚完成的历史任务实例并返回</li>
	 * </ol>
	 *
	 * @param taskInfo Flowable 任务信息（运行中的 Task），为 null 时直接返回 null
	 * @param routes   路由选择（多个路由以逗号分隔）
	 * @param remarks  办理意见/备注
	 * @return 完成后的 HistoricTaskInstance，若 taskInfo 为 null 则返回 null
	 */
	@Transactional
	public TaskInfo completeTask(TaskInfo taskInfo, String routes, String remarks) {
		if (taskInfo == null)
			return null;
		String taskId = taskInfo.getId();

		TaskService taskService = this.processEngine.getTaskService();

		XUser xuser = (XUser) SessionHelper.getCurrentUser();
		List<XWorkTask> workItems = this.flowRepository.findXWorkItem(taskInfo);
		for (XWorkTask workItem : workItems) {
			if (FlameUtils.isNotBlank(routes)) {
				workItem.setRoutes(routes);
			}
			workItem.setStatus(FlowStatus.COMPLETED);
			workItem.setRemarks(remarks);
			workItem.setCompletedBy(xuser.getNumber());
			workItem.setCompletedOn(new Timestamp((new Date()).getTime()));
			workItem = PersistenceHelper.service().save(workItem);
		}
		taskService.claim(taskInfo.getId(), xuser.getOid());
		taskService.complete(taskInfo.getId());

		HistoryService historyService = this.processEngine.getHistoryService();
		return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
	}

	/**
	 * 完成工作活动（XWorkActivity），由 XFlowUserTaskBehavior 回调触发。
	 *
	 * <p>该方法在用户任务节点所有工作项处理完毕后被调用，执行以下逻辑：</p>
	 * <ol>
	 *   <li>收集所有 COMPLETED 状态 XWorkItem 的路由（routes），去重后合并为 XWorkActivity 的路由字符串</li>
	 *   <li>删除该活动下所有 OPEN 状态的 XWorkItem（这些工作项已不需要处理）</li>
	 *   <li>将 XWorkActivity 状态置为 COMPLETED 并持久化</li>
	 * </ol>
	 *
	 * <p><b>路由合并规则：</b>多个工作项的路由取并集，例如工作项 A 路由 "同意,驳回" + 工作项 B 路由 "同意" → 最终路由 "同意,驳回"。</p>
	 *
	 * @param workActivity 要完成的工作活动
	 * @return 更新后的 XWorkActivity
	 */
	@Transactional
	public XWorkActivity completeXWorkActivity(XWorkActivity workActivity) {
		Set<String> routeSet = new HashSet<>();
		List<XWorkTask> workItems = this.flowRepository.findXWorkItem(workActivity, FlowStatus.COMPLETED);
		for (XWorkTask workItem : workItems) {
			String routes = workItem.getRoutes();
			if (FlameUtils.isBlank(routes))
				continue;
			routeSet.addAll(Arrays.asList(routes.split(",")));
		}

		StringBuilder builder = new StringBuilder();
		for (String route : routeSet) {
			if (builder.toString().isEmpty()) {
				builder.append(route);
			} else {
				builder.append(",").append(route);
			}
		}

		List<XWorkTask> openWorkItems = this.flowRepository.findXWorkItem(workActivity, FlowStatus.OPEN);
		if (!openWorkItems.isEmpty()) {
			PersistenceHelper.service().remove(openWorkItems);
		}

		workActivity.setRoutes(builder.toString());
		workActivity.setStatus(FlowStatus.COMPLETED);
		return PersistenceHelper.service().save(workActivity);
	}

	/**
	 * 完成单个工作项（XWorkItem），并根据活动的必要性（necessity）决定是否推进 Flowable 任务。
	 *
	 * <p>该方法处理三种必要性模式：</p>
	 * <ul>
	 *   <li><b>ANY：</b>任意一个办理人完成即可推进 —— 立即 sign 并 complete 对应的 Flowable Task</li>
	 *   <li><b>ALL：</b>所有办理人都完成后才推进 —— 检查该活动下是否还有 OPEN 状态的 XWorkItem，若无剩余则推进</li>
	 *   <li><b>其他（默认为 ANY 行为）：</b>立即 sign 并 complete 对应的 Flowable Task</li>
	 * </ul>
	 *
	 * <p>无论哪种模式，都会先将当前 XWorkItem 的状态更新为 COMPLETED、设置路由/备注/完成信息并持久化。</p>
	 *
	 * @param workItem 要完成的工作项
	 * @param routes   路由选择（会被转为 String 存储）
	 * @param remarks  办理意见/备注
	 * @return 更新后的 XWorkItem
	 */
	@Transactional
	public XWorkTask completeXWorkItem(XWorkTask workItem, Object routes, String remarks) {
		XWorkInstance instance = workItem.getInstance();
		XUser xuser = (XUser) SessionHelper.getCurrentUser();
		workItem.setRoutes((String) routes);
		workItem.setStatus(FlowStatus.COMPLETED);
		workItem.setRemarks(remarks);
		workItem.setCompletedBy(xuser.getNumber());
		workItem.setCompletedOn(new Timestamp((new Date()).getTime()));
		workItem = PersistenceHelper.service().save(workItem);

		XWorkActivity workActivity = workItem.getActivity();
		String necessity = workActivity.getNecessity();
		if ("ANY".equals(necessity)) {
			TaskService taskService = this.processEngine.getTaskService();
			Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstId()).taskId(workItem.getTaskId()).singleResult();
			taskService.claim(task.getId(), xuser.getOid());
			taskService.complete(workItem.getTaskId());
		} else if ("ALL".equals(necessity)) {
			List<XWorkTask> workItems = this.flowRepository.findXWorkItem(workActivity, FlowStatus.OPEN);
			if (workItems.isEmpty()) {
				TaskService taskService = this.processEngine.getTaskService();
				Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstId()).taskId(workItem.getTaskId()).singleResult();
				taskService.claim(task.getId(), xuser.getOid());
				taskService.complete(workItem.getTaskId());
			}
		} else {
			TaskService taskService = this.processEngine.getTaskService();
			Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstId()).taskId(workItem.getTaskId()).singleResult();
			taskService.claim(task.getId(), xuser.getOid());
			taskService.complete(workItem.getTaskId());
		}

		return workItem;
	}

	/**
	 * 强制完成指定流程实例中某个活动节点的所有任务。
	 *
	 * <p>用于管理员干预流程的场景：查出该 activityId 下的所有 Execution，逐一对每个 Execution 对应的 Task
	 * 进行 claim + complete 操作，以当前登录用户身份签收并完成。</p>
	 *
	 * @param workInstance XWork 流程实例业务对象，为 null 时直接返回不做任何操作
	 * @param activityId   要强制完成的 BPMN 活动节点 ID
	 */
	@Transactional
	public void completeActivity(XWorkInstance workInstance, String activityId) {
		if (workInstance == null)
			return;

		TaskService taskService = this.processEngine.getTaskService();
		List<Execution> executions = this.getExecutionActivity(workInstance, activityId);
		for (Execution execution : executions) {
			Task task = taskService.createTaskQuery().processInstanceId(workInstance.getProcessInstId()).taskDefinitionKey(execution.getActivityId()).singleResult();
			XUser xuser = (XUser) SessionHelper.getCurrentUser();
			taskService.claim(task.getId(), xuser.getOid());
			task.setAssignee(xuser.getOid());
			taskService.complete(task.getId());
		}
	}

	/**
	 * 终止一个 ExecutionEntity 及其关联的所有业务对象。
	 *
	 * <p>终止操作包含三个层面的清理：</p>
	 * <ol>
	 *   <li><b>XWorkItem 层：</b>删除该执行下所有 OPEN 状态的 XWorkItem</li>
	 *   <li><b>XWorkActivity 层：</b>将该执行下所有 OPEN 状态的 XWorkActivity 状态更新为 TERMINATED</li>
	 *   <li><b>Flowable 层：</b>删除该执行下所有未删除的 TaskEntity，最后删除 ExecutionEntity 本身</li>
	 * </ol>
	 *
	 * @param execution 要终止的 Flowable 执行实体，为 null 时直接返回
	 * @param reason    终止原因（日志/审计用），暂未持久化到业务对象中
	 * @param cascade   是否级联删除（当前实现中该参数未使用，保留用于未来扩展）
	 */
	@Transactional
	public void terminateExecutionEntity(ExecutionEntity execution, String reason, boolean cascade) {
		if (execution == null)
			return;

		List<XWorkActivity> workActivities = this.getXWorkActivities(execution, FlowStatus.OPEN);
		for (XWorkActivity activity : workActivities) {
			if (!FlowStatus.OPEN.equals(activity.getStatus()))
				continue;

			List<XWorkTask> workItems = this.flowRepository.findXWorkItem(activity, FlowStatus.OPEN);
			for (XWorkTask workItem : workItems) {
				if (workItem.isOpenStatus()) {
					PersistenceHelper.service().remove(workItem);
				}
			}

			activity.setStatus(FlowStatus.TERMINATED);
			PersistenceHelper.service().update(activity);
		}

		TaskEntityManager taskEntityManager = this.engineConfiguration.getTaskServiceConfiguration().getTaskEntityManager();
		List<TaskEntity> taskEntities = taskEntityManager.findTasksByExecutionId(execution.getId());
		for (TaskEntity taskEntity : taskEntities) {
			if (!taskEntity.isDeleted()) {
				taskEntityManager.delete(taskEntity, true);
			}
		}
		this.engineConfiguration.getExecutionEntityManager().delete(execution);
	}

	/**
	 * 流程流经 OrGateway 节点后，终止该网关之前未选中分支上的任务和执行实体。
	 *
	 * <p>OrGateway（排他网关/包容网关）根据条件选择部分分支继续执行，未选中的分支上可能仍存在
	 * OPEN 状态的 ExecutionEntity / TaskEntity / XWorkItem，需要被清理以避免资源泄漏。</p>
	 *
	 * <p>处理逻辑：</p>
	 * <ol>
	 *   <li>根据 definition 和 exclusiveGateway 的 ID 查找对应的 XFlowGateway 配置</li>
	 *   <li>获取网关配置的 arguments（网关之前所有可能的来源节点 ID）</li>
	 *   <li>遍历 arguments 中的每个节点：
	 *     <ul>
	 *       <li>若为 XFlowUserTask：查找该 processInstId 下匹配 activityId 的 ExecutionEntity 并终止</li>
	 *       <li>若为 XFlowThing：当前为空实现，预留扩展</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * <p><b>注意：</b>BPMN 活动 ID 必须以字母开头，但 XFlowNode 的 nodeId 是 UUID（可能以数字开头），
	 * 因此在匹配时通过 "x" 前缀进行了转换。</p>
	 *
	 * @param definition       流程定义
	 * @param exclusiveGateway BPMN 排他网关元素
	 * @param processInstId    Flowable 流程实例 ID
	 */
	public void terminatePrevTaskEntity(XFlowDefinition definition, ExclusiveGateway exclusiveGateway, String processInstId) {
		if (definition == null || exclusiveGateway == null || processInstId == null)
			return;
		List<XFlowGateway> gateways = XFlowRepositoryHelper.repository().findXFlowGatewayById(definition, exclusiveGateway.getId());
		if (gateways.isEmpty())
			return;

		XFlowGateway gateway = gateways.get(0);

		RuntimeService runtimeService = this.processEngine.getRuntimeService();
		List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstId).list();
		for (String nodeId : gateway.getArguments()) {
			XFlowNode prevNode = XFlowDefinitionHelper.definition().findXFlowNodeById(definition, nodeId);
			if (prevNode instanceof XFlowUserTask) {
				for (Execution execution : executions) {
					/** bpmn的活动Id必须是以字母开头, 但是uuid可能是以数字开头, XFlowNode的id(uuid)就通过加"x"前缀转换成活动Id */
					if (execution.getActivityId().equals(prevNode.getNodeId())) {
						this.terminateExecutionEntity((ExecutionEntity) execution, "Necessity is ANY.", true);
					}
				}
			} else if (prevNode instanceof XFlowThing) {

			}
		}
	}

	/**
	 * 从 ExecutionEntity 中提取对应的 XWorkInstance。
	 *
	 * <p>解析策略：先取当前 ExecutionEntity 的 businessKey，若为空则向父 ExecutionEntity 追溯
	 * （因为子 Execution 的 businessKey 可能未被设置，但父级根 ProcessInstance 一定携带 businessKey）。
	 * 获取 businessKey 后通过 PersistenceHelper 查找 XWorkInstance。</p>
	 *
	 * @param execution Flowable 执行实体
	 * @return 对应的 XWorkInstance，若无法定位则返回 null
	 */
	public XWorkInstance getXWorkInstance(ExecutionEntity execution) {
		String businessKey = execution.getBusinessKey();
		if (FlameUtils.isBlank(businessKey)) {
			ExecutionEntity parentEntity = execution.getParent();
			if (parentEntity != null) {
				businessKey = parentEntity.getBusinessKey();
			}
		}

		if (FlameUtils.isNotBlank(businessKey)) {
			return PersistenceHelper.service().find(businessKey);
		} else {
			return null;
		}
	}

	/**
	 * 从 ProcessInstance 中提取对应的 XWorkInstance。
	 *
	 * <p>直接从 ProcessInstance.getBusinessKey() 获取 businessKey，再通过 PersistenceHelper 查找。
	 * 若 businessKey 为空则返回 null。</p>
	 *
	 * @param instance Flowable 流程实例
	 * @return 对应的 XWorkInstance，若 businessKey 为空或找不到则返回 null
	 */
	public XWorkInstance getXWorkInstance(ProcessInstance instance) {
		String businessKey = instance.getBusinessKey();
		if (FlameUtils.isBlank(businessKey))
			return null;

		return PersistenceHelper.service().find(businessKey);
	}

	/**
	 * 关闭一个流程实例（正常结束流程）。
	 *
	 * <p>该方法适用于流程流转到 EndEvent 后需要清理场景：</p>
	 * <ol>
	 *   <li>遍历流程实例下的所有子 ExecutionEntity，逐一调用 {@link #terminateExecutionEntity(ExecutionEntity, String, boolean)} 终止</li>
	 *   <li>调用 ExecutionEntityManager.deleteProcessInstance() 删除根 ProcessInstance
	 *       （cascade=false 表示保留历史记录，cascade=true 会删除历史）</li>
	 *   <li>更新 XWorkInstance 状态为 CLOSED 并持久化</li>
	 * </ol>
	 *
	 * <p><b>注意：</b>不能使用 executionManager.delete(processInstance) 直接删除，会导致外键约束异常，
	 * 必须使用 deleteProcessInstance() 方法。</p>
	 *
	 * @param workInstance 要关闭的 XWork 流程实例，为 null 时直接返回
	 */
	@Transactional
	public void closeXWorkInstance(XWorkInstance workInstance) {
		if (workInstance == null)
			return;

		ProcessInstance processInstance = this.getProcessInstance(workInstance);
		List<Execution> executions = this.getExecutionEntity(workInstance);
		for (Execution _execution : executions) {
			if (_execution instanceof ExecutionEntity) {
				this.terminateExecutionEntity((ExecutionEntity) _execution, "EndEvent terminated it.", false);
			}
		}
		if (processInstance != null) {
			ExecutionEntityManager executionManager = this.engineConfiguration.getExecutionEntityManager();
			/**
			 * 删除Process Instance不能够使用executionManager.delete(processInstance), 会导致外键约束的异常;
			 * cascade:true - 会删除历史记录; cascade:false - 会保留历史记录
			 */

			executionManager.deleteProcessInstance(processInstance.getId(), "EndEvent closed process instance.", false);
			workInstance.setStatus(FlowStatus.CLOSED);
			PersistenceHelper.service().save(workInstance);
		}
	}

	/**
	 * 彻底删除一个流程实例及其所有关联数据（硬删除，不可恢复）。
	 *
	 * <p>删除顺序（避免外键约束冲突）：</p>
	 * <ol>
	 *   <li>调用 ExecutionEntityManager.deleteProcessInstance(cascade=true) 删除 Flowable 层数据（包含历史记录）</li>
	 *   <li>删除该流程实例下所有 XWorkItem</li>
	 *   <li>删除该流程实例下所有 XWorkActivity</li>
	 *   <li>最后删除 XWorkInstance 本身</li>
	 * </ol>
	 *
	 * <p><b>警告：</b>此操作会连同 Flowable 历史表数据一起清除，适用于测试数据清理，生产环境需谨慎使用。</p>
	 *
	 * @param workInstance 要删除的 XWork 流程实例，不能为 null
	 * @throws XException 当 workInstance 为 null 时抛出
	 */
	@Transactional
	public void removeProcessInstance(XWorkInstance workInstance) {
		if (workInstance == null)
			throw new XException("参数是空.");

		ExecutionEntityManager executionManager = this.engineConfiguration.getExecutionEntityManager();
		executionManager.deleteProcessInstance(workInstance.getProcessInstId(), "Clean flow instance.", true);

		List<XWorkTask> workItems = this.flowRepository.findXWorkItem(workInstance);
		PersistenceHelper.service().remove(workItems);
		List<XWorkActivity> workActivities = this.flowRepository.findXWorkActivity(workInstance);
		PersistenceHelper.service().remove(workActivities);
		PersistenceHelper.service().remove(workInstance);
	}

	/**
	 * 删除一个 TaskEntity 及其关联的所有 XWorkItem。
	 *
	 * <p>通常用于手动清理某个特定任务节点的场景：先删除该 Task 对应的所有 XWorkItem，
	 * 再通过 TaskEntityManager.delete() 删除 Flowable 任务实体（cascade=true），并同步删除历史记录。</p>
	 *
	 * @param taskEntity 要删除的 Flowable TaskEntity
	 * @param reason     删除原因（审计/日志用途）
	 */
	@Transactional
	public void removeTaskEntity(TaskEntity taskEntity, String reason) {
		TaskEntityManager entityManager = this.engineConfiguration.getTaskServiceConfiguration().getTaskEntityManager();
		List<XWorkTask> workItems = this.flowRepository.findXWorkItem(taskEntity);
		PersistenceHelper.service().remove(workItems);
		entityManager.delete(taskEntity, true);
	}
}
