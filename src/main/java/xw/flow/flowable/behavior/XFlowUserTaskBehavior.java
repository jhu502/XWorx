package xw.flow.flowable.behavior;

import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.DynamicPropertyUtil;
import org.flowable.engine.impl.bpmn.helper.SkipExpressionUtil;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.MigrationContext;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import tools.jackson.databind.node.ObjectNode;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;
import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.entity.XWorkActivity;
import xw.flow.entity.XWorkInstance;
import xw.flow.entity.XWorkTask;

import java.util.List;

/**
 * XFlow 框架的自定义用户任务行为类，继承 Flowable 原生的 {@link UserTaskActivityBehavior}。
 *
 * <h3>核心职责</h3>
 * <p>在 Flowable BPMN 引擎执行到用户任务节点（UserTask）时，将标准的 Flowable 任务创建流程
 * 与 XFlow 自身的业务模型（{@link XWorkInstance} / {@link XWorkActivity} / {@link XWorkTask}）衔接起来：</p>
 * <ul>
 *   <li><b>扩展元素驱动</b>：从 BPMN 模型中读取 {@code <x:Actor>} 扩展元素，解析其中配置的用户和用户组，
 *       并将其展开为具体的 {@link XUser} 列表。</li>
 *   <li><b>双模式分发</b>：
 *     <ul>
 *       <li>如果扩展元素中配置了用户/用户组 → 进入 <em>XFlow 模式</em>：创建一个共享的 Flowable {@link TaskEntity}
 *           （不做用户指派），然后为每个用户分别创建独立的 {@link XWorkTask} 记录。</li>
 *       <li>如果扩展元素中未配置用户 → 回退到 <em>原生 Flowable 模式</em>：调用父类的 {@code super.execute()}
 *           按标准 assignee / candidateUsers / candidateGroups 逻辑处理。</li>
 *     </ul>
 *   </li>
 *   <li><b>必要性标记</b>：从扩展元素中读取 {@code necessity} 属性（如 "ALL" / "ANY"），
 *       写入 {@link XWorkActivity} 用于后续审批决策。</li>
 *   <li><b>任务完成回调</b>：在 {@link #trigger} 中，先完成 {@link XWorkActivity} 的生命周期，
 *       再委托父类驱动流程流转到下一节点。</li>
 * </ul>
 *
 * <h3>与父类的关系</h3>
 * <p>{@link UserTaskActivityBehavior} 是 Flowable 内置的用户任务行为实现，负责：
 *   创建 TaskEntity → 解析 assignee/candidate → 触发 TaskListener → 插入任务。
 *   本类在父类基础上做了两处关键改动：</p>
 * <ol>
 *   <li>{@link #execute} 拦截任务创建入口，根据扩展元素决定走 XFlow 模式还是原生模式。</li>
 *   <li>{@link #trigger} 拦截任务完成信号，追加 XFlow 侧的 {@link XWorkActivity} 完成逻辑。</li>
 * </ol>
 *
 * <h3>BPMN 扩展元素示例</h3>
 * <pre>{@code
 *  <userTask id="x91292665-490f-4b35-aafa-78a37466fa44" name="提交申请" flowable:formKey="thymeleaf/xflow/taskform/flowWorkTaskReview">
 *     <extensionElements>
 *         <xflow:x-config>
 *              <xflow:type value="userTask"></xflow:type>
 *         </xflow:x-config>
 *         <xflow:x-variable>
 *              <xflow:variable name="bool" type="java.lang.Boolean" display="bool" value="false"></xflow:variable>
 *         </xflow:x-variable>
 *         <xflow:x-route type="Exclusive">
 *              <xflow:route name="提交" language="juel"></xflow:route>
 *              <xflow:route name="取消" language="juel"></xflow:route>
 *         </xflow:x-route>
 *         <xflow:x-event>
 *              <xflow:event name="Created" language="juel"></xflow:event>
 *              <xflow:event name="Initialized" language="juel"></xflow:event>
 *              <xflow:event name="Completed" language="juel"></xflow:event>
 *              <xflow:event name="Deleted" language="juel"></xflow:event>
 *         </xflow:x-event>
 *         <xflow:x-actor necessity="ANY">
 *              <xflow:user name="Guest" display="Guest (Guest)"></xflow:user>
 *              <xflow:user name="Administrator" display="Administrator (Administrator)"></xflow:user>
 *         </xflow:x-actor>
 *     </extensionElements>
 *  </userTask>
 * }</pre>
 *
 * @see UserTaskActivityBehavior 父类 — Flowable 原生用户任务行为
 * @see XFlowDefinitionHelper#getUsers(UserTask) 从扩展元素解析用户列表
 * @see XFlowDefinitionHelper#getGroups(UserTask) 从扩展元素解析用户组列表
 * @see XWorkTask 每个用户对应一条工作任务记录
 * @see XWorkActivity 记录当前活动节点的上下文信息
 */
