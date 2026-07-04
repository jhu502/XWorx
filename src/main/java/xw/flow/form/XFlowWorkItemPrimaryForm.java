package xw.flow.form;

import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.orm.ObjectReference;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.xui.XUIMeshGrid;
import com.thing.entity.XThingModel;

import xw.flow.XFlowRepositoryHelper;
import xw.flow.entity.*;

@UIMeshGrid(grids = {
        @UIGrid(provider = XWorkTask.class, alignLabel = false, rows = { //
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
                        @UICell(label = "label_instructions", widget = { //
                                @UIWidget(type = WidgetType.TextDisplay, id = "instructions", name = "instructions", style = "height:45px")//
                        }),
                }), //
        })
})
public class XFlowWorkItemPrimaryForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
        XWorkTask workItem = (XWorkTask) commandBean.getPrimaryObj();
        XWorkInstance instance = workItem.getInstance();
        ObjectReference<?> primaryRef = instance.getBusinessRef();
        if (primaryRef != null) {
            IconBox primaryIcon = (IconBox) formConfig.getXUIWidget("primaryIcon");
            XObject primaryObj = (XObject) primaryRef.getObject();
            StringBuilder text = new StringBuilder();
            if (primaryObj instanceof IModelManaged) {
                IModelManaged typeManaged = (IModelManaged) primaryObj;
                XThingModel thingModel = (XThingModel) typeManaged.getThingModel();
                primaryIcon.setUrl(typeManaged.getIcon());
                text.append(thingModel.getName()).append(" - ");
            }

            text.append(primaryObj.getDisplay()).append(", ").append(primaryObj.getCreatedStamp());
            HyperLink primaryInfo = (HyperLink) formConfig.getXUIWidget("primaryInfo");
            primaryInfo.setUrl(HREFactory.hashInfoPage(primaryObj));
            primaryInfo.setText(text.toString());
        }

        HyperLink processInfo = (HyperLink) formConfig.getXUIWidget("processInfo");
        processInfo.setUrl(HREFactory.hashInfoPage(instance));
        processInfo.setText(instance.getName() + "," + instance.getCreatedStamp());

        TextDisplay instructions = (TextDisplay) formConfig.getXUIWidget("instructions");
        XFlowDefinition definition = instance.getDefinition();
        String nodeId = workItem.getActivity().getActivityId();
        List<XFlowUserTask> taskList = XFlowRepositoryHelper.repository().findXFlowUserTaskById(definition, nodeId);
        if (!taskList.isEmpty()) {
            XFlowUserTask xflowTask = taskList.get(0);
            instructions.setText(xflowTask.getInstructions());
        }

        return formConfig;
    }
}
