package plm.part.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.ArrayComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.DetailIcon;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.orm.XObject;
import com.thing.entity.XThingModel;
import plm.part.XPart;
import plm.part.XPartUsageLink;
import plm.part.service.XPartServiceHelper;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "rowId", treeField = "identity", sortName = "identity", sortOrder = "asc", contextMenu = "XPart:PSB-ContextMenu", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "identity", width = "200", align = "left", sortable = true), //
				@UIColumn(field = "details", width = "25"), //
				@UIColumn(field = "number", width = "100", align = "left"), //
				@UIColumn(field = "quantity", width = "40", align = "center") //
		} //
)
public class ProductStructureTreeBuilder extends AbstractTreeComponentBuilder {
	private static final String IDENTITY = "identity";
	private static final String QUANTITY = "quantity";
	private static final String DETAILS = "details";

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XObject xobject = commandBean.getPrimaryObj();
		if (xobject instanceof XPart) {
			XPart xpart = (XPart) xobject;
			XThingModel thingModel = (XThingModel) xpart.getThingModel();
			TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(xpart);
			rootNode.setRowId(xpart.getOid());
			rootNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(xpart.getName())));
			rootNode.addAttribute(DETAILS, new DetailIcon(xpart));
			result.add(rootNode);

			List<?> list = XPartServiceHelper.repository().getUsedbyXPart(xpart);
			for (Object o : list) {
				Object[] objs = (Object[]) o;
				XPartUsageLink link = (XPartUsageLink) objs[0];
				XPart part = (XPart) objs[1];
				TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(part);
				childNode.setRowId(this.assemRowId(link.getOid(), part.getOid()));
				childNode.addAttribute(QUANTITY, link.getQuantity());
				childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(part.getName())));
				childNode.addAttribute(DETAILS, new DetailIcon(part));
				rootNode.addChildren(childNode);
			}
		}
		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		List<Object> rowObjs = commandBean.getRowObjects();
		for (Object rowObject : rowObjs) {
			XPart xpart = (XPart) rowObject;
			if (xpart == null)
				return result;

			List<?> list = XPartServiceHelper.repository().getUsedbyXPart(xpart);
			for (Object o : list) {
				Object[] objs = (Object[]) o;
				XPartUsageLink link = (XPartUsageLink) objs[0];
				XPart part = (XPart) objs[1];
				XThingModel thingModel = (XThingModel) xpart.getThingModel();
				TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(part);
				childNode.setRowId(this.assemRowId(link.getOid(), part.getOid()));
				childNode.addAttribute(QUANTITY, link.getQuantity());
				childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(part.getName())));
				childNode.addAttribute(DETAILS, new DetailIcon(part));
				result.add(childNode);
			}
		}
		return result;
	}

}
