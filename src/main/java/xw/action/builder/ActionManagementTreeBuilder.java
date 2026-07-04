package xw.action.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.ArrayComponent;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.thing.entity.XThingModel;

import xw.action.entity.AbstractAction;
import xw.action.entity.XActionItem;
import xw.action.entity.XActionItemLink;
import xw.action.entity.XActionModel;
import xw.action.service.XActionServiceHelper;

@UITreeGrid(idField = "oid", treeField = "identity", rowNumber = false, fit = true, //
        actions = {
                @UIAction(name = "delete", processor = "xw.action.processor.DeleteActionObjectProcessor", icon = "images/delete.png", beforeJS = "return xworx.validateSelect(p, true);", winType = WinType.invoke), //
        },
        columns = { //
                @UIColumn(field = "oid", checkbox = true), //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "blank", width = "20"), //
                @UIColumn(field = "identity", width = "200"), //
                @UIColumn(field = "name", width = "150"), //
                @UIColumn(field = "display", width = "150"), //
                @UIColumn(field = "type", width = "150"), //
                @UIColumn(field = "processor", width = "200"), //
                @UIColumn(field = "supported_type", width = "120"), //
                @UIColumn(field = "url", title = "URL", width = "200"), //
                @UIColumn(field = "winType", width = "80"), //
                @UIColumn(field = "sortNo", width = "80"), //
                @UIColumn(field = "createdStamp", width = "125"), //
                @UIColumn(field = "modifiedStamp", width = "125"), //
        } //
)
public class ActionManagementTreeBuilder extends AbstractTreeComponentBuilder {
    @Override
    public List<?> getRootNode(XCommandBean commandBean) {
        List<TreeComponentNode> rootNodeList = new ArrayList<>();
        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XThingModel) {
            XThingModel thingModel = (XThingModel) primary;
            List<XActionModel> actionModels = XActionServiceHelper.repository().queryXActionModels(thingModel.getModelKey());
            for (XActionModel actionModel : actionModels) {
                TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(actionModel, actionModel.getOid());
                IconBox iconBox = new IconBox(actionModel.getIcon());
                iconBox.setStyle("width:16px;height:16px;");
                rootNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(actionModel.getActionKey())));
                rootNode.addAttribute("sortNo", "");
                rootNodeList.add(rootNode);
            }
        } else {
            String name = commandBean.getTextParameter("name");
            String display = commandBean.getTextParameter("display");
            if (StringUtils.isBlank(name) && StringUtils.isBlank(display))
                return rootNodeList;

            name = StringUtils.isBlank(name) ? "%" : "%" + name + "%";
            display = StringUtils.isBlank(display) ? "%" : "%" + display + "%";
            List<XActionModel> actionModels = XActionServiceHelper.repository().findXActionModels(name, display);
            for (XActionModel actionModel : actionModels) {
                TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(actionModel, actionModel.getOid());
                IconBox iconBox = new IconBox(actionModel.getIcon());
                iconBox.setStyle("width:16px;height:16px;");
                rootNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(actionModel.getActionKey())));
                rootNode.addAttribute("sortNo", "");
                rootNodeList.add(rootNode);
            }

            List<XActionItem> actionItems = XActionServiceHelper.repository().findXActionItems(name, display);
            for (XActionItem actionItem : actionItems) {
                TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(actionItem, actionItem.getOid());
                IconBox iconBox = new IconBox(actionItem.getIcon());
                iconBox.setStyle("width:16px;height:16px;");
                rootNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(actionItem.getActionKey())));
                rootNode.addAttribute("sortNo", "");
                rootNode.setLeaf(true);
                rootNodeList.add(rootNode);
            }
        }
        return rootNodeList;
    }

    @Override
    public List<?> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        Object rowObject = commandBean.getRowObject();
        if (rowObject instanceof XActionModel) {
            XActionModel actionModel = (XActionModel) rowObject;
            List<Object[]> itemAndLinks = XActionServiceHelper.service().getActionAndLink(actionModel);
            for (Object[] objects : itemAndLinks) {
                AbstractAction absAction = (AbstractAction) objects[0];
                XActionItemLink itemLink = (XActionItemLink) objects[1];
                if (absAction instanceof XActionModel) {
                    XActionModel model = (XActionModel) absAction;
                    TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(model, this.assemRowId(model.getOid(), itemLink.getOid()));
                    IconBox iconBox = new IconBox(model.getIcon());
                    iconBox.setStyle("width:16px;height:16px;");
                    childNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(model.getName())));
                    childNode.addAttribute("sortNo", itemLink.getSort());
                    childNode.setLeaf(false);
                    result.add(childNode);
                } else if (absAction instanceof XActionItem) {
                    XActionItem action = (XActionItem) absAction;
                    TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(action, this.assemRowId(action.getOid(), itemLink.getOid()));
                    IconBox iconBox = new IconBox(action.getIcon());
                    iconBox.setStyle("width:16px;height:16px;");
                    childNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(action.getName())));
                    childNode.addAttribute("sortNo", itemLink.getSort());
                    childNode.setLeaf(true);
                    result.add(childNode);
                }
            }
        }

        return result;
    }
}