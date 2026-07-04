package com.thing.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;
import com.flame.thing.PropertyType;
import com.flame.util.XException;
import com.thing.entity.XPropertyDefinition;

public class RemoveXPropertyDefProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		Object oids = commandBean.getParameter("oids");
		if (oids instanceof Object[]) {
			for (Object oid : (Object[]) oids) {
				if (!(oid instanceof String))
					continue;
				String _oid = (String) oid;
				if (oid == null || !PersistenceHelper.isOidFormat(_oid))
					continue;
				XPersistable persist = PersistenceHelper.getPersistable(_oid);
				if (persist instanceof XPropertyDefinition) {
					XPropertyDefinition definition = (XPropertyDefinition) persist;
					if (PropertyType.MBA.equals(definition.getPropertyType())) {
						throw new XException(S("不允许删除MBA属性."));
					} else {
						PersistenceHelper.service().remove(persist);
					}
				}
			}
		}

		return formResult;
	}

}
