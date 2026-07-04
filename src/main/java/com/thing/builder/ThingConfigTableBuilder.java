package com.thing.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.orm.XPersistable;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.ThingUtilities;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "blank", width = "25"), //
				@UIColumn(field = "name", title = "Name", width = "150"), //
				@UIColumn(field = "localDisplay", width = "250", sortable = true), //
				@UIColumn(field = "value", width = "400") //
		} //
)
public class ThingConfigTableBuilder extends AbstractTableComponentBuilder {
	@Override
	public List<? extends Object> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPersistable persist = commandBean.getPrimaryObj();
		if (persist == null)
			return result;

		return ThingUtilities.getThingConfiguration(persist);
	}
}
