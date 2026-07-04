package com.thing.form;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.ComboBox;
import com.flame.xui.widget.TextBox;
import com.flame.xui.XUIMeshGrid;
import com.thing.entity.XServiceDefinition;

@UIMeshGrid(grids = {
		@UIGrid(provider = XServiceDefinition.class, rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "targetOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*名称：", style = "width:70px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "serviceName", name = "name", traits = "editable:true,required:true", style = "width:240px;height:25px;")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "描述：", style = "width:70px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "serviceDescription", name = "description", traits = "editable:true,multiline:true", style = "width:240px;height:70px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*服务类型：", style = "width:70px", widget = { //
								@UIWidget(type = WidgetType.ComboBox, id = "serviceType", name = "serviceType", traits = "panelHeight:'auto',editable:false,required:true", style = "width:140px;height:25px;")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*返回类型：", style = "width:70px", widget = { //
								@UIWidget(type = WidgetType.ComboBox, id = "resultType", name = "resultType", traits = "panelHeight:'auto',editable:false,required:true", style = "width:140px;height:25px;")//
						}), //
				}) //
		})
})
public class ServicePropertyDefForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		XObject primary = commandBean.getPrimaryObj();

		ComboBox serviceBox = (ComboBox) formConfig.getXUIWidget("serviceType");
		serviceBox.addTrait("editable", false);
		if (primary instanceof XServiceDefinition) {
			XServiceDefinition serviceDef = (XServiceDefinition) primary;
			TextBox nameBox = (TextBox) formConfig.getXUIWidget("serviceName");
			nameBox.setValue(serviceDef.getName());

			TextBox descriptionBox = (TextBox) formConfig.getXUIWidget("serviceDescription");
			descriptionBox.setValue(serviceDef.getDescription());

			for (XServiceType serviceType : XServiceType.values()) {
				serviceBox.addOption(serviceType.name(), serviceType.getName());
			}

			ComboBox resultBox = (ComboBox) formConfig.getXUIWidget("resultType");
			for (XBaseType baseType : XBaseType.parameterTypes()) {
				resultBox.addOption(baseType.name(), baseType.getDisplay());
			}
		} else {
			for (XServiceType serviceType : XServiceType.values()) {
				serviceBox.addOption(serviceType.name(), serviceType.getName());
			}

			ComboBox resultBox = (ComboBox) formConfig.getXUIWidget("resultType");
			resultBox.addTrait("editable", false);
			for (XBaseType baseType : XBaseType.parameterTypes()) {
				resultBox.addOption(baseType.name(), baseType.getDisplay());
			}
		}

		return formConfig;
	}
}
