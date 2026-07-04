package plm.dynamic.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.thing.entity.XPropertyDefinition;
import com.thing.entity.XThingModel;

import plm.dynamic.XCharacteristic;
import plm.part.XPart;

@UIDataGrid(idField = "rowId", rowNumber = true, pagination = false, singleSelect = true, selectOnCheck = true, checkOnSelect = true, fit = true, //
		columns = { //
				@UIColumn(field = "icon", width = "20", expander = true, frozen = true), //
				@UIColumn(field = "attribute", width = "100"), //
				@UIColumn(field = "description", width = "300"), //
		} //
)
public class MappingAttrTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XObject xobject = commandBean.getPrimaryObj();
		XPart xpart = null;
		if (xobject instanceof XPart) {
			xpart = (XPart) xobject;
		}
		if (xobject instanceof XCharacteristic) {
			XCharacteristic xcharact = (XCharacteristic) xobject;
			xpart = (XPart) xcharact.getCharacted();
		}
		if (xpart != null) {
			for (Method method : xpart.getClass().getMethods()) {
				if (isTargetMethod(method)) {
					String name = this.getAttrName(method.getName());
					String field = lower(name);
					Map<String, String> row = new HashMap<>();
					row.put("attribute", field);
					row.put("description", name);
					result.add(row);
				}
			}
			XThingModel thingModel = (XThingModel) xpart.getThingModel();
			List<?> list = ThingModelHelper.manager().getPropertyDefinition(thingModel);
			if (list != null) {
				for (Object object : list) {
					XPropertyDefinition propertyDef = (XPropertyDefinition) object;
					Map<String, String> row = new HashMap<>();
					row.put("attribute", propertyDef.getName());
					row.put("description", propertyDef.getDisplay());
					result.add(row);
				}
			}
		}
		return result;
	}

	/**
	 * 判断当前方法是否是需要进行序列化的方法
	 * @param method
	 * @return
	 */
	public boolean isTargetMethod(Method method) {
		if ("getClass".equals(method.getName()))
			return false;

		if (method.getParameterCount() > 0 || void.class.equals(method.getReturnType()))
			return false;

		Class<?> x = method.getReturnType();
		if (String.class.equals(x) || Double.class.equals(x) || Integer.class.equals(x) || Float.class.equals(x) || Boolean.class.equals(x) || double.class.equals(x) || int.class.equals(x)
				|| float.class.equals(x) || boolean.class.equals(x)) {
			return method.getName().startsWith("get") || method.getName().startsWith("is");
		} else {
			return false;
		}
	}

	public String getAttrName(String methodName) {
		if (methodName.startsWith("get")) {
			return methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			return methodName.substring(2);
		}

		return methodName;
	}

	public String lower(String string) {
		char[] chars = string.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

}
