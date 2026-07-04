package com.thing.mesher.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetMode;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "partOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_number", required = true, widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "partNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_name", required = true, widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "partName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_description", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "partDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_endItem", required = true, widget = { //
								@UIWidget(type = WidgetType.ComboBox, id = "partEndItem", name = "endItem", traits = "panelHeight:'auto',editable:false,required:true", style = "height:25px; width:100px;")//
						}), //
				}) //
		})
})
public class EditModelEntityPropertyForm extends AbstractMeshComponentBuilder {
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
		formConfig.setWidgetMode(WidgetMode.Edit);
		XObject primary = (XObject) commandBean.getPrimaryObj();

		return formConfig;
	}

}
