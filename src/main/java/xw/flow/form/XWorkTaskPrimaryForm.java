package xw.flow.form;

import java.util.List;

import com.flame.annotations.*;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.xui.HREFactory;
import com.flame.xui.widget.*;
import com.thing.entity.XThingModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import com.flame.xui.XCommandBean;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.FlowRoute;
import xw.flow.entity.XFlowDefinition;
import xw.flow.entity.XFlowUserTask;
import xw.flow.entity.XWorkInstance;
import xw.flow.entity.XWorkTask;

@UIMeshGrid(grids = {
        @UIGrid(provider = XWorkTask.class, rows = { //
                @UIRow(cells = { //
                        @UICell(label = "label_routes", widget = { //
                                @UIWidget(type = WidgetType.RadioBox, id = "taskRoutes", name = "taskRoutes", style = "margin-left:8px")//
                        }), //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Button, id = "completeTask", name = "taskRoutes", text = "Complete Task", style="margin-left:200px;", events = { //
                                        @UIEvent(name = "onclick", value = "flame.submitForm(this, 'xw.flow.processor.CompleteWorkTaskProcessor', completeCallback, (typeof(taskComplete)==='function')?taskComplete:undefined);") //
                                })//
                        }) //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_remarks", colspan = 2, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "taskRemarks", name = "taskRemarks", traits = "editable:true,multiline:true", style = "height:50px;width:80%;")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_subject", widget = { //
                                @UIWidget(type = WidgetType.IconBox, id = "primaryIcon", name = "primaryIcon", style = ""),//
                                @UIWidget(type = WidgetType.HyperLink, id = "primaryInfo", name = "primaryInfo", style = "height:45px;margin-right:30px;")//
                        }),
                        @UICell(label = "label_process", widget = { //
                                @UIWidget(type = WidgetType.HyperLink, id = "processInfo", name = "processInfo", style = "height:45px")//
                        }),
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_instructions", colspan = 2, widget = { //
                                @UIWidget(type = WidgetType.TextDisplay, id = "instructions", name = "instructions", style = "height:45px")//
                        }),
                }), //
        })
})
public class XWorkTaskPrimaryForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
        XWorkTask workTask = (XWorkTask) commandBean.getPrimaryObj();

        XWorkInstance instance = workTask.getInstance();
        XObject businessObj = instance.getBusinessObject();
        if (businessObj != null) {
            IconBox primaryIcon = (IconBox) formConfig.getXUIWidget("primaryIcon");
            StringBuilder builder = new StringBuilder();
            if (businessObj instanceof IModelManaged) {
                IModelManaged typeManaged = (IModelManaged) businessObj;
                XThingModel thingModel = (XThingModel) typeManaged.getThingModel();
                primaryIcon.setUrl(typeManaged.getIcon());
                builder.append(thingModel.getName()).append(" - ");
            }

            builder.append(businessObj.getDisplay()).append(", ").append(businessObj.getCreatedStamp());
            HyperLink primaryInfo = (HyperLink) formConfig.getXUIWidget("primaryInfo");
            primaryInfo.setUrl(HREFactory.hashInfoPage(businessObj));
            primaryInfo.setText(builder.toString());
        }

        HyperLink processInfo = (HyperLink) formConfig.getXUIWidget("processInfo");
        processInfo.setUrl(HREFactory.hashInfoPage(instance));
        processInfo.setText(instance.getName() + "," + instance.getCreatedStamp());

        TextDisplay instructions = (TextDisplay) formConfig.getXUIWidget("instructions");
        XFlowDefinition definition = instance.getDefinition();
        String nodeId = workTask.getActivity().getActivityId();
        List<XFlowUserTask> taskList = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, nodeId);
        if (!taskList.isEmpty()) {
            XFlowUserTask xflowTask = taskList.get(0);
            instructions.setText(xflowTask.getInstructions());
        }

        TaskInfo taskInfo = XFlowExecutionHelper.execution().getTaskInfo(workTask);
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
                if (workTask.getRemarks() != null) {
                    taskRemarks.setValue(workTask.getRemarks());
                }
                RadioBox taskRoutes = (RadioBox) formConfig.getXUIWidget("taskRoutes");
                taskRoutes.setReadOnly(true);
                taskRoutes.setValue(workTask.getRoutes());
                for (FlowRoute route : routeList) {
                    taskRoutes.addRadio(route.getName(), route.getName());
                }
            }
        }

        Button completeTask = (Button) formConfig.getXUIWidget("completeTask");
        completeTask.setText(LocalizationHelper.get("completeTask"));

        return formConfig;
    }
}
