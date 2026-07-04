package xw.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.flame.auths.SessionHelper;
import com.flame.orm.PersistenceHelper;

import xw.auths.entity.XUser;
import xw.flow.entity.XFlowDefinition;
import xw.flow.entity.XFlowEdge;
import xw.flow.entity.XFlowUserTask;
import xw.flow.entity.XWorkInstance;

@SpringBootTest
@ActiveProfiles("Test")
class XWorxFlowApplicationTest {
	protected Logger logger = LoggerFactory.getLogger(XWorxFlowApplicationTest.class);

	@Test
	void deployXFlowDefinition() {
		List<XFlowDefinition> definitions = XFlowRepositoryHelper.repository().findDefinitionByName("mi_电子物料申请流程");
		for (XFlowDefinition definition : definitions) {
			System.out.println(XFlowDefinitionHelper.definition().generateBpmnXML(definition));
			XFlowDefinitionHelper.definition().deployFlowDefinition(definition);
		}
	}

	@Test
	void testOrGatewayPrevNode() {
		List<XFlowDefinition> definitions = XFlowRepositoryHelper.repository().findDefinitionByName("mi_电子物料申请流程");
		if (!definitions.isEmpty()) {
			XFlowDefinition definition = definitions.get(0);
			Map<String, Set<String>> ORGatewayMap = XFlowDefinitionHelper.definition().getPreviousNode(definition);
			for (Map.Entry<String, Set<String>> entry : ORGatewayMap.entrySet()) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		}
	}

	public static void recurNodePath(List<XFlowEdge> edgeList, String source, String target, List<String> path, List<List<String>> result) {
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
				recurNodePath(edgeList, edge.getTarget(), target, path, result);
			}
		}
		return;
	}

	public static List<String> cleanDuplicateNode(List<String> path) {
		List<String> result = new ArrayList<>();
		for (String node : path) {
			if (!result.contains(node)) {
				result.add(node);
			}
		}
		return result;
	}

	@Test
	void completeTask() {
		SessionHelper.setCurrentUser("Guest");
		XWorkInstance instance = PersistenceHelper.service().find("OR:xw.flow.entity.XWorkInstance:1094");
		XFlowExecutionHelper.execution().completeTask(instance, "714469d6-5dc6-11ed-87a3-e0d045a8e935", "", "");
	}

	@Test
	void addAssigneeTask() {
		XWorkInstance instance = PersistenceHelper.service().find("OR:xw.flow.entity.XWorkInstance:1094");
		SessionHelper.setCurrentUser("Guest");
		XUser xuser = (XUser) SessionHelper.getCurrentUser();
		XFlowExecutionHelper.execution().addTaskAssignee(instance,"xf9e84e1b-ecf1-4a96-a9db-b66429ee905d", xuser);
	}

	@Test
	public void showTaskVariable() {
		String instanceId = "adc6a5f0-a2c3-11e9-bb83-00e04c83a7ff"; // 任务ID

		TaskService taskService = XFlowExecutionHelper.getProcessEngine().getTaskService();
		Task task = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();
		String days = (String) taskService.getVariable(task.getId(), "days");
		Date date = (Date) taskService.getVariable(task.getId(), "date");
		String reason = (String) taskService.getVariable(task.getId(), "reason");
		String userId = (String) taskService.getVariable(task.getId(), "userId");
		System.out.println("请假天数:  " + days);
		System.out.println("请假理由:  " + reason);
		System.out.println("请假人id:  " + userId);
		System.out.println("请假日期:  " + date.toString());
	}

    @Test
    public void testTaskVariable() { //mi_电子物料申请流程
        XFlowDefinition definition = PersistenceHelper.getPersistable(XFlowDefinition.class, 1317);
        List<XFlowUserTask> userTasks = XFlowRepositoryHelper.repository().findXFlowUserTask(definition);
        for (XFlowUserTask userTask : userTasks) {
            System.out.println(userTask + "      " + userTask.getNodeId());
            List<XFlowUserTask> userTaskx = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, userTask.getNodeId() + "x");
        }

    }

	@Test
	public void testBpmnModel() {
		XFlowDefinition definition = PersistenceHelper.getPersistable(XFlowDefinition.class, 1317);
		BpmnModel bpmnModel = XFlowDefinitionHelper.definition().generateBpmnModel(definition);
		System.out.println(bpmnModel);
	}
}