public class XFlowUserTaskBehavior extends UserTaskActivityBehavior {

    /**
     * 使用 BPMN 模型中的 {@link UserTask} 定义构造行为实例。
     *
     * @param userTask BPMN 用户任务节点定义，包含扩展元素、属性等元数据
     */
    public XFlowUserTaskBehavior(UserTask userTask) {
        super(userTask);
    }

    /**
     * 执行用户任务节点：解析扩展元素中配置的用户/用户组，并根据解析结果选择两种执行路径之一。
     *
     * <h3>执行流程</h3>
     * <ol>
     *   <li><b>解析用户</b>：调用 {@link XFlowDefinitionHelper#getUsers(UserTask)} 和
     *       {@link XFlowDefinitionHelper#getGroups(UserTask)} 从 BPMN 扩展元素中读取配置的用户和用户组。
     *       用户组中的成员通过 {@link XGroup#members()} 展开后合并入用户列表。</li>
     *   <li><b>校验上下文</b>：如果当前 execution 是 {@link ExecutionEntity} 实例，则校验对应的
     *       {@link XWorkInstance} 和 {@link XWorkActivity} 必须存在，否则抛出 {@link XException}。
     *       这是保证 XFlow 与 Flowable 状态一致性的关键检查。</li>
     *   <li><b>路径选择</b>：
     *     <ul>
     *       <li><em>用户列表为空</em> → 调用 {@code super.execute()} 回退到 Flowable 原生行为，
     *           按照 BPMN 标准属性（assignee、candidateUsers、candidateGroups）创建任务。</li>
     *       <li><em>用户列表非空</em> → 进入 XFlow 自定义模式：
     *         <ol>
     *           <li>调用 {@link #createTaskEntity} 创建一个共享的 Flowable TaskEntity
     *               （不设置 assignee/candidate，因为用户指派由 XWorkTask 管理）。</li>
     *           <li>从扩展元素读取 {@code necessity} 属性写入 {@link XWorkActivity}：
     *               "ALL" 表示需要所有人审批同意，"ANY" 表示任意一人同意即可。</li>
     *           <li>为每个用户创建一条 {@link XWorkTask} 记录，所有 XWorkTask 共享同一个 TaskEntity。
     *               这种多对一的设计使得每个用户有独立的审批状态和轨迹，但底层 Flowable 任务只需完成一次。</li>
     *         </ol>
     *       </li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param execution         Flowable 执行实例，包含当前流程执行的上下文信息
     * @param migrationContext  流程迁移上下文，用于流程版本迁移场景下的状态传递
     * @throws XException 当 XWorkInstance 或 XWorkActivity 无法从 execution 中解析时抛出
     */
    @Override
    public void execute(DelegateExecution execution, MigrationContext migrationContext) {
        // ── 1. 从 BPMN 扩展元素中解析用户和用户组，并将用户组成员展开合并 ──
        List<XUser> userList = XFlowDefinitionHelper.getUsers(userTask);
        List<XGroup> groupList = XFlowDefinitionHelper.getGroups(userTask);
        for (XGroup group : groupList) {
            userList.addAll(group.members());
        }

        // ── 2. 校验 XFlow 与 Flowable 的状态一致性：确保 XWorkInstance 和 XWorkActivity 已就绪 ──
        if (execution instanceof ExecutionEntity executionEntity) {
            XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(executionEntity);
            if (workInstance == null) {
                throw new XException("XWorkInstance can't be found by Execution:%s ", (executionEntity).getName());
            }

            XWorkActivity workActivity = XFlowExecutionHelper.execution().getCurrentXWorkActivity(executionEntity);
            if (workActivity == null) {
                throw new XException("Related XWorkActivity was not found.");
            }
        }

        if (userList.isEmpty()) {
            // ── 3a. 扩展元素中无用户配置 → 回退到 Flowable 原生行为（标准 assignee/candidateUsers/candidateGroups）──
            super.execute(execution, migrationContext);
        } else {
            // ── 3b. 扩展元素中有用户配置 → XFlow 自定义模式：一个共享 TaskEntity + 每人一个 XWorkTask ──
            // 创建共享的 Flowable TaskEntity（不设置 assignee/candidate，由 XWorkTask 管理用户指派）
            TaskEntity taskEntity = this.createTaskEntity(execution, migrationContext);

            ExecutionEntity executionEntity = (ExecutionEntity) execution;
            XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(executionEntity);
            XWorkActivity workActivity = XFlowExecutionHelper.execution().getCurrentXWorkActivity(executionEntity);

            // 从扩展元素读取审批必要性（ALL=会签 / ANY=或签），写入 XWorkActivity 供后续审批判断使用
            String necessity = XFlowDefinitionHelper.getNecessity(userTask);
            workActivity.setNecessity(necessity);
            PersistenceHelper.service().save(workActivity);

            // 为每个用户创建独立的 XWorkTask，所有任务引用同一个 Flowable TaskEntity
            for (XUser xuser : userList) {
                XWorkTask workTask = XWorkTask.newInstance(workInstance, workActivity, taskEntity, xuser);
                PersistenceHelper.service().save(workTask);
            }
        }
    }

