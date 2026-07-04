package plm.dynamic.processor;

import java.util.List;

import plm.dynamic.XExpression;
import plm.dynamic.service.DynamicServiceHelper;
import plm.part.XPart;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.XException;

public class CreateXExpressionProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String _number = commandBean.getTextParameter("number");
		String _name = commandBean.getTextParameter("name");
		String _description = commandBean.getTextParameter("description");
		String _sentence = commandBean.getTextParameter("sentence");
		XObject context = commandBean.getPrimaryObj();

		if (context instanceof XPart) {
			XPart part = (XPart) context;
			List<XExpression> charList = DynamicServiceHelper.repository().getRelatedXExpression(part, _name);
			if (charList != null && !charList.isEmpty()) {
				throw new XException("表达式：" + _name + " 已经存在！");
			}
			XExpression xexpress = XExpression.newXExpression(part);
			xexpress.setNumber(_number.toUpperCase());
			xexpress.setName(_name);
			xexpress.setDescription(_description);
			xexpress.setExpression(_sentence);

			xexpress = PersistenceHelper.service().save(xexpress);
			formResult.setData(xexpress);
		}

		return formResult;
	}
}
