package com.thing.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.flame.xui.XCommandBean;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.DetailIcon;
import com.thing.entity.XPropertyDefinition;
import com.thing.entity.XThingModel;

@UIDataGrid(idField = "oid", fit = true, //
		actions = {
				@UIAction(name = "newProperty", url = "thymeleaf/thing/model/newProperty.html", icon = "images/add16x16.gif", winType = WinType.popup, style = "width:1000px;height:338px;padding:5px;"), //
				@UIAction(name = "delete", processor = "com.thing.processor.RemoveXPropertyDefProcessor", icon = "images/delete.png", beforeJS = "return flame.validateSelect(p, true);", winType = WinType.invoke), //
		},
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "persistentImg", width = "25"), //
				@UIColumn(field = "readOnlyImg", width = "25"), //
				@UIColumn(field = "loggedImg", width = "25"), //
				@UIColumn(field = "nullableImg", width = "24"), //
				@UIColumn(field = "localDisplay", width = "150", sortable = true), //
				@UIColumn(field = "name", width = "130", sortable = true), //
				@UIColumn(field = "baseType", width = "80"), //
				@UIColumn(field = "details", width = "25"), //
				@UIColumn(field = "description", width = "200"), //
				@UIColumn(field = "defaultValue", width = "150"), //
				@UIColumn(field = "value", width = "150"), //
				@UIColumn(field = "createdStamp", width = "130"), //
				@UIColumn(field = "modifiedStamp", width = "130") //
		} //
)
public class ThingModelPropertyBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<TableComponentRow> result = new ArrayList<>();

		XObject xobj = commandBean.getPrimaryObj();
		if (xobj instanceof XThingModel) {
			XThingModel thingModel = (XThingModel) xobj;

			Map<String, IPropertyDefinition> propertyMap = new TreeMap<>();
			List<IPropertyDefinition> list = ThingModelHelper.manager().getPropertyDefinition(thingModel);
			for (IPropertyDefinition definition : list) {
				propertyMap.put(definition.getName(), definition);
				TableComponentRow tableRow = TableComponentRow.newInstance(definition);
				DetailIcon detailIcon = new DetailIcon(definition);
				detailIcon.addEvent(DetailIcon.ON_CLICK, "openPropertyDefLayout(event, '" + definition.getOid() + "')");
				tableRow.addAttribute("details", detailIcon);
				result.add(tableRow);
			}
			Map<String, Field> fieldMap = thingModel.getNativeFields();
			for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
				String source = entry.getKey();
				Field field = entry.getValue();
				IPropertyDefinition definition = propertyMap.get(field.getName());
				if (definition == null) {
					definition = XPropertyDefinition.newPropertyDefinition(thingModel, field);
					definition.setSource(source);
					definition = PersistenceHelper.service().save(definition);
					propertyMap.put(definition.getName(), definition);

					TableComponentRow tableRow = TableComponentRow.newInstance(definition);
					DetailIcon detailIcon = new DetailIcon(definition);
					detailIcon.addEvent(DetailIcon.ON_CLICK, "openPropertyDefLayout(event, '" + definition.getOid() + "')");
					tableRow.addAttribute("details", detailIcon);
					result.add(tableRow);
				}
			}
		}
		return result;
	}

}
