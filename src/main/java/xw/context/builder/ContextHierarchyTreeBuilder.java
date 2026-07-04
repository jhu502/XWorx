package xw.context.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.ArrayComponent;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.localize.LocalizationHelper;
import xw.context.ContextHelper;
import xw.context.entity.XLibrary;
import xw.context.entity.XOrganization;
import xw.context.entity.XSite;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "oid", treeField = "identity", rowNumber = false, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "oid", hidden = true), //
                @UIColumn(field = "identity", width = "200px"), //
                @UIColumn(field = "context", width = "80px") //
        } //
)
public class ContextHierarchyTreeBuilder extends AbstractTreeComponentBuilder {
    @Override
    public List<? extends Object> getRootNode(XCommandBean commandBean) {
        List<TreeComponentNode> result = new ArrayList<>();

        XSite xsite = ContextHelper.repository().getXSite();
        TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(xsite, xsite.getOid());
        IconBox iconBox = new IconBox("images/sitemgmt.png");
        iconBox.setStyle("width:16px;height:16px;");
        rootNode.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(xsite.getName())));
        rootNode.addAttribute("context", xsite.getThingModel().getDisplay());
        result.add(rootNode);
        List<XOrganization> orgList = ContextHelper.repository().getXOrganization(xsite);
        for (XOrganization xorg : orgList) {
            TreeComponentNode node = TreeComponentNode.newTreeComponentNode(xorg, xorg.getOid());
            IconBox _iconBox = new IconBox("images/navorg.png");
            _iconBox.setStyle("width:16px;height:16px;");
            node.addAttribute("identity", new ArrayComponent(_iconBox, new TextDisplay(xorg.getName())));
            node.addAttribute("context", xorg.getThingModel().getDisplay());
            rootNode.addChildren(node);
        }
        return result;
    }

    @Override
    public List<? extends Object> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        Object rowObject = commandBean.getRowObject();
        if (rowObject instanceof XSite) {
            XSite xsite = (XSite) rowObject;
            List<XOrganization> orgList = ContextHelper.repository().getXOrganization(xsite);
            for (XOrganization xorg : orgList) {
                TreeComponentNode node = TreeComponentNode.newTreeComponentNode(xorg, xorg.getOid());
                IconBox iconBox = new IconBox("images/navorg.png");
                iconBox.setStyle("width:16px;height:16px;");
                node.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(xorg.getName())));
                node.addAttribute("context", xorg.getThingModel().getDisplay());
                result.add(node);
            }
        } else if (rowObject instanceof XOrganization) {
            XOrganization xOrgan = (XOrganization) rowObject;
            List<XLibrary> libList = ContextHelper.repository().listLibrary(xOrgan);
            for (XLibrary xLib : libList) {
                TreeComponentNode node = TreeComponentNode.newTreeComponentNode(xLib, xLib.getOid());
                IconBox iconBox = new IconBox(xLib.getThingModel().getIcon());
                iconBox.setStyle("width:16px;height:16px;");
                node.addAttribute("identity", new ArrayComponent(iconBox, new TextDisplay(xLib.getName())));
                node.addAttribute("context", xLib.getLibraryType().getDisplay(LocalizationHelper.getLocale()));
                result.add(node);
            }
        }

        return result;
    }
}
