package com.thing.builder;

import com.flame.xui.XCommandBean;
import com.flame.orm.AbstractEntity;
import com.flame.orm.XObject;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.ThingEntityHelper;
import com.thing.entity.ModeledEntity;
import com.thing.entity.XThingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "pageUri", hidden = true), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "identity", width = "200", sortable = true), //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "description", width = "250"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class ThingEntityTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XObject xobj = commandBean.getPrimaryObj();
		if (xobj instanceof XThingModel) {
			XThingModel model = (XThingModel) xobj;
			List<?> list = ThingEntityHelper.service().queryConfigEntity(model, null, null);
			for (Object obj : list) {
				if (obj instanceof AbstractEntity) {
					ModeledEntity entity = (ModeledEntity) obj;
					Map<String, Object> row = new HashMap<>();
					row.put("oid", entity.getOid());
					row.put("pageUri", entity.getPageUri());
					row.put("icon", "<img src='" + entity.getIcon() + "'/>");
					row.put("identity", entity.getThingIdentity());
					row.put("name", entity.getName());
					row.put("description", entity.getDescription());
					row.put("createdStamp", entity.getCreatedStamp());
					row.put("modifiedStamp", entity.getModifiedStamp());

					result.add(row);
				}
			}
		}

		return result;
	}

}
