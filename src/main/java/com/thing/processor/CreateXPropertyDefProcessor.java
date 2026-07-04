package com.thing.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;
import com.thing.entity.XPropertyDefinition;
import com.thing.entity.XThingModel;

public class CreateXPropertyDefProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		XObject primary = commandBean.getPrimaryObj();
		if (primary instanceof XThingModel) {
			XThingModel thingModel = (XThingModel) primary;
			XPropertyDefinition propertyDef = new XPropertyDefinition();
			propertyDef.setPropertyProvider(thingModel);
			String baseType = commandBean.getTextParameter("baseType");
			propertyDef.setBaseType(XBaseType.valueOf(baseType));
			propertyDef.setName(commandBean.getTextParameter("name"));
			String description = commandBean.getTextParameter("description");
			propertyDef.setDisplay(description == null ? "" : description);
			String readOnly = commandBean.getTextParameter("readOnly");
			propertyDef.setReadOnly("on".equals(readOnly) ? true : false);
			String persistent = commandBean.getTextParameter("persistent");
			propertyDef.setPersistent("on".equals(persistent) ? true : false);
			String logged = commandBean.getTextParameter("logged");
			propertyDef.setLogged("on".equals(logged) ? true : false);
			String nullable = commandBean.getTextParameter("nullable");
			propertyDef.setNullable("on".equals(nullable) ? true : false);
			String defaultValue = commandBean.getTextParameter("defaultValue");
			propertyDef.setDefaultValue(defaultValue == null ? "" : defaultValue);
			propertyDef = PersistenceHelper.service().save(propertyDef);

			formResult.setData(propertyDef);
			formResult.setStatus(FormStatus.SUCCESS);

			//ThingPerformHelper.service().loadPropertyDefinition(propertyDef);
		}

		return formResult;
	}

}
