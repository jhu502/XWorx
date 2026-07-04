package plm.part.processor;

import plm.part.XPart;
import plm.part.XPartUsageLink;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;

public class InsertExistPartProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		XPart context = (XPart) commandBean.getPrimaryObj();

		Object value = commandBean.getParameter("selected_oid");
		if (value instanceof String) {
			XPart selectedPart = PersistenceHelper.service().refresh(new ObjectReference<XPart>((String) value));
			XPartUsageLink usageLink = XPartUsageLink.newInstance(context, selectedPart.getMaster());
			usageLink.setQuantity(1);
			usageLink.setUnit("EA");
			usageLink = PersistenceHelper.service().save(usageLink);
			formResult.setData(usageLink);
		} else if (value instanceof String[]) {
			for (Object obj : (String[]) value) {
				XPart xpart = PersistenceHelper.service().refresh(new ObjectReference<XPart>((String) obj));
				XPartUsageLink usageLink = XPartUsageLink.newInstance(context, xpart.getMaster());
				usageLink.setQuantity(1);
				usageLink.setUnit("EA");
				usageLink = PersistenceHelper.service().save(usageLink);
				formResult.setData(usageLink);
			}
		}

		return formResult;
	}
}
