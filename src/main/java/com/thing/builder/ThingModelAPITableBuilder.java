package com.thing.builder;

import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.ThingUtilities;
import com.thing.entity.XThingModel;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, pagination = true, //
		columns = { //
				@UIColumn(field = "blank", width = "20"), //
				@UIColumn(field = "type", width = "110", align = "left"), //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "localDisplay", width = "250"), //
				@UIColumn(field = "description", width = "250", align = "center", sortable = true), //
		} //
)
public class ThingModelAPITableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		String oid = commandBean.getPrimaryOid();

		XThingModel thingModel = PersistenceHelper.service().refresh(new ObjectReference<XThingModel>(oid));
		Class<?> modelCls = thingModel.getModelClass();
		return ThingUtilities.getThingConfiguration(modelCls);
	}

}
