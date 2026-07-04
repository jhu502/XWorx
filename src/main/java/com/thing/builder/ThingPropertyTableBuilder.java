package com.thing.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IThingModel;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.DetailIcon;
import com.thing.entity.ModeledEntity;
import com.thing.entity.XThingModel;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "blank", expander = true, width = "25"), //
				@UIColumn(field = "persistentImg", width = "25"), //
				@UIColumn(field = "readOnlyImg", width = "25"), //
				@UIColumn(field = "loggedImg", width = "25"), //
				@UIColumn(field = "name", width = "130", sortable = true), //
				@UIColumn(field = "baseType", width = "80"), //
				@UIColumn(field = "details", width = "25"), //
				@UIColumn(field = "description", width = "200"), //
				@UIColumn(field = "defaultValue", width = "150"), //
				@UIColumn(field = "value", width = "150"), //
				@UIColumn(field = "nullableImg", width = "20"),//
		}//
)
public class ThingPropertyTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Map<String, Object>> result = new ArrayList<>();

		XObject xObject = commandBean.getPrimaryObj();
		if (xObject instanceof ModeledEntity) {
			ModeledEntity entityThing = (ModeledEntity) xObject;

			IThingModel imodel = entityThing.getThingModel();
			if (imodel instanceof XThingModel) {
				XThingModel thingModel = (XThingModel) imodel;
				for (IPropertyDefinition propertyDef : thingModel.getPropertyDefinitions()) {
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("oid", propertyDef.getOid());
					row.put("persistentImg", propertyDef.getPersistentImg());
					row.put("readOnlyImg", propertyDef.getReadOnlyImg());
					row.put("loggedImg", propertyDef.getLoggedImg());
					row.put("name", propertyDef.getName());
					row.put("baseType", propertyDef.getBaseType());
					row.put("details", new DetailIcon(propertyDef));
					row.put("description", propertyDef.getDisplay());
					row.put("defaultValue", propertyDef.getDefaultValue());
					row.put("value", "");
					row.put("nullableImg", propertyDef.getNullableImg());
					row.put("expander", true);

					result.add(row);
				}
			}
		}

		return result;
	}

}
