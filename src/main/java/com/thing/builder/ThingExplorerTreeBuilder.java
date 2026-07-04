package com.thing.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.ArrayComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.thing.entity.XThingModel;

@UITreeGrid(idField = "oid", treeField = "name", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "name", width = "250", align = "left") //
		} //
)
public class ThingExplorerTreeBuilder extends AbstractTreeComponentBuilder {

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		for (IThingModel rootModel : ThingModelHelper.manager().getRootModel()) {
			TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(rootModel);
			rootNode.addAttribute("name", new ArrayComponent(new IconBox(rootModel.getIcon()), new TextDisplay(rootModel.getName())));
			result.add(rootNode);

			List<IThingModel> list = ThingModelHelper.manager().getChildModel(rootModel);
			for (IThingModel model : list) {
				TreeComponentNode node = TreeComponentNode.newTreeComponentNode(model);
				node.addAttribute("name", new ArrayComponent(new IconBox(model.getIcon()), new TextDisplay(model.getName())));
				rootNode.addChildren(node);
			}
		}

		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
	    XUIRowId uiRowId = commandBean.getRowId();
		XThingModel parentModel = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(uiRowId.getValue()));

		List<Object> result = new ArrayList<>();
		List<IThingModel> list = ThingModelHelper.manager().getChildModel(parentModel);
		for (IThingModel model : list) {
			TreeComponentNode node = TreeComponentNode.newTreeComponentNode(model);
			node.addAttribute("name", new ArrayComponent(new IconBox(model.getIcon()), new TextDisplay(model.getName())));
			result.add(node);
		}
		return result;
	}

}
