package xw.flow.form;

import java.util.List;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import com.flame.xui.XCommandBean;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.RadioBox;
import com.flame.xui.widget.TextBox;
import com.flame.xui.XUIMeshGrid;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.bean.FlowRoute;
import xw.flow.entity.XWorkTask;

@UIMeshGrid(grids = {
        @UIGrid(provider = XWorkTask.class, rows = { //
                @UIRow(cells = { //
                        @UICell(label = "label_remarks", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "taskRemarks", name = "taskRemarks", traits = "editable:true,multiline:true", style = "width:350px;height:45px")//
                        }), //
                        @UICell(label = "label_routes", style = "margin-left:30px;", widget = { //
                                @UIWidget(type = WidgetType.RadioBox, id = "taskRoutes", name = "taskRoutes", style = "margin-left:8px")//
                        }), //
                }), //
        })
})
public class XFlowWorkitemRouteForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
        XWorkTask workItem = (XWorkTask) commandBean.getPrimaryObj();

        TaskInfo taskInfo = XFlowExecutionHelper.execution().getTaskInfo(workItem);
        if (taskInfo instanceof Task) {
            FlowElement flowElement = XFlowExecutionHelper.execution().getFlowElement(taskInfo);
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<FlowRoute> routeList = XFlowDefinitionHelper.definition().getFlowRoute(userTask);
                RadioBox taskRoutes = (RadioBox) formConfig.getXUIWidget("taskRoutes");
                for (FlowRoute route : routeList) {
                    taskRoutes.addRadio(route.getName(), route.getName());
                }
            }
        } else if (taskInfo instanceof HistoricTaskInstance) {
            FlowElement flowElement = XFlowExecutionHelper.execution().getFlowElement(taskInfo);
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<FlowRoute> routeList = XFlowDefinitionHelper.definition().getFlowRoute(userTask);
                TextBox taskRemarks = (TextBox) formConfig.getXUIWidget("taskRemarks");
                taskRemarks.setReadOnly(true);
                if (workItem.getRemarks() != null) {
                    taskRemarks.setValue(workItem.getRemarks());
                }
                RadioBox taskRoutes = (RadioBox) formConfig.getXUIWidget("taskRoutes");
                taskRoutes.setReadOnly(true);
                taskRoutes.setValue(workItem.getRoutes());
                for (FlowRoute route : routeList) {
                    taskRoutes.addRadio(route.getName(), route.getName());
                }
            }
        }

        return formConfig;
    }
}
