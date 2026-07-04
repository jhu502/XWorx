package com.thing.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.Argument;
import com.flame.type.XBaseType;
import com.flame.xui.GridComponent;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractComponentBuilder;
import com.flame.xui.XUIDataGrid;
import com.flame.xui.widget.GridColumn;
import com.thing.entity.XServiceDefinition;

@UIDataGrid(idField = "oid", toolbar = "#inputOutput-tb", singleSelect = true, rowNumber = false, pagination = false, fit = true,//
		columns = { //
				@UIColumn(field = "name", width = "150", widget = @UIWidget(type = WidgetType.TextBox, traits = "required:true")), //
				@UIColumn(field = "type", width = "80", widget = @UIWidget(type = WidgetType.ComboBox)), //
				@UIColumn(field = "description", width = "250", widget = @UIWidget(type = WidgetType.TextBox)) //
		} //
)
public class InputParameterBuilder extends AbstractComponentBuilder {
	@Override
	public XUIComponent buildComponentConfig(XCommandBean commandBean) {
		GridComponent resultConfig = (GridComponent) super.buildComponentConfig(commandBean);
		if (resultConfig instanceof XUIDataGrid) {
			XUIDataGrid datagrid = (XUIDataGrid) resultConfig;
			for (List<GridColumn> list : datagrid.getColumns()) {
				for (GridColumn gridColumn : list) {
					if ("type".equals(gridColumn.getField())) {
						List<Map<String, String>> baseTypes = new ArrayList<>();
						for (XBaseType baseType : XBaseType.parameterTypes()) {
							Map<String, String> row = new HashMap<>();
							row.put("type", baseType.name());
							row.put("display", baseType.getDisplay());
							baseTypes.add(row);
						}
						gridColumn.getEditor().addTrait("valueField", "type");
						gridColumn.getEditor().addTrait("textField", "display");
						gridColumn.getEditor().addTrait("required", true);
						gridColumn.getEditor().addTrait("data", baseTypes);
						gridColumn.getEditor().addTrait("panelHeight", "auto");
						break;
					}
				}
			}
		}

		return resultConfig;
	}

	@Override
	public Object buildComponentData(XCommandBean commandBean) {
		List<Map<String, Object>> result = new ArrayList<>();
		XObject xobj = commandBean.getPrimaryObj();

		if (xobj instanceof XServiceDefinition) {
			XServiceDefinition serviceDef = (XServiceDefinition) xobj;
			for (Argument argument : serviceDef.getArguments()) {
				Map<String, Object> row = new HashMap<>();
				row.put("name", argument.getName());
				row.put("type", argument.getBaseType());
				row.put("description", argument.getDescription());
				result.add(row);
			}
		}

		return result;
	}
}
