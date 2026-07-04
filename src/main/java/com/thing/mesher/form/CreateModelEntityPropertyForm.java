package com.thing.mesher.form;

import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.util.FlameUtils;
import com.flame.xui.WidgetMode;
import com.flame.annotations.UIMeshGrid;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;
import com.flame.xui.widget.Hidden;
import com.thing.entity.MeshLayout;
import com.thing.entity.XPropertyLayout;

@UIMeshGrid()
public class CreateModelEntityPropertyForm extends AbstractMeshComponentBuilder {
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid xuiMeshGrid = super.buildComponentConfig(commandBean);
		xuiMeshGrid.setWidgetMode(WidgetMode.Create);
		xuiMeshGrid.addHidden(new Hidden("widgetMode", "widgetMode", WidgetMode.Create.name()));

		String entityType = commandBean.getEntityType();
		xuiMeshGrid.addHidden(new Hidden("entityType", "entityType", entityType));

		if (FlameUtils.isNotBlank(entityType)) {
			IThingModel thingModel = ThingModelHelper.manager().getThingModel(entityType);
			if (thingModel == null) {
				return xuiMeshGrid;
			}
			xuiMeshGrid.addHidden(new Hidden("modelType", "modelType", thingModel.getOid()));

			List<IPropertyLayout> layoutList = ThingModelHelper.manager().getPropertyLayout(thingModel);
			if (layoutList.isEmpty()) {
				return xuiMeshGrid;
			}

			IPropertyLayout propertyLayout = layoutList.get(0);
			if (propertyLayout instanceof XPropertyLayout) {
				MeshLayout meshLayout = ((XPropertyLayout) propertyLayout).getLayout();
				if (meshLayout != null) {
					for (MeshLayout.MeshGrid mGrid : meshLayout.getGrids()) {
						XUIMeshGrid.XUIGrid xuiGrid = new XUIMeshGrid.XUIGrid(mGrid, thingModel, xuiMeshGrid.getWidgetMode());
						xuiMeshGrid.addGrid(xuiGrid);
					}
				}
			}
		}

		return xuiMeshGrid;
	}
}
