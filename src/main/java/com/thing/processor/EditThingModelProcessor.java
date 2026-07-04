package com.thing.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.XTransaction;

public class EditThingModelProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		
		XTransaction tran = new XTransaction();
		try {
			tran.begin();
			
			
			tran.commit();
			tran = null;
		} finally {
			if (tran != null) {
				tran.rollback();
			}
		}
		
		return formResult;
	}
}
