package plm.dynamic.processor;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.XExpression;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;

public class RemoveXExpressionProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		List<XExpression> removeList = new ArrayList<>();
		Object[] objs = (Object[]) commandBean.getParameter("rowIds");
		for (Object _object : objs) {
			if (ObjectReference.isOid((String) _object)) {
				XExpression xexpress = PersistenceHelper.service().refresh(new ObjectReference<XExpression>((String) _object));
				removeList.add(xexpress);
			}
		}
		if (!removeList.isEmpty()) {
			PersistenceHelper.service().remove(removeList);
		}

		return formResult;
	}
}
