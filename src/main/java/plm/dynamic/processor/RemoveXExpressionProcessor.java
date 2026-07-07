package plm.dynamic.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XUIRowId;
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

		XUIRowId[] xuiRowIds = commandBean.getRowIds();
		for (XUIRowId xuiRowId : xuiRowIds) {
			XExpression expression = (XExpression)xuiRowId.getRowObject();
			removeList.add(expression);
		}
		if (!removeList.isEmpty()) {
			PersistenceHelper.service().remove(removeList);
		}

		return formResult;
	}
}
