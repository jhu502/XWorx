package plm.dynamic.processor;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.XCaseTable;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;

public class RemoveXCaseTableProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		List<XCaseTable> removeList = new ArrayList<>();
		Object[] objs = (Object[]) commandBean.getParameter("rowIds");
		for (Object _object : objs) {
			if (ObjectReference.isOid((String) _object)) {
				XCaseTable casetable = PersistenceHelper.service().refresh(new ObjectReference<XCaseTable>((String) _object));
				removeList.add(casetable);
			}
		}
		if (!removeList.isEmpty()) {
			PersistenceHelper.service().remove(removeList);
		}

		return formResult;
	}
}
