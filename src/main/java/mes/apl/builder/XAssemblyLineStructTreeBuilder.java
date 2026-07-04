package mes.apl.builder;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.ArrayComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.DetailIcon;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.orm.XObject;
import com.thing.entity.XThingModel;
import mes.apl.XAssemblyLine;
import mes.apl.XAssemblyLineUsageLink;
import mes.apl.XPLineServiceHelper;
import mes.equipt.XEquipment;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "rowId", treeField = "identity", sortName = "identity", sortOrder = "asc", contextMenu = "XPart:PLineEquipt-ContextMenu", rowNumber = false, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "identity", width = "200", align = "left", sortable = true), //
                @UIColumn(field = "details", width = "25"), //
                @UIColumn(field = "number", width = "100", align = "left") //
        } //
)
public class XAssemblyLineStructTreeBuilder extends AbstractTreeComponentBuilder {
    private static final String ROW_ID = "rowId";
    private static final String IDENTITY = "identity";
    private static final String DETAILS = "details";

    @Override
    public List<? extends Object> getRootNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        XObject xobject = commandBean.getPrimaryObj();
        if (xobject instanceof XAssemblyLine) {
            XAssemblyLine xprodLine = (XAssemblyLine) xobject;
            XThingModel thingModel = (XThingModel) xprodLine.getThingModel();
            TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(xprodLine);
            rootNode.addAttribute(ROW_ID, xprodLine.getOid());
            rootNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(xprodLine.getName())));
            HyperLink linkComp = new HyperLink();
            linkComp.setInnerObject(new IconBox("images/details.gif"));
            linkComp.setUrl(HREFactory.hashInfoPage(xprodLine));
            rootNode.addAttribute("details", linkComp);
            result.add(rootNode);

            List<?> list = XPLineServiceHelper.service().getUsedbyXPLine(xprodLine);
            XThingModel equiptModel = null;
            for (Object o : list) {
                Object[] objs = (Object[]) o;
                XAssemblyLineUsageLink link = (XAssemblyLineUsageLink) objs[0];
                XEquipment equipt = (XEquipment) objs[1];
                if (equiptModel == null) {
                    equiptModel = (XThingModel) equipt.getThingModel();
                }
                TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(equipt);
                childNode.setRowId(this.assemRowId(link.getOid(), equipt.getOid()));
                childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(equiptModel.getIcon()), new TextDisplay(equipt.getName())));
                childNode.addAttribute(DETAILS, new DetailIcon(equipt));
                rootNode.addChildren(childNode);
            }
        }

        return result;
    }

    @Override
    public List<? extends Object> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        return result;
    }
}
