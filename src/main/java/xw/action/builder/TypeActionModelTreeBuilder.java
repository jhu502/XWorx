package xw.action.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.ArrayComponent;
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

@UITreeGrid(idField = "rowId", treeField = "identity", rowNumber = false, fit = true, pagination = true,//
        columns = { //
                @UIColumn(field = "oid", hidden = true), //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "blank", width = "20"), //
                @UIColumn(field = "identity", width = "200"), //
                @UIColumn(field = "name", width = "120"), //
                @UIColumn(field = "type", width = "150"), //
                @UIColumn(field = "processor", width = "200"), //
                @UIColumn(field = "supported_type", width = "120"), //
                @UIColumn(field = "url", width = "200"), //
                @UIColumn(field = "winType", width = "80"), //
                @UIColumn(field = "sortNo", width = "80"), //
                @UIColumn(field = "createdStamp", width = "120"), //
                @UIColumn(field = "modifiedStamp", width = "120"), //
        } //
)
public class TypeActionModelTreeBuilder extends AbstractTreeComponentBuilder {
    @Override
    public List<?> getRootNode(XCommandBean commandBean) {
        List<TreeComponentNode> result = new ArrayList<>();
        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XThingModel) {
            XThingModel thingModel = (XThingModel) primary;
            List<XActionModel> actionModels = XActionServiceHelper.repository().queryXActionModels(thingModel.getModelKey());
            for (XActionModel actionModel : actionModels) {
                TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(actionModel, actionModel.getOid());
                IconBox iconBox = new IconBox(actionModel.getIcon());
                iconBox.setStyle("width:16px;height:16px;");
                rootNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(actionModel.getName())));
                rootNode.addAttribute("sortNo", "");
                result.add(rootNode);
            }
        }
        return result;
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
