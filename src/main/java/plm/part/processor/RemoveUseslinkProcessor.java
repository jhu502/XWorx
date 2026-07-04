package plm.part.processor;

import plm.part.XPartUsageLink;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;

public class RemoveUseslinkProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		Object rowIds = commandBean.getParameter("rowIds");
		if (rowIds instanceof Object[]) {
			for (Object ids : (Object[]) rowIds) {
				if (ids != null) {
					String[] xx = ((String) ids).split("~");
					String useOid = xx[0];
					XPartUsageLink usagelink = PersistenceHelper.service().refresh(new ObjectReference<XPartUsageLink>(useOid));
					if (usagelink != null) {
						PersistenceHelper.service().remove(usagelink);
					}
				}
			}
		}

		return formResult;
	}
}
