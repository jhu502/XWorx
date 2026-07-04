package com.thing.processor;

import com.thing.entity.XPropertyDefinition;
import com.thing.ThingPerformHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;

public class EditPropertyDefinitionProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		try {
			XPropertyDefinition propertyDef = (XPropertyDefinition) commandBean.getPrimaryObj();
			if (propertyDef != null) {
				propertyDef = this.convertSafe(commandBean.getParameterMap(), propertyDef);
				propertyDef.setPersistent("on".equals(commandBean.getParameter("persistent")));
				propertyDef.setReadOnly("on".equals(commandBean.getParameter("readOnly")));
				propertyDef.setLogged("on".equals(commandBean.getParameter("logged")));
				propertyDef.setNullable("on".equals(commandBean.getParameter("nullable")));
				propertyDef = PersistenceHelper.service().update(propertyDef);
				ThingPerformHelper.service().loadPropertyDefinition(propertyDef);
			}
			formResult.setData(propertyDef);
		} finally {
		}

		return formResult;
	}
}
