package com.thing.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.Argument;
import com.flame.thing.IServiceDefinition;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.Button;
import com.flame.xui.widget.IconBox;
import com.thing.ThingUtilities;
import com.thing.entity.ModeledEntity;
import com.thing.entity.XThingModel;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "blank", width = "20"), //
				@UIColumn(field = "icon", width = "20"), //
				@UIColumn(field = "type", width = "110"), //
				@UIColumn(field = "result", width = "100", align = "right"), //
				@UIColumn(field = "serviceName", width = "200", sortable = true), //
				@UIColumn(field = "input", width = "250"), //
				@UIColumn(field = "invoke", width = "70") //
		} //
)
public class ThingServiceTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		XObject xobject = commandBean.getPrimaryObj();
		if (!(xobject instanceof ModeledEntity))
			return result;

		ModeledEntity entityThing = (ModeledEntity) xobject;
		XThingModel thingModel = (XThingModel) entityThing.getThingModel();
		for (IServiceDefinition serviceDef : thingModel.getServiceDefinitions()) {

			int j = 0;
			StringBuffer paramBuf = new StringBuffer("");
			for (Argument argument : serviceDef.getArguments()) {
				if (j++ == 0) {
					paramBuf.append(argument.getBaseType().getDisplay()).append(" ").append(argument.getName());
				} else {
					paramBuf.append(", ").append(argument.getBaseType().getDisplay()).append(" ").append(argument.getName());
				}
			}

			Map<String, Object> row = new HashMap<String, Object>();
			row.put("oid", serviceDef.getOid());
			row.put("icon", new IconBox(serviceDef.getServiceType().getIcon()));
			row.put("type", serviceDef.getServiceType().name());
			row.put("result", serviceDef.getResultType().getDisplay());
			row.put("serviceName", serviceDef.getName());
			row.put("input", paramBuf.toString());
			String params = "{oid:'" + entityThing.getOid() + "',name:'" + serviceDef.getName() + "'}";
			String invokeFunc = "flame.popupWindow('Invoke Thing Service','freemarker/thing/invokeServices', " + params+ ", 'width:1024px;height:600px;padding:5px;');";
			row.put("invoke", new Button("Invoke", invokeFunc));

			result.add(row);
		}

		Class<?> thingClass = thingModel.getModelClass();
		List<Map<String, Object>> serviceList = ThingUtilities.getThingServices(thingClass);
		result.addAll(serviceList);

		return result;
	}

}