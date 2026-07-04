package mes.equipt.form;

import com.flame.annotations.*;
import mes.equipt.XEquiptParameter;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.ComboBox;
import com.flame.xui.widget.Hidden;
import com.flame.xui.widget.TextBox;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIMeshGrid;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;

@UIMeshGrid(grids = {
		@UIGrid(provider = XEquiptParameter.class, rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "equiptOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*参数：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "equiptNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*名称：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "equiptName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*类型：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.ComboBox, id = "baseType", name = "baseType", traits = "panelHeight:'auto',editable:false,required:true", style = "width:140px;height:25px;")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "描述：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "equiptDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "值：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "equiptValue", name = "value", traits = "editable:true", style = "width:350px;height:24px")//
						}), //
				})
		})
})
public class XEquiptParameterForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		XObject primary = commandBean.getPrimaryObj();
		String model = commandBean.getTextParameter("model");
		if (primary instanceof XEquiptParameter) {
			XEquiptParameter eParam = (XEquiptParameter) primary;
			Hidden hiddenBox = (Hidden) formConfig.getXUIWidget("equiptOid");
			hiddenBox.setValue(commandBean.getPrimaryOid());
			TextBox numberBox = (TextBox) formConfig.getXUIWidget("equiptNumber");
			numberBox.addTrait(EDITABLE, false);
			numberBox.setValue(eParam.getNumber());
			ComboBox baseTypeBox = (ComboBox) formConfig.getXUIWidget("baseType");
			for (XBaseType baseType : XBaseType.parameterTypes()) {
				baseTypeBox.addOption(baseType.name(), baseType.getDisplay());
			}
			baseTypeBox.setValue(eParam.getBaseType().name());
			baseTypeBox.addTrait(EDITABLE, false);
			baseTypeBox.addTrait(DISABLED, true);
			if (EDIT.equals(model)) {
				TextBox nameBox = (TextBox) formConfig.getXUIWidget("equiptName");
				nameBox.setValue(eParam.getName());
				TextBox descriptionBox = (TextBox) formConfig.getXUIWidget("equiptDescription");
				descriptionBox.setValue(eParam.getDescription());
				TextBox valueBox = (TextBox) formConfig.getXUIWidget("equiptValue");
				valueBox.setValue(eParam.getValue());
			} else if (VALUE.equals(model)) {
				TextBox nameBox = (TextBox) formConfig.getXUIWidget("equiptName");
				nameBox.setValue(eParam.getName());
				nameBox.addTrait(EDITABLE, false);
				TextBox descriptionBox = (TextBox) formConfig.getXUIWidget("equiptDescription");
				descriptionBox.setValue(eParam.getDescription());
				descriptionBox.addTrait(EDITABLE, false);
				TextBox valueBox = (TextBox) formConfig.getXUIWidget("equiptValue");
				valueBox.setValue(eParam.getValue());
			}
		} else {
			if (CREATE.equals(model)) {
				ComboBox baseTypeBox = (ComboBox) formConfig.getXUIWidget("baseType");
				for (XBaseType baseType : XBaseType.parameterTypes()) {
					baseTypeBox.addOption(baseType.name(), baseType.getDisplay());
				}
			}
		}

		return formConfig;
	}
}
