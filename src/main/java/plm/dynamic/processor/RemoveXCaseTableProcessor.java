package plm.dynamic.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XUIRowId;
import plm.dynamic.XCaseTable;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;

public class RemoveXCaseTableProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		List<XCaseTable> removeList = new ArrayList<>();

		XUIRowId[] xuiRowIds = commandBean.getRowIds();
		for (XUIRowId xuiRowId : xuiRowIds) {
			XCaseTable casetable = (XCaseTable) xuiRowId.getRowObject();
			removeList.add(casetable);
		}
		if (!removeList.isEmpty()) {
			PersistenceHelper.service().remove(removeList);
		}

		return formResult;
	}
}
