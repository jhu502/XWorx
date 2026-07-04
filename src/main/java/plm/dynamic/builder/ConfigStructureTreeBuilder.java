package plm.dynamic.builder;

import com.flame.xui.XCommandBean;
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
import plm.part.XPart;
import plm.part.XPartUsageLink;
import plm.part.service.XPartServiceHelper;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "rowId", treeField = "identity", sortName = "identity", sortOrder = "asc", title = "结构", rowNumber = false, fit = true, contextMenu = "XPart:PSB-ContextMenu", //
		contexts = {
				@UIAction(name = "copy", winType = WinType.invoke, icon = "images/newpart.gif", processor = "xw.object.processor.CopyObjectProcessor"), //
				@UIAction(name = "paste", winType = WinType.invoke, icon = "images/paste.gif", processor = "xw.object.processor.PasteObjectProcessor"), //
				@UIAction(name = "newPart", winType = WinType.popup, icon = "images/newpart.gif", url = "thymeleaf/part/newPart.html", processor = "plm.part.processor.CreateXPartProcessor", style = "width:680px;height:580px;padding:5px;"), //
				@UIAction(name = "insertExistPart", winType = WinType.popup, icon = "images/part_add.gif", url = "thymeleaf/part/insertExistPart.html", processor = "plm.part.processor.InsertExistPartProcessor", style = "width:680px;height:480px;padding:5px;"), //
		},
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "identity", width = "200", align = "left", order = "asc", sortable = true), //
				@UIColumn(field = "number", width = "100", align = "left"), //
				@UIColumn(field = "quantity", width = "40", align = "center") //
		} //
)
public class ConfigStructureTreeBuilder extends AbstractTreeComponentBuilder {
	private static final String ROWID = "rowId";
	private static final String IDENTITY = "identity";
	private static final String QUANTITY = "quantity";

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart xpart = (XPart) commandBean.getPrimaryObj();
		if (xpart == null)
			return result;

		XThingModel thingModel = (XThingModel) xpart.getThingModel();
		TreeComponentNode rowNode = TreeComponentNode.newTreeComponentNode(xpart);
		rowNode.addAttribute(ROWID, xpart.getOid());
		rowNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(xpart.getName())));
		result.add(rowNode);

		List<?> list = XPartServiceHelper.repository().getUsedbyXPart(xpart);
		for (Object o : list) {
			Object[] objs = (Object[]) o;
			XPartUsageLink link = (XPartUsageLink) objs[0];
			XPart part = (XPart) objs[1];
			TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(part);
			childNode.addAttribute(ROWID, this.assemRowId(link.getOid(), part.getOid()));
			childNode.addAttribute(QUANTITY, link.getQuantity());
			childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(part.getName())));
			rowNode.addChildren(childNode);
		}
		return result;
	}

	@Override
	public List<TreeComponentNode> getNode(XCommandBean commandBean) {
		List<TreeComponentNode> result = new ArrayList<>();
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
				TreeComponentNode rowNode = TreeComponentNode.newTreeComponentNode(part);
				rowNode.addAttribute(ROWID, this.assemRowId(link.getOid(), part.getOid()));
				rowNode.addAttribute(QUANTITY, link.getQuantity());
				rowNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(part.getName())));
				result.add(rowNode);
			}
		}
		return result;
	}

}
