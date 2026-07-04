package com.thing.builder;

import com.flame.xui.XCommandBean;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.entity.XPropertyDefinition;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, fitColumns = false, //
		columns = {  //
				@UIColumn(field = "oid", checkbox = true),  //
				@UIColumn(field = "persistentImg", width = "25"),  //
				@UIColumn(field = "readOnlyImg", width = "25"),  //
				@UIColumn(field = "loggedImg", width = "25"),  //
				@UIColumn(field = "name", width = "130", sortable = true), //
				@UIColumn(field = "baseType", width = "80"), //
				@UIColumn(field = "description", width = "200"), //
				@UIColumn(field = "defaultValue", width = "150"), //
				@UIColumn(field = "value", width = "150"), //
				@UIColumn(field = "nullableImg", width = "20"),  //
				@UIColumn(field = "createdStamp", width = "130"), //
				@UIColumn(field = "modifiedStamp", width = "130") //
		} //
)
public class ThingModelInheritedBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<XPropertyDefinition> result = new ArrayList<XPropertyDefinition>();
		return result;
	}

}