    /**
     * 创建 Flowable {@link TaskEntity} 实例。
     *
     * <h3>背景</h3>
     * <p>此方法是父类 {@code UserTaskActivityBehavior#execute} 中任务创建逻辑的定制化重实现。
     * 不能直接调用父类的对应方法（父类将创建和指派耦合在一起），因此在这里独立实现以支持
     * XFlow 的特殊需求：创建 TaskEntity 但不做用户指派（指派由后续的 XWorkTask 管理）。</p>
     *
     * <h3>处理步骤</h3>
     * <ol>
     *   <li><b>基础属性设置</b>：设置 executionId、taskDefinitionKey（关联 BPMN 节点）、
     *       propagatedStageInstanceId（阶段传播 ID）。</li>
     *   <li><b>动态属性解析</b>：根据 {@code isEnableProcessDefinitionInfoCache} 配置决定数据来源：
     *     <ul>
     *       <li><em>缓存启用</em>：通过 {@link BpmnOverrideContext} 获取流程定义级别的覆盖属性，
     *           使用 {@link DynamicPropertyUtil#getActiveValue} 合并静态定义和动态覆盖。</li>
     *       <li><em>缓存禁用</em>：直接读取 {@link UserTask} 上的静态定义值。</li>
     *     </ul>
     *     解析的属性包括：name、description、dueDate、priority、category、formKey、
     *     skipExpression、assignee、owner、candidateUsers、candidateGroups、taskIdVariableName。
     *   </li>
     *   <li><b>活动名称覆盖</b>：如果 execution 中存在当前活动名称（运行时注入），优先使用它覆盖静态任务名。</li>
     *   <li><b>拦截器回调</b>：如果配置了 {@code CreateUserTaskInterceptor}，在任务属性最终确定前调用
     *       {@code beforeCreateUserTask} 允许外部介入修改。</li>
     *   <li><b>属性写入</b>：依次调用 {@code handleName}、{@code handleDescription}、{@code handleDueDate}、
     *       {@code handlePriority}、{@code handleCategory}、{@code handleFormKey} 将解析结果写入 TaskEntity。</li>
     *   <li><b>跳过表达式评估</b>：如果配置了 skipExpression 且条件满足，则任务自动跳过（不进入待办列表）。</li>
     *   <li><b>持久化</b>：通过 {@link TaskHelper#insertTask} 将 TaskEntity 插入数据库。</li>
     * </ol>
     *
     * @param execution         Flowable 执行实例
     * @param migrationContext  流程迁移上下文
     * @return 创建完成的 TaskEntity 实例（已持久化，但未设置 assignee/candidate）
     */
    public TaskEntity createTaskEntity(DelegateExecution execution, MigrationContext migrationContext) {
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        TaskService taskService = processEngineConfiguration.getTaskServiceConfiguration().getTaskService();

        // ── 1. 创建空的 TaskEntity 并设置基础标识属性 ──
        TaskEntity taskEntity = taskService.createTask();
        taskEntity.setExecutionId(execution.getId());
        taskEntity.setTaskDefinitionKey(userTask.getId());
        taskEntity.setPropagatedStageInstanceId(execution.getPropagatedStageInstanceId());

        // ── 2. 声明所有动态属性变量（name/description/dueDate/priority/category/formKey/skipExpression/assignee/owner/candidateUsers/candidateGroups/taskIdVariableName）──
        String activeTaskName = null;
        String activeTaskDescription = null;
        String activeTaskDueDate = null;
        String activeTaskPriority = null;
        String activeTaskCategory = null;
        String activeTaskFormKey = null;
        String activeTaskSkipExpression = null;
        String activeTaskAssignee = null;
        String activeTaskOwner = null;
        String activeTaskIdVariableName = null;
        List<String> activeTaskCandidateUsers = null;
        List<String> activeTaskCandidateGroups = null;

        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        // ── 3. 根据缓存配置选择属性解析路径 ──
        if (processEngineConfiguration.isEnableProcessDefinitionInfoCache()) {
            // 缓存模式：从 BpmnOverrideContext 获取动态覆盖属性，支持运行时修改流程定义
            ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(userTask.getId(), execution.getProcessDefinitionId());
            activeTaskName = getNameValue(userTask, migrationContext, taskElementProperties);
            activeTaskDescription = DynamicPropertyUtil.getActiveValue(userTask.getDocumentation(), DynamicBpmnConstants.USER_TASK_DESCRIPTION, taskElementProperties);
            activeTaskDueDate = getDueDateValue(userTask, migrationContext, taskElementProperties);
            activeTaskPriority = getPriorityValue(userTask, migrationContext, taskElementProperties);
            activeTaskCategory = getCategoryValue(userTask, migrationContext, taskElementProperties);
            activeTaskFormKey = getFormKeyValue(userTask, migrationContext, taskElementProperties);
            activeTaskSkipExpression = DynamicPropertyUtil.getActiveValue(userTask.getSkipExpression(), DynamicBpmnConstants.TASK_SKIP_EXPRESSION, taskElementProperties);
            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, taskElementProperties);
            activeTaskOwner = getOwnerValue(userTask, migrationContext, taskElementProperties);
            activeTaskCandidateUsers = getCandidateUsersValue(userTask, migrationContext, taskElementProperties);
            activeTaskCandidateGroups = getCandidateGroupsValue(userTask, migrationContext, taskElementProperties);
            activeTaskIdVariableName = DynamicPropertyUtil.getActiveValue(userTask.getTaskIdVariableName(), DynamicBpmnConstants.USER_TASK_TASK_ID_VARIABLE_NAME, taskElementProperties);
        } else {
            // 非缓存模式：直接读取 UserTask 上的静态定义
            activeTaskName = getNameValue(userTask, migrationContext, null);
            activeTaskDescription = userTask.getDocumentation();
            activeTaskDueDate = getDueDateValue(userTask, migrationContext, null);
            activeTaskPriority = getPriorityValue(userTask, migrationContext, null);
            activeTaskCategory = getCategoryValue(userTask, migrationContext, null);
            activeTaskFormKey = getFormKeyValue(userTask, migrationContext, null);
            activeTaskSkipExpression = userTask.getSkipExpression();
            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, null);
            activeTaskOwner = getOwnerValue(userTask, migrationContext, null);
            activeTaskCandidateUsers = getCandidateUsersValue(userTask, migrationContext, null);
            activeTaskCandidateGroups = getCandidateGroupsValue(userTask, migrationContext, null);
            activeTaskIdVariableName = userTask.getTaskIdVariableName();
        }

        // ── 4. 运行时活动名称覆盖静态任务名 ──
        if (execution.getCurrentActivityName() != null) {
            activeTaskName = execution.getCurrentActivityName();
        }

        // ── 5. 构建拦截器上下文并触发 beforeCreateUserTask 回调 ──
        CreateUserTaskBeforeContext beforeContext = new CreateUserTaskBeforeContext(userTask, execution, activeTaskName, activeTaskDescription,
                activeTaskDueDate, activeTaskPriority, activeTaskCategory, activeTaskFormKey, activeTaskSkipExpression, activeTaskAssignee,
                activeTaskOwner, activeTaskCandidateUsers, activeTaskCandidateGroups);

        if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
            processEngineConfiguration.getCreateUserTaskInterceptor().beforeCreateUserTask(beforeContext);
        }

        // ── 6. 将解析后的属性写入 TaskEntity ──
        handleName(beforeContext, expressionManager, taskEntity, execution);
        handleDescription(beforeContext, expressionManager, taskEntity, execution);
        handleDueDate(beforeContext, expressionManager, taskEntity, execution, processEngineConfiguration, activeTaskDueDate);
        handlePriority(beforeContext, expressionManager, taskEntity, execution, activeTaskPriority);
        handleCategory(beforeContext, expressionManager, taskEntity, execution);
        handleFormKey(beforeContext, expressionManager, taskEntity, execution);

        // ── 7. 评估跳过表达式：条件满足则任务不进待办列表 ──
        boolean skipUserTask = SkipExpressionUtil.isSkipExpressionEnabled(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext)
                && SkipExpressionUtil.shouldSkipFlowElement(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext);

        // ── 8. 持久化 TaskEntity ──
        TaskHelper.insertTask(taskEntity, (ExecutionEntity) execution, !skipUserTask, (!skipUserTask && processEngineConfiguration.isEnableEntityLinks()));

        return taskEntity;
    }

    /**
     * 触发用户任务完成信号：先完成 XFlow 侧的活动记录，再委托父类驱动流程继续流转。
     *
     * <h3>执行顺序</h3>
     * <ol>
     *   <li>通过 {@link XFlowExecutionHelper} 获取当前 execution 对应的 {@link XWorkActivity}。</li>
     *   <li>调用 {@code completeXWorkActivity} 标记 XFlow 侧的活动为已完成，
     *       这会级联处理关联的 {@link XWorkTask} 状态。</li>
     *   <li>调用 {@code super.trigger()} 让 Flowable 引擎执行标准的任务完成逻辑：
     *       触发 CompleteTaskListener → 删除 TaskEntity → 驱动流程流转到下一节点。</li>
     * </ol>
     *
     * <p><b>设计要点</b>：XFlow 的完成必须在父类 trigger 之前执行，
     * 因为父类 trigger 会驱动流程离开当前节点，之后 {@link XWorkActivity} 的上下文可能不可用。</p>
     *
     * @param execution   Flowable 执行实例
     * @param signalName  触发信号名称（来自 BPMN signal 事件，一般为 {@code null}）
     * @param signalData  触发信号携带的数据（一般为 {@code null}）
     */
    @Override
    public void trigger(DelegateExecution execution, String signalName, Object signalData) {
        // 先完成 XFlow 侧的活动记录（必须在父类 trigger 驱动流程流转之前执行）
        XWorkActivity workActivity = XFlowExecutionHelper.execution().getCurrentXWorkActivity((ExecutionEntity) execution);
        XFlowExecutionHelper.execution().completeXWorkActivity(workActivity);

        // 委托父类执行 Flowable 标准任务完成逻辑（触发监听器 → 删除任务 → 流程流转）
        super.trigger(execution, signalName, signalData);
    }
}
