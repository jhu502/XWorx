package xw.flow.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.util.XException;
import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.entity.XWorkTask;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.TaskInfo;

import java.util.List;

public class CompleteWorkTaskProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XWorkTask workItem = (XWorkTask) commandBean.getPrimaryObj();
        if (workItem == null)
            throw new XException("Primary Object is null.");

        Object taskRoutes = commandBean.getParameter("taskRoutes");
        String taskRemarks = commandBean.getTextParameter("taskRemarks");

        TaskInfo taskInfo = XFlowExecutionHelper.execution().getTaskInfo(workItem);
        FlowElement flowElement = XFlowExecutionHelper.execution().getFlowElement(taskInfo);
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            List<String> routeList = XFlowDefinitionHelper.getRoutes(userTask);
            if (routeList != null && !routeList.isEmpty()) {
                if (taskRoutes == null) {
                    throw new XException("请选择路由!");
                }
            }
        }

        workItem = XFlowExecutionHelper.execution().completeXWorkItem(workItem, taskRoutes, taskRemarks);

        formResult.setStatus(FormStatus.SUCCESS);
        formResult.setData(workItem);

        return formResult;
    }
}
