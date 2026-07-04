package com.thing.builder;

import java.util.ArrayList;
import java.util.Collections;
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

@UITreeGrid(idField = "oid", treeField = "display", contextMenu = "XThingModel:ModelTree_RightMenu", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "display", width = "300", align = "left") //
		} //
)
public class ThingModelTreeBuilder extends AbstractTreeComponentBuilder {
	private static final String STYLE = "width:16px;height:16px";

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<TreeComponentNode> result = new ArrayList<>();

		List<IThingModel> roots = ThingModelHelper.manager().getRootModel();
		Collections.sort(roots);
		for (IThingModel root : roots) {
			TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(root);
			rootNode.addAttribute("display", new ArrayComponent(new IconBox(root.getIcon(), STYLE), new TextDisplay(root.getLocalDisplay())));
			if (XThingModel.class.getSimpleName().equals(root.getModelKey())) {
				result.add(0, rootNode);
			} else {
				result.add(rootNode);
			}

			List<IThingModel> models = ThingModelHelper.manager().getChildModel(root);
			Collections.sort(models);
			for (IThingModel model : models) {
				TreeComponentNode node = TreeComponentNode.newTreeComponentNode(model);
				node.addAttribute("display", new ArrayComponent(new IconBox(model.getIcon(), STYLE), new TextDisplay(model.getLocalDisplay())));
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
		List<IThingModel> models = ThingModelHelper.manager().getChildModel(parentModel);
		Collections.sort(models);
		for (IThingModel model : models) {
			TreeComponentNode node = TreeComponentNode.newTreeComponentNode(model);
			node.addAttribute("display", new ArrayComponent(new IconBox(model.getIcon(), STYLE), new TextDisplay(model.getLocalDisplay())));
			result.add(node);
		}
		return result;
	}

}
