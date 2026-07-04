package plm.dynamic.form;

import com.flame.annotations.*;
import plm.dynamic.XExpression;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.TextBox;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIMeshGrid;
import com.flame.orm.XObject;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "expressOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_number", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "expressNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_name", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "expressName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_description", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "expressDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:40px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_expression", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "expressSentence", name = "sentence", traits = "editable:true,multiline:true", style = "width:500px;height:200px")//
						}), //
				})//
		})
})
public class ExpressionPropertyForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		XObject primary = commandBean.getPrimaryObj();

		if (primary instanceof XExpression) {
			this.editForm((XExpression) primary, formConfig);
		} else {
			this.createForm(formConfig);
		}

		return formConfig;
	}

	private void editForm(XExpression express, XUIMeshGrid formConfig) {
		TextBox numberBox = (TextBox) formConfig.getXUIWidget("expressNumber");
		numberBox.addTrait("editable", false);
		numberBox.setValue(express.getNumber());
		TextBox nameBox = (TextBox) formConfig.getXUIWidget("expressName");
		nameBox.setValue(express.getName());
		TextBox descriptionBox = (TextBox) formConfig.getXUIWidget("expressDescription");
		descriptionBox.setValue(express.getDescription());
		TextBox expressSentence = (TextBox) formConfig.getXUIWidget("expressSentence");
		expressSentence.setValue(express.getExpression());
	}

	private void createForm(XUIMeshGrid formConfig) {
	}

}
