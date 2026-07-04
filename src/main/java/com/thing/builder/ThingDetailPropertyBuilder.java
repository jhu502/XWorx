package com.thing.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.orm.XPersistable;
import com.flame.thing.IModelManaged;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.ThingEntityHelper;
import com.thing.common.IBindable;
import com.thing.common.IConnectable;
import com.thing.common.IThingManaged;
import com.thing.entity.ModeledEntity;
import com.thing.entity.XThingModel;

@UIDataGrid(groupField = "group", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "value", width = "300", sortable = true), //
				@UIColumn(field = "group", width = "300", sortable = false) //
		} //
)
public class ThingDetailPropertyBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPersistable persist = commandBean.getPrimaryObj();
		if (persist instanceof ModeledEntity) {
			ModeledEntity ething = (ModeledEntity) persist;

			Map<String, Object> identity = new HashMap<String, Object>();
			identity.put("name", "Identity");
			identity.put("value", ething.getThingIdentity());
			identity.put("group", "Basic Info");
			result.add(identity);

			Map<String, Object> number = new HashMap<String, Object>();
			number.put("name", "Number");
			number.put("value", ething.getNumber());
			number.put("group", "Basic Info");
			result.add(number);

			Map<String, Object> name = new HashMap<String, Object>();
			name.put("name", "Name");
			name.put("value", ething.getName());
			name.put("group", "Basic Info");
			result.add(name);

			Map<String, Object> description = new HashMap<String, Object>();
			description.put("name", "Description");
			description.put("value", ething.getDescription());
			description.put("group", "Basic Info");
			result.add(description);

			Map<String, Object> createdOn = new HashMap<String, Object>();
			createdOn.put("name", "Created On");
			createdOn.put("value", ething.getCreatedStamp());
			createdOn.put("group", "Basic Info");
			result.add(createdOn);

			Map<String, Object> lastModified = new HashMap<String, Object>();
			lastModified.put("name", "Last Modified");
			lastModified.put("value", ething.getModifiedStamp());
			lastModified.put("group", "Basic Info");
			result.add(lastModified);

			IThingManaged<IModelManaged> pthing = ThingEntityHelper.dispatch().getInflatedThingEntity(ething.getThingIdentity());
			if (pthing instanceof IConnectable) {
				Map<String, Object> connected = new HashMap<String, Object>();
				connected.put("name", "Connected");
				connected.put("value", ((IConnectable<?>) pthing).isConnected());
				connected.put("group", "State Info");
				result.add(connected);
			}

			if (pthing instanceof IBindable) {
				IBindable bindable = (IBindable) pthing;
				Map<String, Object> binded = new HashMap<String, Object>();
				binded.put("name", "Binding");
				binded.put("value", bindable.isBinding());
				binded.put("group", "State Info");
				result.add(binded);
			}

			XThingModel tmodel = (XThingModel) ething.getThingModel();

			Map<String, Object> thingType = new HashMap<String, Object>();
			thingType.put("name", "Identity");
			thingType.put("value", tmodel.getOid());
			thingType.put("group", "Model Info");
			result.add(thingType);
		}

		return result;
	}

}
