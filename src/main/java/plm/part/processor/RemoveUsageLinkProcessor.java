package plm.part.processor;

import plm.part.XPartUsageLink;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;

public class RemoveUsageLinkProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		XUIRowId[] xuiRowIds = commandBean.getRowIds();
		for (XUIRowId xuiRowId : xuiRowIds) {
			XPartUsageLink usagelink = (XPartUsageLink) xuiRowId.getRowObject();
			PersistenceHelper.service().remove(usagelink);
		}

		return formResult;
	}
}
