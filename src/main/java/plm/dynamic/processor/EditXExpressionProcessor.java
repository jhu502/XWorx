package plm.dynamic.processor;

import plm.dynamic.XExpression;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

public class EditXExpressionProcessor extends DefaultFormProcessor {

	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String _name = commandBean.getTextParameter("name");
		String _description = commandBean.getTextParameter("description");
		String _sentence = commandBean.getTextParameter("sentence");

		XUIRowId uiRowId = commandBean.getRowId();
		XObject xobject = (XObject) PersistenceHelper.getPersistable(uiRowId.getObjectId());
		if (xobject instanceof XExpression) {
			XExpression xexpress = (XExpression) xobject;
			xexpress.setName(_name);
			xexpress.setDescription(_description);
			xexpress.setExpression(_sentence);

			xexpress = PersistenceHelper.service().save(xexpress);
			formResult.setData(xexpress);
		}

		return formResult;
	}
}
